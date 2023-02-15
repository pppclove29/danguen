package com.example.danguen;

import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.image.dto.ImageDto;
import com.example.danguen.domain.model.post.article.Article;
import com.example.danguen.domain.model.post.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.model.user.User;
import com.example.danguen.domain.repository.ArticleRepository;
import com.example.danguen.domain.repository.UserRepository;
import com.example.danguen.domain.repository.comment.CommentRepository;
import com.example.danguen.domain.repository.image.ArticleImageRepository;
import com.example.danguen.domain.repository.image.UserImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class BaseTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext ctx;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserImageRepository userImageRepository;
    @Autowired
    ArticleImageRepository articleImageRepository;
    @Autowired
    ObjectMapper mapper;

    String sessionName = "박이름";
    String sessionEmail = "email@temp.com";

    @BeforeEach
    public void 임의유저_생성_및_세션등록() {
        User user = makeUserProc(sessionName, sessionEmail);

        PrincipalUserDetails userDetails = new PrincipalUserDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()));

        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();

        System.out.println("유저_생성");
    }

    @AfterEach
    public void 초기화() {
        commentRepository.deleteAll();
        userImageRepository.deleteAll();
        articleImageRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    String city = "서울시";
    String street = "길로";
    String zipcode = "1234";

    public User makeUserProc(String name, String email) {
        Address address = new Address(city, street, zipcode);

        User user = User.builder().name(name).email(email).address(address).build();

        ImageDto image = new ImageDto(user.getName() + ".jpg", "path:");

        userImageRepository.save(image.toUserImage(user));
        userRepository.save(user);

        return user;
    }

    String title = "제목 ";
    String category = "카테고리";
    String articleContent = "내용";
    int price = 10000;
    String hopeCity = "희망주소";
    String hopeStreet = "희망주소";
    String hopeZipcode = "희망주소";

    public void articleSaveProc(int idx) throws Exception { // 중고 물품 등록
        RequestArticleSaveOrUpdateDto dto = RequestArticleSaveOrUpdateDto.builder()
                .title(title + idx)
                .content(articleContent)
                .price(price)
                .category(category)
                .dealHopeAddress(new Address(hopeCity + idx / 3, hopeStreet + idx / 3, hopeZipcode + idx))
                .build();

        String dtoJson = new ObjectMapper().writeValueAsString(dto);
        MockMultipartFile request = new MockMultipartFile(
                "request",
                "request",
                "application/json",
                dtoJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "input.png",
                "image/png",
                new FileInputStream("src/test/java/testImage/input.png"));

        mockMvc.perform(multipart("/article")
                        .file(image)
                        .file(request)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    public void noneSessionsArticleSaveProc(User noneSessionUser, int idx) {
        RequestArticleSaveOrUpdateDto dto = RequestArticleSaveOrUpdateDto.builder()
                .title(title + noneSessionUser.getName())
                .content(articleContent)
                .price(price * idx)
                .category(category)
                .dealHopeAddress(new Address(
                        noneSessionUser.getAddress().getCity(),
                        noneSessionUser.getAddress().getStreet(),
                        noneSessionUser.getAddress().getZipcode()))
                .build();

        Article article = dto.toEntity();

        noneSessionUser.addSellArticle(article);

        ImageDto image = new ImageDto(article.getId() + ".jpg", "path:");

        articleImageRepository.save(image.toArticleImage(article));
        articleRepository.save(article);
    }

    String commentContent = "댓글 내용";

    public void commentSaveProc() throws Exception { // 댓글 등록
        articleSaveProc(0);

        Long articleId = articleRepository.findAll().get(0).getId();

        mockMvc.perform(post("/article/" + articleId + "/comment")
                        .param("content", commentContent))
                .andExpect(status().is3xxRedirection());
    }
}
