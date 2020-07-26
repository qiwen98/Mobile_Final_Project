package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;


import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeCapsuleNavigatePage extends AppCompatActivity {

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference TimeCapsuleRef=db.collection("TimeCapsuleBook");

    private  TimeCapsuleAdapter adapter;
    private TimeLineAdapter TimelineAdapter;

    private  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String TAG="TimeCapsuleNavigatePage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_capsule_navigate_page);
        setTitle("                        Received Capsules");

        setUpRecyclerView();

        //setUpTimelineView();



        FloatingActionButton buttonAddTimeCapsule=findViewById(R.id.floating_action_button);
        buttonAddTimeCapsule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=new Intent(TimeCapsuleNavigatePage.this, NewTimeCapsuleActivity.class);
                startActivity(intent);
            }
        });

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set home selected
        bottomNavigationView.setSelectedItemId(R.id.capsule);

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
                        return true;
                    case R.id.contact:
                        startActivity(new Intent(getApplicationContext(),SelectReceiverActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.timeline:
                        startActivity(new Intent(getApplicationContext(),TimeLineActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    private void setUpTimelineView() {

        Query query= TimeCapsuleRef.whereArrayContains("receiver", user.getEmail())
                .orderBy("validTimeStampForOpen", Query.Direction.DESCENDING);

        // Query query= TimeCapsuleRef.orderBy("priority",Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<TimeCapsule> options= new FirestoreRecyclerOptions.Builder<TimeCapsule>()
                .setQuery(query,TimeCapsule.class)
                .build();


        TimelineAdapter=new TimeLineAdapter(options);
        RecyclerView recyclerView=findViewById(R.id.Timeline_view);
        recyclerView.setHasFixedSize(true);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.navigation_page_menu,menu);

       MenuItem searchItem=menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               // adapter.getFilter().filter(newText);
               // adapter.notifyDataSetChanged();
                firebaseTimeCapsuleSearch(newText);
                return true;
            }
        });




        return true;
    }

    private void setUpRecyclerView() {
        Query query= TimeCapsuleRef.whereArrayContains("receiver", user.getEmail())
                .orderBy("validTimeStampForOpen", Query.Direction.DESCENDING);

        // Query query= TimeCapsuleRef.orderBy("priority",Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<TimeCapsule> options= new FirestoreRecyclerOptions.Builder<TimeCapsule>()
                .setQuery(query,TimeCapsule.class)
                .build();


        adapter=new TimeCapsuleAdapter(options);
        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
      // recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT |ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new TimeCapsuleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                TimeCapsule timeCapsule=documentSnapshot.toObject(TimeCapsule.class);
                String id=documentSnapshot.getId();
                String path=documentSnapshot.getReference().getPath();
                String title=timeCapsule.getTitle();
                String VideoURL=timeCapsule.getVideoDownloadURL();
                String CapsuleType=timeCapsule.getCapsuleType();
                Double latitude=timeCapsule.getGoogleMapLocation_latitude();
                Double longtitude=timeCapsule.getGoogleMapLocation_longitude();
                Timestamp timetoopen=timeCapsule.getValidTimeStampForOpen();
                Timestamp CurrentTimeStamp=Timestamp.now();
                String sceneformKey=timeCapsule.getSceneformKey();

                adapter.setOpened(position);



                Timestamp timestamp=timeCapsule.getValidTimeStampForOpen();
                String description=timeCapsule.getDescription();

                Toast.makeText(TimeCapsuleNavigatePage.this,"Position: "+position+" and ID "+id+" and Title "+title+ latitude,Toast.LENGTH_LONG).show();



               if(CapsuleType.equals("ArCapsule"))
               {
                   LatLng Time_Capsule_latling=new LatLng(latitude, longtitude);

                   Intent intent = new Intent(TimeCapsuleNavigatePage.this, Map_Activity.class);
                   intent.putExtra("Location", Time_Capsule_latling);
                   intent.putExtra("AnchorID",sceneformKey );
                   intent.putExtra("Video_URL", VideoURL);

                    startActivity(intent);
                 }

               else  // if time capsule
               {

                   if(CurrentTimeStamp.compareTo(timetoopen)>=0)
                   {
                       Log.d(TAG, "onItemClick: the compare is over");
                       if(!VideoURL.isEmpty())
                       {
                           Intent intent = new Intent(TimeCapsuleNavigatePage.this, PlayerActivity.class);
                           intent.putExtra("Video_URL", VideoURL);
                           startActivity(intent);
                       }
                       else
                       {
                           Toast.makeText(TimeCapsuleNavigatePage.this,"this time capsule is empty",Toast.LENGTH_LONG).show();
                       }

                   }
                   else
                   {
                       Log.d(TAG, "onItemClick: you cannot open yet "+CurrentTimeStamp+"   "+timetoopen);
                       // this is how to create a date

                       Toast.makeText(TimeCapsuleNavigatePage.this,"You cannot open yet",Toast.LENGTH_LONG).show();


                   }


               }



            }
        });



    }

    private void firebaseTimeCapsuleSearch(String searchText) {

        // Toast.makeText(SelectReceiverActivity.this, "Started Search", Toast.LENGTH_LONG).show();



        Log.d(TAG, "firebaseTimeCapsuleSearch: "+searchText);

        Query query = TimeCapsuleRef.orderBy("title")
                .whereArrayContains("receiver", user.getEmail())

                .startAt(searchText).endAt(searchText + "\uf8ff");

        FirestoreRecyclerOptions<TimeCapsule> options= new FirestoreRecyclerOptions.Builder<TimeCapsule>()
                .setQuery(query,TimeCapsule.class)
                .build();

        adapter.updateOptions(options);



    }



    @Override
    protected void onStart() {
         super.onStart();
         adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
        {
            adapter.stopListening();
        }
    }
}