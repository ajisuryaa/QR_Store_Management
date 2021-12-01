package com.putrabatam.qrstore.view;

import static com.putrabatam.qrstore.util.ImageHandler.getPath;
import static com.putrabatam.qrstore.util.ImageHandler.scaleDown;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

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
import com.putrabatam.qrstore.util.HttpsTrustManager;
import com.putrabatam.qrstore.util.ImageHandler;
import com.putrabatam.qrstore.util.PopUpMessage;
import com.putrabatam.qrstore.util.RequestHandler;
import com.putrabatam.qrstore.util.Server_Configuration;
import com.putrabatam.qrstore.util.Spinner_Employee_Position_Adapter;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Form_Employee_Page extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    public final int REQUEST_IMAGE_CAPTURE = 1;
    public final int REQUEST_IMAGE_GALLERY = 2;
    Uri image_uri;

    ImageView avatar_image;
    EditText badge_id, name, password, confirmation_password;
    Spinner position;
    Button choose_image, save_data;
    private String string_image="";
    String[] positionNames={"MH", "Receiving"};
    Account data_akun = new Account();
    PopUpMessage popUpMessage= new PopUpMessage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_employee_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog = new ProgressDialog(Form_Employee_Page.this);
        requestQueue = Volley.newRequestQueue(Form_Employee_Page.this);
        avatar_image = findViewById(R.id.iv_avatar_photo_fp);
        choose_image = findViewById(R.id.btn_choose_photo_fp);
        badge_id = findViewById(R.id.et_badge_id_fp);
        name = findViewById(R.id.et_name_fp);
        position = findViewById(R.id.sp_position_fp);
        password = findViewById(R.id.et_password_fp);
        confirmation_password = findViewById(R.id.et_repass_fp);
        save_data = findViewById(R.id.btn_save_fp);
        position.setOnItemSelectedListener(this);
        Spinner_Employee_Position_Adapter customAdapter=new Spinner_Employee_Position_Adapter(getApplicationContext(), positionNames);
        position.setAdapter(customAdapter);

        set_form_type(getIntent());

        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(Form_Employee_Page.this, view);
                popup.setOnMenuItemClickListener(Form_Employee_Page.this);
                popup.inflate(R.menu.menu_choose_image);
                popup.show();
            }
        });
        save_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account data_account = new Account();
                BitmapDrawable drawable = (BitmapDrawable) avatar_image.getDrawable();
                Bitmap bitmap= drawable.getBitmap();
                data_account.setAccountEmployee(
                        badge_id.getText().toString(),
                        name.getText().toString(),
                        bitmap,
                        password.getText().toString(),
                        confirmation_password.getText().toString(),
                        data_akun.getEmployee_position()
                );
                String return_validation = data_account.validation_register_account();
                if(return_validation.equals("done")){
                    if(getIntent().getStringExtra("type").equals("add")){
                        Log.i("Action", "Add Account");
                        Add_New_Employee(data_account);
                    } else{
                        Log.i("Action", "Edit Account");
                        Log.i("Badge Number", data_account.badge_number);
                        Log.i("Name", data_account.name);
                        Log.i("Password", data_account.password);
                        Log.i("Confirmation Pass", confirmation_password.getText().toString());
                        //Log.i("Photo", data_account.photo);
                        Update_Employee(data_account);
                    }
                } else{
                    popUpMessage.validation_error(return_validation, Form_Employee_Page.this);
                }
            }
        });
    }

    void set_form_type(Intent data){
        if(data.getStringExtra("type").equals("edit")){
            badge_id.setEnabled(false);
            badge_id.setText(data.getStringExtra("badge_number"));
            name.setText(data.getStringExtra("name"));
            if(data.getStringExtra("position").equals("Waterspider")){
                position.setSelection(0);
            } else{
                position.setSelection(1);
            }
            Picasso.get().load(data.getStringExtra("photo")).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // Set it in the ImageView
                    string_image = ImageHandler.BitMapToString(bitmap);
                    avatar_image.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    //
                }
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });
            password.setText(data.getStringExtra("password"));
            confirmation_password.setText(data.getStringExtra("password"));
        }
    }

    private void Update_Employee(final Account account) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        HttpsTrustManager.allowAllSSL();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, Server_Configuration.address_update_employee,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if(obj.getBoolean("status")){
                                Toast.makeText(Form_Employee_Page.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                                Intent kembali = new Intent(Form_Employee_Page.this, Employee_Page.class);
                                startActivity(kembali);
                                finish();
                            } else{
                                Toast.makeText(Form_Employee_Page.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Form_Employee_Page.this,
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
                params.put("badge_number", account.badge_number);
                params.put("name", account.getName());
                params.put("password", account.getPassword());
                params.put("position", account.getEmployee_position());
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("photo", new DataPart(imagename + ".jpg", ImageHandler.getFileDataFromDrawable(account.photo_bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private void Add_New_Employee(final Account account) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        HttpsTrustManager.allowAllSSL();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Server_Configuration.address_add_new_employee,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if(obj.getBoolean("status")){
                                Toast.makeText(Form_Employee_Page.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                                Intent kembali = new Intent(Form_Employee_Page.this, Employee_Page.class);
                                finish();
                                startActivity(kembali);
                            } else{
                                Toast.makeText(Form_Employee_Page.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Form_Employee_Page.this,
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
                params.put("badge_number", account.badge_number);
                params.put("name", account.getName());
                params.put("password", account.getPassword());
                params.put("position", account.getEmployee_position());
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("photo", new DataPart(imagename + ".jpg", ImageHandler.getFileDataFromDrawable(account.photo_bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(Form_Employee_Page.this, Employee_Page.class);
        startActivity(back);
        finish();
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        CameraActivityResultLauncher.launch(cameraIntent);
    }

    ActivityResultLauncher<Intent> CameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri fileUri = image_uri;
                        String selectedImagePath = getPath(getApplicationContext(), fileUri);
                        if (selectedImagePath != "Not found") {
                            ContentResolver contentResolver = getContentResolver();

                            try {
                                // Open the file input stream by the uri.
                                InputStream inputStream = contentResolver.openInputStream(fileUri);

                                // Get the bitmap.
                                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                                Bitmap scaledBitmap = scaleDown(imgBitmap, 300, true);
                                avatar_image.setImageBitmap(scaledBitmap);
                                inputStream.close();
                            } catch (FileNotFoundException ex) {
                                Log.e("FAILED", ex.getMessage(), ex);
                            } catch (IOException ex) {
                                Log.e("FAILED", ex.getMessage(), ex);
                            }
                        }
                    } else{
                        Log.e("Failed", "Failed Load Image From Camera!");
                    }
                }
            });

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.choose_camera:
                openCamera();
                return true;
            case R.id.choose_folder:

                return true;
            default:
                return false;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        data_akun.setEmployee_position(positionNames[i]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}