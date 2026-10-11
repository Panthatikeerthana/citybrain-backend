package com.citybrain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final HttpClient httpClient;

    @Value("${gemini.api.key:}")
    private String configuredApiKey;

    @Value("${gemini.api.model:gemini-1.5-flash}")
    private String model;

    public GeminiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    /**
     * Resolves the Gemini API Key from application property or system environment.
     */
    public String getApiKey() {
        if (configuredApiKey != null && !configuredApiKey.isBlank()) {
            return configuredApiKey.trim();
        }
        String envKey = System.getenv("GEMINI_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            return envKey.trim();
        }
        return null;
    }

    /**
     * Generates an urban management intelligence response from Gemini using MySQL database context.
     */
    public String generateResponse(String userMessage, String dbContext) {
        String apiKey = getApiKey();

        // Construct structured prompt
        String fullPrompt = buildSystemPrompt(userMessage, dbContext);

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY is not configured. Returning database reasoning fallback response.");
            return buildFallbackResponse(userMessage, dbContext);
        }

        try {
            return callGeminiApi(apiKey, fullPrompt, dbContext);
        } catch (Exception e) {
            log.error("Failed to query Gemini API: {}", sanitizeErrorMessage(e.getMessage()));
            return "City Brain AI was unable to reach the Google Gemini service at this moment (" +
                    sanitizeErrorMessage(e.getMessage()) + ").\n\n" +
                    "--- Available City Brain Database Context ---\n" + dbContext;
        }
    }

    private String buildSystemPrompt(String userMessage, String dbContext) {
        return """
            SYSTEM INSTRUCTIONS:
            You are City Brain AI — an intelligent, authoritative urban management and prediction assistant for Greater Hyderabad.
            You assist municipal command center operators, city authorities, and citizens by providing actionable, evidence-based urban insights.

            CORE DOMAINS:
            - Traffic congestion and arterial flow
            - Meteorological conditions and rainfall impact
            - Water pipeline networks, pressure, and leakages
            - Solid waste bin monitoring and sanitation
            - Ambient air quality (AQI) and pollution mitigation
            - Emergency dispatches and public safety
            - Citizen grievances and infrastructure complaints
            - Predictive urban risk matrices

            DATABASE CONTEXT (Ground Truth):
            [CITY BRAIN DATA]
            %s

            OPERATIONAL CONSTRAINTS:
            1. Ground your factual statements strictly on the supplied CITY BRAIN DATA above.
            2. Clearly distinguish LIVE TELEMETRY (from live external Open-Meteo and TomTom APIs) from STORED DATABASE RECORDS (entered into MySQL).
            3. Do NOT invent, assume, or fabricate sensor measurements, traffic numbers, weather data, or incidents that are not provided in the context.
            4. If live traffic is unavailable or TRAFFIC_API_KEY is missing, state explicitly: "Live traffic unavailable — TomTom API key required" and never invent traffic numbers.
            5. If the user asks about records or domains with no data available, explicitly state that no records or data are currently available in the system. Never invent mock or sample data.
            6. Clearly distinguish known database facts from analytical observations and operational recommendations.
            7. Provide clear, practical, and structured recommendations (bullet points or numbered lists).
            8. Keep the response concise and readable (target 100-300 words).

            USER QUESTION:
            %s
            """.formatted(dbContext, userMessage);
    }

    private String callGeminiApi(String apiKey, String prompt, String dbContext) throws IOException, InterruptedException {
        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        String escapedPrompt = escapeJson(prompt);
        String jsonBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + escapedPrompt + "\"}]}],\"generationConfig\":{\"temperature\":0.4,\"maxOutputTokens\":800}}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(25))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            String errorMsg = extractErrorMessage(response.body(), response.statusCode());
            log.warn("Gemini API returned status {}: {}", response.statusCode(), errorMsg);
            return "City Brain AI: The Gemini service responded with status " + response.statusCode() +
                   " (" + errorMsg + ").\n\n" +
                   "--- Available City Brain Database Context ---\n" + dbContext;
        }

        String textResponse = extractJsonStringField(response.body(), "text");
        if (textResponse != null && !textResponse.isBlank()) {
            return textResponse.trim();
        }

        return "City Brain AI received an empty response from Gemini.\n\nDatabase Context:\n" + dbContext;
    }

    private String extractErrorMessage(String responseBody, int statusCode) {
        String msg = extractJsonStringField(responseBody, "message");
        if (msg != null && !msg.isBlank()) {
            return sanitizeErrorMessage(msg);
        }
        return "HTTP " + statusCode;
    }

    private String sanitizeErrorMessage(String msg) {
        if (msg == null) return "Unknown error";
        return msg.replaceAll("key=[A-Za-z0-9_-]+", "key=[REDACTED]")
                  .replaceAll("AIza[0-9A-Za-z-_]{35}", "[REDACTED]");
    }

    private String buildFallbackResponse(String userMessage, String dbContext) {
        return "City Brain AI — Intelligent Urban Assistant:\n\n" +
               "Notice: The Google Gemini API key (GEMINI_API_KEY) is currently not configured on the server. " +
               "Operating in local database analytical mode.\n\n" +
               "Query: \"" + userMessage + "\"\n\n" +
               "Relevant City Brain Database Telemetry:\n" +
               dbContext + "\n\n" +
               "Operational Recommendation:\n" +
               "To enable live generative responses with Google Gemini, set the GEMINI_API_KEY environment variable on the backend server.";
    }

    public static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < ' ') {
                        String hex = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(hex.substring(hex.length() - 4));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String extractJsonStringField(String json, String fieldName) {
        if (json == null) return null;
        String search = "\"" + fieldName + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;

        int colonIdx = json.indexOf(':', idx + search.length());
        if (colonIdx == -1) return null;

        int firstQuote = json.indexOf('"', colonIdx + 1);
        if (firstQuote == -1) return null;

        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        for (int i = firstQuote + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                switch (c) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'u' -> {
                        if (i + 4 < json.length()) {
                            String hex = json.substring(i + 1, i + 5);
                            try {
                                sb.append((char) Integer.parseInt(hex, 16));
                                i += 4;
                            } catch (NumberFormatException e) {
                                sb.append("\\u");
                            }
                        }
                    }
                    default -> sb.append(c);
                }
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                return sb.toString();
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
