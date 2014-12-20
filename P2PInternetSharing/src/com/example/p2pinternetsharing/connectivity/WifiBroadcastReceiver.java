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
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.ArrayAdapter;

public class WifiBroadcastReceiver extends BroadcastReceiver implements PeerListListener{

	private WifiP2pManager p2pManager;
	private WifiManager wifiManager;
    private Channel p2pChannel;
    
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
        this.p2pChannel = channel;
        this.activity = activity;
    }
        
    @Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
			Log.d("Intent","connection changed");
			WifiP2pInfo info = (WifiP2pInfo)intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
			Log.d("group formed",Boolean.toString(info.groupFormed));
			
			WifiP2pGroup gp = (WifiP2pGroup)intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
			
			NetworkInfo netinfo = (NetworkInfo)intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			Log.d("network connection ",Boolean.toString(netinfo.isConnected()));
			Log.d("network information",netinfo.toString());
			
		}else if(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)){
			Log.d("Intent","discovery changed");
			
			int discState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
			if(discState == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED){
				Log.d("discovery state","started");
			}else if(discState == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED){
				Log.d("discovery state","stoped");
				startDiscovery();
		        
			}
			
		}else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
			Log.d("Intent","peers changed");
			p2pManager.requestPeers(p2pChannel,this);
			getScanList();
			
		}else if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
			Log.d("Intent","state changed");
			int wifistate = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if(wifistate == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
				Log.d("p2p state","enabled");
				startDiscovery();
			}else if(wifistate == WifiP2pManager.WIFI_P2P_STATE_DISABLED){
				Log.d("p2pstate","disabled");
			}
					
			
		}else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
			Log.d("Intent","device changed");
		}else if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)){
        	getScanList();
        }
		else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			handleNetworkActionChanged(networkInfo);
			
		}
		else{
			Log.d("Intent","unknown");
		}
		
	}
    
	private void handleNetworkActionChanged(NetworkInfo networkInfo) {
		if (networkInfo.getState() != State.DISCONNECTED) {
			return;
		}

		// TODO See if all the cases are handled.
		/*
		// If I've received the shadowConfig
		if (NetworkController.shadowConfigReceived && !NetworkController.amIShadowMaster) {
			// Try connecting to the shadow master configuration.
			WifiManager wifiManager = (WifiManager)this.activity.getSystemService(Context.WIFI_SERVICE); 
			wifiManager.addNetwork(NetworkController.shadowConfig);
			wifiManager.saveConfiguration();
			List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
			for( WifiConfiguration i : list ) {
			    if(i.SSID != null && i.SSID.equals(NetworkController.shadowConfig.SSID )) {
			         wifiManager.disconnect();
			         wifiManager.enableNetwork(i.networkId, true);
			         wifiManager.reconnect();               
			         
			         break;
			    }           
			 }
		}
		*/
		// The other case will be handled in passPhraseReceiver.
		
		if (NetworkController.amIShadowMaster) {
			// Try starting the new AP.
			ShadowMaster.startShadowMaster();
		}
		
	}

	private void startDiscovery(){
		p2pManager.discoverPeers(p2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("discovery","success");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d("discovery","failure");
            }
        });
	}
	
    public void getScanList(){
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
