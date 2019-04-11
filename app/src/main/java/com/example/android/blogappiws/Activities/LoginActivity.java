package com.example.android.blogappiws.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.blogappiws.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button btnLogin;
    private ProgressBar progBar;
    private FirebaseAuth mAuth;

    private Intent homeActivity ;
    private ImageView profilePhoto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.name_login);
        password = findViewById(R.id.password_login);
        progBar = findViewById(R.id.prog_bar_login);
        btnLogin = findViewById(R.id.btn_login);
        profilePhoto =  findViewById(R.id.login_img);
        mAuth = FirebaseAuth.getInstance();
        homeActivity = new Intent(this,com.example.android.blogappiws.Activities.Home.class);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(getApplicationContext(),Home.class);
                startActivity(registerActivity);
                finish();

            }
        });



        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                progBar.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);
                    String name = username.getText().toString();
                    String pass = password.getText().toString();

                    if(pass.isEmpty() || name.isEmpty() )
                    {
                            showMessage("Fill all field before login ");

                    }
                    else
                    {
                        signIn(name,pass);
                    }


            }
        });


    }

    private void signIn(String email, String pass) {

            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {

                        progBar.setVisibility(View.INVISIBLE);
                        btnLogin.setVisibility(View.VISIBLE);
                        updateUI();
                    }
                    else
                    {
                        showMessage("Login failed"+task.getException().toString());
                        progBar.setVisibility(View.INVISIBLE);
                        btnLogin.setVisibility(View.VISIBLE);

                    }
                }
            });

    }

    private void updateUI() {
            startActivity(homeActivity);
            finish();
    }


    private void showMessage(String s) {

        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onStart() {
        super.onStart();
        //treat user
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            //user is already connected  so we need to redirect him to home page
            //updateUI();

        }






    }


}
