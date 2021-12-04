package com.putrabatam.qrstore.view;

import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.putrabatam.qrstore.R;
import com.putrabatam.qrstore.controller.Material;
import com.putrabatam.qrstore.util.Server_Configuration;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Card_List_Material_Employee extends RecyclerView.Adapter<Card_List_Material_Employee.PastBookingViewHolder>{

    ProgressDialog progressDialog;
    private ArrayList<Material> dataList;
    private Card_List_Material_Employee adapter;

    public Card_List_Material_Employee(ArrayList<Material> dataList) {
        this.dataList = dataList;
        this.adapter = this;
    }

    @Override
    public PastBookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_list_material_employee, parent, false);
        progressDialog = new ProgressDialog(view.getContext());
        return new PastBookingViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull PastBookingViewHolder holder, final int position) {
        holder.nama_material.setText(dataList.get(holder.getAdapterPosition()).name);
        holder.id_material.setText("(" + dataList.get(holder.getAdapterPosition()).id + ")");
        holder.kmin.setText(String.valueOf(dataList.get(holder.getAdapterPosition()).kmin));
        holder.kmax.setText(String.valueOf(dataList.get(holder.getAdapterPosition()).kmax));
        holder.curr.setText(String.valueOf(dataList.get(holder.getAdapterPosition()).current_stock));
        setColorCurrentStock(holder,
                dataList.get(holder.getAdapterPosition()).kmin,
                dataList.get(holder.getAdapterPosition()).kmax,
                dataList.get(holder.getAdapterPosition()).current_stock);
//        Picasso.get().load(Server_Configuration.address_image + dataList.get(position).photo)
//                .into(holder.foto_pegawai);
        Picasso.get()
                .load(Server_Configuration.address_image + dataList.get(holder.getAdapterPosition()).photo_address)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.foto_material);
    }

    void setColorCurrentStock(PastBookingViewHolder holder,
                              int kmin, int kmax, int current_stock){
        if(current_stock >= kmin && current_stock < kmax ){
            int color = ContextCompat.getColor(
                    holder.itemView.getContext(),
                    R.color.Stock_Yellow_Text
            ) ;
            holder.curr.setTextColor(color);
        } else if(current_stock >= kmax){
            int color = ContextCompat.getColor(
                    holder.itemView.getContext(),
                    R.color.Stock_Green_Text
            ) ;
            holder.curr.setTextColor(color);
        } else if(current_stock < kmin){
            int color = ContextCompat.getColor(
                    holder.itemView.getContext(),
                    R.color.Stock_Red_Text
            ) ;
            holder.curr.setTextColor(color);
        } else {
            int color = ContextCompat.getColor(
                    holder.itemView.getContext(),
                    R.color.colorText
            ) ;
            holder.curr.setTextColor(color);
        }
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class PastBookingViewHolder extends RecyclerView.ViewHolder{
        TextView nama_material, id_material;
        TextView kmin, kmax, curr;
        ImageView foto_material;
        public PastBookingViewHolder(View itemView, final int position) {
            super(itemView);
            this.nama_material = itemView.findViewById(R.id.txt_name_material_clm_e);
            this.id_material = itemView.findViewById(R.id.txt_id_material_clm_e);
            this.kmin = itemView.findViewById(R.id.txt_kmin_material_clm_e);
            this.kmax = itemView.findViewById(R.id.txt_kmax_material_clm_e);
            this.curr = itemView.findViewById(R.id.txt_curr_clm_e);
            this.foto_material = itemView.findViewById(R.id.image_view_material_clm_e);
        }
    }
}
