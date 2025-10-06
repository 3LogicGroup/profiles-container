package ru.graviton.profiles.service.licenses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import ru.graviton.profiles.dto.HybridEncrypted;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptographyService {
    private static final String ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private final ResourceLoader resourceLoader;

    public String signData(String message) throws Exception {

        // Достаём приватный ключ
        PrivateKey privateKey = getServerPrivateKey();
        byte[] data = message.getBytes();
        // Подписание
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        byte[] signatureBytes = signature.sign();

        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    public boolean verifyActivationRequest(PublicKey publicKey, String data, String signature) {
        try {
            return verify(publicKey, data.getBytes(StandardCharsets.UTF_8),
                    Base64.getDecoder().decode(signature));
        } catch (Exception e) {
            log.error("Error verifying activation request", e);
            return false;
        }
    }

    public PublicKey getPublicKey(String keyStr) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyStr);

            // Воссоздаем PublicKey из байт
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM); // алгоритм ключа
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load public key", e);
        }
    }

    public boolean verify(PublicKey publicKey, byte[] data, byte[] sigBytes) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sigBytes);
    }

    public PrivateKey getServerPrivateKey() {
        try {
            String keyPem = loadPrivateKeyFromResource("private-server-key.pem");
            keyPem = keyPem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            // Декодируем Base64
            byte[] keyBytes = Base64.getDecoder().decode(keyPem);

            // Восстанавливаем PrivateKey
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM); // или "EC", если генерил EC ключ
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to server client public key", e);
        }
    }


    private String loadPrivateKeyFromResource(String key) {
        try {
            var resource = resourceLoader.getResource("classpath:" + key);
            if (!resource.exists()) {
                throw new RuntimeException("private key resource not found: private");
            }

            try (InputStream inputStream = resource.getInputStream()) {
                return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.error("Failed to load private key from resource", e);
            throw new RuntimeException("Failed to load private key from resource", e);
        }
    }

    public HybridEncrypted hybridEncrypt(String data, PublicKey rsaPublicKey) throws Exception {
        // 1. Генерируем AES ключ
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey aesKey = keyGen.generateKey();

        // 2. Шифруем данные AES (GCM для аутентификации)
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = SecureRandom.getInstanceStrong().generateSeed(12);
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));
        byte[] encryptedData = aesCipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // 3. Шифруем AES ключ RSA
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] encryptedKey = rsaCipher.doFinal(aesKey.getEncoded());

        HybridEncrypted result = new HybridEncrypted();
        result.encryptedKey = Base64.getEncoder().encodeToString(encryptedKey);
        result.iv = Base64.getEncoder().encodeToString(iv);
        result.encryptedData = Base64.getEncoder().encodeToString(encryptedData);
        return result;
    }

    public String hybridDecrypt(HybridEncrypted encrypted) throws Exception {
        // 1. Расшифровываем AES ключ RSA
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, getServerPrivateKey());
        byte[] aesKeyBytes = rsaCipher.doFinal(Base64.getDecoder().decode(encrypted.encryptedKey));
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        // 2. Расшифровываем данные AES
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = Base64.getDecoder().decode(encrypted.iv);
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));
        byte[] decryptedData = aesCipher.doFinal(Base64.getDecoder().decode(encrypted.encryptedData));

        return new String(decryptedData, StandardCharsets.UTF_8);
    }


}