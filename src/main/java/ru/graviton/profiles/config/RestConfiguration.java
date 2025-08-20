package ru.graviton.profiles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.*;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Конфиг для REST API взаимодействия
 */
@Configuration
public class RestConfiguration {
    @Bean("defaultRestClient")
    @Primary
    public RestClient restClient(RestClient.Builder builder) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, null);

// Создаем SSLEngine и отключаем endpoint identification
        SSLEngine sslEngine = sslContext.createSSLEngine();
        SSLParameters params = sslEngine.getSSLParameters();
        params.setEndpointIdentificationAlgorithm(null);
        sslEngine.setSSLParameters(params);


        HttpClient build = HttpClient.newBuilder()
                .sslContext(sslContext)
                .connectTimeout(Duration.of(5, ChronoUnit.SECONDS))
                .build();
        JdkClientHttpRequestFactory jdkClientHttpRequestFactory = new JdkClientHttpRequestFactory(build);
        jdkClientHttpRequestFactory.setReadTimeout(Duration.of(30, ChronoUnit.SECONDS));

        return builder
                .uriBuilderFactory(new SchemeAppendingUriBuilderFactory("https", null))
                .requestFactory(jdkClientHttpRequestFactory)
                .build();
    }

    @Bean
    public ClientHttpRequestFactory getClientHttpRequestFactory() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpClient build = HttpClient.newBuilder()
                .sslContext(sc)
                .connectTimeout(Duration.of(5, ChronoUnit.SECONDS))
                .build();
        build.sslParameters().setEndpointIdentificationAlgorithm(null);


        JdkClientHttpRequestFactory jdkClientHttpRequestFactory = new JdkClientHttpRequestFactory(build);
        jdkClientHttpRequestFactory.setReadTimeout(Duration.of(30, ChronoUnit.SECONDS));
        return jdkClientHttpRequestFactory;
    }
}
