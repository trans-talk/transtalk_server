package com.wootech.transtalk.config.jwt;

import com.wootech.transtalk.config.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandShakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            // 쿼리 스트링에서 token 파라미터 추출
            String query = servletRequest.getServletRequest().getQueryString();
            String token = null;

            if (query != null) {
                // 쿼리 스트링 여러 파라미터 중 token 파라미터 찾기
                for (String param : query.split("&")) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                        token = keyValue[1];
                        break;
                    }
                }
            }

            if (token == null || !jwtUtil.validateToken(token)) {
                return false;
            }

            String userEmail = jwtUtil.getEmail(token);
            attributes.put("userEmail", userEmail); // WebSocketSession에 저장
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
