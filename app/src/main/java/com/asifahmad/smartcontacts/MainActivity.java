package com.asifahmad.smartcontacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ContactAdapter.OnContactDeleteListener {

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
        contactAdapter = new ContactAdapter(this, contactList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Add_Contact.class);
            startActivityForResult(intent, ADD_CONTACT_REQUEST);
        });

        enableSwipeToDelete();

        FloatingActionButton search_button = findViewById(R.id.button_search);
        search_button.setOnClickListener(v -> {
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();

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

            long deleteTime = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000); // 7 din ka default delete time

            Contact contact = new Contact(name, phone, time, imageUri, email, address, deleteTime);
            contactDatabase.contactDao().insert(contact);
            contactList.add(contact);
            contactAdapter.notifyItemInserted(contactList.size() - 1);
        }
    }


    private void enableSwipeToDelete() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleteContact(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void deleteContact(int position) {
        if (position >= 0 && position < contactList.size()) {
            Contact contact = contactList.get(position);
            contactDatabase.contactDao().delete(contact);
            contactList.remove(position);
            contactAdapter.notifyItemRemoved(position);

            contactAdapter.notifyDataSetChanged();

            // ✅ Broadcast send karein
            Intent updateIntent = new Intent("CONTACT_DELETED");
            sendBroadcast(updateIntent);
        }
    }


    @Override
    public void onContactDelete(int position) {
        deleteContact(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.info_icon) {
            // Settings button click action
            Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(contactDeletedReceiver, new IntentFilter("CONTACT_DELETED"), Context.RECEIVER_NOT_EXPORTED);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(contactDeletedReceiver);
    }

    private void loadContacts() {
        contactList = contactDatabase.contactDao().getAllContacts(); //  fresh data from Database
        contactAdapter.updateContacts(contactList); // Adapter update
    }

    private final BroadcastReceiver contactDeletedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MainActivity", "Broadcast received: CONTACT_DELETED");
            loadContacts();
        }
    };


}
