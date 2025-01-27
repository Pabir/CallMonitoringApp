package com.yourpackage.callmonitoringapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

    private static String lastState = TelephonyManager.EXTRA_STATE_IDLE; // Track the last call state
    private static String incomingNumber = null; // Track the number of the current call

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String currentNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        CallLogDatabaseHelper dbHelper = new CallLogDatabaseHelper(context);

        if (state == null) return;

        // Replace `switch` with `if-else`
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            Log.d("CallReceiver", "Incoming call from: " + currentNumber);
            incomingNumber = currentNumber;
            lastState = TelephonyManager.EXTRA_STATE_RINGING;

        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            Log.d("CallReceiver", "Call answered: " + incomingNumber);
            lastState = TelephonyManager.EXTRA_STATE_OFFHOOK;

        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            if (lastState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                // Call ended
                Log.d("CallReceiver", "Call ended with: " + incomingNumber);
                dbHelper.addCall(incomingNumber, "Incoming", 0); // Add meaningful log only
            }
            lastState = TelephonyManager.EXTRA_STATE_IDLE;
        }

        // Notify the UI to update
        Intent updateIntent = new Intent("com.yourpackage.callmonitoringapp.UPDATE_CALL_LOG");
        context.sendBroadcast(updateIntent);
    }
}
