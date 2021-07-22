package com.example.raktseva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class GetUserDetailsActivity extends AppCompatActivity {

    private EditText et_userName;
    private NumberPicker np_age;
    private Spinner spinner_bloodGroup, spinner_state;
    private RadioGroup rg_gender;
    private RadioButton rb_gender;

    ArrayList<String> bloodGroupList, stateList;
    ArrayAdapter<String> bloodGroupSpinnerAdapter, stateSpinnerAdapter;

    private String userName, userState, userBloodGroup, userPhoneNumber, userGender;
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

        // setting up the number picker
        np_age.setMinValue(1);
        np_age.setMaxValue(100);
        np_age.setValue(1);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.get_user_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save_user_details: 
                // perform save operation
                saveUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveUser() {
        if (allDetailsValid()) {

            // proceed to add user
            UserProfile userProfile = new UserProfile(userName, userBloodGroup, userPhoneNumber, userAge, userState, userGender);
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
        rb_gender = findViewById(radioId);
        userGender = rb_gender.getText().toString();

        // get user age
        userAge = np_age.getValue();

        // get user phone number
        userPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        // if all details are valid, return true and proceed to save data
        return true;
    }
}