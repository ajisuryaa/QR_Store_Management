package com.putrabatam.qrstore.controller;

import android.graphics.Bitmap;

public class Material {
    public String id, name, photo_address;
    public Bitmap photo;
    public int current_stock, kmin, kmax;

    public Material(){

    }
    public Material(String id, String name, Bitmap photo, String kmin, String kmax){
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.kmin = value_of_K(kmin);
        this.kmax = value_of_K(kmax);
    }

    int value_of_K(String kmin){
        if(kmin.equals("") || kmin.length() <= 0 || kmin.isEmpty()){
            return 0;
        } else {
            return Integer.parseInt(kmin);
        }
    }

    public void set_material(String id, String name, String photo, int current_stock, int kmin, int kmax){
        this.id = id;
        this.name = name;
        this.photo_address = photo;
        this.current_stock = current_stock;
        this.kmin = kmin;
        this.kmax = kmax;
    }

    public String validation_adding_material(){
        if(id.equals("") || id.length()<=0 || id.isEmpty()){
            return "ID material tidak boleh kosong";
        } else if(name.equals("") || name.length()<=0 || name.isEmpty()){
            return "Nama material tidak boleh kosong";
        } else if(kmin == 0){
            return "KMIN material tidak boleh kosong";
        } else if(kmax == 0){
            return "KMAX material tidak boleh kosong";
        } else {
            return "done";
        }
    }
}
