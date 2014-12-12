package com.example.p2pinternetsharing.connectivity;



import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.util.Log;

public class AutonomousGroupManager implements Runnable {
	private static final String DEBUG_TAG = "MAIN_AADHA_DEBUGGING_AND_SHIT";

	private WifiP2pGroup savedgroup;
	
	private Boolean isAPRunning;
	//private WifiP2pGroup

	private WifiP2pManager mManager;
	
	private static Channel mChannel;
	
	
	public AutonomousGroupManager(WifiP2pManager mManager, Channel mChannel) {
		this.mChannel = mChannel;
		this.mManager = mManager;
		isAPRunning = false;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		mManager.createGroup(mChannel, new ActionListener() {

			@Override
            public void onSuccess() {

					try {
						Log.d(DEBUG_TAG, "Sleeping, on success and group info takes time.");
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            	
            	// Check if the group is not null.
            	mManager.requestGroupInfo(mChannel, new GroupInfoListener() {
         			@Override
         			public void onGroupInfoAvailable(WifiP2pGroup group) {
         				while (true) {
         					if(group != null ){
         					// Remove the progress bar.
     							Log.d(DEBUG_TAG, "Group formed, exiting.");
     			 				String s = group.getPassphrase();
     			 				Log.d("passphrase", s);
     			 				savedgroup = group;
     			 				stopGp();
								return;
         					} else {
         						try {
         							Log.d(DEBUG_TAG, "Group is null, Sleeping");
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
         					}
         					
         					// Problem while selcting.
         				}
         			}
         		});

            	Log.d("creation", "success");
            	

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
	
	public void startGp() {
        
        new Thread(new Runnable() {
			@Override
			public void run() {
		        mManager.createGroup(mChannel, new ActionListener() {

					@Override
		            public void onSuccess() {

 						try {
 							Log.d(DEBUG_TAG, "Sleeping, on success and group info takes time.");
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		            	
		            	// Check if the group is not null.
		            	mManager.requestGroupInfo(mChannel, new GroupInfoListener() {
		         			@Override
		         			public void onGroupInfoAvailable(WifiP2pGroup group) {
		         				while (true) {
		         					if(group != null ){
		         					// Remove the progress bar.
	         							Log.d(DEBUG_TAG, "Group formed, exiting.");
	         			 				String s = group.getPassphrase();
	         			 				Log.d("passphrase", s);
										return;
		         					} else {
		         						try {
		         							Log.d(DEBUG_TAG, "Group is null, Sleeping");
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
		         					}
		         					
		         					// Problem while selcting.
		         				}
		         			}
		         		});

		            	Log.d("creation", "success");
		            	

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
		}
        ).start();
        
	}

	
	
	public void stopGp(){
		
		mManager.removeGroup(mChannel,  new ActionListener() {
	
			@Override
	        public void onSuccess() {
	            // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
	        	Log.d("closing", "success");
	        	
	        }
	
	        @Override
	        public void onFailure(int reason) {
	        	if(reason == 2){
	            	Log.d("error","busy");
	         	}
	         	else
	         		Log.d("error","closing failure:other " + reason);
	
	        }
			});
	}
	
	
	public Boolean isGroupFormed(){
		isAPRunning = false;
		mManager.requestGroupInfo(mChannel, new GroupInfoListener() {
				@Override
				public void onGroupInfoAvailable(WifiP2pGroup gp) {
					
					if(gp != null ){
					// Remove the progress bar.
							Log.d(DEBUG_TAG, "Group formed, exiting.");
			 				savedgroup = gp;	
			 				isAPRunning = true;
					} else {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Log.d(DEBUG_TAG, "Group is null, Sleeping");
					}
					
				}
			});
		
		return isAPRunning;
			
	}
	
	
	/* returns empty string if group not formed yet */
	public String getPassphrase(){
		
		if(isGroupFormed())
			return savedgroup.getPassphrase();
		else
			return "";
	}
	
	public String getSavedPassPhrase(){
		if(savedgroup == null)
			return "";
		else
			return savedgroup.getPassphrase();
	}

	

}
