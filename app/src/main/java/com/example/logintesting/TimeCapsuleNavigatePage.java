package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;


import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class TimeCapsuleNavigatePage extends AppCompatActivity {

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference TimeCapsuleRef=db.collection("TimeCapsuleBook");

    private  TimeCapsuleAdapter adapter;
    private  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText search_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_capsule_navigate_page);

        setUpRecyclerView();




        FloatingActionButton buttonAddTimeCapsule=findViewById(R.id.floating_action_button);
        buttonAddTimeCapsule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=new Intent(TimeCapsuleNavigatePage.this, NewTimeCapsuleActivity.class);
                startActivity(intent);
            }
        });
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
                adapter.getFilter().filter(newText);
                return false;
            }
        });




        return true;
    }

    private void setUpRecyclerView() {
        Query query= TimeCapsuleRef.whereArrayContains("receiver", user.getUid())
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
        adapter.notifyDataSetChanged();

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


                Timestamp timestamp=timeCapsule.getValidTimeStampForOpen();
                String description=timeCapsule.getDescription();

                Toast.makeText(TimeCapsuleNavigatePage.this,"Position: "+position+" and ID "+id+" and Title "+title+ latitude,Toast.LENGTH_LONG).show();

               if(CapsuleType.equals("ArCapsule"))
               {
                   LatLng Time_Capsule_latling=new LatLng(latitude, longtitude);

                   Intent intent = new Intent(TimeCapsuleNavigatePage.this, Map_Activity.class);
                   intent.putExtra("Location", Time_Capsule_latling);
                    startActivity(intent);
             }

                //intent.putExtra("Video_URL", VideoURL);

               // Intent intent = new Intent(TimeCapsuleNavigatePage.this, PlayerActivity.class);
                //intent.putExtra("Video_URL", VideoURL);
               // startActivity(intent);

            }
        });



    }



    @Override
    protected void onStart() {
         super.onStart();
         adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}