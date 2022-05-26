package org.tantama.anchoco.sample.httpclient.rest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.tantama.anchoco.helper.JsonHelper;

/**
 * 認証データ基盤の通信
 * データ基盤通信部品
 */
@Component
public class RestSample {

    /** http client */
    @Autowired
    private HttpClient httpClient;

    /** 読み取りタイムアウト(秒) */
    @Value("${rest.timeout-second.read:5}")
    private long readTimeout;

    /**
     * getによるjson接続を行う<br>
     * このサンプルではstatus code が 200の場合のみレスポンスのjsonをdtoに変換して返却する
     * 
     * @param <RequestDto>  リクエストの型
     * @param <ResponseDto> レスポンスの型
     * @param url           接続先url
     * @param requestDto    リクエスト情報
     * @param responseClass レスポンスの型
     * @return レスポンス情報
     */
    public <RequestDto, ResponseDto> ResponseDto getJson(String url, RequestDto requestDto, Class<ResponseDto> responseClass) {

        // パラメータのURL組み立て

        // e.g.
        // url : http://localhost:6180/users
        // requestDto.hoge : poiyu
        // requestDto.fuga : fluss
        // ->
        // uri : http://localhost:6180/users?hoge=poiyu&fuga=fluss
        URI uri = UriComponentsBuilder.fromUriString(url)
                .queryParam("hoge", "requestDto.getHoge()")
                .queryParam("fuga", "requestDto.getFuga()")
                .build().toUri();

        // http requestの作成
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(readTimeout))
                .uri(uri)
                .headers("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, BodyHandlers.ofString());

            // httpstatus handling.
            return switch (HttpStatus.valueOf(response.statusCode())) {
                case OK -> JsonHelper.toDto(response.body(), responseClass);
                default -> {
                    // 想定外のステータス的な処理
                    // server error
                    throw new RuntimeException("error status");
                }
            };

        } catch (IOException | InterruptedException e) {
            // tips. 実際に利用する場合は適当なクラスにwrapする
            throw new RuntimeException(e);
        }
    }

    /**
     * postによるjson接続を行う<br>
     * このサンプルではstatus code が 200, 201 の場合のみレスポンスのjsonをdtoに変換して返却する
     * 
     * @param <RequestDto>  リクエストの型
     * @param <ResponseDto> レスポンスの型
     * @param url           接続先url
     * @param requestDto    リクエスト情報
     * @param responseClass レスポンスの型
     * @return レスポンス情報
     */
    public <RequestDto, ResponseDto> ResponseDto postJson(URI url, RequestDto requestDto, Class<ResponseDto> responseClass) {

        // http requestの作成
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(readTimeout))
                .uri(url)
                .headers("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .POST(BodyPublishers.ofString(JsonHelper.toJson(requestDto)))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, BodyHandlers.ofString());

            // httpstatus handling.
            return switch (HttpStatus.valueOf(response.statusCode())) {
                case OK, CREATED -> JsonHelper.toDto(response.body(), responseClass);
                default -> {
                    // 想定外のステータス的な処理
                    // server error
                    throw new RuntimeException("error status");
                }
            };

        } catch (IOException | InterruptedException e) {
            // tips. 実際に利用する場合は適当なクラスにwrapする
            throw new RuntimeException(e);
        }
    }

    /**
     * postによるjson接続を行う
     *
     * @param <RequestDto>  リクエストの型
     * @param <ResponseDto> レスポンスの型
     * @param url           接続先url
     * @param requestDto    リクエスト情報
     * @param responseClass レスポンスの型
     * @return レスポンス情報
     */
    public <RequestDto, ResponseDto> ResponseDto postJson(String url, RequestDto requestDto, Class<ResponseDto> responseClass) {
        return postJson(URI.create(url), requestDto, responseClass);
    }
}
