package com.example.arlambee;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MainAdapter extends FirebaseRecyclerAdapter<MainModel2,MainAdapter.ViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MainAdapter(@NonNull FirebaseRecyclerOptions<MainModel2> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull MainModel2 model) {
        holder.time.setText(model.getTime());
        holder.description.setText(model.getDescription());


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        TextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = (itemView).findViewById(R.id.time);
            description = (itemView).findViewById(R.id.description);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Show the Bottom Sheet
                    showBottomSheet();
                    return true;
                }
            });
        }

        private void showBottomSheet() {
            final MainModel2 item = getItem(getAdapterPosition());
            if (item != null) {
                DeleteBottomSheetFragment bottomSheet = DeleteBottomSheetFragment.newInstance(item.getId());
                bottomSheet.show(((AppCompatActivity) itemView.getContext()).getSupportFragmentManager(), bottomSheet.getTag());
                bottomSheet.setOnDeleteClickListener(new DeleteBottomSheetFragment.OnDeleteClickListener() {
                    @Override
                    public void onDeleteClick(String itemId) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // Remove item from RecyclerView
                            getSnapshots().getSnapshot(position).getRef().removeValue();
                        }
                    }

                    @Override
                    public void onDeleteClick() {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // Remove item from RecyclerView
                            getSnapshots().getSnapshot(position).getRef().removeValue();
                        }
                    }
                });
            }
        }
    }
}
