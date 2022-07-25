package com.example.quitesmoking;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddGoalActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerPeriod;
    EditText editTextNumber;
    Button buttonAdd;
    ImageView buttonBack;
    Long endDate;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        editTextNumber = findViewById(R.id.editTextNumber);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonBack = findViewById(R.id.buttonBack);
        spinnerPeriod = findViewById(R.id.spinnerPeriod);
        spinnerPeriod.setOnItemSelectedListener(AddGoalActivity.this);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        List<String> categories = new ArrayList<>();
        categories.add("Days");
        categories.add("Months");
        categories.add("Years");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(dataAdapter);

        buttonBack.setOnClickListener(view -> finish());

        buttonAdd.setOnClickListener(view -> {

            String number = editTextNumber.getText().toString().trim();

            if (TextUtils.isEmpty(number)) {
                editTextNumber.setError("Goal number is require");
                editTextNumber.requestFocus();
                return;
            }

            HashMap<String, Object> updates = new HashMap<>();
            updates.put("goalDate", endDate);
            updates.put("achieved", false);
            fStore.collection("User").document(userID).collection("Goal").add(updates);
            Toast.makeText(AddGoalActivity.this, "Goal Added!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int rangeNum = editTextNumber.getText().toString().isEmpty()?1:Integer.parseInt(editTextNumber.getText().toString().trim());
        long millSec;
        switch (adapterView.getItemAtPosition(i).toString()){
            case "Years":
                millSec = (long) rangeNum *8760 *3600000;

                break;
            case "Months":
                millSec = (long) (rangeNum * 2629746000.0);


                break;
            case "Days":
                millSec = rangeNum * 86400000L;
                break;
            default:
                millSec = 0;
        }
        millSec += System.currentTimeMillis();

        endDate = millSec;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}