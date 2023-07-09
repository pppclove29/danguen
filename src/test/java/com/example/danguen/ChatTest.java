//package com.example.danguen;
//
//import com.example.danguen.config.oauth.PrincipalUserDetails;
//import com.example.danguen.domain.user.entity.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.filter.CharacterEncodingFilter;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import org.springframework.web.socket.sockjs.client.SockJsClient;
//import org.springframework.web.socket.sockjs.client.WebSocketTransport;
//
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//
//@AutoConfigureMockMvc
//public class ChatTest extends BaseTest {
//
//    private WebSocketStompClient target;
//    // 참조 https://docs.spring.io/spring-framework/reference/web/websocket/stomp/client.html
//    @BeforeEach
//    public void 웹소켓_생성() {
//        User user = makeUserProc("targetName", "target@mail.com");
//
//        PrincipalUserDetails userDetails = new PrincipalUserDetails(user);
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()));
//
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(ctx)
//                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
//                .alwaysDo(print())
//                .build();
//
//        //SockJsClient 는 WebSocketClient 를 구현했다!
//        //SockJsClient 는 List<Transport> transports 를 받아야한다 이게 뭐냐?
//        //Transport를 구현한 객체중 WebSocketTransport를 발견했다 이걸로 써보자
//        //근데 WebSocketTransport는 WebSocketClient가 필요하다 뭔 ㅋㅋㅋ
//        //StandardWebSocketClient 또한 WebSocketClient 를 구현했다
//        //이는 새로운 WebSocketContainer를 반환한다
//        //솔직히 이렇게 정리해도 잘 모르겠다 여튼 이렇게 새로운 클라이언트를 생성했다. 한거맞나?\
//        target = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
//
//        // default = SimpleMessageConverter
//        target.setMessageConverter(new MappingJackson2MessageConverter());
//    }
//    @Test
//    public void 채팅_시도() throws Exception {
//        //given
//        Long targetUID = userRepository.findByEmail("target@mail.com").get().getId();
//        System.out.println(targetUID);
//
//        //when
//        mockMvc.perform(get("/message/new")
//                .param("targetUID",targetUID.toString()));
//
//        //then
//    }
//}
