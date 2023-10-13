package com.getit.app.persenters.firebase;

import com.getit.app.persenters.BaseCallback;

public interface FirebaseCallback extends BaseCallback {
    default void onSaveTokenComplete() {
    }

    default void onGetTokenComplete(String token) {
    }
}
