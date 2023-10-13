package com.getit.app.persenters.user;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.getit.app.Constants;
import com.getit.app.models.User;
import com.getit.app.persenters.BasePresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UsersPresenter implements BasePresenter {
    private DatabaseReference reference;
    private ValueEventListener listener;
    private UsersCallback callback;

    public UsersPresenter(UsersCallback callback) {
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_NAME_USERS).getRef();
        this.callback = callback;
    }

    public void signup(User user) {
        if (callback != null) {
            callback.onShowLoading();
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getUsername(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = task.getResult().getUser().getUid();
                    user.setId(id);
                    save(user);

                    if (callback != null) {
                        callback.onGetSignupUserComplete();
                    }
                } else {
                    if (callback != null) {
                        callback.onGetSignupUserFail(task.getException().getMessage());
                    }
                }
                if (callback != null) {
                    callback.onHideLoading();
                }
            }
        });
    }

    public void save(User... users) {
        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference node = dp.getReference(Constants.NODE_NAME_USERS);
        for (User user : users) {
            node.child(user.getId()).setValue(user);
        }
        if (callback != null) {
            callback.onSaveUserComplete();
        }
    }

    public void getUsers(int type) {
        callback.onShowLoading();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getType() == type) {
                        users.add(user);
                    }
                }

                if (callback != null) {
                    Collections.sort(users, new Comparator<User>() {
                        @Override
                        public int compare(User o1, User o2) {
                            return o1.getFullName().compareTo(o2.getFullName());
                        }
                    });
                    callback.onGetUsersComplete(users);
                    callback.onHideLoading();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (callback != null) {
                    callback.onFailure("Unable to get message: " + databaseError.getMessage(), null);
                    callback.onHideLoading();
                }
            }
        };
        reference.addListenerForSingleValueEvent(listener);
    }

    public void getUserById(String id) {
        callback.onShowLoading();
        reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (callback != null) {
                    callback.onGetUserComplete(snapshot.getValue(User.class));
                }
                callback.onHideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onHideLoading();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }
}
