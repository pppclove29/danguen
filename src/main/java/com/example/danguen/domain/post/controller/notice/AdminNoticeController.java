package com.example.danguen.domain.post.controller.notice;

import com.example.danguen.annotation.SessionUserId;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/admin2463463")
@RequiredArgsConstructor
@RestController
public class AdminNoticeController {
    @PostMapping("/article")
    public void save(@ModelAttribute("request") RequestArticleSaveOrUpdateDto request,
                     @RequestParam(value = "images") List<MultipartFile> images,
                     @SessionUserId Long userId) throws IOException {
    }
}
