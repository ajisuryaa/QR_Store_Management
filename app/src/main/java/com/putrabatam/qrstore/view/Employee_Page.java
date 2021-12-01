package com.putrabatam.qrstore.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.putrabatam.qrstore.util.Server_Configuration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Employee_Page extends AppCompatActivity {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    private ArrayList<Account> employeeArrayList = new ArrayList<>();
    private Card_List_Employee adapter;

    LinearLayout body1, body2;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog = new ProgressDialog(Employee_Page.this);
        requestQueue = Volley.newRequestQueue(Employee_Page.this);
        body1 = findViewById(R.id.ll_empty_content_ep);
        body2 = findViewById(R.id.ll_content_ep);
        recyclerView = findViewById(R.id.rv_view_employee_ep);
        adapter = new Card_List_Employee(employeeArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Employee_Page.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        Get_List_Employee();
    }

    void Get_List_Employee() {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        Server_Configuration server = new Server_Configuration();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server.address_get_list_employee,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        try {
                            JSONObject obj = new JSONObject(ServerResponse);
                            boolean status = obj.getBoolean("status");
                            String message = obj.getString("message");
                            Log.i("Message List Employee: ", message);
                            progressDialog.dismiss();
                            if(status){
                                if(message.equals("Data pegawai masih kosong!")){
                                    body1.setVisibility(View.VISIBLE);
                                    body2.setVisibility(View.GONE);
                                } else{
                                    body1.setVisibility(View.GONE);
                                    body2.setVisibility(View.VISIBLE);
                                    JSONArray response = new JSONArray(obj.getString("data"));
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            JSONObject data_employee = response.getJSONObject(i);
                                            Account employee = new Account();
                                            employee.setParseListEmployee(
                                                    data_employee.getString("employee_id"),
                                                    data_employee.getString("employee_name"),
                                                    data_employee.getString("employee_password"),
                                                    data_employee.getString("employee_photo"),
                                                    data_employee.getString("employee_position")
                                            );
                                            employeeArrayList.add(employee);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }else{
                                body1.setVisibility(View.VISIBLE);
                                body2.setVisibility(View.GONE);
                                Toast.makeText(Employee_Page.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                        //adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(Employee_Page.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Error Vollee", volleyError.toString());
                    }
                }) {
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(Employee_Page.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Memanggil/Memasang menu item pada toolbar dari layout menu_bar.xml
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.employee_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_new_employee:
                Intent add_employee = new Intent(Employee_Page.this, Form_Employee_Page.class);
                add_employee.putExtra("type", "add");
                startActivity(add_employee);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(Employee_Page.this, Home_Admin.class);
        startActivity(back);
        finish();
    }
}