package com.putrabatam.qrstore.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.putrabatam.qrstore.util.ImageHandler;

public class Account {
    public String badge_number, email, username, name, password, conf_pass, type_account, photo;
    public String employee_position;
    public Bitmap photo_bitmap;

    public void setAccountEmployee(String badge_number, String name, Bitmap photo, String password,
                                   String conf_pass, String employee_position){
        this.badge_number = badge_number;
        this.name = name;
        this.photo_bitmap = photo;
        this.photo = ImageHandler.BitMapToString(photo);
        this.password = password;
        this.conf_pass = conf_pass;
        this.employee_position = employee_position;
    }

    public void setParseListEmployee(String id, String name, String password, String photo, String employee_position){
        this.badge_number = id;
        this.name = name;
        this.password = password;
        this.photo = photo;
        this.employee_position = employee_position;
    }

    public Bitmap getPhoto_bitmap() {
        return photo_bitmap;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType_account(String type_account) {
        this.type_account = type_account;
    }

    public String getType_account() {
        return type_account;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmployee_position(String employee_position) {
        this.employee_position = employee_position;
    }

    public String getEmployee_position() {
        return employee_position;
    }

    public String validation_login(Account data){
        if(data.username.equals("") || data.username.isEmpty() && data.password.equals("")
                || password.isEmpty()){
            return "username atau kata sandi tidak boleh kosong!";
        } else if(data.username.length() > 0 && data.password.equals("")
                || password.length() == 0){
            return "Password tidak boleh kosong";
        } else if(data.password.length() > 0 && data.username.equals("")
                || data.username.length() == 0){
            return "Username tidak boleh kosong";
        } else{
            return "lolos validasi";
        }
    }

    public String validation_register_account(){
        if(badge_number.equals("") || badge_number.length() <= 0 || badge_number.isEmpty()){
            return "Nomor badge pegawai tidak boleh kosong";
        } else if(name.equals("") || name.length()<=0 || name.isEmpty()){
            return "Nama pegawai tidak boleh kosong";
        } else if(password.equals("") || password.length()<=0 || password.isEmpty()){
            return "Kata sandi tidak boleh kosong";
        } else if(conf_pass.equals("") || conf_pass.length()<=0 || conf_pass.isEmpty()){
            return "Ketik ulang kata sandi anda";
        } else if(!conf_pass.equals(password)){
            return "Konfirmasi kata sandi anda harus sama";
        } else{
            return "done";
        }
    }

}
