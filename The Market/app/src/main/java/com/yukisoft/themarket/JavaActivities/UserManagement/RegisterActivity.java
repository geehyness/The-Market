package com.yukisoft.themarket.JavaActivities.UserManagement;

import android.content.Intent;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukisoft.themarket.JavaActivities.Home.HomeActivity;
import com.yukisoft.themarket.JavaRepositories.CollectionName;
import com.yukisoft.themarket.JavaRepositories.UserModel;
import com.yukisoft.themarket.MainActivity;
import com.yukisoft.themarket.R;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private UserModel unregisteredUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent i = getIntent();
        unregisteredUser = (new Gson()).fromJson(i.getStringExtra(MainActivity.USER_MODEL), UserModel.class);

        Button register = findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        TextView login = findViewById(R.id.tvLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)
                        .putExtra(MainActivity.USER_MODEL, (new Gson()).toJson(unregisteredUser)));
                finish();
            }
        });
    }

    private void register(){
        EditText studentNumber = findViewById(R.id.txtRegStudentNum);
        EditText studentName = findViewById(R.id.txtRegUsername);
        EditText studentPassword = findViewById(R.id.txtRegPassword);

        final String sNum = studentNumber.getText().toString();
        final String sName = studentName.getText().toString();
        String sPass = studentPassword.getText().toString();
        String email = "";

        if (TextUtils.isEmpty(sNum)){
            studentNumber.setError("Student number cannot be empty.");
            return;
        } else {
            String pattern = "[2](0|1)\\d{7}";

            if (Pattern.matches(pattern, sNum)){
                email = sNum + "@student.uj.ac.za";
            } else {
                studentNumber.setError("Student number is invalid.");
                return;
            }
        }

        if (TextUtils.isEmpty(sName)){
            studentName.setError("Name cannot be empty.");
            return;
        }

        if (TextUtils.isEmpty(sPass) || sPass.length() < 8){
            studentPassword.setError("Password should be 8 characters long.");
            return;
        }

        final String sEmail = email;

        final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        fbAuth.createUserWithEmailAndPassword(sEmail, sPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                final UserModel newUser = new UserModel(fbAuth.getUid(), sEmail, sNum, sName, -1, 0, true);

                DocumentReference userRef = FirebaseFirestore.getInstance()
                        .document(CollectionName.USERS + "/" + newUser.getId());
                userRef.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class)
                            .putExtra(MainActivity.USER_MODEL, (new Gson()).toJson(newUser)));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, HomeActivity.class)
                .putExtra(MainActivity.USER_MODEL, (new Gson()).toJson(unregisteredUser)));
        finish();
    }
}
