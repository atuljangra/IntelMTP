package com.example.p2pinternetsharing.connectivity;

/*
 *  Responsibilities of shadow master.
 *  Start bdcasting wifi Config in the network in one thread.
 *  Whenever the connection is lost to the currentAP, wait for a timeout and then start it's own AP.
 */

public class ShadowMaster implements Runnable{

	@Override
	public void run() {
		
	}

}
