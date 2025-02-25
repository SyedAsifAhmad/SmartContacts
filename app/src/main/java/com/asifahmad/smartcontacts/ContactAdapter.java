package com.asifahmad.smartcontacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
    private final OnContactDeleteListener deleteListener;

    public ContactAdapter(Context context, List<Contact> contactList, OnContactDeleteListener deleteListener) {
        this.context = context;
        this.contactList = contactList;
        this.deleteListener = deleteListener;
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

        String time = contact.getTime();
        if (time != null && !time.trim().isEmpty() && !time.equalsIgnoreCase("null") && !time.equalsIgnoreCase("Time")) {
            holder.timer_icon.setVisibility(View.VISIBLE);
        } else {
            holder.timer_icon.setVisibility(View.GONE);
        }

        if (holder.timer_icon == null) {
            Log.e("DEBUG", "timer_icon reference is null");
        }

    }
    public void updateContacts(List<Contact> newContacts) {
        this.contactList.clear();
        this.contactList.addAll(newContacts);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < contactList.size()) {
            contactList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, details, email, address;
        ShapeableImageView profileImage;
        ImageButton phoneButton, timer_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            phone = itemView.findViewById(R.id.item_phone);
            details = itemView.findViewById(R.id.item_show_details);
            profileImage = itemView.findViewById(R.id.item_profile_image);
            email = itemView.findViewById(R.id.item_email);
            address = itemView.findViewById(R.id.item_address);
            phoneButton = itemView.findViewById(R.id.phone_button);
            timer_icon = itemView.findViewById(R.id.timer_icon);
        }
    }

    // Interface for deleting a contact
    public interface OnContactDeleteListener {
        void onContactDelete(int position);
    }
}
