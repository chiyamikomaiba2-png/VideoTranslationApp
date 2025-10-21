/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unilus.videotranslator.videotranslationapp.audio;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import java.util.logging.Logger;

public class OfflineSpeechRecognizer {
    private static final Logger logger = Logger.getLogger(OfflineSpeechRecognizer.class.getName());
    private LiveSpeechRecognizer recognizer;
    private boolean listening = false;
    private boolean recognizerStarted = false;
    private Thread recognitionThread;

    public interface SpeechCallback {
        void onSpeechRecognized(String text);
        void onError(String error);
    }

    public void startListening(SpeechCallback callback) {
        try {
            logger.info("Initializing offline speech recognition...");
            
            Configuration config = new Configuration();
            config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
            config.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
            config.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

            recognizer = new LiveSpeechRecognizer(config);
            recognizer.startRecognition(true);
            recognizerStarted = true;
            listening = true;

            logger.info("🎤 Offline speech recognition started");

            recognitionThread = new Thread(() -> {
                while (listening) {
                    SpeechResult result = recognizer.getResult();
                    if (result != null) {
                        String text = result.getHypothesis();
                        if (text != null && !text.trim().isEmpty()) {
                            logger.info("Recognized: " + text);
                            callback.onSpeechRecognized(text);
                        }
                    }
                }
            });
            recognitionThread.start();

        } catch (Exception e) {
            logger.severe("Sphinx initialization error: " + e.getMessage());
            e.printStackTrace();
            callback.onError("Failed to start offline recognition: " + e.getMessage());
        }
    }

    public void stopListening() {
        listening = false;
        
        // Wait for recognition loop to finish
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        // Stop the recognition thread
        if (recognitionThread != null && recognitionThread.isAlive()) {
            recognitionThread.interrupt();
            try {
                recognitionThread.join(1000);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        
        // Stop the recognizer safely
        if (recognizer != null && recognizerStarted) {
            try {
                recognizer.stopRecognition();
                recognizerStarted = false;
            } catch (IllegalStateException e) {
                logger.info("Recognizer already stopped");
            }
        }
        
        logger.info("🛑 Offline recognition stopped");
    }
}
