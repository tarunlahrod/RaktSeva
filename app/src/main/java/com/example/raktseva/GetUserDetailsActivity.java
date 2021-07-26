package com.example.raktseva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class GetUserDetailsActivity extends AppCompatActivity {

    private EditText et_userName;
    private NumberPicker np_age;
    private Spinner spinner_bloodGroup, spinner_state;
    private RadioGroup rg_gender;
    private RadioButton rb_gender;
    private SwitchCompat switch_donor;
    private Button bt_save_details;

    ArrayList<String> bloodGroupList, stateList;
    ArrayAdapter<String> bloodGroupSpinnerAdapter, stateSpinnerAdapter;

    private String userName, userState, userBloodGroup, userPhoneNumber, userGender;
    private boolean userDonor;
    private int userAge;

    FirebaseAuth mAuth;
    FirebaseDatabase root;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_details);
        getSupportActionBar().setTitle("My Info");

        mAuth = FirebaseAuth.getInstance();

        et_userName = findViewById(R.id.et_userName);
        spinner_state = findViewById(R.id.spinner_state);
        np_age = findViewById(R.id.np_age);
        spinner_bloodGroup = findViewById(R.id.spinner_bloodGroup);
        rg_gender = findViewById(R.id.rg_gender);
        bt_save_details = findViewById(R.id.bt_save_details);
        switch_donor = findViewById(R.id.switch_donor);

        // setting up the number picker
        np_age.setMinValue(1);
        np_age.setMaxValue(100);
        np_age.setValue(1);

        // setting up the donor switch
        switch_donor.setChecked(false);

        // Adding blood groups to the spinner array
        bloodGroupList = new ArrayList<>(Arrays.asList("A+", "O+", "B+", "AB+", "A-", "O-", "B-", "AB-"));

        // initializing the adapter for spinner and setting it to the spinner
        bloodGroupSpinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, bloodGroupList);
        spinner_bloodGroup.setAdapter(bloodGroupSpinnerAdapter);

        // on item selected in spinner
        spinner_bloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // get selected blood group from the spinner
                userBloodGroup = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Adding state to the spinner array
        stateList = new ArrayList<>(Arrays.asList("Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttarakhand", "Uttar Pradesh", "West Bengal", "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli", "Daman and Diu", "Delhi", "Lakshadweep", "Puducherry"));

        // initializing the adapter for spinner and setting it up
        stateSpinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, stateList);
        spinner_state.setAdapter(stateSpinnerAdapter);

        // on item selected in spinner
        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // get selected blood group from the spinner
                userState = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Catching the intent for updating the activity
        Intent intent = getIntent();
        int flag = intent.getIntExtra("flag", -1);

        if (flag == 1) {
            // setting up the spinners to have the values of existing users

            ArrayAdapter<String> adapterBlood = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, bloodGroupList);
            adapterBlood.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_bloodGroup.setAdapter(adapterBlood);

            ArrayAdapter<String> adapterState = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, stateList);
            adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_state.setAdapter(adapterState);

            // edit details for existing user
            userPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
            root = FirebaseDatabase.getInstance();
            reference = root.getReference("users").child(userPhoneNumber);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userName = snapshot.child("name").getValue().toString();
                    userAge = Integer.parseInt(snapshot.child("age").getValue().toString());
                    userBloodGroup = snapshot.child("bloodGroup").getValue().toString();
                    userGender = snapshot.child("gender").getValue().toString();
                    userState = snapshot.child("state").getValue().toString();

                    // setting the name
                    et_userName.setText(userName);

                    // setting the age
                    np_age.setValue(userAge);

                    // setting up the spinners (contd.)
                    String compareValueBlood = userBloodGroup;
                    if (compareValueBlood != null) {
                        int spinnerPosition = adapterBlood.getPosition(compareValueBlood);
                        spinner_bloodGroup.setSelection(spinnerPosition);
                    };
                    String compareValueState = userState;
                    if (compareValueState != null) {
                        int spinnerPosition = adapterState.getPosition(compareValueState);
                        spinner_state.setSelection(spinnerPosition);
                    };

                    switch (userGender) {
                        case "Male": findViewById(R.id.rb_male).setEnabled(true); break;
                        case "Female": findViewById(R.id.rb_female).setSelected(true); break;
                        case "Non Binary": findViewById(R.id.rb_nb).setSelected(true); break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            // enter details for new user

        }

        // saving the user details on click save button
        bt_save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });
    }

    // removing the menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.get_user_activity_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_item_save_user_details:
//                // perform save operation
//                saveUser();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void saveUser() {
        if (allDetailsValid()) {
            // proceed to add user
            UserProfile userProfile = new UserProfile(userName, userBloodGroup, userPhoneNumber, userAge, userState, userGender, userDonor);
            root = FirebaseDatabase.getInstance();
            reference = root.getReference("users");
            reference.child(userPhoneNumber).setValue(userProfile);
            startActivity(new Intent(GetUserDetailsActivity.this, MainMenuActivity.class));
            finish();
        }
    }

    private boolean allDetailsValid() {

        // get user name
        userName = et_userName.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
            return false;
        }

        // get gender
        int radioId = rg_gender.getCheckedRadioButtonId();
        if (radioId == -1) {
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            rg_gender.setFocusable(true);
            rg_gender.requestFocus();
            return false;
        }
        rb_gender = findViewById(radioId);
        userGender = rb_gender.getText().toString();

        // get user age
        userAge = np_age.getValue();

        // get donor or not
        userDonor = switch_donor.isChecked();

        // get user phone number
        userPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        // if all details are valid, return true and proceed to save data
        return true;
    }
}