package com.example.p2pinternetsharing.connectivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.ArrayAdapter;

public class WifiBroadcastReceiver extends BroadcastReceiver implements PeerListListener{

	private WifiP2pManager p2pManager;
	private WifiManager wifiManager;
    private Channel channel;
    
    // Needed to make changes in the UI.
    private Activity activity;
    
    public static List<ScanResult> wifiList = new ArrayList<ScanResult>();
    public static List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    
    public static List<WifiP2pDevice> validAP = new ArrayList<WifiP2pDevice>();
    public static ArrayAdapter<WifiP2pDevice> adapter;
    //private WifiMainActivity activity;
	
    public WifiBroadcastReceiver(WifiP2pManager p2pManager,WifiManager wifiManager, Channel channel, Activity activity) {
        super();
        this.p2pManager = p2pManager;
        this.wifiManager = wifiManager;
        this.channel = channel;
        this.activity = activity;
    }
        
    @Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
        	Log.d("state changed", " state changed");
            
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	
        	Log.d("peers changed", " peers changed");
            // The peer list has changed!  We should probably do something about
            // that.
        	p2pManager.requestPeers(channel, this);

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        	
            if (p2pManager == null) {
                return;
            }

        	NetworkInfo networkInfo = (NetworkInfo) intent
                      .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        	
            // Connection state changed!  We should probably do something about
            // that.
        	
        	Log.d("connection changed", " connection changed");

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            
        	Log.d("device changed", " device changed");
        }else if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)){
        	getScanList();
        }

		
	}
    private void getScanList(){
    	wifiList = wifiManager.getScanResults();
        List<ScanResult> newList = new ArrayList<ScanResult>();
        StringBuilder sb = new StringBuilder();
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
        getValidAccessPoint();
    }

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peerList) {
		// TODO Auto-generated method stub
		peers.clear();
	    peers.addAll(peerList.getDeviceList());	
		Collection<WifiP2pDevice> list  = peerList.getDeviceList();
		
		
		Iterator<WifiP2pDevice> it = list.iterator();
		while(it.hasNext()){
			WifiP2pDevice device = it.next();
			
			String adr = device.deviceName;
			if(device.isGroupOwner())
				Log.d("peer",adr);
			else
				Log.d("peer",adr);
		}
		getValidAccessPoint();
	}
	
	void getValidAccessPoint(){

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
    		
    		// Notify the main thread.
    		activity.runOnUiThread(new Runnable() {
    		    public void run() {
    		        adapter.notifyDataSetChanged();
    		    }
    		});         }
		 
	}

}
