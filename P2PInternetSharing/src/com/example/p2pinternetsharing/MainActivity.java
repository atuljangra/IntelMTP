package com.example.p2pinternetsharing;


import java.net.UnknownHostException;

import com.example.p2pinternetsharing.connectivity.NetworkController;


import android.support.v7.app.ActionBarActivity;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private  NetworkController networkController;
	private Thread networkThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkController = new NetworkController(this.getApplicationContext());
		networkThread = new Thread(networkController);
		networkThread.start();
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		networkController.onResume();
        
	}
	
	@Override
	public void onPause() {
		super.onPause();
		networkController.onPause();
				
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void startGP(View view) {
		networkController.autoGPManager.startGp();
        
	}
	
	public void stopGP(View view){
		networkController.autoGPManager.stopGp();
	}
	
	
	public void showPass(View view) throws UnknownHostException{
		String password = networkController.autoGPManager.getPassphrase();
		if(password == ""){
			//group not formed;
		}		
	}
	
	public void join(View view){
		networkController.joinAP();
	}


}
