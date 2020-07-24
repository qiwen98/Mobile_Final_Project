package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ARactivity<placeModel> extends AppCompatActivity {

    private CustomArFragment arFragment;

    private enum AppAnchorState {
        NONE,
        HOSTING,
        HOSTED,

    }

    private AppAnchorState appAnchorState = AppAnchorState.NONE;
    private Anchor anchor;
    private boolean isPlaced;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    int tap_amount=0;

   // SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    LatLng Current_loc_latLng;

    private final String TAG = "ARacivity";
    private  String prevactivity;

    private boolean fromMap=false;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference TimeCapsuleRef=db.collection("TimeCapsuleBook");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arctivity);

        prefs = getSharedPreferences("AnchorID", MODE_PRIVATE);
        editor = prefs.edit();

        // google map part


        client = LocationServices.getFusedLocationProviderClient(this);


            getCurrentLocation();





        //google map part


        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);



        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {


            tap_amount++;
            if (fromMap)
            {
                String VideoURL=getIntent().getStringExtra("Video_URL");
                if(!VideoURL.isEmpty())
                {
                    Intent intent = new Intent(ARactivity.this, PlayerActivity.class);
                    intent.putExtra("Video_URL", VideoURL);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(ARactivity.this,"this time capsule is empty",Toast.LENGTH_LONG).show();
                }
            }

            if(tap_amount==1)
            {


                if(fromMap)
                {

                }
                else
                {
                    anchor=arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                    createModel(anchor);
                    showToast("Drag your Model to change the location");
                    showToast("Click on the plane for confirming the location");
                }

            }




            if(!isPlaced&& tap_amount==2&&!fromMap)
            {


              //  anchor=arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                appAnchorState=AppAnchorState.HOSTING;
                showToast("Uplaoding...");

               // createModel(anchor);
                isPlaced=true;
            }

        });

        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {

            if(appAnchorState!=AppAnchorState.HOSTING)
                return;
            Anchor.CloudAnchorState cloudAnchorState=anchor.getCloudAnchorState();

            if(cloudAnchorState.isError())
            {
                showToast(cloudAnchorState.toString());
            }
            else if ( cloudAnchorState==Anchor.CloudAnchorState.SUCCESS){

                appAnchorState=AppAnchorState.HOSTED;


                String anchorID=anchor.getCloudAnchorId();

                //here changed to firebase id
                editor.putString("AnchorID",anchorID);
                editor.apply();;
               // showToast("Anchor Hosted succesfully. Anchor Id " +anchorID);

                if(!anchorID.isEmpty()&&Current_loc_latLng!=null)
                {
                   // showToast("Anchor Hosted succesfully. Anchor Id " +anchorID);

                    updatetheTimeCapsule(anchorID);
                }

            }


        });

        Button resolve= findViewById(R.id.resolve);
        resolve.setVisibility(View.GONE);

        prevactivity=getIntent().getStringExtra("Activity");
        if(prevactivity.equals("Map_Activity") )
        {
            Log.d(TAG, "onCreate: frommap");
            fromMap=true;
            resolve.setVisibility(View.VISIBLE);

            resolve.setOnClickListener(view->{
                String anchorID= getIntent().getStringExtra("AnchorID");
                showToast("Tap on the ground to open you ARCapsule");
                if(anchorID.equals("null"))
                {
                    showToast("No anchordId found");
                    return;
                }
                Anchor resolvedAnchor=arFragment.getArSceneView().getSession().resolveCloudAnchor(anchorID);
                createModel(resolvedAnchor);
            });

        }


     /*   Button resolve= findViewById(R.id.resolve);
        resolve.setOnClickListener(view->{
            String anchorID= prefs.getString("AnchorID","null");
            if(anchorID.equals("null"))
            {
                showToast("No anchordId found");
                return;
            }
            Anchor resolvedAnchor=arFragment.getArSceneView().getSession().resolveCloudAnchor(anchorID);
            createModel(resolvedAnchor);
        });

      */



    }

    private void updatetheTimeCapsule(String AnchorID) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String TimecapsuleRefID=getIntent().getStringExtra("TimecapsuleRefID");
        ArrayList<String>receiverList= (ArrayList<String>) getIntent().getSerializableExtra("Receivers");;
        //List<String>receiverList = null;

        //showToast(TimecapsuleRefID);
        TimeCapsuleRef.document(TimecapsuleRefID)
                .update("capsuleType","ArCapsule",
                        "sceneformKey",AnchorID,
                        "googleMapLocation_latitude",Current_loc_latLng.latitude,
                        "googleMapLocation_longitude",Current_loc_latLng.longitude,
                        "receiver", receiverList
                        )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        showToast("Your Ar Capsule is ready !!");
                        showToast(TimecapsuleRefID);

                        Intent intent = new Intent(ARactivity.this, TimeCapsuleNavigatePage.class);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });


    }

    private void showToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }

    private void createModel(Anchor anchor) {

        ModelRenderable.builder()
                .setSource(this, Uri.parse("andy.sfb"))
                .build()
                .thenAccept(modelRenderable -> addModelToScene(anchor,modelRenderable))
                .exceptionally(
                        throwable -> {
                            Log.e(TAG, "Unable to load Renderable.", throwable);
                            return null;
                        });
    }

    private void placeModel(Anchor anchor,ModelRenderable modelRenderable)
    {
        AnchorNode anchorNode=new AnchorNode(anchor);
       anchorNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);

    }

    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {

        AnchorNode anchorNode=new AnchorNode(anchor);
        TransformableNode transformableNode=new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }

    private void getCurrentLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
               // locationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        44);
            }

            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "onMapReady: record location gg ");

            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                Log.d(TAG, "onMapReady: record location done ");
                 Current_loc_latLng = new LatLng(location.getLatitude(), location.getLongitude());



            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==44)
        {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getCurrentLocation();
            }
        }
    }
}