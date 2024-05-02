package com.example.arlambee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MainAdapter2 extends FirebaseRecyclerAdapter<MainModel,MainAdapter2.myViewHolder> {

    private Context context;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MainAdapter2(@NonNull FirebaseRecyclerOptions<MainModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull MainModel model) {
        holder.date.setText(model.getDate());
        holder.reminderText.setText(model.getReminderText());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.btnDelete.setVisibility(View.VISIBLE); // Show delete button
                return true;
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference itemRef = getRef(position);
                itemRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Item removed successfully from Firebase
                                // Notify the adapter to remove it from the RecyclerView
                                notifyItemRemoved(position);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error occurred while removing item from Firebase
                                Toast.makeText(context, "Failed to delete reminder", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_remainders,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        TextView date,reminderText;
        Button btnDelete;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            date =(TextView)itemView.findViewById(R.id.date_select);
            reminderText = (TextView)itemView.findViewById(R.id.reminderText);
            btnDelete = (Button)itemView.findViewById(R.id.btnDelete);
        }
    }

}
