package com.example.p2pinternetsharing.connectivity;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pGroup;

/*
 *  Responsibilities of shadow master.
 *  Start bdcasting wifi Config in the network in one thread.
 *  Whenever the connection is lost to the currentAP, wait for a timeout and then start it's own AP.
 */

public class ShadowMaster implements Runnable{

	private Thread bcaster;
	private WifiP2pGroup savedGroup;
	@SuppressWarnings("unused")
	private Context context;
	
	public ShadowMaster(WifiP2pGroup gp, Context context) {
		this.savedGroup = gp;
		this.context = context;
	}
	@Override
	public void run() {
		// Broadcast the ssid and password.
		String msg = savedGroup.getNetworkName()+":"+savedGroup.getPassphrase();
		String code = Message.SHADOWDETAILS;
		bcaster = new Thread(new APMessageSender(savedGroup, new Message(code, msg)));
		bcaster.start();
	}
	
	public static void startShadowMaster() {
		// Restart
		NetworkController.restart();
	}
}
