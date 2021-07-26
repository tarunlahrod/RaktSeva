package com.example.raktseva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tv_user_name, tv_user_phone_number, tv_user_blood_group, tv_user_age;
    private ProgressBar pb_profile_loader;
    private TextInputEditText tied_userName, tied_userPhoneNumber, tied_userGender, tied_userState;
    private Button btn_updateProfile;

    FirebaseDatabase root;
    DatabaseReference ref;
    String userName, userAge, userBloodGroup, userGender, userState, userPhoneNumber;
    Boolean myProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_phone_number = findViewById(R.id.tv_user_phone_number);
        tv_user_blood_group = findViewById(R.id.tv_user_blood_group);
        tv_user_age = findViewById(R.id.tv_user_age);
        pb_profile_loader = findViewById(R.id.pb_profile_loader);
        tied_userName = findViewById(R.id.tied_userName);
        tied_userGender = findViewById(R.id.tied_userGender);
        tied_userPhoneNumber = findViewById(R.id.tied_userPhoneNumber);
        tied_userState = findViewById(R.id.tied_userState);
        btn_updateProfile = findViewById(R.id.btn_updateProfile);

        pb_profile_loader.setVisibility(View.VISIBLE);

        // getting the intent to decide what to do, view a someone else's profile or view my own profile
        Intent intent = getIntent();
        userPhoneNumber = intent.getStringExtra("userPhoneNumber");

        if (userPhoneNumber != null) {
            // Someone else's profile, do nothing since we already have the userPhoneNumber
            // from the received intent. Rest work will be done after the if-else statement
            myProfile = false;

            // change the text on the button to "Call"
            btn_updateProfile.setText("Call");
        }
        else {
            // My own profile
            userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            myProfile = true;
        }

        // Button to update personal details (of my profile, obviously)
        btn_updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myProfile) {
                    // move to edit profile activity
                    Intent intent = new Intent(UserProfileActivity.this, GetUserDetailsActivity.class);
                    intent.putExtra("flag", 1);
                    startActivity(intent);
                }
                else {
                    // make a phone call
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    Log.i("check", "onClick: " + userPhoneNumber);
                    callIntent.setData(Uri.parse("tel:" + userPhoneNumber));
                    startActivity(callIntent);
                }
            }
        });

        root = FirebaseDatabase.getInstance();
        ref = root.getReference().child("users").child(userPhoneNumber);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
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
                    tied_userGender.setText(userGender);
                    tied_userPhoneNumber.setText(userPhoneNumber);
                    tied_userState.setText(userState);

                    // making the TextInputEditText behave as TextView
                    makeEditTextBehaveAsTextView(tied_userName);
                    makeEditTextBehaveAsTextView(tied_userGender);
                    makeEditTextBehaveAsTextView(tied_userState);
                    makeEditTextBehaveAsTextView(tied_userPhoneNumber);

                    pb_profile_loader.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeEditTextBehaveAsTextView(TextInputEditText textInputEditText) {
        textInputEditText.setClickable(false);
        textInputEditText.setFocusable(false);
        textInputEditText.setTextIsSelectable(false);
        textInputEditText.setCursorVisible(false);
    }
}