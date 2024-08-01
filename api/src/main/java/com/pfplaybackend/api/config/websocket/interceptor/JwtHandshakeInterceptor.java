package com.pfplaybackend.api.config.websocket.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.pfplaybackend.api.config.jwt.JwtValidator;
import com.pfplaybackend.api.config.jwt.enums.TokenClaim;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.WebUtils;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);
    private final JwtValidator jwtValidator;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletServerRequest) {
            HttpServletRequest servletRequest = servletServerRequest.getServletRequest();
            final String accessToken = jwtValidator.extractAccessTokenFromCookie(servletRequest).orElseThrow(
                    () -> new AuthenticationServiceException("Token does not exist"));
            if(jwtValidator.isTokenValid(accessToken)) {
                DecodedJWT decodedJWT = jwtValidator.getDecodedJWT(accessToken);
                String extractedUid = decodedJWT.getClaim(TokenClaim.UID.getValue()).asString();
                System.out.println(extractedUid);
                attributes.put("uid", extractedUid);
            }else {
                return false;
            }
        }
        return true;  // JWT 토큰이 유효하지 않은 경우, 연결 거부(false)
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 필요 시, 핸드셰이크 이후에 수행할 작업
        logger.info("After Handshake");
    }
}
