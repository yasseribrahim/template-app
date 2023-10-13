package com.getit.app.persenters.messaging;

import com.getit.app.models.Message;
import com.getit.app.persenters.BaseCallback;

public interface MessagingCallback extends BaseCallback {
    void onSendMessageSuccess();

    void onSendMessageFailure(String message);

    void onGetMessageSuccess(Message message);

    void onGetMessageFailure(String message);

    void onEmptyMessaging();
}
