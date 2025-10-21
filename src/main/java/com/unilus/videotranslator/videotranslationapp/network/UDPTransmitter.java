/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unilus.videotranslator.videotranslationapp.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPTransmitter {
    private DatagramSocket socket;
    private InetAddress receiverAddress;
    private int receiverPort;

    public UDPTransmitter(String ip, int port) throws Exception {
        socket = new DatagramSocket();
        receiverAddress = InetAddress.getByName(ip);
        receiverPort = port;
    }

    public void send(byte[] data) throws Exception {
        DatagramPacket packet = new DatagramPacket(data, data.length, receiverAddress, receiverPort);
        socket.send(packet);
    }

    public void close() {
        socket.close();
    }
}
