package com.example.p2pinternetsharing.connectivity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
public class WifiDirectScanner implements PeerListListener{

	public static List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peerList) {
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
		// TODO Auto-generated method stub
				
	}

}
