package com.example.p2pinternetsharing.connectivity;

import java.util.List;

import com.example.p2pinternetsharing.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
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

	private final IntentFilter intentFilter = new IntentFilter();

	private static WifiP2pManager mManager;
	
	private WifiManager wifiManager;
	
	private static Channel mChannel;
	
	private static WifiBroadcastReceiver receiver;
	
	private Context activityContext;
	
	
	public static AutonomousGroupManager autoGPManager;
	
	private Boolean join = false;
	
	private static Activity activity;
	
	public static Thread pwrdSender;
	public static Thread leaderElection;

	public static Thread leaderRcv;
	public static Thread pwrdReceiver;	

	public static boolean leaderRcvStop = false;
	public static boolean pwrdReceiverStop = false;
	// Just a placeholder.
	public static boolean amIShadowMaster = false;
	public static boolean shadowConfigReceived = false;
	public static String shadowMasterAddress;
	public static WifiConfiguration shadowConfig;

	//public Thread accessPointsLocator;
	
	//public WifiScanner wifiScanner;
	
	//public WifiDirectScanner wifiDirectScanner;
	
	
	public NetworkController(Context activityContext, Activity activity){
		
		this.activityContext = activityContext;
		NetworkController.activity = activity;
		// TODO Auto-generated method stub
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

	    // Indicates a change in the list of available peers.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);

	    // Indicates the state of Wi-Fi P2P connectivity has changed.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

	    // Indicates this device's details have changed.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    
	    intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
	    intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
	    
	    mManager = (WifiP2pManager) activityContext.getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(activityContext, activityContext.getMainLooper(), null);	
	    
		wifiManager = (WifiManager) activityContext.getSystemService(Context.WIFI_SERVICE);
		
		receiver = new WifiBroadcastReceiver(mManager, wifiManager, mChannel,activity);
	       	    
	}
	

	
	public void onResume() {
		if(join)
			return;
	
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
	    // TODO Make sure that nothing is happening when the group is being formed.
	    /*This is for the initialization . An autonomous group is formed and closed */
		(new Thread(autoGPManager)).start();
		
		// AP will never exit this function.
		while(true){
			wifiManager.startScan();
	        
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
		
		// Non - AP functions.
		setStatus("Starting password Receiver Thread");
		pwrdReceiver = new Thread(new passPhraseReceiver(activityContext));
		pwrdReceiver.start();
		
		// Start the leader receiving thread.
		setStatus("Starting Leader Received thread");
		leaderRcv = new Thread(new LeaderReceiver(activityContext, autoGPManager.savedgroup));
		leaderRcv.start();
		
	}

	public void joinAP(){
		
		List<WifiP2pDevice> validAP2pDevices = WifiBroadcastReceiver.validAP;
		if(validAP2pDevices == null || validAP2pDevices.size() == 0)
			return;
		 WifiP2pConfig config = new WifiP2pConfig();
         config.deviceAddress = validAP2pDevices.get(0).deviceAddress;
         config.wps.setup = WpsInfo.PBC;
		mManager.connect(mChannel, config, new ActionListener() {

			@Override
            public void onSuccess() {
				join = true;
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


	public void joinAP(final int position){
		
		final List<WifiP2pDevice> validAP2pDevices = WifiBroadcastReceiver.validAP;
		if(validAP2pDevices == null || validAP2pDevices.size() == 0)
			return;
		WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = validAP2pDevices.get(position).deviceAddress;
        config.wps.setup = WpsInfo.PBC;
		mManager.connect(mChannel, config, new ActionListener() {

			@Override
            public void onSuccess() {
				join = true;
            	Log.d(MainActivity.TAG, "joining " + validAP2pDevices.get(position).deviceName + "success");
            	setStatus("Joining " + validAP2pDevices.get(position).deviceName + " Success");
            }
			
            @Override
            public void onFailure(int reason) {
            	Log.d(MainActivity.TAG, "creation " + validAP2pDevices.get(position).deviceName + "failure");
            	setStatus("Joining " + validAP2pDevices.get(position).deviceName + " Failure Reason:" + reason);
             	if(reason == 2){
                	Log.d("error","busy");
             	}
            }

        });
			
	}

	public void disconnectAP(){
		mManager.removeGroup(mChannel,new ActionListener() {

			@Override
            public void onSuccess() {
            	Log.d("disconnect", "success");
            }
			
            @Override
            public void onFailure(int reason) {
            	Log.d("disconnect", "failiure");
            }

        });
	}
		
	// This will be called in case of shadow master.
	public static void startGp() {
		setStatus("starting AP");
		autoGPManager.startGp();
		
		setStatus("Starting password Sender thread");
		// Broadcast the ssid and password.
		String msg = autoGPManager.savedgroup.getNetworkName()+":"+autoGPManager.savedgroup.getPassphrase();
		String code = Message.APDETAILS;
		pwrdSender = new Thread(new APMessageSender(autoGPManager.savedgroup, new Message(code, msg)));
		pwrdSender.start();
		
		setStatus("Starting leader election thread");
		// Start the leader election thread that will be responsible for choosing a leader and broadcasting that.
		leaderElection = new Thread(new LeaderElection(autoGPManager.savedgroup));
		leaderElection.start();
		
	}
		
	public static void restart() {
		NetworkController.setStatus("Restarting as shadow");
		destroy();
		startGp();
	}

	private static void destroy() {
		setStatus("Destroying threads");
		// Stop all the receiver threads.
		NetworkController.leaderRcvStop = true; // Unused
		NetworkController.pwrdReceiverStop = true;
		
	}



	public void stopGp(){
		autoGPManager.stopGp();
	}

	public Boolean isGroupFormed(){		
		return true;
	}
	
	// returns empty string if group not formed yet 
	public String getPassphrase(){
		
			return "";
	}

	public static void setStatus(final String s) {
		NetworkController.activity.runOnUiThread( new Runnable() {
			
			@Override
			public void run() {
				MainActivity.status.setText(s);
				Log.d(MainActivity.TAG, "Setting text to: " + s);
			}
		});
	}
}


