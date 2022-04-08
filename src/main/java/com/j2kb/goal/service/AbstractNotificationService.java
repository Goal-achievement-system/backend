package com.j2kb.goal.service;

import com.j2kb.goal.dto.Notification;

import java.util.List;

public interface AbstractNotificationService {
    Notification insertNotification(Notification notification);
    List<Notification> getNotificationsByEmail(String email);
    void readNotification(long id);
}
