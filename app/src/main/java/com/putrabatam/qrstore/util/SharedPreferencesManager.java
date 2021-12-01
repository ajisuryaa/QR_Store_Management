package com.putrabatam.qrstore.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.putrabatam.qrstore.controller.Account;
import com.putrabatam.qrstore.view.MainActivity;

public class SharedPreferencesManager {
    public static final String my_shared_preferences = "my_shared_preferences";

    //Fungsi untuk membuka otomatis halaman home page jika user belum logout
    public Account mode_login(SharedPreferences setting){
//        if(setting.getBoolean("session_status", false)==true){
//            Account data_akun = new Account();
//            data_akun.setUsername(setting.getString("username", ""));
//            data_akun.setUsername(setting.getString("name", ""));
//            data_akun.setUsername(setting.getString("password", ""));
//            data_akun.setUsername(setting.getString("photo", ""));
//            data_akun.setUsername(setting.getString("employee_position", ""));
//            return data_akun;
//        }
//        else{
//            Log.e("Login Stat: ", "false");
//            return null;
//        }
        Log.i("Session Status: ", setting.getString("username", "KOSONG"));
        return null;
    }

    public void set_account_session(SharedPreferences sharedPreferences, Account data_akun){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("session_status", true);
        editor.putString("username", data_akun.getUsername());
        editor.putString("name", data_akun.getName());
        editor.putString("password", data_akun.getPassword());
        editor.putString("photo", data_akun.getPhoto());
        editor.putString("employee_position", data_akun.getEmployee_position());
        editor.putString("type_account", data_akun.getType_account());
        editor.commit();
    }
    public void logout_session(SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("session_status", false);
        editor.putString("username", "");
        editor.putString("name", "");
        editor.putString("password", "");
        editor.putString("photo", "");
        editor.putString("employee_position", "");
        editor.commit();
    }
}
