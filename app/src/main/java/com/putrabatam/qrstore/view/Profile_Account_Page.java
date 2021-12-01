package com.putrabatam.qrstore.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.putrabatam.qrstore.R;
import com.putrabatam.qrstore.util.SharedPreferencesManager;

public class Profile_Account_Page extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    Intent data_account = getIntent();
    TextView name, position;
    ImageView photo;
    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_account_page);
        sharedpreferences = getSharedPreferences(SharedPreferencesManager.my_shared_preferences, Context.MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        photo = findViewById(R.id.pa_avatar_profile);
        name = findViewById(R.id.pa_name_txt);
        position = findViewById(R.id.pa_position_txt);
        logout = findViewById(R.id.pa_btn_logout);
        set_profile();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesManager shared = new SharedPreferencesManager();
                shared.logout_session(sharedpreferences);
                Intent back = new Intent(Profile_Account_Page.this, MainActivity.class);
                startActivity(back);
                finish();
            }
        });
    }

    void set_profile(){
        name.setText(sharedpreferences.getString("name", ""));
        position.setText(sharedpreferences.getString("type_account", ""));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(sharedpreferences.getString("type_account", "admin").equals("admin")){
            Intent back = new Intent(Profile_Account_Page.this, Home_Admin.class);
            startActivity(back);
            finish();
        } else{
            Intent back = new Intent(Profile_Account_Page.this, Home_Employee.class);
            startActivity(back);
            finish();
        }
    }
}