package com.putrabatam.qrstore.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.putrabatam.qrstore.R;
import com.putrabatam.qrstore.controller.Account;
import com.putrabatam.qrstore.controller.DataPart;
import com.putrabatam.qrstore.controller.Material;
import com.putrabatam.qrstore.util.HttpsTrustManager;
import com.putrabatam.qrstore.util.ImageHandler;
import com.putrabatam.qrstore.util.PopUpMessage;
import com.putrabatam.qrstore.util.RequestHandler;
import com.putrabatam.qrstore.util.Server_Configuration;
import com.putrabatam.qrstore.util.VolleyMultipartRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Form_Material_Page extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    PopUpMessage popUpMessage = new PopUpMessage();
    public final int REQUEST_IMAGE_CAPTURE = 1;
    public final int REQUEST_IMAGE_GALLERY = 2;
    ImageView material_photo;
    private String string_image="";
    EditText id_material, name_material, kmin_material, kmax_material;
    Button Save, choose_photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_material_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog = new ProgressDialog(Form_Material_Page.this);
        requestQueue = Volley.newRequestQueue(Form_Material_Page.this);
        Intent form_page = getIntent();
        material_photo = findViewById(R.id.iv_material_photo_fm);
        id_material = findViewById(R.id.et_material_id_fm);
        name_material = findViewById(R.id.et_name_fm);
        kmin_material = findViewById(R.id.et_kmin_fm);
        kmax_material = findViewById(R.id.et_kmax_fm);
        choose_photo = findViewById(R.id.btn_choose_photo_fm);
        Save = findViewById(R.id.btn_save_fm);

        if(!form_page.getStringExtra("type").equals("add")){
            set_form_page(form_page);
        }

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable) material_photo.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                Material data_material = new Material(
                        id_material.getText().toString(),
                        name_material.getText().toString(),
                        bitmap,
                        kmin_material.getText().toString(),
                        kmax_material.getText().toString()
                );
                String return_validation = data_material.validation_adding_material();
                if(return_validation.equals("done")){
                    if(form_page.getStringExtra("type").equals("edit")){
                        Update_Material(data_material);
                    } else{
                        Add_New_Material(data_material);
                    }
                } else{
                    popUpMessage.validation_error(return_validation, Form_Material_Page.this);
                }
            }
        });
        choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(Form_Material_Page.this, view);
                popup.setOnMenuItemClickListener(Form_Material_Page.this);
                popup.inflate(R.menu.menu_choose_image);
                popup.show();
            }
        });
    }

    private void set_form_page(Intent form_page){
        Picasso.get().load(form_page.getStringExtra("material_photo")).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // Set it in the ImageView
                string_image = ImageHandler.BitMapToString(bitmap);
                material_photo.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                //
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
        id_material.setEnabled(false);
        id_material.setText(form_page.getStringExtra("material_id"));
        name_material.setText(form_page.getStringExtra("material_name"));
        kmin_material.setText(String.valueOf(form_page.getIntExtra("material_kmin", 0)));
        kmax_material.setText(String.valueOf(form_page.getIntExtra("material_kmax", 0)));
    }

    private void Add_New_Material(final Material material) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        HttpsTrustManager.allowAllSSL();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Server_Configuration.address_add_new_material,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if(obj.getBoolean("status")){
                                Toast.makeText(Form_Material_Page.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                                Intent kembali = new Intent(Form_Material_Page.this, Home_Admin.class);
                                startActivity(kembali);
                                finish();
                            } else{
                                Toast.makeText(Form_Material_Page.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Form_Material_Page.this,
                                    e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", material.id);
                params.put("name", material.name);
                params.put("kmin", String.valueOf(material.kmin));
                params.put("kmax", String.valueOf(material.kmax));
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("photo", new DataPart(imagename + ".jpg", ImageHandler.getFileDataFromDrawable(material.photo)));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private void Update_Material(final Material material) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        HttpsTrustManager.allowAllSSL();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, Server_Configuration.address_update_material,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if(obj.getBoolean("status")){
                                Toast.makeText(Form_Material_Page.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                                Intent kembali = new Intent(Form_Material_Page.this, Home_Admin.class);
                                finish();
                                startActivity(kembali);
                            } else{
                                Toast.makeText(Form_Material_Page.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e("response update", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //Toast.makeText(getApplicationContext(), , Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", material.id);
                params.put("name", material.name);
                params.put("kmin", String.valueOf(material.kmin));
                params.put("kmax", String.valueOf(material.kmax));
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("photo", new DataPart(imagename + ".jpg", ImageHandler.getFileDataFromDrawable(material.photo)));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.choose_camera:
                Intent choose_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                choose_camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(choose_camera, REQUEST_IMAGE_CAPTURE);
                return true;
            case R.id.choose_folder:
                Intent choose_folder = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(choose_folder, REQUEST_IMAGE_GALLERY);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                    bitmap= ImageHandler.getResizedBitmap(bitmap, 400);
                    material_photo.setImageBitmap(bitmap);
                    string_image = ImageHandler.BitMapToString(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                thumbnail= ImageHandler.getResizedBitmap(thumbnail, 400);
                Log.w("path of image from gallery......******************.........", picturePath+"");
                material_photo.setImageBitmap(thumbnail);
                string_image = ImageHandler.BitMapToString(thumbnail);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(Form_Material_Page.this, Home_Admin.class);
        startActivity(back);
        finish();
    }
}