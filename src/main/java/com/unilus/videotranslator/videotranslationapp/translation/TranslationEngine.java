/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
    private String apiUrl = "https://libretranslate.com/translate"; // Free, no API key needed

    public String translate(String text, String sourceLang, String targetLang) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String json = String.format(
                "{\"q\":\"%s\",\"source\":\"%s\",\"target\":\"%s\"}",
                text.replace("\"", "\\\""), sourceLang, targetLang
            );
            HttpPost post = new HttpPost(apiUrl);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(json));
            try (CloseableHttpResponse response = client.execute(post)) {
                String body = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(body);
                return root.get("translatedText").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "(Translation failed: " + e.getMessage() + ")";
        }
    }
}

