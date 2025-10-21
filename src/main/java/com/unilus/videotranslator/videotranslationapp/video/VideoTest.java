/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unilus.videotranslator.videotranslationapp.video;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VideoTest {
    public static void main(String[] args) {
        // Load the OpenCV native library
        nu.pattern.OpenCV.loadShared();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Video Test");
            JLabel videoLabel = new JLabel();
            frame.add(videoLabel);
            frame.setSize(680, 520);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            VideoCapture camera = new VideoCapture(0); // 0 means default webcam
            Mat mat = new Mat();

            Timer timer = new Timer(50, e -> {
                if (camera.isOpened()) {
                    camera.read(mat);
                    if (!mat.empty()) {
                        BufferedImage image = matToBufferedImage(mat);
                        videoLabel.setIcon(new ImageIcon(image));
                    }
                }
            });
            timer.start();

            Timer stopTimer = new Timer(30000, e -> {
                camera.release();
                frame.dispose();
            });
            stopTimer.setRepeats(false);
            stopTimer.start();
        });
    }

    // Converts OpenCV Mat to BufferedImage
    private static BufferedImage matToBufferedImage(Mat mat) {
        int width = mat.width(), height = mat.height(), channels = mat.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        mat.get(0, 0, sourcePixels);
        BufferedImage image;
        if (channels == 3) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        image.getRaster().setDataElements(0, 0, width, height, sourcePixels);
        return image;
    }
}
