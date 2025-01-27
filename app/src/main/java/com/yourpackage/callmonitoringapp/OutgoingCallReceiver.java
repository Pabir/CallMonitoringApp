package com.yourpackage.callmonitoringapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OutgoingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        Log.d("OutgoingCallReceiver", "Outgoing call to: " + outgoingNumber);

        // Save the outgoing call log to the database
        CallLogDatabaseHelper dbHelper = new CallLogDatabaseHelper(context);
        dbHelper.addCall(outgoingNumber, "Outgoing", 0); // Duration can be updated after the call ends
    }
}
