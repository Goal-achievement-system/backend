package com.j2kb.goal.service;

import com.j2kb.goal.dto.Announcement;
import com.j2kb.goal.dto.AnnouncementsWithMaxPage;
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
    public List<Announcement> getAnnouncements() {
        try{
            return announcementRepository.selectAnnouncements();
        }catch (DataAccessException e){
            throw new SpringHandledException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNKNOWN,"GET /api/announcements/list","unknown");
        }
    }

    @Override
    public AnnouncementsWithMaxPage getAnnouncements(int page) {
        try{
            List<Announcement> result = announcementRepository.selectAnnouncements(page);
            int maxPage = announcementRepository.selectCountsOfAnnouncements();
            maxPage = maxPage % AnnouncementRepository.COUNT_IN_PAGE == 0 ? maxPage / AnnouncementRepository.COUNT_IN_PAGE : maxPage / AnnouncementRepository.COUNT_IN_PAGE + 1;
            return new AnnouncementsWithMaxPage(maxPage,result);

        }catch (DataAccessException e){
            throw new SpringHandledException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNKNOWN,"GET /api/admin/announcements/list/"+page,"unknown");
        }
    }
}
