package com.j2kb.goal.controller;

import com.j2kb.goal.dto.ErrorCode;
import com.j2kb.goal.exception.NoMatchedCertificationException;
import com.j2kb.goal.exception.SpringHandledException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    @GetMapping(value = "/cert/{goalId:[0-9]+}",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getCertImage(@PathVariable long goalId) throws IOException {
        try {
            File file = new File("cert"+ File.separator+goalId);
            return Files.readAllBytes(file.toPath());
        }catch (NoSuchFileException e){
            throw new SpringHandledException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND,"GET /api/image/goals/"+goalId,"image not found");
        }catch (IOException e){
            throw new SpringHandledException(HttpStatus.INTERNAL_SERVER_ERROR,ErrorCode.UNKNOWN,"GET /api/image/goals/"+goalId,"unknown error");
        }
    }

    @GetMapping(value = "/announcement/{announcementId:[0-9]+}",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getAnnouncementImage(@PathVariable long announcementId) throws IOException {
        try {
            File file = new File("announcement"+ File.separator+announcementId);
            return Files.readAllBytes(file.toPath());
        }catch (NoSuchFileException e){
            throw new SpringHandledException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND,"GET /api/image/announcement/"+announcementId,"image not found");
        }catch (IOException e){
            throw new SpringHandledException(HttpStatus.INTERNAL_SERVER_ERROR,ErrorCode.UNKNOWN,"GET /api/image/announcement/"+announcementId,"unknown error");
        }
    }
}
