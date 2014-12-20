package com.example.p2pinternetsharing.connectivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pGroup;
import android.util.Log;

// TODO Merge all the receiver classes.
// We receive the leader information and process it here.
public class LeaderReceiver implements Runnable {

	public static final int recievingPort = 8081;
	// Identifier for the shadow master.
	public String leaderAddress;
	
	@SuppressWarnings("unused")
	private Context context;
	private WifiP2pGroup group;
	public LeaderReceiver (Context con, WifiP2pGroup gp) {
		this.context = con;
		this.group = gp;
	}
	@Override
	public void run() {
		DatagramSocket socket;
		leaderAddress = null;
		try {
			socket = new DatagramSocket(recievingPort);
			socket.setBroadcast(true);
			byte[] buff = new byte[256];
			DatagramPacket packet = new DatagramPacket(buff, buff.length);
			
			while(true){
					socket.receive(packet);
					byte byteData[] = packet.getData();

					Log.d("data received ",new String(byteData,0,packet.getLength()));
					String data = new String(byteData, 0, packet.getLength());
					
					// Convert to Message.
					Message m = Message.createMessage(data);
					// We only process leader election.
					if (m.getCode() != Message.LEADERDETAILS) {
						continue;
					}					
					leaderAddress = m.getMsg();
					break;
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
			}		
	
			NetworkController.shadowMasterAddress = leaderAddress;
			// If I am the appointed leader, do stuff.
			if (leaderAddress.compareTo(getMACAddress()) == 0) {
				NetworkController.amIShadowMaster = true;	
				// Spawn a new thread.
				new Thread(new ShadowMaster(this.group, context)).start();
			
			} else {
				NetworkController.amIShadowMaster = false;
			}
			
	}


public static String getMACAddress() {
    try {
        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface intf : interfaces) {
        	if (!intf.getName().contains("p2p")) {
        		continue;
        	}

        	byte[] mac = intf.getHardwareAddress();
            if (mac==null) return "";
            StringBuilder buf = new StringBuilder();
            for (int idx=0; idx<mac.length; idx++)
                buf.append(String.format("%02X:", mac[idx]));       
            if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
            return buf.toString();
        }
    } catch (Exception ex) { } // for now eat exceptions
    return "";
}


}