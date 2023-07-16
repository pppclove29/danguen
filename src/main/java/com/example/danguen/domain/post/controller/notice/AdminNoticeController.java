package com.example.danguen.domain.post.controller.notice;

import com.example.danguen.annotation.SessionUserId;
import com.example.danguen.domain.post.dto.request.RequestPostSaveOrUpdateDto;
import com.example.danguen.domain.post.entity.PostKind;
import com.example.danguen.domain.post.service.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/admin/post")
@RequiredArgsConstructor
@RestController
public class AdminNoticeController {

    private final PostServiceImpl postService;

    @PostMapping("/notice")
    public void save(@ModelAttribute("request") RequestPostSaveOrUpdateDto request,
                     @RequestParam(value = "images", required = false) List<MultipartFile> images,
                     @SessionUserId Long userId) throws IOException {
        //todo 모든 post에 대해 값이 null이 아니게 검증 할 것
        postService.save(request, userId, PostKind.Kind.NOTICE);
        //todo image save
    }
}
