/*
 *   Copyright 2012 Shell M. Shrader
 *   
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.shellware.adaptronic.adaptive.tuner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.BatteryManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.shellware.adaptronic.adaptive.tuner.MainActivity;
import com.shellware.adaptronic.adaptive.tuner.services.ConnectionService;

public class BatteryStatusReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(final Context ctx, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){

			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);	
			
			final int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
			final int lastStatus = prefs.getInt("last_battery_status", BatteryManager.BATTERY_STATUS_UNKNOWN);
			
			// long winded way of acting if charging or full charge but 
			// only if previous status was not charging or full charge
			if (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL) {
				if (lastStatus != BatteryManager.BATTERY_STATUS_CHARGING && lastStatus != BatteryManager.BATTERY_STATUS_FULL) {
					
					final boolean enabled = prefs.getBoolean("prefs_connect_on_charge", false);
					
					if (enabled && ConnectionService.state == ConnectionService.State.DISCONNECTED) {
				    	final String name = prefs.getString("prefs_remote_name", "");
				    	final String addr = prefs.getString("prefs_remote_mac", "");
				    	final int waitTime = (int) prefs.getFloat("prefs_connect_on_charge_wait_time", 30);
				    	final boolean waiting = prefs.getBoolean("prefs_connect_on_charge_waiting", false);
				    	
			    		// only run if not active
				    	if (addr.length() > 0  && !waiting) {
				    		final Editor edit = prefs.edit();
				    		edit.putBoolean("prefs_connect_on_charge_waiting", true);
				    		edit.commit();
				    		
				    		if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "waiting to connect");
//				    		Toast.makeText(ctx,  "waiting to connect", Toast.LENGTH_LONG);
				    		
				    		new Handler().postDelayed(new Runnable() {

								public void run() {
						    		
						        	Intent service = new Intent(ConnectionService.ACTION_CONNECT_BT);
						        	service.putExtra("name", name);
						        	service.putExtra("addr", addr);
						        	
						        	ctx.startService(service);
						        	
						    		edit.putBoolean("prefs_connect_on_charge_waiting", false);
						    		edit.commit();
						    		
						    		if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "intent sent");
//						    		Toast.makeText(ctx,  "intent sent", Toast.LENGTH_LONG);									
								}
								
							} , waitTime * 1000);
				    	}
					}
				}
			}
			
			final Editor edit = prefs.edit();
			edit.putInt("last_battery_status", status);
			edit.commit();
		}
	}

}
