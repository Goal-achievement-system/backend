package com.j2kb.goal.service;

import com.j2kb.goal.dto.Notification;
import com.j2kb.goal.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class NotificationService implements AbstractNotificationService{

    private NotificationRepository notificationRepository;
    public NotificationService(NotificationRepository repository){
        this.notificationRepository = repository;
    }

    @Override
    public Notification insertNotification(Notification notification) {
        return notificationRepository.insertNotification(notification);
    }

    @Override
    public List<Notification> getNotificationsByEmail(String email) {
        return notificationRepository.selectNotificationsByEmail(email);
    }

    @Override
    public void readNotification(long id) {

    }
}
