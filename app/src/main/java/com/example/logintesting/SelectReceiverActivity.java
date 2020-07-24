package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectReceiverActivity extends AppCompatActivity {

    String TAG="SelectReceiverActivity";
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    String TimecapsuleRef_ID;
    private RecyclerView mResultList;
    private FirestoreRecyclerAdapter adapter;
     private List<String> receiverList = new ArrayList<>();


    private CollectionReference UserRef=db.collection("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_receiver);

        setTitle("Select Receiver");
        TimecapsuleRef_ID=getIntent().getStringExtra("TimecapsuleRefID");

        mResultList = (RecyclerView) findViewById(R.id.receiver_recycler_view);

        FloatingActionButton buttoncompletereceiver=findViewById(R.id.floating_receiver_button);
        buttoncompletereceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!receiverList.isEmpty())
                {
                    Intent intent=new Intent(SelectReceiverActivity.this, Choose_ar_or_time_capsule.class);
                    intent.putExtra("TimecapsuleRefID",  TimecapsuleRef_ID);
                    intent.putExtra("Receivers", (Serializable) receiverList);
                    startActivity(intent);
                }
                else
                {
                    showToast("Please select a receiver");
                }


            }
        });


        firebaseinitialise();
    }

    private void firebaseUserSearch(String searchText) {

       // Toast.makeText(SelectReceiverActivity.this, "Started Search", Toast.LENGTH_LONG).show();


        Log.d(TAG, "firebaseUserSearch: "+searchText);

        Query query = UserRef.orderBy("email").startAt(searchText).endAt(searchText + "\uf8ff");

        FirestoreRecyclerOptions<Receiver> options= new FirestoreRecyclerOptions.Builder<Receiver>()
                .setQuery(query,Receiver.class)
                .build();

            adapter.updateOptions(options);



    }

    private void firebaseinitialise() {//String searchText

       // Toast.makeText(SelectReceiverActivity.this, "Started Search", Toast.LENGTH_LONG).show();

        Query query = UserRef.orderBy("email");//.startAt(searchText).endAt(searchText + "\uf8ff");

        FirestoreRecyclerOptions<Receiver> options= new FirestoreRecyclerOptions.Builder<Receiver>()
                .setQuery(query,Receiver.class)
                .build();

        adapter= new FirestoreRecyclerAdapter<Receiver, ReceiverViewHolder>(options) {





            @NonNull
            @Override
            public ReceiverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_item,parent,false);
                return new ReceiverViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull ReceiverViewHolder holder, int position, @NonNull Receiver model) {
                holder.Receiver_name.setText(model.getName());
                holder.Receiver_email.setText(model.getEmail());
                holder.Receiver_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.setSelected(!model.isSelected());
                        holder.Receiver_background.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);
                        if(model.isSelected()==true)
                        {
                            Log.d(TAG, "onClick: savereceiver"+model.getEmail());
                           receiverList.add(model.getEmail());
                        }
                        else
                        {
                            Log.d(TAG, "onClick: removeeceiver"+model.getEmail());
                           receiverList.remove(model.getEmail());
                           // Log.d(TAG, "onClick: removeeceiver"+receiverlist);
                        }
                    }
                });
                Picasso.get().load(model.getImage()).noFade().into(holder.Receiver_image);


            }




        };


        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));
        mResultList.setAdapter(adapter);


       // gotonextpage();

    }








    //view holder class




    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {


        String TAG="SelectReceiverActivity";

        TextView Receiver_name;
        TextView Receiver_email;
        ImageView Receiver_image;
        Context context;
        CardView Receiver_background;

        public ReceiverViewHolder(View itemView) {
            super(itemView);

             Receiver_name = (TextView)itemView.findViewById(R.id.text_view_Name);
            Receiver_email = (TextView) itemView.findViewById(R.id.text_view_email);
            Receiver_image = (ImageView) itemView.findViewById(R.id.Receiver_imageView_photo);
            Receiver_background=itemView.findViewById(R.id.receiver_card_background);



        }



    }

    private void showToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.receiver_page_menu,menu);

        MenuItem searchItem=menu.findItem(R.id.receiver_search);
        SearchView searchView = (SearchView) searchItem.getActionView();



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                  firebaseUserSearch(newText);

                return true;
            }
        });




        return true;
    }




    private void gotonextpage()
    {
        Intent intent = new Intent(SelectReceiverActivity.this, Choose_ar_or_time_capsule.class);
        intent.putExtra("TimecapsuleRefID",TimecapsuleRef_ID);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
        {
            adapter.stopListening();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}