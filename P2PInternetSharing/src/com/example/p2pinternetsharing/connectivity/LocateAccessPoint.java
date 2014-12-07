package com.example.p2pinternetsharing.connectivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

public class LocateAccessPoint implements Runnable {
	WifiP2pManager manager;
	Channel channel;
	Context activityContext;
	public WifiScanner wifiScanner;
	public static List<WifiP2pDevice> validAP = new ArrayList<WifiP2pDevice>();
	
	public LocateAccessPoint(WifiP2pManager manager, Channel channel,Context activityContext) {
		this.manager = manager;
		this.channel = channel;
		this.activityContext = activityContext;
		wifiScanner = new WifiScanner(manager,channel,activityContext);
		// TODO Auto-generated constructor stub
	}

	void getValidAccessPoint(){
		List<WifiP2pDevice> peers = WifiDirectScanner.peers;
		List<ScanResult> wifiList = WifiScanner.wifiList;
		validAP.clear();
		if(peers == null)
			return;
		if(wifiList == null) 
			return;
		
		
		 for(int i = 0; i < wifiList.size(); i++){
         	ScanResult r = wifiList.get(i);
         	String name = r.SSID;
         	int index = name.indexOf('-', 7);
         	
         	if(index == -1)
         		continue;
         	
         	name = name.substring(index+1);
         	
         	Iterator<WifiP2pDevice> it = peers.iterator();
    		while(it.hasNext()){
    			WifiP2pDevice device = it.next();
    			
    			String adr = device.deviceName;
    			if(adr.equals(name)){
    				Log.d("AP found ", name);
    				validAP.add(device);
    			}
    			
    		}
  
         }
		 
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			wifiScanner.register();
			wifiScanner.starScan();
	        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
	            @Override
	            public void onSuccess() {
	                Log.d("dicovery","successful");
	            }
	            @Override
	            public void onFailure(int reasonCode) {
	                Log.d("dicovery","failure");
	            }
	        });
	        
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getValidAccessPoint();
		}
		
	}
}
