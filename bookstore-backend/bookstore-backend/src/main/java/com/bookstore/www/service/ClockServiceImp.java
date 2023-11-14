package com.bookstore.www.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import java.text.SimpleDateFormat;
import  java.util.*;
import java.text.*;


@Service
@Scope("session")
public class ClockServiceImp implements ClockService{

    boolean settled = false;
    Date startDate;
    String startTimeString = "";
    @Override
    public String startClockCounting() {
        if(!settled){
            startDate = new Date();
            startDate.setHours(startDate.getHours());
            startTimeString = startDate.toString();
            settled = true;
        }
        return startTimeString;
    }

    @Override
    public String endClockCounting() {
        Date now = new Date();
        int hour = now.getHours()-startDate.getHours();
        int minute = now.getMinutes()-startDate.getMinutes();
        int second = now.getSeconds()-startDate.getSeconds();

        while(second < 0) {
            second += 60;
            minute -= 1;
        }

        while(minute < 0){
            minute += 60;
            hour -= 1;
        }

        if(hour < 0)
            return "计时器出错或未成功启动!";
        else return "最早一次登录时间为：" + startTimeString + ", 本次会话持续时间为："
            + hour + "时" + minute + "分" + second + "秒。";
    }

    @Override
    public void resetTime() {
        settled = false;
        startTimeString = "";
    }
}
