package com.example.danguen;

import com.example.danguen.config.webSocket.CustomSessionHandlerAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
public class ChatTest extends BaseTest {

    private StompSession stompSession1, stompSession2;
    private WebSocketStompClient client1, client2;
    @Autowired
    private CustomSessionHandlerAdapter customSessionHandlerAdapter;

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
    @AfterEach
    public void 세션_초기화(){
        customSessionHandlerAdapter.sessionClear();
    }

    @Test
    public void 세션_연결() throws ExecutionException, InterruptedException {
        //when
        stompSession1 = connectToWs(client1);

        //then
        assertThat(customSessionHandlerAdapter.getSessionsCount()).isEqualTo(1);
    }

    @Test
    public void 구독_및_메세지_전송() throws ExecutionException, InterruptedException {
        //1이 구독하고 2가 거기에 메세지를 보내보자
        stompSession1 = connectToWs(client1);
        stompSession2 = connectToWs(client2);

        //1이 구독
        stompSession1.subscribe("/topic/message", customSessionHandlerAdapter);
        Thread.sleep(1000);

        //2가 메세지 전송
        stompSession2.send("/appDes/topic", "공지입니다");
        Thread.sleep(1000);

        stompSession2.subscribe("/topic/message", customSessionHandlerAdapter);
        Thread.sleep(1000);
    }


    public StompSession connectToWs(WebSocketStompClient client) throws ExecutionException, InterruptedException {
        // 연결 및 핸들러에 알린다
        StompSession stompSession = client
                .connect(url + port + "/ws-endpoint", customSessionHandlerAdapter) // 연결 및 핸들러에 알린다
                .get();

        return stompSession;
    }
}
