package com.putrabatam.qrstore.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.putrabatam.qrstore.R;
import com.putrabatam.qrstore.controller.Log_Activity;
import com.putrabatam.qrstore.controller.Material;
import com.putrabatam.qrstore.util.Server_Configuration;
import com.putrabatam.qrstore.util.SharedPreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Log_Activity_Employee extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    private ArrayList<Log_Activity> showListHistory = new ArrayList<Log_Activity>();
    private Card_List_History_Activity adapter;
    RecyclerView recyclerView;
    LinearLayout body1, body2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_employee);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog = new ProgressDialog(Log_Activity_Employee.this);
        requestQueue = Volley.newRequestQueue(Log_Activity_Employee.this);
        sharedpreferences = getSharedPreferences(SharedPreferencesManager.my_shared_preferences, Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.rv_list_history);
        body1 = findViewById(R.id.ll_content_empty_history);
        body2 = findViewById(R.id.ll_content_history);
        adapter = new Card_List_History_Activity(showListHistory);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Log_Activity_Employee.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setActivated(true);
        Get_List_History(sharedpreferences.getString("username", ""));
    }

    void Get_List_History(String id_employee) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        showListHistory.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server_Configuration.address_get_list_log_activity,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        try {
                            JSONObject obj = new JSONObject(ServerResponse);
                            boolean status = obj.getBoolean("status");
                            String message = obj.getString("message");
                            Log.i("Message List History ", message);
                            progressDialog.dismiss();
                            if(status){
                                if(message.equals("Data log masih kosong!")){
                                    body1.setVisibility(View.VISIBLE);
                                    body2.setVisibility(View.GONE);
                                } else{
                                    body1.setVisibility(View.GONE);
                                    body2.setVisibility(View.VISIBLE);
                                    JSONArray response = new JSONArray(obj.getString("data"));
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            JSONObject data_material = response.getJSONObject(i);
                                            Log_Activity material = new Log_Activity(
                                                    data_material.getString("material_id"),
                                                    data_material.getString("action_name"),
                                                    data_material.getString("num_stock"),
                                                    data_material.getString("datetime")
                                            );
                                            showListHistory.add(material);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }else{
                                body1.setVisibility(View.VISIBLE);
                                body2.setVisibility(View.GONE);
                                Toast.makeText(Log_Activity_Employee.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(Log_Activity_Employee.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Error Vollee", volleyError.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_employee", id_employee);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(Log_Activity_Employee.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(Log_Activity_Employee.this, Home_Employee.class);
        startActivity(back);
        finish();
    }
}