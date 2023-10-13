package com.getit.app.ui.adptres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.getit.app.R;
import com.getit.app.databinding.ItemChatBinding;
import com.getit.app.databinding.ItemChatOtherBinding;
import com.getit.app.utilities.helpers.StorageHelper;
import com.getit.app.models.Message;
import com.getit.app.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private final List<Message> messages;
    private final User user;

    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
        user = StorageHelper.getCurrentUser();
    }

    public void add(Message messages) {
        this.messages.add(messages);
        notifyItemInserted(this.messages.size() - 1);
    }

    public void clear() {
        this.messages.clear();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat, parent, false);
                holder = new ChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                holder = new ChatOtherViewHolder(viewChatOther);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isOutComing(position)) {
            bindChatViewHolder((ChatViewHolder) holder, position);
        } else {
            bindChatOtherViewHolder((ChatOtherViewHolder) holder, position);
        }
    }

    private void bindChatViewHolder(ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        String alphabet = message.getSenderName() != null && !message.getSenderName().isEmpty() ? message.getSenderName().substring(0, 1) : "N/A";
        holder.binding.textViewChatMessage.setText(message.getMessage());
        holder.binding.time.setText(getTime(message.getTimestamp()).toUpperCase());
        holder.binding.textViewUserAlphabet.setText(alphabet);
        if (!isPreviousOutComing(position)) {
            holder.binding.textViewUserAlphabet.setText(alphabet);
            holder.binding.textViewUserAlphabet.setVisibility(View.VISIBLE);
        } else {
            holder.binding.textViewUserAlphabet.setVisibility(View.INVISIBLE);
        }
    }

    private void bindChatOtherViewHolder(ChatOtherViewHolder holder, int position) {
        Message message = messages.get(position);
        String alphabet = message.getSenderName() != null && !message.getSenderName().isEmpty() ? message.getSenderName().substring(0, 1) : "N/A";
        holder.binding.textViewChatMessage.setText(message.getMessage());
        holder.binding.time.setText(getTime(message.getTimestamp()).toUpperCase());
        if (!isPreviousInComing(position)) {
            holder.binding.textViewUserAlphabet.setText(alphabet);
            holder.binding.textViewUserAlphabet.setVisibility(View.VISIBLE);
        } else {
            holder.binding.textViewUserAlphabet.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isOutComing(int position) {
        return messages.get(position).getSenderId().equalsIgnoreCase(user.getId());
    }

    private boolean isPreviousOutComing(int position) {
        return position > 0 && isOutComing(position - 1);
    }

    private boolean isPreviousInComing(int position) {
        return position > 0 && !isOutComing(position - 1);
    }

    public String getTime(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return dateFormat.format(new Date(milliseconds));
    }

    @Override
    public int getItemCount() {
        if (messages != null) {
            return messages.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isOutComing(position)) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        ItemChatBinding binding;

        public ChatViewHolder(View view) {
            super(view);
            binding = ItemChatBinding.bind(view);
        }
    }

    public class ChatOtherViewHolder extends RecyclerView.ViewHolder {
        ItemChatOtherBinding binding;

        public ChatOtherViewHolder(View view) {
            super(view);
            binding = ItemChatOtherBinding.bind(view);
        }
    }
}

