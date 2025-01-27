package com.yourpackage.callmonitoringapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    private ArrayList<CallLog> callLogs;

    public CallLogAdapter(ArrayList<CallLog> callLogs) {
        this.callLogs = callLogs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallLog callLog = callLogs.get(position);
        holder.phoneNumber.setText(callLog.getPhoneNumber());
        holder.callType.setText(callLog.getCallType());
        holder.duration.setText(String.valueOf(callLog.getDuration()));
    }

    @Override
    public int getItemCount() {
        return callLogs.size();
    }

    public void updateData(ArrayList<CallLog> newCallLogs) {
        this.callLogs.clear();
        this.callLogs.addAll(newCallLogs);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView phoneNumber, callType, duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNumber = itemView.findViewById(R.id.phone_number);
            callType = itemView.findViewById(R.id.call_type);
            duration = itemView.findViewById(R.id.duration);
        }
    }
}
