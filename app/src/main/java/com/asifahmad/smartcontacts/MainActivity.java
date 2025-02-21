package com.asifahmad.smartcontacts;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;
    private ContactDatabase contactDatabase;
    private static final int ADD_CONTACT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("SmartContacts");
        }

        recyclerView = findViewById(R.id.recycler);
        FloatingActionButton addButton = findViewById(R.id.add_btn);

        contactDatabase = ContactDatabase.getInstance(this);
        contactList = contactDatabase.contactDao().getAllContacts();
        contactAdapter = new ContactAdapter(this, contactList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Add_Contact.class);
            startActivityForResult(intent, ADD_CONTACT_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CONTACT_REQUEST && resultCode == RESULT_OK && data != null) {
            String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            String time = data.getStringExtra("time");
            String imageUri = data.getStringExtra("imageUri");
            String email = data.getStringExtra("email");
            String address = data.getStringExtra("addr");

            Contact contact = new Contact(name, phone, time, imageUri, email, address);
            contactDatabase.contactDao().insert(contact);
            contactList.add(contact);
            contactAdapter.notifyItemInserted(contactList.size() - 1);
        }
    }
}
