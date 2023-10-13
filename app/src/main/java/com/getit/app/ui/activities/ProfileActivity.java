package com.getit.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.getit.app.databinding.ActivityProfileBinding;
import com.getit.app.Constants;
import com.getit.app.models.User;
import com.getit.app.utilities.UIUtils;
import com.getit.app.utilities.helpers.StorageHelper;
import com.getit.app.R;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private User user;
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ValueEventListener valueEventListenerUser;
    private String userPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = StorageHelper.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        userPath = Constants.NODE_NAME_USERS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = database.getReference(userPath);

        valueEventListenerUser = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                bind();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userReference.addValueEventListener(valueEventListenerUser);

        binding.btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class));
            }
        });

        bind();
    }

    private void bind() {
        binding.username.setText("@" + user.getUsername());
        binding.type.setText(UIUtils.getAccountType(user.getType()));
        binding.nameTextView.setText(user.getUsername());
        binding.emailTextView.setText(user.getUsername());
        binding.phoneTextView.setText(user.getPhone());
        binding.addressTextView.setText(user.getAddress());

        Glide.with(ProfileActivity.this).load(user.getImageProfile()).placeholder(R.drawable.ic_account_circle).into(binding.profileImage);
    }
}