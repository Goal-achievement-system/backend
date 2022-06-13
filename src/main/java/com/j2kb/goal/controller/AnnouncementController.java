package com.j2kb.goal.controller;

import com.j2kb.goal.dto.Announcement;
import com.j2kb.goal.service.AbstractAnnouncementService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    static final int COUNT_IN_PAGE = 6;
    @Autowired
    private AbstractAnnouncementService announcementService;
    @GetMapping("/list/{page:[0-9]+}")
    public ResponseEntity<?> getAnnouncements(@PathVariable int page){
        List<Announcement> result = announcementService.getAnnouncements(page);
        int count = announcementService.getCountsOfAnnouncements();
        count = count / COUNT_IN_PAGE;
        if(count%COUNT_IN_PAGE!=0)count++;
        if (count==0)count++;
        return ResponseEntity.ok(new AnnouncementWithPage(count,result));
    }
    @AllArgsConstructor
    @Getter
    private class AnnouncementWithPage{
        private int maxPage;
        private List<Announcement> announcements;
    }
}
