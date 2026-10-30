package ma.adria.bank.chatbot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class ChatController {
    private final ChatClient chat;

    public ChatController(ChatClient chat) { this.chat = chat; }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> body) {
        String answer = chat.prompt().user(body.get("message")).call().content();
        return Map.of("answer", answer);
    }
}