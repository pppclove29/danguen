package com.example.danguen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class ChatTest extends BaseTest {

    private StompSession stompSession;
    private WebSocketStompClient client;

    @LocalServerPort
    private int port;

    private String url;

    @BeforeEach
    public void 웹소켓생성() throws ExecutionException, InterruptedException, TimeoutException {
        //SockJsClient 는 WebSocketClient 를 구현했다!
        //SockJsClient 는 List<Transport> transports 를 받아야한다 이게 뭐냐?
        //Transport를 구현한 객체중 WebSocketTransport를 발견했다 이걸로 써보자
        //근데 WebSocketTransport는 WebSocketClient가 필요하다 뭔 ㅋㅋㅋ
        //StandardWebSocketClient 또한 WebSocketClient 를 구현했다
        //이는 새로운 WebSocketContainer를 반환한다
        //솔직히 이렇게 정리해도 잘 모르겠다 여튼 이렇게 새로운 클라이언트를 생성했다. 한거맞나?
        this.client = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));

        // default = SimpleMessageConverter
        this.client.setMessageConverter(new MappingJackson2MessageConverter());

        this.url = "ws://localhost:";

        System.out.println("소켓 클라이언트 생성 및 기본 값 초기화");

        System.out.println("final url = " + url + port + "/ws" );

        this.stompSession = this.client
                .connect(url + port + "/ws", new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        System.out.println("연결되었습니다");
                    }
                }) // 연결 및 핸들러에 알린다
                .get(3, TimeUnit.SECONDS); // 최대 대기시간?

        //근데 저 함수는 익명함수다
        //그렇다면 내 맘대로 해당 함수를 구현해서 각 요청에 대한 처리를 하면 되지 않을까?
    }

    @Test
    public void 메세지_송신() throws Exception {
        //
    }
}
