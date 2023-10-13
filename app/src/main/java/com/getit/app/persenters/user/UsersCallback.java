package com.getit.app.persenters.user;

import com.getit.app.models.User;
import com.getit.app.persenters.BaseCallback;

import java.util.List;

public interface UsersCallback extends BaseCallback {
    default void onGetUsersComplete(List<User> users) {
    }

    default void onSaveUserComplete() {
    }

    default void onGetDeleteUserComplete(int position) {
    }

    default void onGetSignupUserComplete() {
    }

    default void onGetSignupUserFail(String message) {
    }

    default void onGetUserComplete(User user) {
    }
}
