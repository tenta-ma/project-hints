package org.tantama.anchoco.sample.httpclient.rest;

import java.net.http.HttpClient;
import java.time.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.tantama.anchoco.TestHttpRequestDto;
import org.tantama.anchoco.TestHttpResponseDto;
import org.tantama.anchoco.helper.JsonHelper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * {@link RestSample}のテストクラス
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("http client のためのmock server sample")
class RestSampleTest {

    /** テスト来小クラス */
    @InjectMocks
    private RestSample target;

    /** mock server */
    private static ClientAndServer mockServer;

    /** mock server のhost名 */
    private static final String MOCK_SERVER_HOST = "localhost";

    /** mock server のport */
    private static final int MOCK_SWERVER_PORT = 6180;

    /**
     * テストクラス初期処理
     *
     * @throws Exception 処理時例外
     */
    @BeforeAll
    public static void beforeClass() throws Exception {
        // mock server start.
        mockServer = ClientAndServer.startClientAndServer(MOCK_SWERVER_PORT);
    }

    /** 初期化処理 */
    @BeforeEach
    public void setUp() {

        final long timeout = 3;

        ReflectionTestUtils.setField(target, "readTimeout", timeout);

        HttpClient.Builder builder = HttpClient.newBuilder();
        builder.connectTimeout(Duration.ofSeconds(timeout));
        ReflectionTestUtils.setField(target, "httpClient", builder.build());

        // mock server re-start.
        mockServer.reset();
    }

    /**
     * {@link HttpConnection#postJson(String, Object, Class)}のテスト
     */
    @Test
    @SuppressWarnings("resource")
    @DisplayName("mock serverの利用サンプル")
    void testPostJsonURIRequestDtoClassOfResponseDto() {

        // set mock server.
        TestHttpResponseDto response = new TestHttpResponseDto();
        response.setResultCode("01");

        // tips. use SuppressWarnings resource
        // new MockServerClient(MOCK_SERVER_HOST, MOCK_SWERVER_PORT) を
        // try-resourceで記述すれば、SuppressWarnings resourceは不要だが
        // MockServerClientが落ちた、というエラーがでてそれはそれでうざいので、抑止でごまかし
        new MockServerClient(MOCK_SERVER_HOST, MOCK_SWERVER_PORT)
                .when(
                    org.mockserver.model.HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/hogehoge"))
                .respond(
                    org.mockserver.model.HttpResponse.response(JsonHelper.toJson(response))
                            // withDelay : time out settings
                            // .withDelay(TimeUnit.SECONDS, timeout + 1)
                            .withStatusCode(HttpStatus.OK.value()));

        // setting test method parameter.
        final String url = "http://" + MOCK_SERVER_HOST + ":" + MOCK_SWERVER_PORT + "/hogehoge";

        TestHttpRequestDto request = new TestHttpRequestDto();
        request.setId(1);
        request.setName("hoge");

        TestHttpResponseDto actualResponse = target.postJson(url, request, TestHttpResponseDto.class);

        // assertion
        // mock server からの返却値が利用される
        assertNotNull(actualResponse);
        assertEquals(response.getResultCode(), actualResponse.getResultCode());
    }

    /**
     * {@link HttpConnection#postJson(java.net.URI, Object, Class)}のテスト
     */
    @Test
    @DisplayName("利用サンプルのオーバーロードメソッド")
    @Disabled("string to uri のオーバーロードなので、サンプルとしては同じになるため実装しない")
    void testPostJsonStringRequestDtoClassOfResponseDto() {
        fail("Not yet implemented");
    }

}
