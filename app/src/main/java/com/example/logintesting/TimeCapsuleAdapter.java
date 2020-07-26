package com.example.logintesting;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.media.CamcorderProfile.get;

public class TimeCapsuleAdapter  extends FirestoreRecyclerAdapter<TimeCapsule,TimeCapsuleAdapter.TimeCapsuleHolder>  {

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
    String TAG="TimeCapsuleAdapter";
    SimpleExoPlayer exoPlayer;


   private OnItemClickListener listener;
    public TimeCapsuleAdapter(@NonNull FirestoreRecyclerOptions<TimeCapsule> options) {
        super(options);
        timeCapsuleFilteredList=options.getSnapshots();
        timeCapsuleList=new ArrayList<>();
        if (options.getOwner() != null) {
            options.getOwner().getLifecycle().addObserver(this);
        }


        Log.d(TAG, "TimeCapsuleAdapter: snapshots:"+options);
    }










    @Override
    protected void onBindViewHolder(@NonNull TimeCapsuleHolder holder, int position, @NonNull TimeCapsule model) {

        //TimeCapsule timeCapsule= pictureList.get(position);

        holder.textViewTitle.setText(model.getTitle());
        holder.textViewDescription.setText(model.getDescription());
        /*holder.textViewPriority.setText(String.valueOf(model.getPriority()));*/
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.getFavouritebyUser()!=null) {
                    if (model.getFavouritebyUser().contains(user.getUid())) {
                        //  holder.textViewTitle.setText("AR");
                        setunFavourite(position);
                    } else {
                        setFavourite(position);
                    }


                }
                else
                {
                    setFavourite(position);
                }

            }
        });

        if(model.getCapsuleType().equals("ArCapsule"))
        {
          //  holder.textViewTitle.setText("AR");
            holder.cardView.setCardBackgroundColor(Color.parseColor("#EE6363"));
        }


        if(model.getOpenedbyUser()!=null)
        {
            if(model.getOpenedbyUser().contains(user.getUid()))
            {
                //  holder.textViewTitle.setText("AR");
                holder.cardView.setCardBackgroundColor(Color.parseColor("#EEDFCC"));
            }
        }


        if(model.getFavouritebyUser()!=null)
        {
            if(model.getFavouritebyUser().contains(user.getUid()))
            {
                //  holder.textViewTitle.setText("AR");
                holder.textViewTitle.setText("FAVOURITE");
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFC1C1"));
            }
        }



        if(model.getImageDownloadURL().isEmpty())
        {
            return;
        }
        else
        {
            Picasso.get().load(model.getImageDownloadURL()).noFade().into(holder.ImageViewPhoto, new Callback() {
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


        /*
        else if (!model.getVideoDownloadURL().isEmpty()&&model.getImageDownloadURL().isEmpty())
        {
            //process the

            Picasso.get().load(model.getImageDownloadURL()).fit().centerCrop().into(holder.ImageViewPhoto, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "image downlaod sucess" );
                }

                @Override
                public void onError(Exception e) {
                    Log.d(TAG, "image downlaod:" +e.getMessage());

                }
            });
        }*/




    }



    @NonNull
    @Override
    public TimeCapsuleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.capsule_item,parent,false);

        return new TimeCapsuleHolder(v);
    }

    public void deleteItem(int position)
    {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public void setOpened(int position)
    {
        getSnapshots().getSnapshot(position).getReference().update("openedbyUser", FieldValue.arrayUnion(user.getUid()));
    }

    public void setFavourite(int position)
    {
        getSnapshots().getSnapshot(position).getReference().update("favouritebyUser", FieldValue.arrayUnion(user.getUid()));
    }

    public void setunFavourite(int position)
    {
        getSnapshots().getSnapshot(position).getReference().update("favouritebyUser", FieldValue.arrayRemove(user.getUid()));
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



    class TimeCapsuleHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewPriority;
        ImageView ImageViewPhoto;
        CardView cardView;
        Button button;

        public  TimeCapsuleHolder(View itemView){
            super(itemView);
            textViewTitle=itemView.findViewById(R.id.text_view_title);
            textViewDescription=itemView.findViewById(R.id.text_view_description);
            //textViewPriority=itemView.findViewById(R.id.text_view_priority);
            ImageViewPhoto=itemView.findViewById(R.id.imageView_photo);
            cardView=itemView.findViewById(R.id.capsule_background);
            button=itemView.findViewById(R.id.favouritebut);



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
        void onItemClick(DocumentSnapshot documentSnapshot,int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
            this.listener=listener;
    }




}
