package org.tantama.anchoco.sample.httpclient.config;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link java.net.http.HttpClient}設定
 */
@Configuration
public class HttpClientConfig {

    /**
     * {@link java.net.http.HttpClient}のbean定義
     * 
     * @param connectionTimeout 接続タイムアウト(秒)
     * @return {@link java.net.http.HttpClient}
     */
    @Bean
    public HttpClient httpClient(@Value("${rest.timeout-second.connection:5}") long connectionTimeout) {
        HttpClient.Builder builder = HttpClient.newBuilder();
        builder.connectTimeout(Duration.ofSeconds(connectionTimeout));
        return builder.build();
    }

}
