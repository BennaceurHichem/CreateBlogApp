package com.example.android.blogappiws.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    static int PregCode=1;
    static int REQUESCODE=1;
    private ImageView imageUserPhoto;
    Uri pickedImage;
    private EditText userEmail,userPassword, userName, userPassword2;
    private ProgressBar progBar;
    private Button regBtn;

    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

             imageUserPhoto = findViewById(R.id.profile_img);
             userEmail = findViewById(R.id.email_text);
             userPassword = findViewById(R.id.password_text);
             userPassword2 = findViewById(R.id.confirmpass_text);
             userName = findViewById(R.id.name_text);
             progBar = findViewById(R.id.progressBar);
             regBtn = findViewById(R.id.btn_login);
        progBar.setVisibility(View.INVISIBLE);

            mAuth = FirebaseAuth.getInstance();


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regBtn.setVisibility(View.INVISIBLE);
                progBar.setVisibility(View.VISIBLE);

                final String email = userEmail.getText().toString();
                final String name =  userName.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();
                if( email.isEmpty() || password.isEmpty() || password2.isEmpty() || name.isEmpty() || !password.equals(password2))
                {
                    //Something goes wrong ..
                    //Show an error mesage
                    showMessage("You should fill all required fields..");
                    regBtn.setVisibility(View.VISIBLE);
                    progBar.setVisibility(View.INVISIBLE);
                }
                else
                {
                    //if all fields are compketed ==> Start creating a user  Account
                    //Create userAccount method will ccreate an account if the mail is valid
                    CreateUserAccount(name,email,password);




                }






            }
        });


            imageUserPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Build.VERSION.SDK_INT >= 22)
                    {
                        checkAndResquestForPermission();
                    }
                    else
                    {
                        openGallery();
                    }
                }
            });





    }

    private void showMessage(String s) {

        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

    }

    private void CreateUserAccount(final String user, String email, String pass)
    {
        // this method create a userAccount in firebase
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    showMessage("Account Created ");
                    //after creation of the Account we should  update the userInfo like Image ...
                    updateUserInfo(user,pickedImage,mAuth.getCurrentUser());
                }
                else
                {
                    showMessage("account creation failed"+task.getException().getMessage());
                    regBtn.setVisibility(View.VISIBLE);



                }

            }
        });





    }

    private void updateUserInfo(final String user, Uri pickedImage, final FirebaseUser currentUser) {
        // first we need to uplaod userImage to the firebase Storage to use it after in the login

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = storageReference.child(pickedImage.getLastPathSegment());
        imageFilePath.putFile(pickedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // in this case the image uploaded succesfully
                // now we can get our imageUri

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpadate  = new UserProfileChangeRequest.Builder()
                                .setDisplayName(user)
                                .setPhotoUri(uri)
                                .build();


                        currentUser.updateProfile(profileUpadate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //user info updated
                                    showMessage("Register Complete :)");
                                    udpateUI();
                                }







                            }
                        });

                    }
                });
            }
        });



    }

    private void udpateUI() {

            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();


    }

    private void openGallery()
    {
        //this method to open the gallery and pick the user image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);




    }
    private void checkAndResquestForPermission()
    {
        //PAY ATTENTION PERMISSION GRANTED NT DENIED !!
        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            }
            else
            {
                ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PregCode);


            }
        }
        else
        {
           openGallery();
        }

}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null)
        {
            // the user h   as succesfully picked an image
            // we need to save this reference in a uri
            pickedImage =  data.getData();
            imageUserPhoto.setImageURI(pickedImage);

        }

        }


    }

