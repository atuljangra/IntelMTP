package com.example.p2pinternetsharing.connectivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.net.wifi.p2p.WifiP2pGroup;

public class passPhraseSender implements Runnable {

	public  WifiP2pGroup savedgroup;
	
	
	public static int HEARTBLEED_TIME = 1000; // 1 seconds

	int recievingPort = 8080;
	private DatagramSocket socket;
	
	public passPhraseSender(WifiP2pGroup savedgroup) {
		this.savedgroup = savedgroup;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run() {
		String msg = savedgroup.getNetworkName()+":"+savedgroup.getPassphrase();
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);


			while(true){
				
				DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(),
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
