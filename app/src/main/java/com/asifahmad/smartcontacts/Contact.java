package com.asifahmad.smartcontacts;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String phone;
    private String time;
    private String imageUri;
    private String email;
    private String address;

    public Contact(String name, String phone, String time, String imageUri, String email, String address) {
        this.name = name;
        this.phone = phone;
        this.time = time;
        this.imageUri = imageUri;
        this.email = email;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getTime() {
        return time;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
}
