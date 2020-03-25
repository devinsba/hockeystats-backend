package me.hockeystats;

import java.util.Map;
import lombok.Data;

@Data
public class PubSubMessage {
    Message message;
    String subscription;

    @Data
    public static class Message {
        Map<String, String> attributes;
        String data;
        String messageId;
    }
}
