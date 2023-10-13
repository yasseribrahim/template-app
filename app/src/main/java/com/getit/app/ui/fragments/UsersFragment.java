package com.getit.app.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.getit.app.R;
import com.getit.app.databinding.FragmentUsersBinding;
import com.getit.app.Constants;
import com.getit.app.models.User;
import com.getit.app.persenters.user.UsersCallback;
import com.getit.app.persenters.user.UsersPresenter;
import com.getit.app.ui.adptres.UsersAdapter;
import com.getit.app.utilities.helpers.StorageHelper;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment implements UsersCallback, UsersAdapter.OnItemClickListener {
    private FragmentUsersBinding binding;
    private UsersPresenter presenter;
    private UsersAdapter usersAdapter;
    private List<User> users, searchedUsers;
    private User currentUser;
    private int userType;

    public static UsersFragment newInstance(int userType) {
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_ID, userType);
        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater);

        currentUser = StorageHelper.getCurrentUser();
        presenter = new UsersPresenter(this);
        userType = getArguments().getInt(Constants.ARG_ID);

        binding.refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        binding.btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(binding.textSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        users = new ArrayList<>();
        searchedUsers = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersAdapter = new UsersAdapter(searchedUsers, this);
        binding.recyclerView.setAdapter(usersAdapter);

        return binding.getRoot();
    }

    private void load() {
        presenter.getUsers(userType);
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    @Override
    public void onGetUsersComplete(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
        search(binding.textSearch.getText().toString());
    }

    @Override
    public void onShowLoading() {
        binding.refreshLayout.setRefreshing(true);
    }

    @Override
    public void onHideLoading() {
        binding.refreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void search(String searchedText) {
        searchedUsers.clear();
        if (!searchedText.isEmpty()) {
            for (User user : users) {
                if (isMatched(user, searchedText)) {
                    searchedUsers.add(user);
                }
            }
        } else {
            searchedUsers.addAll(users);
        }

        refresh();
    }

    private boolean isMatched(User user, String text) {
        String searchedText = text.toLowerCase();
        boolean result = user.getFullName().toLowerCase().contains(searchedText) ||
                (user.getAddress() != null && user.getAddress().toLowerCase().contains(searchedText)) ||
                (user.getPhone() != null && user.getPhone().toLowerCase().contains(searchedText)) ||
                (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchedText));
        return result;
    }

    private void refresh() {
        binding.message.setVisibility(View.GONE);
        if (searchedUsers.isEmpty()) {
            binding.message.setVisibility(View.VISIBLE);
        }

        usersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemViewListener(int position) {
        User user = searchedUsers.get(position);
    }

    @Override
    public void onDeleteItemViewListener(int position) {
        if (position >= 0 && position < searchedUsers.size()) {
            User child = searchedUsers.get(position);
            int index = users.indexOf(child);
            searchedUsers.remove(position);
            if (index >= 0 && index < users.size()) {
                users.remove(position);
            }

            StorageHelper.setCurrentUser(currentUser);
            presenter.save(currentUser, child);
            onGetDeleteUserComplete(position);
        }
    }

    @Override
    public void onGetDeleteUserComplete(int position) {
        Toast.makeText(getContext(), R.string.str_message_delete_successfully, Toast.LENGTH_LONG).show();
        usersAdapter.notifyItemRemoved(position);
    }
}