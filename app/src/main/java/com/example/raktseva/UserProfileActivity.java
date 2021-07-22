package com.example.raktseva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tv_user_name, tv_user_phone_number, tv_user_blood_group, tv_user_age, tv_user_address;
    private ProgressBar pb_profile_loader;
    private TextInputEditText tied_userName, tied_userPhoneNumber;
    private AutoCompleteTextView actv_userGender, actv_userState;
    FirebaseDatabase root;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle("Profile");

        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_phone_number = findViewById(R.id.tv_user_phone_number);
        tv_user_blood_group = findViewById(R.id.tv_user_blood_group);
        tv_user_age = findViewById(R.id.tv_user_age);
        pb_profile_loader = findViewById(R.id.pb_profile_loader);
        tied_userName = findViewById(R.id.tied_userName);
        actv_userGender = findViewById(R.id.actv_userGender);
        tied_userPhoneNumber = findViewById(R.id.tied_userPhoneNumber);
        actv_userState = findViewById(R.id.actv_userState);

        pb_profile_loader.setVisibility(View.VISIBLE);

        String userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        root = FirebaseDatabase.getInstance();
        ref = root.getReference().child("users").child(userPhoneNumber);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName, userAge, userBloodGroup, userGender, userState;
                    userName = snapshot.child("name").getValue().toString();
                    userAge = snapshot.child("age").getValue().toString() + " Yrs";
                    userBloodGroup = snapshot.child("bloodGroup").getValue().toString();
                    userGender = snapshot.child("gender").getValue().toString();
                    userState = snapshot.child("state").getValue().toString();

                    tv_user_name.setText(userName);
                    tv_user_phone_number.setText(userPhoneNumber);
                    tv_user_age.setText(userAge);
                    tv_user_blood_group.setText(userBloodGroup);
                    tied_userName.setText(userName);
                    actv_userGender.setText(userGender);
                    tied_userPhoneNumber.setText(userPhoneNumber);
                    actv_userState.setText(userState);

                    pb_profile_loader.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}