package com.unilus.videotranslator.videotranslationapp.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class FFmpegMerger {
    
    private String ffmpegPath;
    
    public FFmpegMerger() {
        // Auto-detect ffmpeg in system PATH
        this.ffmpegPath = "ffmpeg";
    }
    
    public FFmpegMerger(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }
    
    public boolean mergeAudioVideo(String videoFile, String audioFile, String outputFile) {
        try {
            System.out.println("🎬 Merging audio and video...");
            System.out.println("Video: " + videoFile);
            System.out.println("Audio: " + audioFile);
            System.out.println("Output: " + outputFile);
            
            // FFmpeg command to merge without re-encoding (fast)
            String[] command = {
                ffmpegPath,
                "-i", videoFile,
                "-i", audioFile,
                "-c:v", "copy",
                "-c:a", "aac",
                "-b:a", "192k",
                "-map", "0:v:0",
                "-map", "1:a:0",
                "-shortest",
                "-y",
                outputFile
            };
            
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // Read FFmpeg output
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            
            // Wait for process to complete
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("✅ Audio and video merged successfully!");
                
                // Delete original files
                new File(videoFile).delete();
                new File(audioFile).delete();
                
                return true;
            } else {
                System.err.println("❌ FFmpeg failed with exit code: " + exitCode);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error merging audio and video:");
            e.printStackTrace();
            return false;
        }
    }
}
