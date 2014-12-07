package com.example.p2pinternetsharing.connectivity;

import java.util.Collection;
import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class WifiBroadcastReceiver extends BroadcastReceiver{

	private WifiP2pManager manager;
    private Channel channel;
    //private WifiMainActivity activity;
	
    public WifiBroadcastReceiver(WifiP2pManager manager, Channel channel) {
        super();
        this.manager = manager;
        this.channel = channel;
        
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
        	manager.requestPeers(channel, new WifiDirectScanner());
        	
    /*    	manager.requestPeers(channel, new PeerListListener() {
				
				@Override
				public void onPeersAvailable(WifiP2pDeviceList peers) {
					
					Collection<WifiP2pDevice> list  = peers.getDeviceList();
					Iterator<WifiP2pDevice> it = list.iterator();
					while(it.hasNext()){
						WifiP2pDevice device = it.next();
						
						String adr = device.deviceName;
						if(device.isGroupOwner())
							Log.d("peer is gp O",adr);
						else
							Log.d("peer is not O",adr);
					}
					// TODO Auto-generated method stub
					
				}
			});*/

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        	
            if (manager == null) {
                return;
            }

        	NetworkInfo networkInfo = (NetworkInfo) intent
                      .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        	
            // Connection state changed!  We should probably do something about
            // that.
        	
        	
        	Log.d("connection changed", " connection changed");

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            
        	Log.d("device changed", " device changed");
        }

		
	}

}
