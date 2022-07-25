package com.example.quitesmoking;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class HomePage extends AppCompatActivity {
    Long startDate;
    Timer timer = new Timer();
    TextView timerCounter, userName, totalMoneySaved;
    Button buttonAchievement;
    ImageView lungImage, imageViewAdd;
    double totalMoneySave;
    FirebaseAuth mAuth;
    String userID;
    String goalID;
    Long goalEndDate =(long)0;

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        FirebaseApp.initializeApp(this);
        super.onStart();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        timerCounter = findViewById(R.id.tv_timer_home);
        lungImage = findViewById(R.id.iv_lung);
        userName = findViewById(R.id.tv_userName);
        imageViewAdd = findViewById(R.id.imageViewAdd);
        buttonAchievement = findViewById(R.id.buttonAchievement);
        totalMoneySaved = findViewById(R.id.tv_total_money_saved);
        mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        userName.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, ProfileSetting.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        buttonAchievement.setOnClickListener(view -> {
            Intent i = new Intent(HomePage.this, AchievementActivity.class);
            startActivity(i);
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference docRef = db.collection("User").document(userID);
        docRef.addSnapshotListener(
                (snapshot, e) -> {
                    if (e != null) {
                        System.err.println("Listen failed: " + e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {

                        totalMoneySave = Double.parseDouble(Objects.requireNonNull(snapshot.getString("pack"))) * Double.parseDouble(Objects.requireNonNull(snapshot.getString("price")));

                        totalMoneySaved.setText("Total Money Saved: " + totalMoneySave);
                        userName.setText("Welcome, " + snapshot.getString("username"));
                        startDate = snapshot.getLong("quiteDate");

                        Query getGoalDate = db.collection("User").document(userID).collection("Goal")
                                .orderBy("goalDate", Query.Direction.ASCENDING).whereEqualTo("achieved",false).limit(1);
                        getGoalDate.get().addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.getDocuments().isEmpty()){
                                goalID = queryDocumentSnapshots.getDocuments().get(0).getId();
                                goalEndDate = queryDocumentSnapshots.getDocuments().get(0).getLong("goalDate");
                            }
                        });

                    }

                    else {
                        System.out.print("Current data: null");
                    }
                });

        //Schedule the timer to update every second
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        updateCounter();
                    }
                }, 0, 1000);


        imageViewAdd.setOnClickListener(view -> {
            Intent intent = new Intent(HomePage.this, AddGoalActivity.class);
            startActivity(intent);
        });
    }


    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "DefaultLocale"})
    void updateCounter() {
        //Run timer on new thread to prevent UI freeze
        runOnUiThread(() -> {
            Calendar now = Calendar.getInstance();

            long milliseconds2 = now.getTimeInMillis();
            long milliseconds1 = startDate == null ? milliseconds2 : startDate;
            long diff = milliseconds2 - milliseconds1;
            long diffSeconds = diff / 1000;
            long diffMinutes = diff / (60 * 1000);
            long diffHours = diff / (60 * 60 * 1000);
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffHours > 24) {
                diffHours -= diffDays * 24;
            }

            if (diffSeconds > 60) {
                diffSeconds -= diffMinutes * 60;
            }

            if (diffMinutes > 60) {
                diffMinutes -= diffDays * 1440;
            }


            if (diffDays > 730) {
                lungImage.setImageDrawable(getResources().getDrawable(R.drawable.good_lung));
            } else {
                lungImage.setImageDrawable(getResources().getDrawable(R.drawable.bad_lung));

            }


//            if(goalEndDate!=0){
//
//                long milliseconds3 = goalEndDate - milliseconds2;
//
//                if(milliseconds3<=0){
//
//                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                    Map<String, Object> hashMap = new HashMap<>();
//                    hashMap.put("achieved",true);
//                    db.collection("User").document(userID).collection("Goal").document(goalID).update(hashMap).addOnSuccessListener(unused -> {
//                        Intent intent = new Intent(HomePage.this,ScheduleNotification2.class);
//                        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(HomePage.this,0,intent,0);
//                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//                        alarmManager.set(AlarmManager.RTC_WAKEUP,milliseconds2,pendingIntent);
//
//                        goalEndDate = (long)0;
//                    });
//
//                }
//
//            }


            double totalMoneySave2 = totalMoneySave * diffDays;
            totalMoneySaved.setText("Total Money Saved: RM" + String.format("%.2f", totalMoneySave2));
            timerCounter.setText(diffDays + " Days " + diffHours + " Hours " + diffMinutes + " Minutes " + diffSeconds + " Seconds");
        });

    }

}