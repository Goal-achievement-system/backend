package com.j2kb.goal.service;

import com.j2kb.goal.dto.ErrorCode;
import com.j2kb.goal.dto.Notification;
import com.j2kb.goal.exception.NoMatchedMemberException;
import com.j2kb.goal.exception.PermissionException;
import com.j2kb.goal.exception.SpringHandledException;
import com.j2kb.goal.repository.NotificationRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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
        try {
            return notificationRepository.selectNotificationsByEmail(email);
        }catch (DataAccessException e){
            throw new NoMatchedMemberException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN,"GET /api/members/myinfo/notifications","member with email = "+email+" is not found");
        }
    }

    @Override
    public void readNotification(long id,String email) {
        List<Notification> notifications = notificationRepository.selectNotificationsByEmail(email);
        if(!notifications.contains(Notification.builder().notificationId(id).build())){
            throw new PermissionException(HttpStatus.FORBIDDEN,ErrorCode.PERMISSION_DENIED,"PUT /myinfo/notifications/"+id,"read other's notifiacation is impossible");
        }
        try{
            notificationRepository.updateNotification(Notification.builder().notificationId(id).read(true).build());
        }catch (DataAccessException e){
            throw new SpringHandledException(HttpStatus.NOT_FOUND,ErrorCode.NOT_FOUND,"PUT /myinfo/notifications/"+id,"notificationis not found");
        }
    }
}
