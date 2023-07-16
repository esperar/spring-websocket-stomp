package esperer.websocket.handler;

import esperer.websocket.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final ResourceBundle resourceBundle;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 웹소켓 연결
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        var sessionId = session.getId();
        sessions.put(sessionId, session); // 세션 저장

        Message message = Message.builder()
                .sender(sessionId)
                .channelId("all")
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
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        Message message = (Message) resourceBundle.getObject(textMessage.getPayload());
        message.setSender(session.getId());

        WebSocketSession receiver = sessions.get(message.getChannelId());

        if(receiver != null && receiver.isOpen()) {
            receiver.sendMessage(new TextMessage(message.toString()));
        }

    }

    // 소켓 연결 종료
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    // 소켓 통신
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        var sessionId = session.getId();

        sessions.remove(sessionId);

        final Message message = new Message();
        message.closeConnector();
        message.setSender(sessionId);

        sessions.values().forEach(s -> {
            try {
                s.sendMessage(new TextMessage(message.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
