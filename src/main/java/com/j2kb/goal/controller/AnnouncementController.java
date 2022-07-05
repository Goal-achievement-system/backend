package com.j2kb.goal.controller;

import com.j2kb.goal.dto.Announcement;
import com.j2kb.goal.service.AbstractAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    static final int COUNT_IN_PAGE = 6;
    @Autowired
    private AbstractAnnouncementService announcementService;
    @GetMapping("/list")
    public ResponseEntity<?> getAnnouncements(){
        List<Announcement> result = announcementService.getAnnouncements();
        return ResponseEntity.ok(result);
    }
}
