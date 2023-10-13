package com.getit.app.ui.adptres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.getit.app.R;
import com.getit.app.databinding.ItemUserBinding;
import com.getit.app.models.User;
import com.getit.app.utilities.helpers.StorageHelper;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<User> users;
    private OnItemClickListener listener;

    // data is passed into the constructor
    public UsersAdapter(List<User> users, OnItemClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);

        holder.binding.name.setText(user.getFullName());
        holder.binding.username.setText(user.getUsername());
        holder.binding.phone.setText(user.getPhone());
        holder.binding.btnDelete.setVisibility(StorageHelper.getCurrentUser() != null ? StorageHelper.getCurrentUser().getId().equalsIgnoreCase(user.getId()) ? View.GONE : View.VISIBLE : View.GONE);
        if (user.getAddress() != null) {
            holder.binding.address.setText(user.getAddress());
        } else {
            holder.binding.address.setText("---");
        }
        Glide.with(holder.itemView.getContext()).load(user.getImageProfile()).placeholder(R.drawable.ic_profile).into(holder.binding.image);
    }

    private int getSize(String id) {
        return users.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return users.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemUserBinding.bind(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemViewListener(getAdapterPosition());
                }
            });
            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onDeleteItemViewListener(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemViewListener(int position);

        void onDeleteItemViewListener(int position);
    }
}