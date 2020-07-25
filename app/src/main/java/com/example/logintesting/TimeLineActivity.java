package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.baoyachi.stepview.VerticalStepView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class TimeLineActivity extends AppCompatActivity {

    private TimeLineAdapter TimelineAdapter;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference TimeCapsuleRef=db.collection("TimeCapsuleBook");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line_view_activity);
        

      setUpTimelineView();

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set home selected
        bottomNavigationView.setSelectedItemId(R.id.timeline);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),Menu_ZMY.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.capsule:
                        startActivity(new Intent(getApplicationContext(),TimeCapsuleNavigatePage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.contact:
                        startActivity(new Intent(getApplicationContext(),SelectReceiverActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.timeline:
                        return true;
                }
                return false;
            }
        });

    }

    private void setUpTimelineView() {

        Query query=  TimeCapsuleRef.whereEqualTo("sender",user.getEmail())

                .orderBy("validTimeStampForOpen", Query.Direction.DESCENDING);




       // Query query= TimeCapsuleRef.whereArrayContains("receiver", user.getEmail())
        //        .orderBy("validTimeStampForOpen", Query.Direction.DESCENDING);
      //  TimeCapsuleRef.whereEqualTo("sender",user.getEmail());

        FirestoreRecyclerOptions<TimeCapsule> options= new FirestoreRecyclerOptions.Builder<TimeCapsule>()
                .setQuery(query,TimeCapsule.class)
                .build();


        TimelineAdapter=new TimeLineAdapter(options);
        RecyclerView recyclerView=findViewById(R.id.Timeline_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(TimelineAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TimelineAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(TimelineAdapter!=null)
        {
            TimelineAdapter.stopListening();
        }
    }
}