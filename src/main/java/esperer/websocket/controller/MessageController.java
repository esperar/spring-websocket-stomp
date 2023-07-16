package esperer.websocket.controller;

import esperer.websocket.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations simpSimpMessageSendingOperations;

    /**
     *  /sub/channel/12345 - 구독(channelId = 12345)
     *  /pub/hello - 메시지 발행
     */
    @MessageMapping("/hello")
    public void message(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        simpSimpMessageSendingOperations.convertAndSend("/topic/" + message.getChannelId(), message);
    }

}
