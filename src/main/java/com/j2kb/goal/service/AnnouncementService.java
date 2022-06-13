package com.j2kb.goal.service;

import com.j2kb.goal.dto.Announcement;
import com.j2kb.goal.dto.ErrorCode;
import com.j2kb.goal.exception.SpringHandledException;
import com.j2kb.goal.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AnnouncementService implements AbstractAnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Override
    public List<Announcement> getAnnouncements(int page) {
        try{
            return announcementRepository.selectAnnouncements(page);
        }catch (DataAccessException e){
            throw new SpringHandledException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNKNOWN,"GET /api/announcements/list/"+page,"unknown");
        }
    }

    @Override
    public int getCountsOfAnnouncements() {
        try{
            return announcementRepository.selectCountsOfAnnouncements();
        }catch (DataAccessException e){
            throw new SpringHandledException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNKNOWN,"","unknown");
        }
    }
}
