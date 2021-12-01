package com.putrabatam.qrstore.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.putrabatam.qrstore.controller.Material;
import com.putrabatam.qrstore.util.Server_Configuration;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Card_List_Material extends RecyclerView.Adapter<Card_List_Material.PastBookingViewHolder>{

    ProgressDialog progressDialog;
    private ArrayList<Material> dataList;
    private Card_List_Material adapter;

    public Card_List_Material(ArrayList<Material> dataList) {
        this.dataList = dataList;
        this.adapter = this;
    }

    @Override
    public PastBookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_list_material, parent, false);
        progressDialog = new ProgressDialog(view.getContext());
        return new PastBookingViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull PastBookingViewHolder holder, final int position) {
        Log.i("Address Image Material", Server_Configuration.address_image + dataList.get(holder.getAdapterPosition()).photo_address);
        holder.nama_material.setText(dataList.get(holder.getAdapterPosition()).name);
        holder.id_material.setText("(" + dataList.get(holder.getAdapterPosition()).id + ")");
        holder.kmin.setText(String.valueOf(dataList.get(holder.getAdapterPosition()).kmin));
        holder.kmax.setText(String.valueOf(dataList.get(holder.getAdapterPosition()).kmax));
        holder.curr.setText(String.valueOf(dataList.get(holder.getAdapterPosition()).current_stock));
//        Picasso.get().load(Server_Configuration.address_image + dataList.get(position).photo)
//                .into(holder.foto_pegawai);
        Picasso.get()
                .load(Server_Configuration.address_image + dataList.get(holder.getAdapterPosition()).photo_address)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.foto_material);
        holder.ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit_material = new Intent(v.getContext(), Form_Material_Page.class);
                edit_material.putExtra("type", "edit");
                edit_material.putExtra("material_id", dataList.get(holder.getAdapterPosition()).id);
                edit_material.putExtra("material_name", dataList.get(holder.getAdapterPosition()).name);
                edit_material.putExtra("material_photo", Server_Configuration.address_image + dataList.get(holder.getAdapterPosition()).photo_address);
                edit_material.putExtra("material_current_stock", dataList.get(holder.getAdapterPosition()).current_stock);
                edit_material.putExtra("material_kmin", dataList.get(holder.getAdapterPosition()).kmin);
                edit_material.putExtra("material_kmax", dataList.get(holder.getAdapterPosition()).kmax);
                ((Activity)v.getContext()).finish();
                v.getContext().startActivity(edit_material);
            }
        });

        holder.hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Hapus Material: ", dataList.get(holder.getAdapterPosition()).id);
                Delete_Material(dataList.get(holder.getAdapterPosition()).id, v.getContext());
            }
        });
    }

    public void Delete_Material(String id_material, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pesan Konfirmasi")
                .setMessage("Apakah anda yakin ingin menghapus material " + id_material + "?")
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Request_delete_material(context, id_material);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void Request_delete_material(Context context, String id_material) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server_Configuration.address_delete_new_material,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        try {
                            JSONObject obj = new JSONObject(ServerResponse);
                            Log.i("Hapus Material: ", ServerResponse);
                            boolean status = obj.getBoolean("status");
                            JSONObject data = new JSONObject(obj.getString("data"));
                            Toast.makeText(context, "Berhasil menghapus material " + data.getString("id_material"), Toast.LENGTH_LONG).show();
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
                params.put("id", id_material);
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
        TextView nama_material, id_material;
        TextView kmin, kmax, curr;
        ImageButton ubah, hapus;
        ImageView foto_material;
        public PastBookingViewHolder(View itemView, final int position) {
            super(itemView);
            this.nama_material = itemView.findViewById(R.id.txt_name_material_clm);
            this.id_material = itemView.findViewById(R.id.txt_id_material_clm);
            this.kmin = itemView.findViewById(R.id.txt_kmin_material_clm);
            this.kmax = itemView.findViewById(R.id.txt_kmax_material_clm);
            this.curr = itemView.findViewById(R.id.txt_curr_material_clm);
            this.ubah = itemView.findViewById(R.id.btn_edit_material_clm);
            this.hapus = itemView.findViewById(R.id.btn_hapus_material_clm);
            this.foto_material = itemView.findViewById(R.id.image_view_material_clm);
        }
    }
}
