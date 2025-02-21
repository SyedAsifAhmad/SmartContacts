package com.asifahmad.smartcontacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private final List<Contact> contactList;
    private final Context context;

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getPhone());

        // Load profile image if available
        if (contact.getImageUri() != null && !contact.getImageUri().isEmpty()) {
            holder.profileImage.setImageURI(Uri.parse(contact.getImageUri()));
        } else {
            holder.profileImage.setImageResource(R.drawable.baseline_account_circle_24);
        }

        // Open dialer on phone button click
        holder.phoneButton.setOnClickListener(v -> {
            String phoneNumber = contact.getPhone();
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                context.startActivity(intent);
            }
        });

        // Toggle email and address visibility
        holder.itemView.setOnClickListener(v -> {
            boolean isVisible = holder.email.getVisibility() == View.VISIBLE;
            holder.email.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            holder.address.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            holder.details.setVisibility(isVisible ? View.VISIBLE : View.GONE);

            holder.email.setText(contact.getEmail() != null && !contact.getEmail().isEmpty() ? "Email: " + contact.getEmail() : "Email: -");
            holder.address.setText(contact.getAddress() != null && !contact.getAddress().isEmpty() ? "Address: " + contact.getAddress() : "Address: -");
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, details, email, address;
        ShapeableImageView profileImage;
        ImageButton phoneButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            phone = itemView.findViewById(R.id.item_phone);
            details = itemView.findViewById(R.id.item_time);
            profileImage = itemView.findViewById(R.id.item_profile_image);
            email = itemView.findViewById(R.id.item_email);
            address = itemView.findViewById(R.id.item_address);
            phoneButton = itemView.findViewById(R.id.phone_button);
        }
    }
}
