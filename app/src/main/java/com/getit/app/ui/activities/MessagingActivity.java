package com.getit.app.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.getit.app.R;
import com.getit.app.databinding.ActivityMessagingBinding;
import com.getit.app.Constants;
import com.getit.app.ui.adptres.MessagesAdapter;
import com.getit.app.utilities.helpers.LocaleHelper;
import com.getit.app.utilities.helpers.StorageHelper;
import com.getit.app.models.ChatId;
import com.getit.app.models.Message;
import com.getit.app.models.User;
import com.getit.app.persenters.firebase.FirebaseCallback;
import com.getit.app.persenters.firebase.FirebasePresenter;
import com.getit.app.persenters.messaging.MessagingCallback;
import com.getit.app.persenters.messaging.MessagingPresenter;
import com.getit.app.persenters.user.UsersCallback;
import com.getit.app.persenters.user.UsersPresenter;

import java.util.ArrayList;

public class MessagingActivity extends BaseActivity implements MessagingCallback, UsersCallback, FirebaseCallback, TextView.OnEditorActionListener {
    private ActivityMessagingBinding binding;
    private MessagesAdapter adapter;
    private MessagingPresenter presenter;
    private FirebasePresenter firebasePresenter;
    private UsersPresenter usersPresenter;
    private ChatId id;
    private User currentUser;
    private boolean isWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        binding = ActivityMessagingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        id = getIntent().getParcelableExtra(Constants.ARG_OBJECT);

        init();
    }

    private void init() {
        binding.iconSendContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        binding.editTextMessage.setOnEditorActionListener(this);

        currentUser = StorageHelper.getCurrentUser();

        adapter = new MessagesAdapter(new ArrayList<>());
        binding.recyclerViewChat.setAdapter(adapter);

        presenter = new MessagingPresenter();
        firebasePresenter = new FirebasePresenter(this);
        usersPresenter = new UsersPresenter(this);
        User otherUser = id.getOther(currentUser);
        usersPresenter.getUserById(otherUser.getId());
        load();

        binding.refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });
    }

    private void load() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        presenter.getMessages(id, this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
            return true;
        }
        return false;
    }

    private void sendMessage() {
        if (!isWait) {
            String text = binding.editTextMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                Message message = new Message();
                message.setSenderId(currentUser.getId());
                message.setSenderName(currentUser.getFullName());
                message.setReceiveName(id.getOther(currentUser).getFullName());
                message.setMessage(text);

                isWait = true;
                presenter.sendMessage(id, message, this);
            } else {
                Toast.makeText(this, R.string.str_please_type_message_firstly, Toast.LENGTH_SHORT).show();
                binding.editTextMessage.requestFocus();
            }
        } else {
            Toast.makeText(this, R.string.str_please_wait, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSendMessageSuccess() {
        isWait = false;
        binding.editTextMessage.setText("");
        Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();

        firebasePresenter.getToken(id.getOther(currentUser));
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetMessageSuccess(Message message) {
        binding.loadingContainer.loadingContainer.setVisibility(View.GONE);
        binding.empty.setVisibility(View.GONE);
        adapter.add(message);
        binding.recyclerViewChat.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onEmptyMessaging() {
        binding.loadingContainer.loadingContainer.setVisibility(View.GONE);
        binding.empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetMessageFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
    public void onSaveTokenComplete() {
    }

    @Override
    public void onGetTokenComplete(String token) {
        firebasePresenter.send(token);
    }

    @Override
    public void onGetUserComplete(User user) {
        id.setUser1(user);
    }
}