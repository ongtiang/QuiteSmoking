package com.example.quitesmoking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quitesmoking.HomePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText userLoginEmail, userLoginPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userLoginEmail = findViewById(R.id.et_email);
        userLoginPassword = findViewById(R.id.et_password);
        Button btn_login = findViewById(R.id.btn_login);
        TextView tv_forget_password = findViewById(R.id.tv_forgetPassword);
        TextView tv_create_new_account = findViewById(R.id.tv_createAccount);
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new ProgressDialog(this);

        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, HomePage.class);
            startActivity(intent);
            finish();
        }

        tv_forget_password.setOnClickListener(view -> {
            final EditText resetPassword_email = new EditText(LoginActivity.this);

            AlertDialog.Builder forgetPasswordDialog = new AlertDialog.Builder(LoginActivity.this);
            forgetPasswordDialog.setTitle("Forget Password?");
            forgetPasswordDialog.setMessage("Enter email to send reset password link");
            resetPassword_email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            forgetPasswordDialog.setView(resetPassword_email);

            forgetPasswordDialog.setPositiveButton("Yes", (dialog, which) -> {
                String userEmail = resetPassword_email.getText().toString().trim();

                if (userEmail.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                } else {
                    sendPasswordToEmail(userEmail);
                }
            });

            forgetPasswordDialog.setNegativeButton("No", (dialog, which) -> {
                //Close
            });

            forgetPasswordDialog.create().show();
        });

        btn_login.setOnClickListener(view -> {
            loadingDialog.setTitle("Login");
            loadingDialog.setMessage("Please Wait, While We Are Checking The Credentials.");
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.show();

            final String userEmail = userLoginEmail.getText().toString();
            final String userPassword = userLoginPassword.getText().toString();

            if (TextUtils.isEmpty(userEmail)) {
                loadingDialog.dismiss();
                userLoginEmail.setError("Please enter your email");
                userLoginEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                loadingDialog.dismiss();
                userLoginEmail.setError("Please enter an valid email");
                userLoginEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(userPassword)) {
                loadingDialog.dismiss();
                userLoginPassword.setError("Password cannot be empty");
                userLoginPassword.requestFocus();
                return;
            }

            if (userPassword.length() < 6) {
                loadingDialog.dismiss();
                userLoginPassword.setError("Password need to be more than 6 character");
                userLoginPassword.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    loadingDialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, HomePage.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();

                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    loadingDialog.dismiss();
                    userLoginPassword.setError("Wrong password");
                    userLoginPassword.requestFocus();

                } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                    loadingDialog.dismiss();
                    userLoginEmail.setError("Invalid user");
                    userLoginEmail.requestFocus();
                }
            });
        });

        tv_create_new_account.setOnClickListener(view -> {
//            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
        });
    }

    public void sendPasswordToEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Password reset email has sent", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error " + e, Toast.LENGTH_SHORT).show());
    }
}