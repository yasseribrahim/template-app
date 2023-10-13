package com.getit.app.persenters;

import android.view.View;

public interface BaseCallback {
    void onFailure(String message, View.OnClickListener listener);
    void onShowLoading();
    void onHideLoading();

    default void onShowConnectionError(View.OnClickListener onClickListener) {
    }

    default void onShowError(String message, View.OnClickListener onClickListener) {
    }

    default void onUnAuthorized() {
    }

    default String onGetErrorMessage(Throwable throwable) {
        return "";
    }
}
