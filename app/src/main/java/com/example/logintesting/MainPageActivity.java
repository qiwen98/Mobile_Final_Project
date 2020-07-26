package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainPageActivity extends AppCompatActivity {

    private Button Resend_code_but, Signout_but;
    TextView Verify_Msg_TextView,NameView,EmailView;
    ImageView profileImage;
    private StorageReference mStorageRef;
    Button TimeCapsule_but,Timeline_but,Menu_but;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private static final String TAG = "MainPageActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page_activity);

       Resend_code_but = (Button) findViewById(R.id.Resend_code_but);
       Verify_Msg_TextView= (TextView)findViewById(R.id.Verify_Msg_TextView);
        EmailView=(TextView)findViewById(R.id.EmailView);
        NameView=(TextView)findViewById(R.id.NameView);
        profileImage=(ImageView) findViewById(R.id.PhotoView);
        TimeCapsule_but = (Button) findViewById(R.id.TimeCapsule);
        Timeline_but = (Button) findViewById(R.id.Timeline);
        Menu_but = (Button) findViewById(R.id.btn_navi);
        Signout_but=(Button) findViewById(R.id.sign_out);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();


        getUserProfile();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                    // gotofirstpage();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                    gotologinpage();
                }
                // ...
            }
        };



        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

        TimeCapsule_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              gototimecapsulepage();
            }
        });

        Timeline_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gototimelinepage();
            }
        });

        Menu_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotomenupage();
            }
        });

        Signout_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signout();
            }
        });
    }

    private void signout() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
      //  toastMessage("Signing Out...");
    }
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1000)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                Uri imageUri=data.getData();
                Picasso.get().load(imageUri).into(profileImage);
            }
        }
    }

    public void getUserProfile() {
        // [START get_user_profile]
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

          NameView.setText(name);
          EmailView.setText(email);

            Picasso.get().load(photoUrl).into(profileImage);
            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            if(!emailVerified)
            {
               Resend_code_but.setVisibility(View.VISIBLE);
                Verify_Msg_TextView.setVisibility(View.VISIBLE);

                Resend_code_but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainPageActivity.this, "Verification email has been sent",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Email not sent");
                            }
                        });
                    }
                });
            }
            //get user profile pic
            if(user.getIdToken(false).getResult().getSignInProvider().equals("password"))
            {
                StorageReference profileRef=mStorageRef.child("users/"+user.getUid()+"/image/profile.jpg");

                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                        Log.d(TAG, "Set Profile Image Sucessful");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Set Profile Image UnSucessful"+user.getUid());
                    }
                });

            }






            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
            Log.d(TAG, "Get user data sucess");
            Toast.makeText(MainPageActivity.this, "Get user data sucess",
                    Toast.LENGTH_SHORT).show();
        }

        // [END get_user_profile]
    }

    private void gototimecapsulepage()
    {
        Intent intent=new Intent(MainPageActivity.this, TimeCapsuleNavigatePage.class);
        startActivity(intent);
    }

    private void gototimelinepage() {
        Intent intent=new Intent(MainPageActivity.this, TimeLineActivity.class);
        startActivity(intent);
    }

    private void gotomenupage() {
        Intent intent=new Intent(MainPageActivity.this, Menu_ZMY.class);
        startActivity(intent);
    }

    private void gotologinpage() {
        Intent intent=new Intent(MainPageActivity.this,MainActivity.class);
        startActivity(intent);
    }



    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


}