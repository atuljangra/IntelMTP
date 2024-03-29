package com.example.p2pinternetsharing.connectivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;


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
		try {
			socket = new DatagramSocket(recievingPort);
			socket.setBroadcast(true);
			byte[] buff = new byte[256];
			DatagramPacket packet = new DatagramPacket(buff, buff.length);
			
			// We want to exit which this shared variable is changed.
			while(!NetworkController.pwrdReceiverStop){
					socket.receive(packet);
					// Extract shit.
					// Packet is as follows: ip_addr::true or ip_addr::false
					
					byte byteData[] = packet.getData();
					Log.d("data received ",new String(byteData,0,packet.getLength()));
					String data = new String(byteData, 0, packet.getLength());
					
					// Convert to Message.
					Message m = Message.createMessage(data);

					String parameters[] = m.getMsg().split(":");
					
					Log.d(parameters[0],parameters[1]);
					String SSID = "\"" + parameters[0] + "\"";
					String preSharedKey = "\"" + parameters[1] + "\"";
					if (m.getCode().equalsIgnoreCase(Message.APDETAILS)) {
						conf.SSID = SSID;
						conf.preSharedKey = preSharedKey;
						WifiManager wifiManager = (WifiManager)activityContext.getSystemService(Context.WIFI_SERVICE); 
						wifiManager.addNetwork(conf);
						wifiManager.saveConfiguration();
						
						// TODO Connect only if we are not already connected to this.
						
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
					if (m.getCode().equalsIgnoreCase(Message.SHADOWDETAILS)) {
						// just save the details. We will use that later.
						NetworkController.shadowConfig.SSID = SSID;
						NetworkController.shadowConfig.preSharedKey = preSharedKey;
						NetworkController.shadowConfigReceived = true;
					}

			}
			
			//Close the socket;
			socket.close();
			
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
	
	}

}
