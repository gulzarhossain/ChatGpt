package com.project.chatgpt.Model;

public class MsgData {
    private String msg;
    private String typ;
    private String time;
    private String name;

    public MsgData(String msg,String typ, String time, String name) {
        this.msg = msg;
        this.time = time;
        this.name = name;
        this.typ=typ;
    }

    public String getTyp() {
        return typ;
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
