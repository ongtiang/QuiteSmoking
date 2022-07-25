package com.example.quitesmoking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class ProfileSetting extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText editTextUsername, editTextAge, editTextPack, editTextPrice;
    Button buttonSaveChanges, buttonChangeQuitDate;
    TextView buttonLogout;
    ImageView buttonBack;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String userID;
    Long quiteDateMill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextAge = findViewById(R.id.editTextAge);
        editTextPack = findViewById(R.id.editTextPack);
        editTextPrice = findViewById(R.id.editTextPrice);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonChangeQuitDate = findViewById(R.id.buttonChangeQuitDate);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonBack =  findViewById(R.id.buttonBack);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


        //prefilled original data from firebase
        DocumentReference documentReference = fStore.collection("User").document(userID);
        documentReference.addSnapshotListener(this, (value, error) -> {

            if (value != null) {
                editTextUsername.setText(value.getString("username"));
                editTextAge.setText(value.getString("age"));
                editTextPack.setText(value.getString("pack"));
                editTextPrice.setText(value.getString("price"));
            } else {
                editTextUsername.setText("-");
                editTextAge.setText("-");
                editTextPack.setText("-");
                Toast.makeText(ProfileSetting.this, "No user account detail", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBack.setOnClickListener(view -> finish());

        buttonChangeQuitDate.setOnClickListener(v -> {
            DatePicker mDatePickerDialogFragment;
            mDatePickerDialogFragment = new DatePicker();
            mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
        });

        buttonSaveChanges.setOnClickListener(view -> {
            //save to firebase
            if (editTextUsername.getText().toString().isEmpty()){
                editTextUsername.setError("Username cannot be empty");
                editTextUsername.requestFocus();
            }else if(editTextAge.getText().toString().isEmpty()){
                editTextAge.setError("Age cannot be empty");
                editTextAge.requestFocus();
            }else if(editTextPack.getText().toString().isEmpty()){
                editTextPack.setError("Pack per day cannot be empty");
                editTextPack.requestFocus();
            }else if(editTextPrice.getText().toString().isEmpty()){
                editTextPrice.setError("Price per pack cannot be empty");
                editTextPrice.requestFocus();
            }else {
                HashMap<String, Object> updates = new HashMap<>();
                updates.put("age", editTextAge.getText().toString().trim());
                updates.put("username", editTextUsername.getText().toString().trim());
                updates.put("pack", editTextPack.getText().toString().trim());
                updates.put("price", editTextPrice.getText().toString().trim());
                updates.put("quiteDate", quiteDateMill);
                fStore.collection("User").document(userID).update(updates);
                Toast.makeText(ProfileSetting.this, "Profile Details Updated!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonLogout.setOnClickListener(view -> {
            AlertDialog.Builder alertBox = new AlertDialog.Builder(ProfileSetting.this);
            alertBox.setTitle("SYSTEM");
            alertBox.setMessage("Are you sure you want to logout?");
            alertBox.setCancelable(false);
            alertBox.setPositiveButton("YES", (dialog, which) -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileSetting.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });

            alertBox.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
            alertBox.show();
        });
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance().format(mCalendar.getTime());
        quiteDateMill = mCalendar.getTimeInMillis();
        buttonChangeQuitDate.setText(selectedDate);
    }
}