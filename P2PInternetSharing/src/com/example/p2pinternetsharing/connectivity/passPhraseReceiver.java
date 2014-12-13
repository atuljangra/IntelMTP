package com.example.p2pinternetsharing.connectivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.util.Pair;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class passPhraseReceiver implements Runnable {
	int recievingPort = 8080;
	Context activityContext;
	public passPhraseReceiver(Context context) {
		activityContext = context;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run(){
		DatagramSocket socket;
		WifiConfiguration conf = new WifiConfiguration();
		// TODO Auto-generated method stub
		String networkSSID;
		try {
			socket = new DatagramSocket(recievingPort);
			socket.setBroadcast(true);
			byte[] buff = new byte[256];
			DatagramPacket packet = new DatagramPacket(buff, buff.length);
			
			while(true){
					socket.receive(packet);
					// Extract shit.
					// Packet is as follows: ip_addr::true or ip_addr::false
					
					byte byteData[] = packet.getData();
					Log.d("data received ",new String(byteData,0,packet.getLength()));
					String data = new String(byteData, 0, packet.getLength());
					String parameters[] = data.split(":");
					
					Log.d(parameters[0],parameters[1]);
					conf.SSID = "\"" + parameters[0] + "\"";
					conf.preSharedKey = "\"" + parameters[1] + "\"";
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
		WifiManager wifiManager = (WifiManager)activityContext.getSystemService(Context.WIFI_SERVICE); 
		wifiManager.addNetwork(conf);
		wifiManager.saveConfiguration();
		List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
		for( WifiConfiguration i : list ) {
		    if(i.SSID != null && i.SSID.equals(conf.SSID )) {
		         wifiManager.disconnect();
		         wifiManager.enableNetwork(i.networkId, true);
		         wifiManager.reconnect();               
		         
		         break;
		    }           
		 }
	
	

	}

}
