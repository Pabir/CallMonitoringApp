package com.yourpackage.callmonitoringapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallStateReceiver extends BroadcastReceiver {

    private static final String TAG = "CallStateReceiver";
    private static String lastState = TelephonyManager.EXTRA_STATE_IDLE;
    private static String incomingNumber = null;
    private static String outgoingNumber = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        CallLogDatabaseHelper dbHelper = new CallLogDatabaseHelper(context);

        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
            // Outgoing call detected
            outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG, "Outgoing call to: " + outgoingNumber);
            lastState = "OUTGOING";

        } else {
            // Handle incoming call states
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String currentNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if (state == null) return;

            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                // Incoming call is ringing
                Log.d(TAG, "Incoming call ringing from: " + currentNumber);
                incomingNumber = currentNumber;
                lastState = TelephonyManager.EXTRA_STATE_RINGING;

            } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                // Call answered
                if ("OUTGOING".equals(lastState)) {
                    Log.d(TAG, "Outgoing call answered: " + outgoingNumber);
                } else {
                    Log.d(TAG, "Incoming call answered: " + incomingNumber);
                }
                lastState = TelephonyManager.EXTRA_STATE_OFFHOOK;

            } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                // Call ended
                if ("OUTGOING".equals(lastState)) {
                    Log.d(TAG, "Outgoing call ended with: " + outgoingNumber);
                    int duration = calculateCallDuration(context, outgoingNumber);
                    dbHelper.addCall(outgoingNumber, "Outgoing", duration);
                } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(lastState)) {
                    Log.d(TAG, "Incoming call ended with: " + incomingNumber);
                    int duration = calculateCallDuration(context, incomingNumber);
                    dbHelper.addCall(incomingNumber, "Incoming", duration);
                } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(lastState)) {
                    Log.d(TAG, "Missed call from: " + incomingNumber);
                    dbHelper.addCall(incomingNumber, "Missed", 0);
                }
                lastState = TelephonyManager.EXTRA_STATE_IDLE;
            }
        }

        // Notify the UI to refresh
        Intent updateIntent = new Intent("com.yourpackage.callmonitoringapp.UPDATE_CALL_LOG");
        context.sendBroadcast(updateIntent);
    }

    private int calculateCallDuration(Context context, String number) {
        if (number == null || number.isEmpty()) return 0;

        int duration = 0;
        try {
            Cursor cursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.DURATION, CallLog.Calls.NUMBER},
                CallLog.Calls.NUMBER + "=?",
                new String[]{number},
                CallLog.Calls.DATE + " DESC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                duration = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                Log.d(TAG, "Call duration for " + number + ": " + duration + " seconds");
            }

            if (cursor != null) cursor.close();
        } catch (SecurityException e) {
            Log.e(TAG, "Permission denied to read call log.", e);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving call duration.", e);
        }

        return duration;
    }
}
