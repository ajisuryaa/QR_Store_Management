package com.putrabatam.qrstore.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.putrabatam.qrstore.R;
import com.putrabatam.qrstore.controller.Account;
import com.putrabatam.qrstore.util.HttpsTrustManager;
import com.putrabatam.qrstore.util.PopUpMessage;
import com.putrabatam.qrstore.util.RequestHandler;
import com.putrabatam.qrstore.util.Server_Configuration;
import com.putrabatam.qrstore.util.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;
    SharedPreferencesManager sharedPreferencesManager;
    PopUpMessage popUpMessage= new PopUpMessage();

    EditText username, password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(MainActivity.this);
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        sharedpreferences = getSharedPreferences(sharedPreferencesManager.my_shared_preferences, Context.MODE_PRIVATE);
        get_session(sharedpreferences);

        username = findViewById(R.id.et_login_username);
        password = findViewById(R.id.et_login_password);
        login = findViewById(R.id.btnMasuk);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pre_login(username.getText().toString(), password.getText().toString());
            }
        });
    }

    //Fungsi untuk membuka otomatis halaman home page jika user belum logout
    private void get_session(SharedPreferences setting){
        if(sharedpreferences.getBoolean("session_status", false)==true){
            Account data_akun = new Account();
            data_akun.setUsername(setting.getString("username", ""));
            data_akun.setName(setting.getString("name", ""));
            data_akun.setPassword(setting.getString("password", ""));
            data_akun.setPhoto(setting.getString("photo", ""));
            data_akun.setType_account(setting.getString("type_account", ""));
            data_akun.setType_account(setting.getString("employee_position", ""));
            Log.e("Session Username: ", data_akun.getUsername());
            Log.e("Session Password: ", data_akun.getPassword());
            login_account(data_akun);
        }
        else{
            Log.e("Login Stat: ", "false");
        }
    }

    void pre_login(String username, String password){
        Account login_con = new Account();
        login_con.setUsername(username);
        login_con.setPassword(password);
        String result_validasi = login_con.validation_login(login_con);
        if(result_validasi.equals("lolos validasi")){
            if(login_con.getUsername().contains("admin")){
                login_con.setType_account("admin");
            } else {
                login_con.setType_account("employee");
            }
            login_account(login_con);
        } else{
            popUpMessage.login_gagal(result_validasi, MainActivity.this);
        }
    }

    public void login_account(Account data_akun) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        HttpsTrustManager.allowAllSSL();
        Server_Configuration server = new Server_Configuration();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server.address_login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        progressDialog.dismiss();
                        try {
                            RequestHandler reqHandler = new RequestHandler();
                            reqHandler.parseDataObject(new JSONObject(ServerResponse));
                            String message = reqHandler.message;
                            if(reqHandler.status){
                                SharedPreferencesManager shared = new SharedPreferencesManager();
                                JSONObject obj = reqHandler.data_object;
                                data_akun.setName(obj.getString("name"));
                                data_akun.setPhoto(obj.getString("photo"));
                                if(data_akun.getType_account().equals("admin")){
                                    shared.set_account_session(sharedpreferences, data_akun);
                                    Intent home_admin = new Intent(MainActivity.this, Home_Admin.class);
                                    startActivity(home_admin);
                                    finish();
                                } else{
                                    data_akun.setEmployee_position(obj.getString("employee_position"));
                                    shared.set_account_session(sharedpreferences, data_akun);
                                    Intent home_employee = new Intent(MainActivity.this, Home_Employee.class);
                                    startActivity(home_employee);
                                    finish();
                                }
                            } else{
                                popUpMessage.login_gagal(message, MainActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Error Vollee", volleyError.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("account_type", data_akun.getType_account());
                params.put("id", data_akun.getUsername());
                params.put("password", data_akun.getPassword());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }
}