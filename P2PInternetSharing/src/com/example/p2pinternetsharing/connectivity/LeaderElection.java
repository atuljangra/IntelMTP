package com.example.p2pinternetsharing.connectivity;

import java.util.Collection;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;

public class LeaderElection implements Runnable{
	private WifiP2pGroup p2pgroup;
	private WifiP2pDevice leader;
	private Thread leaderBcaster;
	
	public static int INTERVAL = 1000; // 1 seconds

	public LeaderElection(WifiP2pGroup group) {
		this.p2pgroup = group;
	}
	
	@Override
	public void run() {
		// Loop until someone is connected to this.
		while (true) {
			if (p2pgroup == null)
				continue;
			// TODO Client might disconnect over wifiDirect.
			Collection<WifiP2pDevice> connectedDevices = p2pgroup.getClientList();
			if (connectedDevices.isEmpty()) {
				// Sleep
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			} else {
				// Choose Leader.
				leader = connectedDevices.iterator().next(); // Weird Syntax
				break;
			}
		}
		
		// We have a leader. Bcast it.
		String code = Message.LEADERDETAILS;
		String msg = leader.deviceAddress;
		leaderBcaster = new Thread(new APMessageSender(p2pgroup, new Message(code, msg), LeaderReceiver.recievingPort));
		leaderBcaster.start();

	}

}
