package com.yukisoft.themarket;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukisoft.themarket.JavaActivities.Home.HomeActivity;
import com.yukisoft.themarket.JavaRepositories.CollectionName;
import com.yukisoft.themarket.JavaRepositories.SignInMethod;
import com.yukisoft.themarket.JavaRepositories.UserModel;

public class MainActivity extends AppCompatActivity {
    public static final String USER_MODEL = "UserModel";
    private int SPLASH_DISPLAY_LENGTH = 3000;
    private UserModel userModel;

    private Button retry;

    FirebaseFirestore ff = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startApp();

        retry = findViewById(R.id.btnRetryStart);
        retry.setVisibility(View.INVISIBLE);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startApp();
                retry.setVisibility(View.INVISIBLE);
            }
        });

        //startApp();
    }

    private void startApp(){
        final ProgressBar startBar = findViewById(R.id.startBar);
        startBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    final String loginId = user.getUid();

                    final DocumentReference userDoc = ff.document(CollectionName.USERS+"/"+loginId);
                    userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            userModel = documentSnapshot.toObject(UserModel.class);

                        }
                    });

                } else {
                    final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
                    fbAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            userModel = new UserModel();
                            userModel.setId(fbAuth.getUid());
                            userModel.setSignInMethod(SignInMethod.ANON);
                            userModel.setRegistered(false);
                            ff.collection(CollectionName.USERS).document(userModel.getId())
                                    .set(userModel);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Start Fail", e.getMessage());
                        }
                    });
                }

                if(userModel != null){
                    String userJSON = (new Gson()).toJson(userModel);
                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.putExtra(USER_MODEL, userJSON);
                    startActivity(i);
                    finish();
                } else {
                    retry.setVisibility(View.VISIBLE);
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}