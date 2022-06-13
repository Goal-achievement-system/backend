package com.j2kb.goal.service;

import com.j2kb.goal.dto.Announcement;

import java.util.List;

public interface AbstractAnnouncementService {
    List<Announcement> getAnnouncements(int page);
    int getCountsOfAnnouncements();
}
