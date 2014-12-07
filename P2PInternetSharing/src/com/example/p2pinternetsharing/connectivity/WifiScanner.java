package com.example.p2pinternetsharing.connectivity;

import java.util.ArrayList;
import java.util.List;

import android.R.string;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

public class WifiScanner {
	WifiManager mainWifi;
    WifiReceiver receiverWifi;
    public static List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    Context activityContext;
    
    public WifiScanner(WifiP2pManager manager, Channel channel,Context activityContext) {
        //mainText = (TextView) findViewById(R.id.mainText);
    	this.activityContext = activityContext;
        mainWifi = (WifiManager) activityContext.getSystemService(Context.WIFI_SERVICE);
       
        receiverWifi = new WifiReceiver();
        activityContext.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
     
        //manager.requestPeers(channel, new WifiDirectScanner());
        //activityContext.unregisterReceiver(receiver)
    	// TODO Auto-generated constructor stub
	}
    
    public void starScan(){
    	mainWifi.startScan();
    }
    public void register(){
    	 //receiverWifi = new WifiReceiver();
    	 activityContext.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
         
    }
    public void unregister(){
    	activityContext.unregisterReceiver(receiverWifi);
    }

    private class WifiReceiver extends BroadcastReceiver {
    	
   
    	@Override
        public void onReceive(Context c, Intent intent) {
            sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            List<ScanResult> newList = new ArrayList<ScanResult>();
            for(int i = 0; i < wifiList.size(); i++){
            	ScanResult r = wifiList.get(i);
            	String ssid = r.SSID;
            	if(ssid.contains("DIRECT-")){
            		newList.add(r);
            		sb.append(new Integer(i+1).toString() + ".");
                    sb.append((wifiList.get(i)).toString());
                    sb.append("\\n");
            	}
               
            }
            wifiList = newList;
            Log.d("Lists",sb.toString());
        }
    }

}
