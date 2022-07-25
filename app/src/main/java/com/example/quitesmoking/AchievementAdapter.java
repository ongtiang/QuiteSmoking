package com.example.quitesmoking;


import android.annotation.SuppressLint;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AchievementAdapter extends FirestoreRecyclerAdapter<Goal, AchievementAdapter.parcelHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     */
    public AchievementAdapter(@NonNull FirestoreRecyclerOptions<Goal> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull parcelHolder holder, int position, @NonNull Goal model) {
        holder.date.setText("Goal " + (position+1) + ": " + getDate(model.getGoalDate()));
    }

    @NonNull
    @Override
    public parcelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_list, parent, false);
        return new parcelHolder(view);
    }

    public static class parcelHolder extends RecyclerView.ViewHolder {

        private final TextView date;

        public parcelHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.achievement_list_date);
        }
    }

    private String getDate(long time) {
        return DateFormat.format("dd-MM-yyyy", time).toString();
    }
}
