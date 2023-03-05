package com.example.danguen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.List;
import java.util.concurrent.ExecutionException;

@AutoConfigureMockMvc
public class ChatTest extends BaseTest {

    private StompSession stompSession1, stompSession2;
    private WebSocketStompClient client1, client2;

    @LocalServerPort
    private int port;

    @Value("${websocket.url.host}")
    private String url;
    @Value("${websocket.url.private}")
    private String privateMsgUrl;


    @BeforeEach
    public void 웹소켓_생성() {
        //SockJsClient 는 WebSocketClient 를 구현했다!
        //SockJsClient 는 List<Transport> transports 를 받아야한다 이게 뭐냐?
        //Transport를 구현한 객체중 WebSocketTransport를 발견했다 이걸로 써보자
        //근데 WebSocketTransport는 WebSocketClient가 필요하다 뭔 ㅋㅋㅋ
        //StandardWebSocketClient 또한 WebSocketClient 를 구현했다
        //이는 새로운 WebSocketContainer를 반환한다
        //솔직히 이렇게 정리해도 잘 모르겠다 여튼 이렇게 새로운 클라이언트를 생성했다. 한거맞나?\
        client1 = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        client2 = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));

        // default = SimpleMessageConverter
        client1.setMessageConverter(new MappingJackson2MessageConverter());
        client2.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void 세션_연결() throws ExecutionException, InterruptedException {

    }

    @Test
    public void 구독_및_메세지_전송() throws ExecutionException, InterruptedException {

    }


}
