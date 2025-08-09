package com.pfplaybackend.api.liveconnect.websocket.interceptor;

import com.pfplaybackend.api.common.config.security.jwt.JwtCookieValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);
    private final JwtCookieValidator jwtCookieValidator;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletServerRequest) {
            HttpServletRequest servletRequest = servletServerRequest.getServletRequest();

            String uid = jwtCookieValidator.extractUserId(servletRequest).orElseThrow(
                    () -> new AuthenticationServiceException("Invalid Access Token"));
            attributes.put("uid", uid);
            // TODO return '예외 발생'이 return false;를 대체할 수 있는가?
        }
        return true;  // JWT 토큰이 유효하지 않은 경우, 연결 거부(=false)
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 필요 시, 핸드셰이크 이후에 수행할 작업
        logger.info("After Handshake");
    }
}
