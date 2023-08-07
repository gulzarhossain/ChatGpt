package com.project.chatgpt.Model;

public class MsgData {
    private String msg;
    private String time;
    private String name;

    public MsgData(String msg, String time, String name) {
        this.msg = msg;
        this.time = time;
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }
}
