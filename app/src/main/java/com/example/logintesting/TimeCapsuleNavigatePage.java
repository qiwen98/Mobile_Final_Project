package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TimeCapsuleNavigatePage extends AppCompatActivity {

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference TimeCapsuleRef=db.collection("TimeCapsuleBook");
    private TimeCapsuleAdapter adapter;

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

    private void setUpRecyclerView() {
        Query query= TimeCapsuleRef.orderBy("priority",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<TimeCapsule> options= new FirestoreRecyclerOptions.Builder<TimeCapsule>()
                .setQuery(query,TimeCapsule.class)
                .build();

        adapter=new TimeCapsuleAdapter(options);
        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                Toast.makeText(TimeCapsuleNavigatePage.this,"Position: "+position+" and ID "+id+" and Title "+title,Toast.LENGTH_LONG).show();

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