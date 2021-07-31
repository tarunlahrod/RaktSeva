package com.example.raktseva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    TextView tv_countdown_timer;
    EditText et_phone, et_otp;
    ProgressBar progressBar;
    CountDownTimer countDownTimer;
    long timeLeftMilliseconds = 60000; // 60 seconds

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
        tv_countdown_timer = findViewById(R.id.tv_countdown_timer);


        // make the verify button and verification code edit text un-clickable
        et_otp.setEnabled(false);
        bt_verify.setEnabled(false);

        // hide the countdown timer and progress bar
        tv_countdown_timer.setVisibility(View.INVISIBLE);
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

                    // disable the get otp button
                    bt_get_otp.setEnabled(false);
                    et_phone.setEnabled(false);

                    // start the countdown timer and make it visible
                    tv_countdown_timer.setVisibility(View.VISIBLE);
                    startCountDownTimer();
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

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeLeftMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeLeftMilliseconds = millisUntilFinished;

                // update the countdown timer
                int minutes = (int) timeLeftMilliseconds / 60000;
                int seconds = (int) (timeLeftMilliseconds % 60000) / 1000;

                String timeLeft = "" + minutes + ":";
                if (seconds < 10)
                    timeLeft += "0";

                timeLeft += seconds;

                tv_countdown_timer.setText("Resend verification code in \n" + timeLeft);
            }

            @Override
            public void onFinish() {
                // once the countdown timer is finished, disable the verify otp button
                // and hide the countdown timer
                bt_verify.setEnabled(false);
                et_otp.setEnabled(false);
                tv_countdown_timer.setVisibility(View.INVISIBLE);

                // enable the phone edit text and get otp button
                et_phone.setEnabled(true);
                bt_get_otp.setEnabled(true);
            }
        }.start();
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