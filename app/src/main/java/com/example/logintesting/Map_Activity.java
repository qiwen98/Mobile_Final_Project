package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class Map_Activity extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    private GoogleMap mMap;

    private LatLng timecapsulelocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_);

        timecapsulelocation=getIntent().getExtras().getParcelable("Location");


        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        client = LocationServices.getFusedLocationProviderClient(this);


            getCurrentLocation();

    }

    private void getCurrentLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
               // locationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        44);
            }
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location!=null)
                {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {

                            LatLng Current_loc_latLng = new LatLng(location.getLatitude(), location.getLongitude());
                           // LatLng Time_Capsule_latling=new LatLng(location.getLatitude(), location.getLongitude());

                           // LatLng Time_Capsule_latling=new LatLng(2.936, 101.7091);

                            float Distance[]=new float[10];
                            Location.distanceBetween(Current_loc_latLng.latitude,Current_loc_latLng.longitude,timecapsulelocation.latitude,timecapsulelocation.longitude,Distance);

                            MarkerOptions current_loc_marker=new MarkerOptions().position(Current_loc_latLng)
                                    .title("I am Here").snippet("You current location");

                            float hue = 120;  //(Range: 0 to 360)
                            MarkerOptions time_capsule_loc_marker=new MarkerOptions().position(timecapsulelocation)
                                    .title("Time Capsule Here").icon(BitmapDescriptorFactory.defaultMarker(hue)).snippet("Distance="+Distance[0]);


                            mMap=googleMap;
                           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Current_loc_latLng,10));

                            mMap.addMarker(current_loc_marker);
                           mMap.addMarker(time_capsule_loc_marker);

                            if(Distance[0]<10)
                            {
                                Toast.makeText(Map_Activity.this, "You have found a time capsule !",
                                        Toast.LENGTH_SHORT).show();

                                String sceneformkey=getIntent().getStringExtra("AnchorID");
                                String VideoURL=getIntent().getStringExtra("Video_URL");

                               Intent intent = new Intent(Map_Activity.this, ARactivity.class);
                                intent.putExtra("Activity", "Map_Activity");
                                intent.putExtra("AnchorID", sceneformkey);
                                intent.putExtra("Video_URL", VideoURL);

                                Handler mHandler = new Handler();
                                mHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                    }

                                }, 4000L);


                            }

                        }
                    });
                }

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