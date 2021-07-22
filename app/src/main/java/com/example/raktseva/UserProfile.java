package com.example.raktseva;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfile {
    private String name;
    private String bloodGroup;
    private String phoneNumber;
    private int age;
    private String state;
    private String gender;
    public UserProfile() {

    }

    public UserProfile(String name, String bloodGroup, String phoneNumber, int age, String state, String gender) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.state = state;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
