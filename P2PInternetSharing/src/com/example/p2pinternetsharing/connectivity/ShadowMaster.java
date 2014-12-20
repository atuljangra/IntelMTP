package com.example.p2pinternetsharing.connectivity;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;

/*
 *  Responsibilities of shadow master.
 *  Start bdcasting wifi Config in the network in one thread.
 *  Whenever the connection is lost to the currentAP, wait for a timeout and then start it's own AP.
 */

public class ShadowMaster implements Runnable{

	private Thread bcaster;
	private static Thread shadowDaemon;
	private WifiP2pGroup savedGroup;
	private Context context;
	
	public ShadowMaster(WifiP2pGroup gp, Context context) {
		this.savedGroup = gp;
		this.context = context;
	}
	@Override
	public void run() {
		// Broadcast the ssid and password.
		String msg = savedGroup.getNetworkName()+":"+savedGroup.getPassphrase();
		String code = Message.APPASSPHRASE;
		bcaster = new Thread(new APMessageSender(savedGroup, new Message(code, msg)));
		bcaster.start();

		shadowDaemon = new Thread(new ShadowDaemon(context));

	}
	
	public static void startShadowMaster() {
		// Start the thread that would keep on listening on the connection.
		shadowDaemon.run();
	}
}

class ShadowDaemon implements Runnable {
	private WifiP2pManager p2pManager;
	private WifiManager wifiManager;
	private Context context;
	
	public ShadowDaemon(Context context) {
		this.context = context;
		
	}
	
	@Override
	public void run() {
		// Start the new group.
	}
	
}