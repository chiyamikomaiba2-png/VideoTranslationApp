package com.unilus.videotranslator.videotranslationapp.translation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class TranslationEngine {
    private String apiUrl = "https://libretranslate.com/translate";
    
    public String translate(String text, String sourceLang, String targetLang) {
        if (text == null || text.trim().isEmpty()) {
            return "(Empty text)";
        }
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Escape quotes in text
            String escapedText = text.replace("\"", "\\\"");
            String json = String.format(
                "{\"q\":\"%s\",\"source\":\"%s\",\"target\":\"%s\"}",
                escapedText, sourceLang, targetLang
            );
            
            HttpPost post = new HttpPost(apiUrl);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(json));
            
            try (CloseableHttpResponse response = client.execute(post)) {
                String body = EntityUtils.toString(response.getEntity());
                
                // Debug: Print response
                System.out.println("API Response: " + body);
                
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(body);
                
                // Check for errors in response
                if (root.has("error")) {
                    String error = root.get("error").asText();
                    return "(Translation API Error: " + error + ")";
                }
                
                // Check if translatedText exists
                if (root.has("translatedText")) {
                    return root.get("translatedText").asText();
                } else {
                    return "(No translation returned)";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "(Translation failed: " + e.getMessage() + ")";
        }
    }
}
