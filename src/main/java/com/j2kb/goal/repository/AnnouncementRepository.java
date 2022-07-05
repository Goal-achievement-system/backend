package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Announcement;

import java.util.List;

public interface AnnouncementRepository {
    static final int COUNT_IN_PAGE = 6;
    public List<Announcement> selectAnnouncements();
    public List<Announcement> selectAnnouncements(int page);
    public int selectCountsOfAnnouncements();
}
