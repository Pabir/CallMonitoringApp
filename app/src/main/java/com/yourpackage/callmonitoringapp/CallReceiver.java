package com.yourpackage.callmonitoringapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;

public class CallReceiver extends BroadcastReceiver {

    private static final String TAG = "CallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        CallLogDatabaseHelper dbHelper = new CallLogDatabaseHelper(context);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            Log.d(TAG, "Incoming call from: " + incomingNumber);
            dbHelper.addCall(incomingNumber, "Incoming", 0);
        }

        ArrayList<CallLog> updatedCallLogs = dbHelper.getAllCalls();
        Intent updateIntent = new Intent("com.yourpackage.callmonitoringapp.UPDATE_CALL_LOG");
        updateIntent.putExtra("updatedCallLogs", updatedCallLogs);
        context.sendBroadcast(updateIntent);
    }
}
