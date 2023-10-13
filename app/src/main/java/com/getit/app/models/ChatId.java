package com.getit.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class ChatId implements Parcelable {
    private User user1;
    private User user2;

    public ChatId() {
    }

    public ChatId(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public User getOther(User user) {
        return user1.equals(user) ? user2 : user1;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatId chatId = (ChatId) o;
        return user1.equals(chatId.user1) && user2.equals(chatId.user2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2);
    }

    @Override
    public String toString() {
        return user1.getId() + ":" + user2.getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user1, flags);
        dest.writeParcelable(this.user2, flags);
    }

    public void readFromParcel(Parcel source) {
        this.user1 = source.readParcelable(User.class.getClassLoader());
        this.user2 = source.readParcelable(User.class.getClassLoader());
    }

    protected ChatId(Parcel in) {
        this.user1 = in.readParcelable(User.class.getClassLoader());
        this.user2 = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<ChatId> CREATOR = new Creator<ChatId>() {
        @Override
        public ChatId createFromParcel(Parcel source) {
            return new ChatId(source);
        }

        @Override
        public ChatId[] newArray(int size) {
            return new ChatId[size];
        }
    };
}
