package ru.graviton.profiles.service.licenses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptographyService {
    private static final String ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final int KEY_SIZE = 2048;
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

    public String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // стандартная схема
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String base64Data) throws Exception {
        PrivateKey privateKey = getServerPrivateKey();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decoded = Base64.getDecoder().decode(base64Data);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }

    public String decrypt(String base64Data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decoded = Base64.getDecoder().decode(base64Data);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
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

    public PublicKey getServerPublicKey() {
        try {
            String keyPem = loadPrivateKeyFromResource("public-server-key.pem");
            keyPem = keyPem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            // Декодируем Base64
            byte[] keyBytes = Base64.getDecoder().decode(keyPem);

            // Восстанавливаем PrivateKey
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM); // или "EC", если генерил EC ключ
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load server public key", e);
        }
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
}