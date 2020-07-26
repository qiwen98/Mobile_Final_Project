package com.example.logintesting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.List;

public class NewTimeCapsuleActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;
    private ImageButton ButtonForImage;
    private ImageButton ButtonForVideo;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DocumentReference documentReference;
    String TAG="NewTimeCapsuleActivity";
    private CircularProgressBar mProgressBar;
    StorageTask<UploadTask.TaskSnapshot> mUplaodTask;
    Uri ImageUrl;
    Uri VideoUrl;
    MediaController mediaController;
    String CapsuleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_time_capsule);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("             Add Time Capsule");
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        editTextTitle=findViewById(R.id.edit_text_title);
        editTextDescription=findViewById(R.id.edit_text_description);
       /*numberPickerPriority=findViewById(R.id.number_picker_priority);*/
       ButtonForImage=findViewById(R.id.ImageUpload);
        ButtonForVideo=findViewById(R.id.VideoUpload);
        mediaController=new MediaController(this);
        mProgressBar =  findViewById(R.id.circularProgressBar);
        // or with gradient
        mProgressBar.setProgressBarColorStart(Color.parseColor("#b6bbd8")) ;
        mProgressBar.setProgressBarColorEnd(Color.WHITE);
        mProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);

       /*numberPickerPriority.setMinValue(1);
       numberPickerPriority.setMaxValue(10);*/

        ButtonForImage.setOnClickListener(this);
        ButtonForVideo.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_time_capsule_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.save_note:
               if(mUplaodTask!=null&&mUplaodTask.isInProgress())
               {
                   Toast.makeText(NewTimeCapsuleActivity.this,"Upload in Progress " ,
                           Toast.LENGTH_SHORT).show();
               }
               else
               {
                   saveNote();
               }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
       
    }

    private void saveNote() {
        String title=editTextTitle.getText().toString();
        String description=editTextDescription.getText().toString();
        int priority=1;
        String ImageDownloadURL;
        String VideoDownloadURL;
        String CapsuleType="";
        List<String> Receiver=null;
        List <String> favouritebyUser=null;
        String SceneformKey="";
        Double GoogleMapLocation_latitude =null;
        Double  GoogleMapLocation_longitude = null;
        Timestamp ValidTimeStampForOpen=Timestamp.now();
        List <String> OpenedbyUser=null;
        String Sender=mAuth.getCurrentUser().getEmail();


        if(ImageUrl!=null)
        {
             ImageDownloadURL=ImageUrl.toString();
        }
        else
        {
            ImageDownloadURL="";
        }
        if(VideoUrl!=null)
        {
           VideoDownloadURL=VideoUrl.toString();
        }
        else
        {
            VideoDownloadURL="";
        }


        //upload the image to firebase storage and get Url

        if(title.trim().isEmpty()|| description.trim().isEmpty())
        {
            Toast.makeText(this,"Please insert title and desciption",Toast.LENGTH_LONG).show();
            return;
        }


        CollectionReference timecapsuleRef= FirebaseFirestore.getInstance()
                .collection("TimeCapsuleBook");
        timecapsuleRef.add(new TimeCapsule(title,description,priority,ImageDownloadURL,VideoDownloadURL,CapsuleType,Sender,Receiver,favouritebyUser,OpenedbyUser,SceneformKey,GoogleMapLocation_latitude,GoogleMapLocation_longitude,ValidTimeStampForOpen))
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    CapsuleID=documentReference.getId();


                Intent intent = new Intent(NewTimeCapsuleActivity.this, SelectReceiverActivity.class);
                intent.putExtra("TimecapsuleRefID",CapsuleID);
                startActivity(intent);
                 finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
       // Toast.makeText(this,"Note added",Toast.LENGTH_LONG).show();


        //gotonextpage




    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.ImageUpload:

                UploadImageToStorage();
                break;
            case R.id.VideoUpload:
                UploadVideoToStorage();
                break;
           
        }
    }

    private void UploadVideoToStorage() {

        Intent openVideoGalleryIntent=new Intent();
        openVideoGalleryIntent.setType("video/*");
        openVideoGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(openVideoGalleryIntent,2000);
    }

    private void UploadImageToStorage() {

        Intent openGalleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1000)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                Uri imageUri=data.getData();
                if(imageUri!=null)
                {
                   // Toast.makeText(this,"Clicked",Toast.LENGTH_LONG).show();
                    uploadImageToFireBase(imageUri);
                }


            }
        }
        if(requestCode==2000)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                Uri VideoUri=data.getData();
                if(VideoUri!=null)
                {
                    Toast.makeText(this,"Video Uplaoding",Toast.LENGTH_LONG).show();
                    uploadVideoToFireBase(VideoUri);
                }


            }
        }
    }

    private void uploadImageToFireBase(Uri imageUri) {

        FirebaseUser user = mAuth.getCurrentUser();
        final String userID=user.getUid();
        final StorageReference ImageRef = mStorageRef.child("users/"+userID+"/TimeCapsuleimage/"+imageUri.getLastPathSegment());

        mUplaodTask=ImageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar. setVisibility(View.VISIBLE);
                                mProgressBar.setProgressWithAnimation(0);
                            }
                        },10);

                        Toast.makeText(NewTimeCapsuleActivity.this,"Image Uploaded",
                                Toast.LENGTH_SHORT).show();

                        Task urlTask = mUplaodTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return ImageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                     ImageUrl= task.getResult();

                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(NewTimeCapsuleActivity.this,"Image upload Failed: " + exception.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        mProgressBar.setProgressWithAnimation((int)progress);


                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        mProgressBar. setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void uploadVideoToFireBase(Uri VideoUri) {

        FirebaseUser user = mAuth.getCurrentUser();
        final String userID=user.getUid();
        final StorageReference VideoRef = mStorageRef.child("users/"+userID+"/TimeCapsulevideo/"+VideoUri.getLastPathSegment());

        mUplaodTask=VideoRef.putFile(VideoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar. setVisibility(View.VISIBLE);
                                mProgressBar.setProgressWithAnimation(0);
                            }
                        },10);

                        Toast.makeText(NewTimeCapsuleActivity.this,"video Uploaded",
                                Toast.LENGTH_SHORT).show();

                        Task urlTask = mUplaodTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return VideoRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    VideoUrl= task.getResult();


                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(NewTimeCapsuleActivity.this,"Image upload Failed: " + exception.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        mProgressBar.setProgressWithAnimation((int)progress);


                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        mProgressBar. setVisibility(View.INVISIBLE);
                    }
                });
    }




}