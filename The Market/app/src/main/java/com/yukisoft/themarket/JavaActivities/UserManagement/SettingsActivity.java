package com.yukisoft.themarket.JavaActivities.UserManagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.yukisoft.themarket.MainActivity;
import com.yukisoft.themarket.R;

public class SettingsActivity extends AppCompatActivity {
    public static String SETTINGS = "SETTINGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button logout = findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth fbAuth = FirebaseAuth.getInstance();
                fbAuth.signOut();
                startActivity(new Intent(SettingsActivity.this, MainActivity.class)
                        .putExtra(SETTINGS, SETTINGS));
                finish();
            }
        });
    }
}
