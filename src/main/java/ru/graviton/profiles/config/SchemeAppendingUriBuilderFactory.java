package ru.graviton.profiles.config;

import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public class SchemeAppendingUriBuilderFactory extends DefaultUriBuilderFactory
{
    private final String defaultScheme;
    private final String baseUrl;

    public SchemeAppendingUriBuilderFactory(String defaultScheme, String baseUrl) {
        super();
        this.defaultScheme = defaultScheme;
        this.baseUrl = baseUrl;
        // Настройка кодирования для поддержки русских символов в URL
        setEncodingMode(EncodingMode.VALUES_ONLY);

    }

    public UriBuilder uriString(String uriString) {
        // Обработка относительных URL путей
        if (uriString.startsWith("/") && baseUrl != null) {
            return super.uriString(baseUrl + uriString);
        }

        // Добавляем схему, если её нет
        if (!uriString.startsWith("http://") && !uriString.startsWith("https://")) {
            // Если URI не начинается с /, объединяем с baseUrl
            if (baseUrl != null && !uriString.startsWith("/")) {
                // Убедимся, что между baseUrl и uriString есть слеш
                if (baseUrl.endsWith("/")) {
                    return super.uriString(baseUrl + uriString);
                } else {
                    return super.uriString(baseUrl + "/" + uriString);
                }
            } else {
                // Просто добавим схему
                return super.uriString(defaultScheme + "://" + uriString);
            }
        }

        return super.uriString(uriString);
    }
    @Override
    public UriBuilder builder() {
        if (baseUrl != null) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);
            return new UriComponentsBuilder(builder) {
            };
        }
        return super.builder();
    }

}