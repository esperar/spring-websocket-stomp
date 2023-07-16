package esperer.websocket.message;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {

    private String type;
    private String sender;
    private String channelId;
    private Object data;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void newConnector() {
        this.type = "new";
    }

    public void closeConnector() {
        this.type = "close";
    }
}
