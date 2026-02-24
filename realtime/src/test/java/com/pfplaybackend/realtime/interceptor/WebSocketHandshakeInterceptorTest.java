package com.pfplaybackend.realtime.interceptor;

import com.pfplaybackend.realtime.port.WebSocketAuthPort;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebSocketHandshakeInterceptor 단위 테스트")
class WebSocketHandshakeInterceptorTest {

    @Mock
    private WebSocketAuthPort webSocketAuthPort;

    @Mock
    private WebSocketHandler wsHandler;

    @Mock
    private ServerHttpResponse response;

    @InjectMocks
    private WebSocketHandshakeInterceptor interceptor;

    @Test
    @DisplayName("유효한 토큰이면 userId를 attributes에 저장한다")
    void beforeHandshakeValidTokenExtractsUserId() throws Exception {
        // given
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        ServletServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        Map<String, Object> attributes = new HashMap<>();

        when(webSocketAuthPort.extractUserId(servletRequest)).thenReturn(Optional.of("user-123"));

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isTrue();
        assertThat(attributes).containsEntry("uid", "user-123");
    }

    @Test
    @DisplayName("유효하지 않은 토큰이면 AuthenticationServiceException을 던진다")
    void beforeHandshakeInvalidTokenThrowsAuthenticationServiceException() {
        // given
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        ServletServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        Map<String, Object> attributes = new HashMap<>();

        when(webSocketAuthPort.extractUserId(servletRequest)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> interceptor.beforeHandshake(request, response, wsHandler, attributes))
                .isInstanceOf(AuthenticationServiceException.class)
                .hasMessage("Invalid Access Token");
    }

    @Test
    @DisplayName("비-Servlet 요청이면 추출 없이 true를 반환한다")
    void beforeHandshakeNonServletRequestReturnsTrueWithoutExtraction() throws Exception {
        // given
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        Map<String, Object> attributes = new HashMap<>();

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isTrue();
        assertThat(attributes).isEmpty();
        verifyNoInteractions(webSocketAuthPort);
    }

    @Test
    @DisplayName("afterHandshake가 예외 없이 실행된다")
    void afterHandshakeLogsWithoutError() {
        // given
        ServerHttpRequest request = mock(ServerHttpRequest.class);

        // when & then — 예외 없이 실행
        interceptor.afterHandshake(request, response, wsHandler, null);
    }
}
