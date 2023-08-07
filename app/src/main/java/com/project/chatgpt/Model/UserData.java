package com.project.chatgpt.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UserData implements Parcelable {
    private String name;
    private String pic;

    private String chat_key;

    protected UserData(Parcel in) {
        name = in.readString();
        pic = in.readString();
        chat_key = in.readString();
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    public String getChat_key() {
        return chat_key;
    }

    public UserData(String name, String pic, String chat_key) {
        this.name = name;
        this.pic = pic;
        this.chat_key = chat_key;
    }

    public String getName() {
        return name;
    }

    public String getPic() {
        return pic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(pic);
        parcel.writeString(chat_key);
    }
}
