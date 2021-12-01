package com.putrabatam.qrstore.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.putrabatam.qrstore.R;
import com.putrabatam.qrstore.controller.Log_Activity;
import com.putrabatam.qrstore.util.Server_Configuration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Card_List_History_Activity extends RecyclerView.Adapter<Card_List_History_Activity.PastBookingViewHolder>{

    ProgressDialog progressDialog;
    private ArrayList<Log_Activity> dataList;
    private Card_List_History_Activity adapter;

    public Card_List_History_Activity(ArrayList<Log_Activity> dataList) {
        this.dataList = dataList;
        this.adapter = this;
    }

    @Override
    public PastBookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_list_history, parent, false);
        progressDialog = new ProgressDialog(view.getContext());
        return new PastBookingViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull PastBookingViewHolder holder, final int position) {
        holder.id_material.setText(dataList.get(holder.getAdapterPosition()).getId_material());
        holder.scan_type.setText(dataList.get(holder.getAdapterPosition()).getScan_type());
        holder.quantity.setText(dataList.get(holder.getAdapterPosition()).getQuantity());
        holder.datetime.setText(dataList.get(holder.getAdapterPosition()).getDatetime());
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class PastBookingViewHolder extends RecyclerView.ViewHolder{
        TextView id_material, scan_type, quantity, datetime;
        public PastBookingViewHolder(View itemView, final int position) {
            super(itemView);
            this.id_material = itemView.findViewById(R.id.txt_id_material_history);
            this.scan_type = itemView.findViewById(R.id.txt_scan_type_history);
            this.quantity = itemView.findViewById(R.id.txt_quantity_history);
            this.datetime = itemView.findViewById(R.id.txt_date_time_history);
        }
    }
}
