/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unilus.videotranslator.videotranslationapp.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

public class AudioCaptureManager {
    private static final Logger LOGGER = Logger.getLogger(AudioCaptureManager.class.getName());
    private AudioFormat format;
    private TargetDataLine line;
    private boolean capturing;
    private int captureDurationMs = 3000; // Record in 3-second chunks

    public AudioCaptureManager() {
        format = new AudioFormat(16000.0f, 16, 1, true, false);
    }

    public void start(AudioCaptureCallback callback) {
        capturing = true;
        new Thread(() -> {
            try {
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                byte[] data = new byte[4096];
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                long startTime = System.currentTimeMillis();

                LOGGER.info("🎤 Audio capture started: 16kHz mono, 3-second chunk size");

                while (capturing) {
                    int bytesRead = line.read(data, 0, data.length);
                    if (bytesRead > 0) buffer.write(data, 0, bytesRead);

                    // Every 3 seconds, send a chunk for recognition
                    if (System.currentTimeMillis() - startTime >= captureDurationMs) {
                        byte[] audioChunk = buffer.toByteArray();
                        if (audioChunk.length > 0) {
                            LOGGER.info("🟢 Sending chunk to recognition (" + audioChunk.length + " bytes)");
                            callback.onAudioData(audioChunk);
                        }
                        buffer.reset();
                        startTime = System.currentTimeMillis();
                    }
                }

                line.stop();
                line.close();
                LOGGER.info("🛑 Audio capture stopped.");

            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.severe("Audio error: " + e.getMessage());
            }
        }).start();
    }

    public void stop() {
        capturing = false;
        if (line != null) line.close();
    }

    public interface AudioCaptureCallback {
        void onAudioData(byte[] audio);
    }
}
