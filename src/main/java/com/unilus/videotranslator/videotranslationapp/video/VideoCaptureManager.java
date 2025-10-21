/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unilus.videotranslator.videotranslationapp.video;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import java.util.logging.Logger;

public class VideoCaptureManager {
    private static final Logger logger = Logger.getLogger(VideoCaptureManager.class.getName());
    private VideoCapture camera;
    private Mat frame;
    private boolean isCapturing;

    public VideoCaptureManager() {
        nu.pattern.OpenCV.loadShared();
        this.camera = new VideoCapture();
        this.frame = new Mat();
        this.isCapturing = false;
        logger.info("VideoCaptureManager initialized");
    }

    public boolean startCapture(int deviceIndex) {
        try {
            if (camera.open(deviceIndex)) {
                camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
                camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
                camera.set(Videoio.CAP_PROP_FPS, 20);
                isCapturing = true;
                logger.info("Camera started successfully");
                return true;
            }
        } catch (Exception e) {
            logger.severe("Failed to start camera: " + e.getMessage());
        }
        return false;
    }

    public Mat captureFrame() {
        if (isCapturing && camera.read(frame)) {
            return frame.clone();
        }
        return null;
    }

    public void stopCapture() {
        isCapturing = false;
        if (camera.isOpened()) {
            camera.release();
            logger.info("Camera stopped");
        }
    }

    public boolean isCapturing() {
        return isCapturing;
    }
}
