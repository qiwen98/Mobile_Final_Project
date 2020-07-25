package com.example.logintesting;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeLineAdapter extends FirestoreRecyclerAdapter<TimeCapsule, TimeLineAdapter.TimeLineHolder>  {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private  Context context;
    private  List<TimeCapsule>timeCapsuleList,timeCapsuleFilteredList;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

   // private final ObservableSnapshotArray<TimeCapsule> mSnapshots;
    private boolean isFiltarable;
    String TAG="TimeLineAdapter";
    SimpleExoPlayer exoPlayer;


   private OnItemClickListener listener;
    public TimeLineAdapter(@NonNull FirestoreRecyclerOptions<TimeCapsule> options) {
        super(options);
        timeCapsuleFilteredList=options.getSnapshots();
        timeCapsuleList=new ArrayList<>();
        if (options.getOwner() != null) {
            options.getOwner().getLifecycle().addObserver(this);
        }


        Log.d(TAG, "TimeCapsuleAdapter: snapshots:"+options);
    }










    @Override
    protected void onBindViewHolder(@NonNull TimeLineHolder holder, int position, @NonNull TimeCapsule model) {

        //TimeCapsule timeCapsule= pictureList.get(position);

        holder.timeline_text_time.setText(converttodate(model.getValidTimeStampForOpen()));
        holder.timeline_item_description.setText(setinfo(model.getReceiver(),model.getCapsuleType(),model.getValidTimeStampForOpen()));

        if(model.getImageDownloadURL().isEmpty())
        {
            Picasso.get().load(R.drawable.cashiconpng2).noFade().into(holder.timeline_photo, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "image downlaod sucess" );
                }

                @Override
                public void onError(Exception e) {
                    Log.d(TAG, "image downlaod:" +e.getMessage());

                }
            });
        }
        else
        {
            Picasso.get().load(model.getImageDownloadURL()).noFade().into(holder.timeline_photo, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "image downlaod sucess" );
                }

                @Override
                public void onError(Exception e) {
                    Log.d(TAG, "image downlaod:" +e.getMessage());

                }
            });
        }



    }

    private String setinfo(List receivers,String Capsuletype, Timestamp timestamp) {

        Date date=timestamp.toDate();
        DateFormat.getDateInstance(DateFormat.SHORT).format(date);
        String stringdate=  DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);


        if(receivers.contains(user.getEmail()))
        {
            return "you have received a "+Capsuletype+" by yourself on "+stringdate;
        }

        return "you have sent a "+Capsuletype+" to "+receivers.toString()+" on "+stringdate;
    }

    private String converttodate(Timestamp timestamp) {



        Date date=timestamp.toDate();
        DateFormat.getDateInstance(DateFormat.SHORT).format(date);
        String stringdate=  DateFormat.getDateInstance(DateFormat.SHORT).format(date);
        Log.d(TAG, "converttodate: "+  stringdate);
        return stringdate;

    }


    @NonNull
    @Override
    public TimeLineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_item,parent,false);

        return new TimeLineHolder(v);
    }


    @Override
    public int getItemViewType(int position) {
       if(getSnapshots().getSnapshot(position).toObject(TimeCapsule.class).getCapsuleType().equals("ArCapsule"))
        {
            Log.d(TAG, "getItemViewType: ARcapsule");
        }
        return super.getItemViewType(position);
    }





    @Override
    public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {

        super.onChildChanged(type, snapshot, newIndex, oldIndex);
    }



    class TimeLineHolder extends RecyclerView.ViewHolder {
        TextView timeline_item_description;
        TextView timeline_text_time;

        ImageView timeline_photo;


        public  TimeLineHolder(View itemView){
            super(itemView);
            timeline_item_description=itemView.findViewById(R.id.post_item_description);
            timeline_text_time=itemView.findViewById(R.id.post_text_time);

           timeline_photo=itemView.findViewById(R.id.post_itemphoto);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION &&listener!=null)
                    {
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);

                    }
                }
            });


    }

    }






    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
            this.listener=listener;
    }




}
