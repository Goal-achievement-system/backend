package com.j2kb.goal.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Builder
@Setter
@Getter
public class Announcement {
    private long announcementId;
    private String title;
    private String description;
    private String image;
    private Timestamp date;
}
