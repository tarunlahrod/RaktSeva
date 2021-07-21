package com.example.raktseva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class MainMenuActivity extends AppCompatActivity {

    FirebaseDatabase root;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        String userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void inputDetails(View view) {
        startActivity(new Intent(this, GetUserDetailsActivity.class));
        finish();
    }
}