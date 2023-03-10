package com.example.danguen;

import com.example.danguen.config.oauth.CustomOAuth2UserService;
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
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
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
    CustomOAuth2UserService userService;
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


    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // ?????? ??????
                .alwaysDo(print())
                .build();

        //registerUserToSession();
    }

    @AfterEach
    public void clear() {
        commentRepository.deleteAll();
        userImageRepository.deleteAll();
        articleImageRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    String sessionName = "?????????";
    String sessionEmail = "email@temp.com";

    public void registerUserToSession(){
        User user = makeUserProc(sessionName, sessionEmail);

        PrincipalUserDetails userDetails = new PrincipalUserDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()));

        OAuth2UserRequest request = new OAuth2UserRequest();

        userService.loadUser()
    }

    String city = "?????????";
    String street = "??????";
    String zipcode = "1234";

    public User makeUserProc(String name, String email) {
        Address address = new Address(city, street, zipcode);

        User user = User.builder().name(name).email(email).address(address).build();

        ImageDto image = new ImageDto(user.getName() + ".jpg", "path:");

        userImageRepository.save(image.toUserImage(user));
        userRepository.save(user);

        return user;
    }

    String title = "?????? ";
    String category = "????????????";
    String articleContent = "??????";
    int price = 10000;
    String hopeCity = "????????????";
    String hopeStreet = "????????????";
    String hopeZipcode = "????????????";

    public void articleSaveProc(int idx) throws Exception { // ?????? ?????? ??????
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

    String commentContent = "?????? ??????";

    public void commentSaveProc() throws Exception { // ?????? ??????
        articleSaveProc(0);

        Long articleId = articleRepository.findAll().get(0).getId();

        mockMvc.perform(post("/article/" + articleId + "/comment")
                        .param("content", commentContent))
                .andExpect(status().is3xxRedirection());
    }
}
