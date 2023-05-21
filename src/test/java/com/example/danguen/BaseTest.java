package com.example.danguen;

import com.example.danguen.config.oauth.CustomOAuth2UserService;
import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.image.dto.ImageDto;
import com.example.danguen.domain.image.repository.ImageRepository;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.repository.PostRepository;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.post.repository.ArticlePostRepository;
import com.example.danguen.domain.user.repository.UserRepository;
import com.example.danguen.domain.comment.repository.CommentRepository;
import com.example.danguen.domain.image.repository.ArticleImageRepository;
import com.example.danguen.domain.image.repository.UserImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.mockito.Mockito.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
    CustomOAuth2UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    ObjectMapper mapper;

    String sessionName = "박이름";
    String sessionEmail = "email@temp.com";

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();

        User user = makeMockUser(sessionName, sessionEmail);
        registerUserToSession(user);

        makeMockUser("김기타", "other@temp.com");
    }

    @AfterEach
    public void clear() {
        commentRepository.deleteAll();
        imageRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }


    public void registerUserToSession(User user) {
        PrincipalUserDetails userDetails = new PrincipalUserDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()));
    }

    String city = "서울시";
    String street = "길로";
    String zipcode = "1234";

    public User makeMockUser(String name, String email) {
        Address address = new Address(city, street, zipcode);
        User user = mock(User.class);
        when(user.getName()).thenReturn(name);
        when(user.getEmail()).thenReturn(email);
        when(user.getAddress()).thenReturn(address);

        makeMockUserImage(user);

        userRepository.save(user);

        return user;
    }

    public void makeMockUserImage(User user) {
        ImageDto image = new ImageDto("path:" + user.getName() + ".jpg");

        userImageRepository.save(image.toUserImage(user));
    }

    String title = "제목 ";
    String category = "카테고리";
    String content = "내용";
    int price = 10000;
    String hopeCity = "희망주소";
    String hopeStreet = "희망주소";
    String hopeZipcode = "희망주소";

    public void makeMockArticle(int idx, String sellerEmail) throws Exception { // 중고 물품 등록
        Address address = new Address(hopeCity + idx / 3, hopeStreet + idx / 3, hopeZipcode + idx);

        ArticlePost articlePost = mock(ArticlePost.class);
        when(articlePost.getTitle()).thenReturn(title + idx);
        when(articlePost.getContent()).thenReturn(content);
        when(articlePost.getPrice()).thenReturn(price);
        when(articlePost.getCategory()).thenReturn(category);
        when(articlePost.getDealHopeAddress()).thenReturn(address);
        when(articlePost.getSeller()).thenReturn(userRepository.findByEmail(sellerEmail).get());

        post

        mockMvc.perform(multipart("/article")
                        .file(image)
                        .file(request)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    public void makeMockArticleImage() {

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

        ArticlePost articlePost = dto.toEntity();

        noneSessionUser.addSellArticle(articlePost);

        ImageDto image = new ImageDto("path:" + articlePost.getId() + ".jpg");

        articleImageRepository.save(image.toArticleImage(articlePost));
        articlePostRepository.save(articlePost);
    }

    String commentContent = "댓글 내용";

    public void commentSaveProc() throws Exception { // 댓글 등록
        articleSaveProc(0);

        Long articleId = articlePostRepository.findAll().get(0).getId();

        mockMvc.perform(post("/article/" + articleId + "/comment")
                        .param("content", commentContent))
                .andExpect(status().is3xxRedirection());
    }
}
