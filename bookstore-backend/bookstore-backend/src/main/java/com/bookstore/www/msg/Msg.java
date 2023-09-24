package com.bookstore.www.msg;

import lombok.Getter;
import lombok.Setter;

public class Msg {
    @Getter @Setter
    private String msg;
    @Getter @Setter
    private Object data;

    public Msg(String msg, Object data){
        this.msg = msg;
        this.data = data;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
