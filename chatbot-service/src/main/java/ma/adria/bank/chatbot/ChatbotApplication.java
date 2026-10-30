package ma.adria.bank.chatbot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChatbotApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatbotApplication.class, args);
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider mcpTools) {
        return builder
                .defaultSystem("Tu es un assistant bancaire. Utilise les outils pour consulter les comptes, "
                        + "enregistrer des transactions et effectuer des virements. "
                        + "N'invente jamais d'identifiant : récupère-le avec les outils. Réponds en français.")
                .defaultToolCallbacks(mcpTools)
                .build();
    }
}