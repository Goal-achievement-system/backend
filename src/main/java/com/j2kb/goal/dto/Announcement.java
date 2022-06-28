package com.j2kb.goal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Builder
@Setter
@Getter
@JsonIgnoreProperties(value = {"image","bannerImage"}, allowSetters = true)
public class Announcement {
    private long announcementId;
    private String title;
    private String description;
    private String image;
    private String bannerImage;
    private Timestamp date;
    private boolean activation;
}
