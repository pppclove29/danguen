package com.example.danguen.handler;

import java.lang.reflect.Type;
import java.util.HashMap;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomStompHandler implements StompSessionHandler {

	// todo 위치변경
	private StompSession session;
	private HashMap<String, String> subList = new HashMap<>();

	public StompSession getStompSession() {
		return this.session;
	}

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		this.session = session;
		System.out.println(session + "님이 연결되엇습니다.");
	}

	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		System.out.println("handleException 작동");
	}

	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		System.out.println("handleTransportError 발생");
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return null;
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {

	}
}
