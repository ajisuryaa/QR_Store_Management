package com.putrabatam.qrstore.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.putrabatam.qrstore.R;
import com.putrabatam.qrstore.controller.Account;
import com.putrabatam.qrstore.controller.Material;
import com.putrabatam.qrstore.util.HttpsTrustManager;
import com.putrabatam.qrstore.util.RequestHandler;
import com.putrabatam.qrstore.util.Server_Configuration;
import com.putrabatam.qrstore.util.SharedPreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.vividcode.android.zxing.CaptureActivity;
import info.vividcode.android.zxing.CaptureActivityIntents;

public class Home_Employee extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    private ArrayList<Material> materialArrayList = new ArrayList<Material>();
    private ArrayList<Material> showListMaterial = new ArrayList<Material>();
    private Card_List_Material_Employee adapter;

    LinearLayout body1, body2;
    RecyclerView recyclerView;
    Button scanQR;
    EditText quantity_material_out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_employee);
        progressDialog = new ProgressDialog(Home_Employee.this);
        requestQueue = Volley.newRequestQueue(Home_Employee.this);
        sharedpreferences = getSharedPreferences(SharedPreferencesManager.my_shared_preferences, Context.MODE_PRIVATE);
        body1 = findViewById(R.id.ll_content_empty_he);
        body2 = findViewById(R.id.ll_content_body_he);
        recyclerView = findViewById(R.id.rv_list_material_he);
        scanQR = findViewById(R.id.btn_scan_qr_he);
        adapter = new Card_List_Material_Employee(showListMaterial);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Home_Employee.this);
        check_permission();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setActivated(true);
        Get_List_Material();

        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Scan Button", "CLICKED");
                // Membuat intent baru untuk memanggil CaptureActivity bawaan ZXing
                Intent captureIntent = new Intent(Home_Employee.this, CaptureActivity.class);

                // Kemudian kita mengeset pesan yang akan ditampilkan ke user saat menjalankan QRCode scanning
                CaptureActivityIntents.setPromptMessage(captureIntent, "Barcode scanning...");

                // Melakukan startActivityForResult, untuk menangkap balikan hasil dari QR Code scanning
                startActivityForResult(captureIntent, 0);
            }
        });
    }

    void Get_List_Material() {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        materialArrayList.clear();
        showListMaterial.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server_Configuration.address_get_list_material,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        try {
                            JSONObject obj = new JSONObject(ServerResponse);
                            boolean status = obj.getBoolean("status");
                            String message = obj.getString("message");
                            Log.i("Message List Material ", message);
                            progressDialog.dismiss();
                            if(status){
                                if(message.equals("Data material masih kosong!")){
                                    body1.setVisibility(View.VISIBLE);
                                    body2.setVisibility(View.GONE);
                                } else{
                                    body1.setVisibility(View.GONE);
                                    body2.setVisibility(View.VISIBLE);
                                    JSONArray response = new JSONArray(obj.getString("data"));
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            JSONObject data_material = response.getJSONObject(i);
                                            Material material = new Material();
                                            material.set_material(
                                                    data_material.getString("material_id"),
                                                    data_material.getString("material_name"),
                                                    data_material.getString("material_photo"),
                                                    data_material.getInt("material_current_stock"),
                                                    data_material.getInt("material_kmin"),
                                                    data_material.getInt("material_kmax")
                                            );
                                            materialArrayList.add(material);
                                            showListMaterial.add(material);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }else{
                                body1.setVisibility(View.VISIBLE);
                                body2.setVisibility(View.GONE);
                                Toast.makeText(Home_Employee.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                        Log.i("Jumlah Material", String.valueOf(showListMaterial.size()));
                        Log.i("Value Name", showListMaterial.get(0).name);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(Home_Employee.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Error Vollee", volleyError.toString());
                    }
                }) {
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(Home_Employee.this);
        requestQueue.add(stringRequest);
    }

    public void scan_in_material(String id_material, int stock_material, String id_account) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        HttpsTrustManager.allowAllSSL();
        Server_Configuration server = new Server_Configuration();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server.address_scan_in_material,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        progressDialog.dismiss();
                        try {
                            RequestHandler reqHandler = new RequestHandler();
                            reqHandler.parseDataObject(new JSONObject(ServerResponse));
                            String message = reqHandler.message;
                            if(reqHandler.status){
                                Get_List_Material();
                            }
                            Toast.makeText(Home_Employee.this, message, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(Home_Employee.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Error Vollee", volleyError.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("material", id_material);
                params.put("employee", id_account);
                params.put("numbers_of_material", String.valueOf(stock_material));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(Home_Employee.this);
        requestQueue.add(stringRequest);
    }

    public void scan_out_material(String id_material, int stock_material, String id_account) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        HttpsTrustManager.allowAllSSL();
        Server_Configuration server = new Server_Configuration();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server.address_scan_out_material,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        progressDialog.dismiss();
                        try {
                            RequestHandler reqHandler = new RequestHandler();
                            reqHandler.parseDataObject(new JSONObject(ServerResponse));
                            String message = reqHandler.message;
                            if(reqHandler.status){
                                Get_List_Material();
                            }
                            Toast.makeText(Home_Employee.this, message, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(Home_Employee.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Error Vollee", volleyError.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("material", id_material);
                params.put("employee", id_account);
                params.put("numbers_of_material", String.valueOf(stock_material));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(Home_Employee.this);
        requestQueue.add(stringRequest);
    }

    private void Dialog_set_quantity_out(String id_material, String id_account) {
        dialog = new AlertDialog.Builder(Home_Employee.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.submit_material_out, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Jumlah yang diambil");

        quantity_material_out    = (EditText) dialogView.findViewById(R.id.et_quantity_smo);

        quantity_material_out.setText(null);

        dialog.setPositiveButton("Ambil", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int quantity = Integer.parseInt(quantity_material_out.getText().toString());
                scan_out_material(id_material, quantity, id_account);
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String value = data.getStringExtra("SCAN_RESULT");
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    String id_material = jsonObj.getString("id");
                    String id_account = sharedpreferences.getString("username", "");
                    String employee_position = sharedpreferences.getString("employee_position", "");

                    if(employee_position.equals("Receiving")){
                        int stock_material = jsonObj.getInt("stock");
                        Log.i("SCAN Type", "Receiving");
                        scan_in_material(id_material, stock_material, id_account);
                    } else if(employee_position.equals("MH")){
                        Log.i("SCAN Type", "MH");
                        Dialog_set_quantity_out(id_material, id_account);
                    }else{
                        Log.e("SCAN Type error", "This account dont have access to scan material");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("SCAN ERROR",  e.toString());
                    Toast.makeText(Home_Employee.this, "Failed Scan QR Code", Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("SCAN", "Canceled scan QR");
            }
        } else {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Code Program pada Method dibawah ini akan Berjalan saat Option Menu Dibuat
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Memanggil/Memasang menu item pada toolbar dari layout menu_bar.xml
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu_home_employee, menu);
        MenuItem searchIem = menu.findItem(R.id.search_e);
        final SearchView searchView = (SearchView) searchIem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("search query on text submit", query);
                showListMaterial.clear();
                for(int i = 0; i < materialArrayList.size(); i++){
                    if(materialArrayList.get(i).name.toLowerCase().contains(query.toLowerCase())){
                        Log.i("Index yang mirip", i + " " + materialArrayList.get(i).name);
                        showListMaterial.add(materialArrayList.get(i));
                    }
                }
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.i("search query on text change", s);
                showListMaterial.clear();
                for(int i = 0; i < materialArrayList.size(); i++){
                    if(materialArrayList.get(i).name.toLowerCase().contains(s.toLowerCase())){
                        Log.i("Index yang mirip", i + " " + materialArrayList.get(i).name);
                        showListMaterial.add(materialArrayList.get(i));
                    }
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_lihat_profile_employee_e:
                Intent add_material = new Intent(Home_Employee.this, Profile_Account_Page.class);
                startActivity(add_material);
                finish();
                return true;
            case R.id.menu_lihat_riwayat_employee_e:
                Intent view_employee = new Intent(Home_Employee.this, Log_Activity_Employee.class);
                startActivity(view_employee);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    void check_permission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                //permission not enabled, request it
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //show popup to request permissions
                requestPermissions(permission, 1000);
            }
            else {
            }
        }
    }
}