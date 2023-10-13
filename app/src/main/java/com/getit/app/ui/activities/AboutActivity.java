package com.getit.app.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.getit.app.databinding.ActivityAboutBinding;

public class AboutActivity extends BaseActivity {
    private ActivityAboutBinding binding;

    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}