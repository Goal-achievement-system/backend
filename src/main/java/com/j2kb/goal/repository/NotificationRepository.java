package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Notification;

import java.util.List;

public interface NotificationRepository {
    Notification insertNotification(Notification notification);
    List<Notification> selectNotificationsByEmail(String email);
    Notification updateNotification(Notification notification);
}
