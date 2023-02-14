package com.example.danguen.service.controller;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.post.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.model.user.User;
import com.example.danguen.domain.repository.UserRepository;
import com.example.danguen.service.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class FormController {

    //for front test
    private final ArticleService articleService;
    private final UserRepository userRepository;

    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");

        return mav;
    }

    @GetMapping("/article-list")
    public ModelAndView articleList() {
        ModelAndView mav = new ModelAndView("articleList");

        return mav;
    }

    @GetMapping("/article-page")
    public ModelAndView articlePage() {
        ModelAndView mav = new ModelAndView("articlePage");

        return mav;
    }

    @GetMapping("/user-page")
    public ModelAndView userPage() {
        ModelAndView mav = new ModelAndView("userPage");

        return mav;
    }

    //아래는 테스트용이다.
    @GetMapping("/article-make")
    public ModelAndView articleSave() throws IOException {
        ModelAndView mav = new ModelAndView("index");

        // 랜덤하게 10개의 게시글을 만든다
        for (int i = 0; i < 10; i++) {
            RequestArticleSaveOrUpdateDto request = RequestArticleSaveOrUpdateDto.builder()
                    .title(ArticleFrame.Title.getRandom().name())
                    .content(ArticleFrame.Content.getRandom().name())
                    .category("카테고리")
                    .price(ArticleFrame.getRandomPrice())
                    .dealHopeAddress(new Address(
                            ArticleFrame.City.getRandom().name(),
                            ArticleFrame.Street.getRandom().name(),
                            ArticleFrame.Zipcode.getRandom().name()
                    ))
                    .build();

            List<User> userList = userRepository.findAll();
            Long userId = userList.get((int) (Math.random() * userList.size())).getId();

            List<MultipartFile> imageList = new ArrayList<>();

            int imageCount = (int) (Math.random() * 4) + 1;
            for (int j = 1; j <= imageCount; j++) {
                int imageIndex = (int) (Math.random() * 12) + 1;
                imageList.add(getMultipartFile(imageIndex));
            }
            articleService.save(request, userId, imageList);
        }

        return mav;
    }

    // 이미지로부터 값얻기
    private MultipartFile getMultipartFile(int idx) throws IOException {
        File file = new File("src/main/resources/testImage/" + idx + ".png");
        FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());

        try {
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            // Or faster..
            // IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
        } catch (IOException ex) {
            // do something.
        }

        //jpa.png -> multipart 변환
        MultipartFile mFile = new CommonsMultipartFile(fileItem);
        return mFile;
    }


}
