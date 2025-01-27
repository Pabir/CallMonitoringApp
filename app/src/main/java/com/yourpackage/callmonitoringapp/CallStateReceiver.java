package com.yourpackage.callmonitoringapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            Log.d("CallStateReceiver", "Incoming call from: " + incomingNumber);

            CallLogDatabaseHelper dbHelper = new CallLogDatabaseHelper(context);
            dbHelper.addCall(incomingNumber, "Incoming", 0);

            // Notify the UI to update
            Intent updateIntent = new Intent("com.yourpackage.callmonitoringapp.UPDATE_CALL_LOG");
            context.sendBroadcast(updateIntent);
        }
    }
}
