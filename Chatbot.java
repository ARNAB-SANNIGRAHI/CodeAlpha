import java.util.*;

public class Chatbot {
    // Intents
    private enum Intent {
        GREETING, FAREWELL, THANKS, HELP, NAME, SERVICES, UNKNOWN
    }

    // Example phrases for each intent
    private final Map<Intent, List<String>> intentExamples = new HashMap<>();
    // Responses for each intent
    private final Map<Intent, List<String>> intentResponses = new HashMap<>();
    private final Random random = new Random();

    public Chatbot() {
        // GREETING
        intentExamples.put(Intent.GREETING, Arrays.asList(
                "hello", "hi", "hey", "good morning", "good afternoon", "good evening"));
        intentResponses.put(Intent.GREETING, Arrays.asList(
                "Hello! How can I help you today?",
                "Hi there! How can I assist you?",
                "Hey! What can I do for you?"));
        // FAREWELL
        intentExamples.put(Intent.FAREWELL, Arrays.asList(
                "bye", "goodbye", "see you", "farewell"));
        intentResponses.put(Intent.FAREWELL, Arrays.asList(
                "Goodbye! Have a great day!",
                "See you soon!",
                "Farewell!"));
        // THANKS
        intentExamples.put(Intent.THANKS, Arrays.asList(
                "thanks", "thank you", "thx", "thankful"));
        intentResponses.put(Intent.THANKS, Arrays.asList(
                "You're welcome!",
                "No problem!",
                "Glad to help!"));
        // HELP
        intentExamples.put(Intent.HELP, Arrays.asList(
                "help", "assist", "support", "can you help", "need help"));
        intentResponses.put(Intent.HELP, Arrays.asList(
                "Sure, I can help! Ask me anything about our services.",
                "I'm here to assist you. What do you need help with?"));
        // NAME
        intentExamples.put(Intent.NAME, Arrays.asList(
                "your name", "who are you", "what is your name"));
        intentResponses.put(Intent.NAME, Arrays.asList(
                "I'm your AI Chatbot.",
                "You can call me Chatbot!"));
        // SERVICES
        intentExamples.put(Intent.SERVICES, Arrays.asList(
                "services", "what can you do", "features", "offerings"));
        intentResponses.put(Intent.SERVICES, Arrays.asList(
                "I can help you with some basic faq type and general questions."));
        // UNKNOWN
        intentResponses.put(Intent.UNKNOWN, Arrays.asList(
                "Sorry, I don't understand. Can you rephrase your question?",
                "I'm not sure I understand. Could you clarify?"));
    }

    // Simple bag-of-words intent classifier
    private Intent classifyIntent(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Intent.UNKNOWN;
        }
        String cleaned = input.trim().toLowerCase().replaceAll("[^a-z0-9 ]", "");
        int maxScore = 0;
        Intent bestIntent = Intent.UNKNOWN;
        for (Intent intent : intentExamples.keySet()) {
            int score = 0;
            for (String example : intentExamples.get(intent)) {
                if (cleaned.contains(example)) {
                    score += example.split(" ").length; // longer matches score higher
                }
            }
            if (score > maxScore) {
                maxScore = score;
                bestIntent = intent;
            }
        }
        return bestIntent;
    }

    public String getResponse(String input) {
        Intent intent = classifyIntent(input);
        List<String> responses = intentResponses.getOrDefault(intent, intentResponses.get(Intent.UNKNOWN));
        return responses.get(random.nextInt(responses.size()));
    }
} 