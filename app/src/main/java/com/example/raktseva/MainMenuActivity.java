package com.example.raktseva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

    FirebaseDatabase root;
    DatabaseReference ref;

    List<UserProfile> usersList = new ArrayList<UserProfile>();

    private RecyclerView recyclerView;
    private RecyclerView.ViewHolder viewHolder;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        String userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        recyclerView = findViewById(R.id.rv_usersList);

        root = FirebaseDatabase.getInstance();

        // check if the "users" child node exists
        ref = root.getReference().child("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    root.getReference().child("users").child("null user").setValue("null user value");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // check if the current user already exists in the database
        ref = root.getReference().child("users").child(userPhoneNumber);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // create new user
                    startActivity(new Intent(MainMenuActivity.this, GetUserDetailsActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addListenerForSingleValueEvent(eventListener);


        // building the userList
        ref = root.getReference().child("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // for handling the null value user which is added by default in the above code
                    if (ds.getKey().equals("null user")) {
                        continue;
                    }
                    // if the user is a donor then add him to the usersList
                    if (Boolean.parseBoolean(ds.child("donor").getValue().toString())) {
                        String name = ds.child("name").getValue().toString();
                        String bloodGroup = ds.child("bloodGroup").getValue().toString();
                        String gender = ds.child("gender").getValue().toString();
                        int age = Integer.parseInt(ds.child("age").getValue().toString());
                        String state = ds.child("state").getValue().toString();
                        String phoneNumber = ds.child("phoneNumber").getValue().toString();
                        boolean isDonor = Boolean.parseBoolean(ds.child("donor").getValue().toString());

                        usersList.add(new UserProfile(name, bloodGroup, phoneNumber, age, state, gender, isDonor));
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Setting up the recycler view
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(usersList, MainMenuActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_sign_out:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainMenuActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainMenuActivity.this,MainActivity.class);
                startActivity(i);
                finish();
                break;

            case R.id.menu_item_about:
                startActivity(new Intent(MainMenuActivity.this, AboutActivity.class));
                break;

            case R.id.menu_item_user_profile:
                startActivity(new Intent(MainMenuActivity.this, UserProfileActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}