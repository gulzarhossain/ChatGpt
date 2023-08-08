package com.project.chatgpt.Model;

public class FriendData{
    private String name;
    private String pic;
    private String frndkey;
    private String reqkey;
    private boolean check;

    public FriendData(String name, String pic, String frndkey, String reqkey, boolean check) {
        this.name = name;
        this.pic = pic;
        this.frndkey = frndkey;
        this.reqkey = reqkey;
        this.check = check;
    }

    public String getReqkey() {
        return reqkey;
    }

    public void setReqkey(String reqkey) {
        this.reqkey = reqkey;
    }

    public String getFrndkey() {
        return frndkey;
    }

    public void setFrndkey(String frndkey) {
        this.frndkey = frndkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
