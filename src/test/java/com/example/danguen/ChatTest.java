package com.example.danguen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
public class ChatTest extends BaseTest {

    private StompSession stompSession1, stompSession2;
    private WebSocketStompClient client1, client2;

    @LocalServerPort
    private int port;

    private final String url = "ws://localhost:";
    private final String connectUrl = "/appDes/connect";
    private final String privateMsgUrl = "/appDes/private-message";
    private String message;
    private Map<String, StompSession> sessions;

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

        sessions = new HashMap<>();
    }

    @Test
    public void 세션_연결() throws ExecutionException, InterruptedException {
        //when
        stompSession1 = connectToWs(client1);

        //then
        assertThat(sessions.size()).isEqualTo(1);
    }

    @Test
    public void 구독_및_메세지_전송() throws ExecutionException, InterruptedException {
        //1이 구독하고 2가 거기에 메세지를 보내보자
        stompSession1 = connectToWs(client1);
        stompSession2 = connectToWs(client2);

        //1이 구독
        stompSession1.subscribe("/user/" + stompSession1.getSessionId() + "/queue/private", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                // 메세지 타입 반환
                return null;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // 메세지 수신 시 호출
                System.out.println("메세지 받았다~");
            }
        });

        stompSession2.send(privateMsgUrl + "/" + stompSession1.getSessionId(), "사랑의 메세지");

        Thread.sleep(1000);
    }


    public StompSession connectToWs(WebSocketStompClient client) throws ExecutionException, InterruptedException {
        // 연결 및 핸들러에 알린다
        StompSession stompSession = client
                .connect(url + port + "/ws-endpoint", new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        sessions.put(session.getSessionId(), session);

                        message = session.getSessionId() + "님이 연결되었습니다";
                        session.send(connectUrl, message);
                    }
                }) // 연결 및 핸들러에 알린다
                .get();

        return stompSession;
    }
}
