/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unilus.videotranslator.videotranslationapp.audio;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.logging.Logger;
import org.apache.http.entity.ByteArrayEntity;

public class AssemblyAISpeechRecognizer {
    private static final Logger logger = Logger.getLogger(AssemblyAISpeechRecognizer.class.getName());
    private final String apiKey;

    public AssemblyAISpeechRecognizer(String apiKey) {
        this.apiKey = apiKey;
    }

    public String recognize(byte[] audioData) {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
        // 1. UPLOAD raw audio as bytes
        String uploadUrl = "https://api.assemblyai.com/v2/upload";
        HttpPost upload = new HttpPost(uploadUrl);
        upload.setHeader("Authorization", apiKey);
        upload.setHeader("Content-Type", "application/octet-stream");
        upload.setEntity(new ByteArrayEntity(audioData));
        try (CloseableHttpResponse uploadResponse = client.execute(upload)) {
            String uploadRes = EntityUtils.toString(uploadResponse.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode uploadJson = mapper.readTree(uploadRes);
            if (!uploadJson.has("upload_url")) {
                return "(Upload failed)";
            }
            String fileUrl = uploadJson.get("upload_url").asText();

            // 2. Transcribe using the returned audio URL
            String requestUrl = "https://api.assemblyai.com/v2/transcript";
            String json = "{ \"audio_url\": \"" + fileUrl + "\" }";
            HttpPost request = new HttpPost(requestUrl);
            request.setHeader("Authorization", apiKey);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(json));
            try (CloseableHttpResponse response = client.execute(request)) {
                String body = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper2 = new ObjectMapper();
                JsonNode node = mapper2.readTree(body);
                if (node.has("id")) {
                    String id = node.get("id").asText();
                    return pollTranscription(id);
                } else return "(Transcription request failed)";
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        return "(Error: " + e.getMessage() + ")";
    }
}



    private String pollTranscription(String transcriptId) {
        String url = "https://api.assemblyai.com/v2/transcript/" + transcriptId;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGetWrapper get = new HttpGetWrapper(url);
            get.setHeader("Authorization", apiKey);

            // Poll until transcript is ready
            for (int i = 0; i < 10; i++) {
                try (CloseableHttpResponse response = client.execute(get)) {
                    String res = EntityUtils.toString(response.getEntity());
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode json = mapper.readTree(res);
                    String status = json.has("status") ? json.get("status").asText() : "unknown";

                    if ("completed".equals(status)) {
                        String text = json.get("text").asText();
                        logger.info("Recognized text: " + text);
                        return text;
                    } else if ("error".equals(status)) {
                        logger.warning("Transcription error: " + res);
                        return "(Error processing audio)";
                    }
                    Thread.sleep(2000); // Wait 2 seconds before rechecking
                }
            }
            return "(Timed out awaiting result)";
        } catch (Exception e) {
            e.printStackTrace();
            return "(Polling error: " + e.getMessage() + ")";
        }
    }

    // Simple GET wrapper for AssemblyAI polls
    private static class HttpGetWrapper extends org.apache.http.client.methods.HttpGet {
        public HttpGetWrapper(String uri) { super(uri); }
    }
}
