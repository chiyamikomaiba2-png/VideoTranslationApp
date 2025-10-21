/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unilus.videotranslator.videotranslationapp.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPReceiver {
    private DatagramSocket socket;
    private int port;

    public UDPReceiver(int port) throws Exception {
        this.port = port;
        socket = new DatagramSocket(port);
    }

    public byte[] receive(int bufferSize) throws Exception {
        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return packet.getData();
    }

    public void close() {
        socket.close();
    }
}

