package com.j2kb.goal.dto;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@Getter
public class Announcement {
    private long announcementId;
    private String title;
    private String description;
    private String content;
    private Timestamp date;
}
