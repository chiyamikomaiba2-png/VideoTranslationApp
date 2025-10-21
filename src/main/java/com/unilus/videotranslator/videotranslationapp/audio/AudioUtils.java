/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unilus.videotranslator.videotranslationapp.audio;

import javax.sound.sampled.*;
import java.io.*;

public class AudioUtils {
    public static byte[] pcmToWav(byte[] pcmData, AudioFormat format) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(pcmData);
        AudioInputStream ais = new AudioInputStream(bais, format, pcmData.length / format.getFrameSize());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, baos);
        return baos.toByteArray();
    }
}
