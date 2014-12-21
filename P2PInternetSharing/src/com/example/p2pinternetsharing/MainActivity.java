package com.example.p2pinternetsharing;


import java.net.UnknownHostException;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p2pinternetsharing.connectivity.NetworkController;
import com.example.p2pinternetsharing.connectivity.WifiBroadcastReceiver;

public class MainActivity extends Activity {

	public static final String TAG = "MAIN_AADHA_DEBUGGING_AND_SHIT";
	private  NetworkController networkController;
	private Thread networkThread;
	public static TextView status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		status = (TextView)findViewById(R.id.status);
		status.setText("App started");
        final ListView listview = (ListView)findViewById(R.id.list);
        listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(), "Trying to connect to " + 
					WifiBroadcastReceiver.validAP.get(position).deviceName, Toast.LENGTH_SHORT).show();
				
				// Connect to the AP with WiFip2p.
				networkController.joinAP(position);
			}
		});

        // Setting the adapter.        
        final ArrayAdapter<WifiP2pDevice> adapter = new ArrayAdapter<WifiP2pDevice>(getApplicationContext(), 
        		R.layout.list, WifiBroadcastReceiver.validAP);
        listview.setAdapter(adapter);
		
        WifiBroadcastReceiver.adapter = adapter;
		
        networkController = new NetworkController(this.getApplicationContext(),this);
		networkThread = new Thread(networkController);
		networkThread.start();
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
		//Need to stop networkcontroller thread
		networkController.startGp();
        
	}
	
	public void stopGP(View view){
		networkController.stopGp();
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
	public void disconnect(View view){
		networkController.disconnectAP();
	}


}
