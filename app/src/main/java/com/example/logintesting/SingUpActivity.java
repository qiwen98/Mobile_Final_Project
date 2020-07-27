package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class SingUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    private EditText mEmail, mPassword,_Password_Retype;
    private Button btnSignUp;
    ImageView profileImage;
    private StorageReference mStorageRef;
    private DocumentReference documentReference;
    Uri imageUri;
   StorageTask  mUplaodTask;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);


        mAuth = FirebaseAuth.getInstance();
        mEmail = (EditText) findViewById(R.id.EmailAddressReg);
        mPassword = (EditText) findViewById(R.id.PasswordReg);
        _Password_Retype= (EditText) findViewById(R.id.PasswordRetype);
        btnSignUp = (Button) findViewById(R.id.SignUp_But);
        profileImage=(ImageView) findViewById(R.id.PhotoView);
        mStorageRef = FirebaseStorage.getInstance().getReference();


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();
                String pass_Retype=_Password_Retype.getText().toString();
                if(!email.equals("") && !pass.equals("")&& !pass_Retype.equals("")){
                    //mAuth.signInWithEmailAndPassword(email,pass);
                    createAccount(email,pass);
                }else{

                    Toast.makeText(SingUpActivity.this, "You didn't fill in all the fields.",
                            Toast.LENGTH_SHORT).show();
                }

                //if( pass!=pass_Retype)
                {
                   // Toast.makeText(SingUpActivity.this, "Please ensure password retype are same !",
                     //       Toast.LENGTH_SHORT).show();
                }

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

    }

    private void createAccount(final String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            final String userID=user.getUid();


                            //store user info into database
                            documentReference=db.collection("users").document(userID);
                            Map<String,Object> userObj=new HashMap<>();
                            userObj.put("email",email);
                            userObj.put("name","usersrandom");


                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName("Jane Q. User")
                                    .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });

                            documentReference.set(userObj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "cUser Profile is created for "+userID);
                                }
                            });

                           //upload the profile to firebase if user upload an image
                            if(imageUri!=null)
                            {
                                uploadImageToFireBase(imageUri);
                            }
                            else
                            {
                                Uri path = Uri.parse("android.resource://com.example.logintesting/drawable/cashiconpng1");
                                uploadImageToFireBase(path);
                                
                            }

                            if(mUplaodTask!=null&&mUplaodTask.isInProgress())
                            {
                                Toast.makeText(SingUpActivity.this,"Upload in Progress " ,
                                        Toast.LENGTH_SHORT).show();
                            }



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            Toast.makeText(SingUpActivity.this,"User Authentication Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1000)
        {
            if(resultCode== Activity.RESULT_OK)
            {
               imageUri=data.getData();
                profileImage.setImageURI(imageUri);

            }
        }
    }

    void uploadImageToFireBase(Uri ImageUri)
    {
        FirebaseUser user = mAuth.getCurrentUser();
        final String userID=user.getUid();
        final StorageReference riversRef = mStorageRef.child("users/"+userID+"/image/profile.jpg");

        mUplaodTask=riversRef.putFile(ImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //mProgressBar.setProgress(0);
                            }
                        },500);

                        Toast.makeText(SingUpActivity.this,"Image Uploaded",
                                Toast.LENGTH_SHORT).show();

                        Upload upload=new Upload("profile.jpg",taskSnapshot.getUploadSessionUri().toString());
                        documentReference
                                .update("ProfileImage",upload)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });

                        // go to first page
                        Intent intent=new Intent(SingUpActivity.this, MainPageActivity.class);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(SingUpActivity.this,"Image upload Failed: " + exception.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                //mProgressBar.setProgress((int)progress);

            }
        });
    }

}