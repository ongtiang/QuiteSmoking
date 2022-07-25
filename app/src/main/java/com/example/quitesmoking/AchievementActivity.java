package com.example.quitesmoking;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class AchievementActivity extends AppCompatActivity {

    private RecyclerView achievementRecyclerView;
    private AchievementAdapter achievementAdapter;
    private String userID;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        achievementRecyclerView = findViewById(R.id.achievement_recyclerView);

        setUpAchievementRecyclerView();
    }

    private void setUpAchievementRecyclerView() {

        Query query = db.collection("User").document(userID).collection("Goal")
                .whereEqualTo("achieved", true);

        FirestoreRecyclerOptions<Goal> options = new FirestoreRecyclerOptions.Builder<Goal>()
                .setQuery(query, Goal.class)
                .build();

        achievementAdapter = new AchievementAdapter(options);

        achievementRecyclerView.setHasFixedSize(true);
        achievementRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        achievementRecyclerView.setAdapter(achievementAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        achievementAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        achievementAdapter.stopListening();
    }
}