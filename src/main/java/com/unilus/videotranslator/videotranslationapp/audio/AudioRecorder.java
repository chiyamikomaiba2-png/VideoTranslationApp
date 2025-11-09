package com.unilus.videotranslator.videotranslationapp.audio;

import javax.sound.sampled.*;
import java.io.*;

public class AudioRecorder {
    private TargetDataLine targetDataLine;
    private AudioInputStream audioInputStream;
    private File audioFile;
    private volatile boolean isRecording = false;
    private Thread recordingThread;
    
    public AudioRecorder(File outputFile) {
        this.audioFile = outputFile;
    }
    
    public void startRecording() {
        try {
            // Audio format configuration
            AudioFormat format = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                44100.0f,  // Sample rate
                16,        // Sample size in bits
                2,         // Channels (stereo)
                4,         // Frame size
                44100.0f,  // Frame rate
                false      // Big-endian
            );
            
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            
            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("❌ Audio line not supported!");
                return;
            }
            
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(format);
            targetDataLine.start();
            
            audioInputStream = new AudioInputStream(targetDataLine);
            isRecording = true;
            
            recordingThread = new Thread(() -> {
                try {
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            
            recordingThread.setDaemon(true);
            recordingThread.start();
            
            System.out.println("✅ Audio recording started: " + audioFile.getAbsolutePath());
            
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.err.println("❌ Audio line unavailable!");
        }
    }
    
    public void stopRecording() {
        isRecording = false;
        
        if (targetDataLine != null) {
            targetDataLine.stop();
            targetDataLine.close();
        }
        
        try {
            if (recordingThread != null) {
                recordingThread.join(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("✅ Audio recording stopped: " + audioFile.getAbsolutePath());
    }
    
    public boolean isRecording() {
        return isRecording;
    }
}
