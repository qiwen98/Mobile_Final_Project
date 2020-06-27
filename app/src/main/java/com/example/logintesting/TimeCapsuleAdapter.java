package com.example.logintesting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;

public class TimeCapsuleAdapter  extends FirestoreRecyclerAdapter<TimeCapsule,TimeCapsuleAdapter.TimeCapsuleHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
   private OnItemClickListener listener;
    public TimeCapsuleAdapter(@NonNull FirestoreRecyclerOptions<TimeCapsule> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TimeCapsuleHolder holder, int position, @NonNull TimeCapsule model) {
        holder.textViewTitle.setText(model.getTitle());
        holder.textViewDescription.setText(model.getDescription());
        holder.textViewPriority.setText(String.valueOf(model.getPriority()));
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

        public  TimeCapsuleHolder(View itemView){
            super(itemView);
            textViewTitle=itemView.findViewById(R.id.text_view_title);
            textViewDescription=itemView.findViewById(R.id.text_view_description);
            textViewPriority=itemView.findViewById(R.id.text_view_priority);

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
