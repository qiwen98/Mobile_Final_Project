package com.example.logintesting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.List;

import static android.media.CamcorderProfile.get;

public class TimeCapsuleAdapter  extends FirestoreRecyclerAdapter<TimeCapsule,TimeCapsuleAdapter.TimeCapsuleHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private  Context context;
    private  List<TimeCapsule>timeCapsuleList;
    String TAG="TimeCapsuleAdapter";
    SimpleExoPlayer exoPlayer;

   private OnItemClickListener listener;
    public TimeCapsuleAdapter(@NonNull FirestoreRecyclerOptions<TimeCapsule> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TimeCapsuleHolder holder, int position, @NonNull TimeCapsule model) {

        //TimeCapsule timeCapsule= pictureList.get(position);

        holder.textViewTitle.setText(model.getTitle());
        holder.textViewDescription.setText(model.getDescription());
        holder.textViewPriority.setText(String.valueOf(model.getPriority()));


        if(model.getImageDownloadURL().isEmpty())
        {
            return;
        }
        else
        {
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

    class TimeCapsuleHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewPriority;
        ImageView ImageViewPhoto;

        public  TimeCapsuleHolder(View itemView){
            super(itemView);
            textViewTitle=itemView.findViewById(R.id.text_view_title);
            textViewDescription=itemView.findViewById(R.id.text_view_description);
            textViewPriority=itemView.findViewById(R.id.text_view_priority);
            ImageViewPhoto=itemView.findViewById(R.id.imageView_photo);


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
