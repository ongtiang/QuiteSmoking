package com.example.quitesmoking;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText register_username, register_email, register_age, register_packPerDay, register_pricePerPack, register_password, register_confirm_password;
    private Button buttonChangeQuitDate;
    private ProgressDialog loadingDialog;

    private long quiteDateMill = -1;
    private FirebaseAuth mAuth;
    private String userID;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_username = findViewById(R.id.et_username);
        register_email = findViewById(R.id.et_email);
        register_age = findViewById(R.id.et_age);
        register_packPerDay = findViewById(R.id.et_packPerDay);
        register_pricePerPack = findViewById(R.id.et_pricePerPack);
        register_password = findViewById(R.id.et_password);
        register_confirm_password = findViewById(R.id.et_confirm_password);
        Button btn_register = findViewById(R.id.btn_register);
        buttonChangeQuitDate = findViewById(R.id.buttonChangeQuitDate);
        TextView tv_login = findViewById(R.id.tv_btn_login);
        loadingDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(RegisterActivity.this, HomePage.class);
            startActivity(intent);
            finish();
        }

        tv_login.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        buttonChangeQuitDate.setOnClickListener(v -> {
            DatePicker mDatePickerDialogFragment;
            mDatePickerDialogFragment = new DatePicker();
            mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
        });

        btn_register.setOnClickListener(view -> {
            loadingDialog.setTitle("Create Account");
            loadingDialog.setMessage("Please Wait, While We Are Checking The Credentials.");
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.show();

            final String userName = register_username.getText().toString();
            final String userEmail = register_email.getText().toString();
            final String age = register_age.getText().toString();
            final String packPerDay = register_packPerDay.getText().toString();
            final String pricePerPack = register_pricePerPack.getText().toString();
            final String userPassword = register_password.getText().toString();
            final String userConfirmPassword = register_confirm_password.getText().toString();

            if (TextUtils.isEmpty(userEmail)) {
                loadingDialog.dismiss();
                register_email.setError("E-Mail is Require");
                register_email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                loadingDialog.dismiss();
                register_email.setError("Please enter an valid email");
                register_email.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(userName)) {
                loadingDialog.dismiss();
                register_username.setError("Username is require");
                register_username.requestFocus();
                return;
            }

            if (TextUtils.isDigitsOnly(userName)) {
                loadingDialog.dismiss();
                register_username.setError("Username cannot contain numbers only");
                register_username.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(age)) {
                loadingDialog.dismiss();
                register_age.setError("Age is require");
                register_age.requestFocus();
                return;
            }

            if (Integer.parseInt(age) < 0) {
                loadingDialog.dismiss();
                register_age.setError("Invalid age");
                register_age.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(packPerDay)) {
                loadingDialog.dismiss();
                register_packPerDay.setError("Pack per day is require");
                register_packPerDay.requestFocus();
                return;
            }

            if (Integer.parseInt(packPerDay) < 0) {
                loadingDialog.dismiss();
                register_packPerDay.setError("Invalid pack per day");
                register_packPerDay.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(pricePerPack)) {
                loadingDialog.dismiss();
                register_pricePerPack.setError("Price per pack is require");
                register_pricePerPack.requestFocus();
                return;
            }

            if (quiteDateMill < 0){
                loadingDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Please enter thr quite date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(userPassword)) {
                loadingDialog.dismiss();
                register_password.setError("Password is Require");
                register_password.requestFocus();
                return;
            }

            if (userPassword.length() < 6) {
                loadingDialog.dismiss();
                register_password.setError("Password Length Cannot Less than 6");
                register_password.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(userConfirmPassword)) {
                loadingDialog.dismiss();
                register_confirm_password.setError("Confirm Password is Require");
                register_confirm_password.requestFocus();
                return;
            }

            if (!userConfirmPassword.equals(userPassword)) {
                loadingDialog.dismiss();
                register_confirm_password.setError("Password UnMatch");
                register_confirm_password.requestFocus();
                return;
            }

            mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userID = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("User").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", userEmail);
                    user.put("username", userName);
                    user.put("age", age);
                    user.put("pack", packPerDay);
                    user.put("price", pricePerPack);
                    user.put("quiteDate", quiteDateMill);
                    user.put("userID", userID);
                    documentReference.set(user).addOnSuccessListener(aVoid -> {
                        Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();

                        Intent intent = new Intent(RegisterActivity.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    });
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(RegisterActivity.this, "This login email already exists. Please try a different email address.", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            });
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