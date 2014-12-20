package com.example.p2pinternetsharing.connectivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.net.wifi.p2p.WifiP2pGroup;

// Broadcast message sender.
public class APMessageSender implements Runnable {

	public  WifiP2pGroup savedgroup;
	
	
	public static int HEARTBLEED_TIME = 1000; // 1 seconds

	int recievingPort = 8080;
	private DatagramSocket socket;
	private String msgToSend;
	public APMessageSender(WifiP2pGroup savedgroup, Message msg, int port) {
		this.savedgroup = savedgroup;
		this.msgToSend = msg.getMessageToSend();
		this.recievingPort = port;
	}

	public APMessageSender(WifiP2pGroup savedgroup, Message msg) {
		this.savedgroup = savedgroup;
		this.msgToSend = msg.getMessageToSend();
		this.recievingPort = 8080;
	}
	

	@Override
	public void run() {
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);


			while(true){
				
				DatagramPacket packet = new DatagramPacket(this.msgToSend.getBytes(), this.msgToSend.length(),
					    InetAddress.getByName("192.168.49.255"), recievingPort);
				socket.send(packet);
				//activity.runOnUiThread(new uiUpdater(new String(packet.getData())));
					
				Thread.sleep(HEARTBLEED_TIME);
					
			}
		
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}

}
