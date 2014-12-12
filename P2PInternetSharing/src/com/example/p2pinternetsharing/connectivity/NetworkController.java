package com.example.p2pinternetsharing.connectivity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;


public class NetworkController implements Runnable{
	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "MAIN_AADHA_DEBUGGING_AND_SHIT";

	private final IntentFilter intentFilter = new IntentFilter();

	private static WifiP2pManager mManager;
	
	private WifiManager wifiManager;
	
	private static Channel mChannel;
	
	private static WifiBroadcastReceiver receiver;
	
	private Context activityContext;
	
	
	public AutonomousGroupManager autoGPManager;
	
	private Boolean join = false;
	
	private Activity activity;
	
	//public Thread accessPointsLocator;
	
	//public WifiScanner wifiScanner;
	
	//public WifiDirectScanner wifiDirectScanner;
	
	
	public NetworkController(Context activityContext, Activity activity){
		
		this.activityContext = activityContext;
		this.activity = activity;
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
		if(join)
			return;
		
        receiver = new WifiBroadcastReceiver(mManager, wifiManager, mChannel, activity);
        activityContext.registerReceiver(receiver, intentFilter);
        
	}
	
	public void onPause() {
		if(join)
			return;
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
			if(join){
				activityContext.unregisterReceiver(receiver);
				break;
			}
		}
		
		//accessPointsLocator = new Thread(new LocateAccessPoint(mManager, mChannel, activityContext));
		//accessPointsLocator.start();
		
	}

	public void joinAP(){
		
		List<WifiP2pDevice> validAP2pDevices = WifiBroadcastReceiver.validAP;
		if(validAP2pDevices == null || validAP2pDevices.size() == 0)
			return;
		join = true;
		 WifiP2pConfig config = new WifiP2pConfig();
         config.deviceAddress = validAP2pDevices.get(0).deviceAddress;
         config.wps.setup = WpsInfo.PBC;
		mManager.connect(mChannel, config, new ActionListener() {

			@Override
            public void onSuccess() {
            	Log.d("joining", "success");
            }
			
            @Override
            public void onFailure(int reason) {
            	Log.d("creation", "failiure");
             	if(reason == 2){
                	Log.d("error","busy");
             	}
            }

        });
			
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


