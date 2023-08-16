package com.project.chatgpt.Model;

import android.net.Uri;

public class ShowCaseData {
    private Uri uri;
    private boolean check;
    private int progrs;

    public ShowCaseData(Uri uri, boolean check) {
        this.uri = uri;
        this.check = check;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getProgrs() {
        return progrs;
    }

    public void setProgrs(int progrs) {
        this.progrs = progrs;
    }
}
