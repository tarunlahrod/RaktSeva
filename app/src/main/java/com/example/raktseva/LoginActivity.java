package com.example.raktseva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    Button bt_get_otp, bt_verify;
    EditText et_phone, et_otp;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // hooks
        bt_get_otp = findViewById(R.id.bt_get_otp);
        bt_verify = findViewById(R.id.bt_verify);
        et_phone = findViewById(R.id.et_phone);
        et_otp = findViewById(R.id.et_otp);
        progressBar = findViewById(R.id.progressBar);


        // make the verify button and verification code edit text un-clickable
        et_otp.setEnabled(false);
        bt_verify.setEnabled(false);

        progressBar.setVisibility(View.INVISIBLE);

        bt_get_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = et_phone.getText().toString();
                if (phoneNumber.length() == 10) {
                    phoneNumber = "+91" + phoneNumber;
                    sendVerificationCodeToUser(phoneNumber);

                    // make the verify button and verification code edit text clickable
                    et_otp.setEnabled(true);
                    bt_verify.setEnabled(true);

                }
                else {
                    Toast.makeText(LoginActivity.this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeEntered = et_otp.getText().toString();
                if (codeEntered.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                    et_otp.requestFocus();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, codeEntered);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void sendVerificationCodeToUser(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
                codeSent = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//            String code = phoneAuthCredential.getSmsCode();
//            if (code != null) {
//                progressBar.setVisibility(View.VISIBLE);
//                verifyCode(code);
//            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //here we can open a new activity
                            Intent i = new Intent(LoginActivity.this, MainMenuActivity.class);
                            // clear all previous tasks
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                            Toast.makeText(getApplicationContext(),"Login successful", Toast.LENGTH_SHORT).show();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.i("hououin", "onComplete: here");
                        }
                    }
                });
    }
}