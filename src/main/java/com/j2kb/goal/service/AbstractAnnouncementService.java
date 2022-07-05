package com.j2kb.goal.service;

import com.j2kb.goal.dto.Announcement;
import com.j2kb.goal.dto.AnnouncementsWithMaxPage;

import java.util.List;

public interface AbstractAnnouncementService {
    List<Announcement> getAnnouncements();
    AnnouncementsWithMaxPage getAnnouncements(int page);
}
