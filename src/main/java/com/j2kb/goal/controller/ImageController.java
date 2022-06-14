package com.j2kb.goal.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    @GetMapping(value = "/goals/{goalId:[0-9]+}",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getCertImage(@PathVariable long goalId) throws IOException {
        File file = new File("cert"+ File.separator+goalId);
        return Files.readAllBytes(file.toPath());
    }

    @GetMapping(value = "/announcement/{announcementId:[0-9]+}",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getAnnouncementImage(@PathVariable long announcementId) throws IOException {
        File file = new File("announcement"+ File.separator+announcementId);
        return Files.readAllBytes(file.toPath());
    }
}
