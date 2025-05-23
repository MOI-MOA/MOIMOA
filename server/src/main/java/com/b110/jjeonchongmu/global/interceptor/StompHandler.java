//package com.b110.jjeonchongmu.global.interceptor;
//
//import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
//import java.util.Objects;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class StompHandler implements ChannelInterceptor {
//	private final JwtTokenProvider jwtTokenProvider;
//
//	@Override
//	public Message<?> preSend(Message<?> message, MessageChannel channel) {
//		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//			jwtTokenProvider.validateToken(
//					Objects.requireNonNull(accessor.getFirstNativeHeader("Authorization")).substring(7));
//		}
//		return message;
//	}
//}