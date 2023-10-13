package com.getit.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.getit.app.R;
import com.getit.app.databinding.ActivityRegistrationBinding;
import com.getit.app.Constants;
import com.getit.app.models.User;
import com.getit.app.persenters.user.UsersCallback;
import com.getit.app.persenters.user.UsersPresenter;
import com.getit.app.utilities.helpers.LocaleHelper;

public class RegistrationActivity extends BaseActivity implements UsersCallback {
    private ActivityRegistrationBinding binding;

    private UsersPresenter presenter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = new UsersPresenter(this);

        user = new User();
        int userType = Constants.USER_TYPE_STUDENT;
        user.setType(userType);

        binding.btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = binding.password.getText().toString().trim();
                String confirmPassword = binding.rePassword.getText().toString().trim();
                String username = binding.username.getText().toString().trim();
                String phone = binding.phone.getText().toString().trim();
                String address = binding.address.getText().toString().trim();
                String fullName = binding.fullName.getText().toString().trim();

                if (username.isEmpty()) {
                    binding.username.setError(getString(R.string.str_username_invalid));
                    binding.username.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    binding.username.setError(getString(R.string.str_username_invalid));
                    binding.username.requestFocus();
                    return;
                }
                if (user.getId() == null) {
                    if (password.isEmpty() || password.length() < 6) {
                        binding.password.setError(getString(R.string.str_password_length_invalid));
                        binding.password.requestFocus();
                        return;
                    }
                    if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
                        binding.password.setError(getString(R.string.str_password_confirm_invalid));
                        binding.password.requestFocus();
                        return;
                    }
                }
                if (fullName.isEmpty()) {
                    binding.phone.setError(getString(R.string.str_full_name_invalid));
                    binding.phone.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    binding.fullName.setError(getString(R.string.str_phone_invalid));
                    binding.fullName.requestFocus();
                    return;
                }
                if (address.isEmpty()) {
                    binding.address.setError(getString(R.string.str_address_invalid));
                    binding.address.requestFocus();
                    return;
                }

                user.setUsername(username);
                user.setPassword(password);
                user.setFullName(fullName);
                user.setPhone(phone);
                user.setAddress(address);

                presenter.signup(user);
            }
        });
    }

    @Override
    public void onGetSignupUserComplete() {
        Toast.makeText(this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onGetSignupUserFail(String message) {
        Toast.makeText(this, getString(R.string.str_signup_fail, message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, getString(R.string.str_signup_fail, message), Toast.LENGTH_LONG).show();
    }
}