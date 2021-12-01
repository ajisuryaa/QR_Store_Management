package com.putrabatam.qrstore.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.putrabatam.qrstore.controller.Account;
import com.putrabatam.qrstore.util.ImageHandler;
import com.putrabatam.qrstore.util.Server_Configuration;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Card_List_Employee extends RecyclerView.Adapter<Card_List_Employee.PastBookingViewHolder>{

    ProgressDialog progressDialog;
    private ArrayList<Account> dataList;
    private Card_List_Employee adapter;

    public Card_List_Employee(ArrayList<Account> dataList) {
        this.dataList = dataList;
        this.adapter = this;
    }

    @Override
    public PastBookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_list_employee, parent, false);
        progressDialog = new ProgressDialog(view.getContext());
        return new PastBookingViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull PastBookingViewHolder holder, int position) {
        Log.i("Address Image", Server_Configuration.address_image + dataList.get(holder.getAdapterPosition()).photo);
        holder.nama.setText(dataList.get(holder.getAdapterPosition()).name);
        holder.badge_number.setText("(" + dataList.get(holder.getAdapterPosition()).badge_number + ")");
        holder.employee_position.setText(dataList.get(holder.getAdapterPosition()).employee_position);
        Picasso.get()
                .load(Server_Configuration.address_image + dataList.get(holder.getAdapterPosition()).photo)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.foto_pegawai);
        holder.ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit_employee = new Intent(v.getContext(), Form_Employee_Page.class);
                edit_employee.putExtra("type", "edit");
                edit_employee.putExtra("badge_number", dataList.get(holder.getAdapterPosition()).badge_number);
                edit_employee.putExtra("photo", Server_Configuration.address_image + dataList.get(holder.getAdapterPosition()).photo);
                edit_employee.putExtra("name", dataList.get(holder.getAdapterPosition()).name);
                edit_employee.putExtra("password", dataList.get(holder.getAdapterPosition()).password);
                edit_employee.putExtra("position", dataList.get(holder.getAdapterPosition()).employee_position);
                ((Activity)v.getContext()).finish();
                v.getContext().startActivity(edit_employee);
            }
        });

        holder.hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Hapus Account: ", dataList.get(holder.getAdapterPosition()).getName());
                Delete_Employee(dataList.get(holder.getAdapterPosition()).badge_number, v.getContext());
            }
        });
    }

    public void Delete_Employee(String badge_number, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pesan Konfirmasi")
                .setMessage("Apakah anda yakin ingin menghapus pegawai " + badge_number + "?")
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Request_delete_employee(context, badge_number);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void Request_delete_employee(Context context, String badge_number) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server_Configuration.address_delete_employee,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        try {
                            JSONObject obj = new JSONObject(ServerResponse);
                            Log.i("Hapus pegawai: ", ServerResponse);
                            boolean status = obj.getBoolean("status");
                            JSONObject data = new JSONObject(obj.getString("data"));
                            Toast.makeText(context, "Berhasil menghapus material " + data.getString("id_employee"), Toast.LENGTH_LONG).show();
                            if(status){
                                ((Activity)context).recreate();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Error Volley", volleyError.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", badge_number);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class PastBookingViewHolder extends RecyclerView.ViewHolder{
        TextView nama, employee_position, badge_number;
        ImageButton ubah, hapus;
        ImageView foto_pegawai;
        public PastBookingViewHolder(View itemView, final int position) {
            super(itemView);
            this.nama = itemView.findViewById(R.id.txt_name_karyawan_cle);
            this.badge_number = itemView.findViewById(R.id.txt_badge_number_cle);
            this.employee_position = itemView.findViewById(R.id.txt_email_karyawan_cle);
            this.ubah = itemView.findViewById(R.id.btn_edit_card_karyawan);
            this.hapus = itemView.findViewById(R.id.btn_hapus_card_karyawan);
            this.foto_pegawai = itemView.findViewById(R.id.image_view_employee);
        }
    }
}
