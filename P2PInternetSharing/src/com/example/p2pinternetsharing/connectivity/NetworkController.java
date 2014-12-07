package com.example.p2pinternetsharing.connectivity;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.util.Log;


public class NetworkController implements Runnable{
	private static final String DEBUG_TAG = "MAIN_AADHA_DEBUGGING_AND_SHIT";

	private final IntentFilter intentFilter = new IntentFilter();

	private static WifiP2pManager mManager;
	
	private WifiManager wifiManager;
	
	private static Channel mChannel;
	
	private static WifiBroadcastReceiver receiver;
	
	private Context activityContext;
	
	
	public AutonomousGroupManager autoGPManager;
	
	//public Thread accessPointsLocator;
	
	//public WifiScanner wifiScanner;
	
	//public WifiDirectScanner wifiDirectScanner;
	
	
	public NetworkController(Context activityContext){
		
		this.activityContext = activityContext;
		// TODO Auto-generated method stub
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

	    // Indicates a change in the list of available peers.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

	    // Indicates the state of Wi-Fi P2P connectivity has changed.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

	    // Indicates this device's details have changed.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    
	    intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
	    
	    mManager = (WifiP2pManager) activityContext.getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(activityContext, activityContext.getMainLooper(), null);	
	    
		wifiManager = (WifiManager) activityContext.getSystemService(Context.WIFI_SERVICE);
	       	    
	}
	

	
	public void onResume() {
		
        receiver = new WifiBroadcastReceiver(mManager, wifiManager, mChannel);
        activityContext.registerReceiver(receiver, intentFilter);
        
	}
	
	public void onPause() {
        activityContext.unregisterReceiver(receiver);
	}



	@Override
	public void run() {

	    
	    autoGPManager = new AutonomousGroupManager(mManager,mChannel);
	    
	    /*This is for the initialization . An autonomous group is formed and closed */
		(new Thread(autoGPManager)).start();
		while(true){
			wifiManager.startScan();
	        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
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
		}
		
		//accessPointsLocator = new Thread(new LocateAccessPoint(mManager, mChannel, activityContext));
		//accessPointsLocator.start();
		
	}

	/*	
	public static void startGp() {
	
	}
		
	public void stopGp(){
		
	}
	
	
	public Boolean isGroupFormed(){		
		return true;
	}
	
	 returns empty string if group not formed yet 
	public String getPassphrase(){
		
			return "";
	}*/
	
}


