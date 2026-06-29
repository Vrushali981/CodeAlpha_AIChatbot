import java.util.*;
import java.util.regex.*;

public class AIChatbot {

    // ─── Intent ──────────────────────────────────────────────────────────────
    enum Intent {
        GREETING, FAREWELL, THANKS, WEATHER, TIME, HELP,
        PROGRAMMING, JAVA, MATH, JOKE, NAME, UNKNOWN
    }

    // ─── Rule ────────────────────────────────────────────────────────────────
    static class Rule {
        Pattern pattern;
        Intent  intent;
        String[] responses;

        Rule(String regex, Intent intent, String... responses) {
            this.pattern   = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            this.intent    = intent;
            this.responses = responses;
        }

        boolean matches(String input) {
            return pattern.matcher(input).find();
        }

        String randomResponse() {
            return responses[new Random().nextInt(responses.length)];
        }
    }

    // ─── NLP Engine ──────────────────────────────────────────────────────────
    static class NLPEngine {
        private final List<Rule> rules = new ArrayList<>();

        NLPEngine() {
            // Greeting
            rules.add(new Rule("\\b(hi|hello|hey|greetings|howdy|good morning|good evening)\\b",
                    Intent.GREETING,
                    "Hello! How can I help you today?",
                    "Hey there! What can I do for you?",
                    "Hi! Nice to meet you. Ask me anything!"));

            // Farewell
            rules.add(new Rule("\\b(bye|goodbye|see you|exit|quit|take care)\\b",
                    Intent.FAREWELL,
                    "Goodbye! Have a great day!",
                    "See you later! Take care!",
                    "Bye! It was nice chatting with you!"));

            // Thanks
            rules.add(new Rule("\\b(thanks|thank you|thx|ty|appreciate)\\b",
                    Intent.THANKS,
                    "You're welcome!",
                    "Happy to help!",
                    "Anytime! That's what I'm here for."));

            // Weather
            rules.add(new Rule("\\b(weather|temperature|rain|sunny|forecast|climate)\\b",
                    Intent.WEATHER,
                    "I don't have real-time weather data, but you can check weather.com!",
                    "I wish I could check the weather! Try a weather app for accurate info.",
                    "Weather varies by location. What city are you in?"));

            // Time / Date
            rules.add(new Rule("\\b(time|date|today|day|year|month)\\b",
                    Intent.TIME,
                    "Current date/time: " + new java.util.Date(),
                    "Time flies! It is currently: " + new java.util.Date()));

            // Help
            rules.add(new Rule("\\b(help|what can you do|capabilities|features)\\b",
                    Intent.HELP,
                    "I can answer questions about: programming, Java, math, jokes, weather, time, and more!",
                    "Try asking me about Java, math problems, jokes, or just say hello!"));

            // Programming general
            rules.add(new Rule("\\b(programming|coding|code|software|developer|algorithm)\\b",
                    Intent.PROGRAMMING,
                    "Programming is the art of telling computers what to do! Any specific language?",
                    "Great field! Are you interested in Java, Python, or another language?",
                    "Algorithms and data structures are the foundation of good programming."));

            // Java specific
            rules.add(new Rule("\\b(java|jvm|oop|class|object|inherit|polymorphism|encapsulation)\\b",
                    Intent.JAVA,
                    "Java is a platform-independent, object-oriented language. 'Write once, run anywhere!'",
                    "Java uses OOP principles: Encapsulation, Inheritance, Polymorphism, and Abstraction.",
                    "Java is great for enterprise apps, Android dev, and web backends!",
                    "Key Java concepts: classes, objects, interfaces, generics, and streams."));

            // Math
            rules.add(new Rule("\\b(math|calculate|formula|equation|algebra|geometry|calculus)\\b",
                    Intent.MATH,
                    "Math is the language of the universe! What would you like to calculate?",
                    "I love math! Ask me a simple arithmetic question and I'll compute it.",
                    "From algebra to calculus, math underpins all of computer science."));

            // Joke
            rules.add(new Rule("\\b(joke|funny|laugh|humor|hilarious)\\b",
                    Intent.JOKE,
                    "Why do programmers prefer dark mode? Because light attracts bugs! 🐛",
                    "Why did the Java developer wear glasses? Because he couldn't C#! 😄",
                    "There are 10 types of people: those who understand binary and those who don't.",
                    "A SQL query walks into a bar, approaches two tables and asks... 'Can I JOIN you?'",
                    "Why do programmers always mix up Christmas and Halloween? Because Oct 31 = Dec 25!"));

            // Name / Identity
            rules.add(new Rule("\\b(your name|who are you|what are you|are you a bot|are you human)\\b",
                    Intent.NAME,
                    "I'm JavaBot, your AI-powered assistant built with Java!",
                    "I'm an AI chatbot created in Java using rule-based NLP techniques.",
                    "You can call me JavaBot! I'm here to help you."));
        }

        Result process(String input) {
            String cleaned = input.trim().toLowerCase();

            // Simple arithmetic evaluation
            String mathResult = tryMath(cleaned);
            if (mathResult != null)
                return new Result(Intent.MATH, mathResult);

            for (Rule rule : rules) {
                if (rule.matches(cleaned))
                    return new Result(rule.intent, rule.randomResponse());
            }
            return new Result(Intent.UNKNOWN,
                    "I'm not sure I understand. Could you rephrase? Type 'help' to see what I can do.");
        }

        private String tryMath(String input) {
            // Match patterns like "what is 5 + 3" or "calculate 10 * 4"
            Pattern p = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(\\d+(?:\\.\\d+)?)");
            Matcher m = p.matcher(input);
            if (m.find()) {
                double a  = Double.parseDouble(m.group(1));
                String op = m.group(2);
                double b  = Double.parseDouble(m.group(3));
                double result;
                switch (op) {
                    case "+" -> result = a + b;
                    case "-" -> result = a - b;
                    case "*" -> result = a * b;
                    case "/" -> {
                        if (b == 0) return "Error: Cannot divide by zero!";
                        result = a / b;
                    }
                    default -> { return null; }
                }
                return String.format("%.0f %s %.0f = %.4g", a, op, b, result);
            }
            return null;
        }
    }

    record Result(Intent intent, String response) {}

    // ─── Chatbot ─────────────────────────────────────────────────────────────
    private final NLPEngine engine  = new NLPEngine();
    private final Scanner   scanner = new Scanner(System.in);
    private final List<String> history = new ArrayList<>();

    public void run() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       JAVA AI CHATBOT  v1.0          ║");
        System.out.println("║  Type 'help' for commands            ║");
        System.out.println("║  Type 'bye' to exit                  ║");
        System.out.println("╚══════════════════════════════════════╝");

        while (true) {
            System.out.print("\nYou: ");
            String input = scanner.nextLine();
            if (input == null || input.isBlank()) continue;

            history.add("You: " + input);
            Result result = engine.process(input);
            String response = result.response();

            System.out.println("Bot: " + response);
            history.add("Bot: " + response);

            if (result.intent() == Intent.FAREWELL) break;
        }
    }

    public static void main(String[] args) {
        new AIChatbot().run();
    }
}