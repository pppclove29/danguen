//package com.example.danguen.front;
//
//import com.example.danguen.config.oauth.PrincipalUserDetails;
//import com.example.danguen.domain.base.Address;
//import com.example.danguen.domain.image.service.ArticleImageService;
//import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
//import com.example.danguen.domain.post.service.ArticleServiceImpl;
//import com.example.danguen.domain.user.entity.User;
//import com.example.danguen.domain.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.multipart.commons.CommonsMultipartFile;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.util.ArrayList;
//import java.util.List;
//
//@RequiredArgsConstructor
//@RestController
//public class FormController {
//
//    //for front test
//    private final ArticleServiceImpl articleService;
//    private final ArticleImageService articleImageService;
//    private final UserRepository userRepository;
//
//    @GetMapping("/user-make")
//    public String userSave() throws IOException {
//        User user = User.builder()
//                .name("김개동")
//                .email("pppclove29@naver.com")
//                .address(new Address("11", "22", "33"))
//                .build();
//
//        PrincipalUserDetails userDetails = new PrincipalUserDetails(user);
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()));
////
//        userRepository.save(user);
//
//        articleSave();
//
//        return "유저 생성 성공 + 게시글 10개 생성 성공";
//    }
//
//    //아래는 테스트용이다.
//    @GetMapping("/article-make")
//    public void articleSave() throws IOException {
//        // 랜덤하게 10개의 게시글을 만든다
//        for (int i = 0; i < 10; i++) {
//            RequestArticleSaveOrUpdateDto request = RequestArticleSaveOrUpdateDto.builder()
//                    .title(ArticleFrame.Title.getRandom())
//                    .content(ArticleFrame.Content.getRandom())
//                    .category("카테고리")
//                    .price(ArticleFrame.getRandomPrice())
//                    .dealHopeAddress(new Address(
//                            ArticleFrame.City.getRandom(),
//                            ArticleFrame.Street.getRandom(),
//                            ArticleFrame.Zipcode.getRandom()
//                    ))
//                    .build();
//
//            List<User> userList = userRepository.findAll();
//            Long userId = userList.get((int) (Math.random() * userList.size())).getId();
//
//            List<MultipartFile> imageList = new ArrayList<>();
//
//            int imageCount = (int) (Math.random() * 4) + 1;
//            for (int j = 1; j <= imageCount; j++) {
//                int imageIndex = (int) (Math.random() * 12) + 1;
//                imageList.add(getMultipartFile(imageIndex));
//            }
//            Long articleId = articleService.save(request, userId);
//            articleImageService.save(articleId, imageList);
//        }
//    }
//
//    // 이미지로부터 값얻기
//    private MultipartFile getMultipartFile(int idx) throws IOException {
//        File file = new File("src/main/resources/testImage/" + idx + ".png");
//
//        try {
//            byte[] fileContent = Files.readAllBytes(file.toPath());
//
//            // 로컬 파일을 MultipartFile로 변환합니다.
//            MultipartFile multipartFile = new CommonsMultipartFile(
//                    file.getName(), // 파일 이름
//                    fileContent     // 파일 내용
//            );
//
//            // 생성된 MultipartFile 객체를 사용하여 원하는 작업을 수행합니다.
//            // 예: 파일 업로드 처리 등
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        File file = new File("src/main/resources/testImage/" + idx + ".png");
////        FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
////
////        try {
////            InputStream input = new FileInputStream(file);
////            OutputStream os = fileItem.getOutputStream();
////            IOUtils.copy(input, os);
////            // Or faster..
////            // IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
////        } catch (IOException ex) {
////            // do something.
////        }
////
////        //jpa.png -> multipart 변환
////        return new CommonsMultipartFile(fileItem);
//    }
//
//
//}
