package com.example.danguen;

import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.model.post.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.model.user.User;
import com.example.danguen.domain.repository.ArticleRepository;
import com.example.danguen.domain.repository.CommentRepository;
import com.example.danguen.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@AutoConfigureMockMvc
public class BaseTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected MockHttpSession session;
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected WebApplicationContext ctx;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ArticleRepository articleRepository;
    @Autowired
    protected CommentRepository commentRepository;

    @BeforeEach
    public void 임의유저_생성_및_세션등록() {
        Address address = new Address("서울시", "서울구", "서울로");

        User user = User.builder().name("박이름").email("email@temp.com").picture("picture").address(address).build();

        userRepository.save(user);

        PrincipalUserDetails userDetails = new PrincipalUserDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()));

        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    @AfterEach
    public void 초기화() {
        userRepository.deleteAll();
        articleRepository.deleteAll();
        commentRepository.deleteAll();
    }

    public void articleRegisterProc(int idx) throws Exception { // 중고 물품 등록
        RequestArticleSaveOrUpdateDto dto = new RequestArticleSaveOrUpdateDto();
        dto.setTitle("제목 " + idx);
        dto.setCategory("카테고리");
        dto.setContent("내용");
        dto.setPicture("사진");
        dto.setDealHopeAddress(new Address("희망주소" + idx / 3, "희망주소" + idx / 3, "희망주소" + idx));
        dto.setPrice(10000);

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "input.png",
                "image/png",
                new FileInputStream("src/test/java/testImage/input.png"));

        String dtoJson = new ObjectMapper().writeValueAsString(dto);
        MockMultipartFile request = new MockMultipartFile(
                "request",
                "request",
                "application/json",
                dtoJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/article")
                        .file(image)
                        .file(request)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    public void commentRegisterProc() throws Exception {
        articleRegisterProc(0);

        Long articleId = articleRepository.findAll().get(0).getId();

        RequestCommentSaveDto dto = new RequestCommentSaveDto();

        dto.setContent("댓글 내용");

        mockMvc.perform(get("/article/" + articleId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
