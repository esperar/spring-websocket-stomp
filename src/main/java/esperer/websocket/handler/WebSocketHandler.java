package esperer.websocket.handler;

import esperer.websocket.Message;
import jdk.jshell.execution.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 웹소켓 연결
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        var sessionId = session.getId();
        sessions.put(sessionId, session); // 세션 저장

        Message message = Message.builder()
                .sender(sessionId)
                .receiver("all")
                .build();
        message.newConnector();

        sessions.values().forEach(s -> {
            try{
                if(!s.getId().equals(sessionId)){
                    s.sendMessage(new TextMessage(message.toString()));
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

    }

    // 양방향 데이터 통신
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    }

    // 소켓 연결 종료
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    // 소켓 통신
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }
}
