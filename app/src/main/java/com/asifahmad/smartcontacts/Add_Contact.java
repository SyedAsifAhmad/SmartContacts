package com.asifahmad.smartcontacts;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.util.Calendar;

public class Add_Contact extends AppCompatActivity {

    private CheckBox checkBox;
    private EditText timeEdit, nameEd, phoneEd, emailEd, addressEd;
    private ShapeableImageView profImage;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private Button saveBtn;
    private ImageButton addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        setupToolbar();
        initializeViews();
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Contact");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeViews() {
        checkBox = findViewById(R.id.checkbox_temporary_save);
        timeEdit = findViewById(R.id.time_edit);
        saveBtn = findViewById(R.id.save_btn);
        nameEd = findViewById(R.id.name_edit_text);
        phoneEd = findViewById(R.id.phone_edit_text);
        emailEd = findViewById(R.id.email_edit_text);
        addressEd = findViewById(R.id.address_edit_text);
        profImage = findViewById(R.id.profile_image);
        addBtn = findViewById(R.id.add_Button);
    }

    private void setupListeners() {
        addBtn.setOnClickListener(v -> toggleAdditionalFields());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> toggleTimePicker(isChecked));
        profImage.setOnClickListener(v -> openGallery());
        saveBtn.setOnClickListener(v -> saveContact());
    }

    private void toggleAdditionalFields() {
        View emailLayout = findViewById(R.id.email_text_input);
        View addressLayout = findViewById(R.id.address_text_input);

        if (emailLayout.getVisibility() == View.VISIBLE) {
            emailLayout.setVisibility(View.GONE);
            addressLayout.setVisibility(View.GONE);
        } else {
            emailLayout.setVisibility(View.VISIBLE);
            addressLayout.setVisibility(View.VISIBLE);
        }
    }

    private void toggleTimePicker(boolean isChecked) {
        if (isChecked) {
            timeEdit.setVisibility(View.VISIBLE);
            showTimePicker();
        } else {
            timeEdit.setVisibility(View.GONE);
            timeEdit.setText(""); // Clear time when unchecked
        }
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) ->
                timeEdit.setText(hourOfDay + ":" + minuteOfHour), hour, minute, false).show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imageUri = selectedImageUri;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    profImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveContact() {
        if (validateFields()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", nameEd.getText().toString());
            resultIntent.putExtra("phone", phoneEd.getText().toString());
            resultIntent.putExtra("time", timeEdit.getText().toString());
            resultIntent.putExtra("email", emailEd.getText().toString());
            resultIntent.putExtra("addr", addressEd.getText().toString());

            if (imageUri != null) {
                resultIntent.putExtra("imageUri", imageUri.toString());
            }

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (nameEd.getText().toString().trim().isEmpty()) {
            nameEd.setError("Name is required");
            isValid = false;
        }

        if (phoneEd.getText().toString().trim().isEmpty()) {
            phoneEd.setError("Phone number is required");
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
