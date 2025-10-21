/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unilus.videotranslator.videotranslationapp.video; // or .utils if you prefer

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

public class ImageUtils {
    // Convert BufferedImage to byte array (JPG encoding)
    public static byte[] bufferedImageToBytes(BufferedImage img) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        return baos.toByteArray();
    }

    // Convert byte array back to BufferedImage
    public static BufferedImage bytesToBufferedImage(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        return ImageIO.read(bais);
    }
}

