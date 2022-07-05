package com.j2kb.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@AllArgsConstructor
@Getter
public class AnnouncementsWithMaxPage {
    private int maxPage;
    private List<Announcement> announcements;
}
