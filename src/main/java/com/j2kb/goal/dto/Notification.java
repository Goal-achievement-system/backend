package com.j2kb.goal.dto;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
@Getter
@Builder
public class Notification {
    private long notificationId;
    private String content;
    private String memberEmail;
    private Timestamp date;
    private String url;
    private boolean read;
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Notification){
            if(((Notification) obj).notificationId == this.notificationId){
                return true;
            }else {
                return false;
            }
        }else{
            return false;
        }
    }
}
