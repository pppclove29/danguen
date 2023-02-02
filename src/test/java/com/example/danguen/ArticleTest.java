package com.example.danguen;


import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.post.article.Article;
import com.example.danguen.domain.model.post.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.model.user.User;
import com.example.danguen.domain.repository.ArticleRepository;
import com.example.danguen.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ArticleTest extends BaseTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;

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
    }

    @WithMockUser
    @Test
    public void 정상적인_물품_등록하기() throws Exception {
        //given & when
        articleRegisterProc(0);

        //then
        Article article = articleRepository.findAll().get(0);

        assertThat(article.getTitle()).isEqualTo("제목 0");
        assertThat(article.getCategory()).isEqualTo("카테고리");
        assertThat(article.getContent()).isEqualTo("내용");
        assertThat(article.getPicture()).isEqualTo("사진");
        assertThat(article.getDealHopeAddress().getCity()).isEqualTo("희망주소0");
        assertThat(article.getDealHopeAddress().getStreet()).isEqualTo("희망주소0");
        assertThat(article.getDealHopeAddress().getZipcode()).isEqualTo("희망주소0");
        assertThat(article.getPrice()).isEqualTo(10000);
        assertThat(article.getSeller().getEmail()).isEqualTo("email@temp.com");
    }

    @WithMockUser
    @Test
    public void 물품_정보수정() throws Exception {
        //given
        articleRegisterProc(0);

        RequestArticleSaveOrUpdateDto dto = new RequestArticleSaveOrUpdateDto();
        dto.setTitle("new제목");
        dto.setCategory("new카테고리");
        dto.setContent("new내용");
        dto.setPicture("new사진");
        dto.setDealHopeAddress(new Address("new희망주소1", "new희망주소2", "new희망주소3"));
        dto.setPrice(30000);

        Long articleId = articleRepository.findAll().get(0).getId();

        //when
        mockMvc.perform(put("/article/" + articleId).contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(dto))).andExpect(status().isOk());

        //then
        Article article = articleRepository.findById(articleId).get();
        assertThat(article.getTitle()).isEqualTo("new제목");
        assertThat(article.getCategory()).isEqualTo("new카테고리");
        assertThat(article.getContent()).isEqualTo("new내용");
        assertThat(article.getPicture()).isEqualTo("new사진");
        assertThat(article.getDealHopeAddress().getCity()).isEqualTo("new희망주소1");
        assertThat(article.getDealHopeAddress().getStreet()).isEqualTo("new희망주소2");
        assertThat(article.getDealHopeAddress().getZipcode()).isEqualTo("new희망주소3");
        assertThat(article.getPrice()).isEqualTo(30000);
        assertThat(article.getSeller().getEmail()).isEqualTo("email@temp.com");
    }

    @WithMockUser
    @Test
    public void 물품_삭제() throws Exception {
        //given
        articleRegisterProc(0);
        Long articleId = articleRepository.findAll().get(0).getId();


        //when
        mockMvc.perform(delete("/article/" + articleId));

        //then
        User user = userRepository.findAll().get(0);

        assertThat(articleRepository.findAll().size()).isEqualTo(0);
        assertThat(user.getSellArticles().size()).isEqualTo(0);
    }

    @Test
    public void 등록자_회원탈퇴시_중고물품_처리테스트() throws Exception {
        //given
        articleRegisterProc(0);
        Long userId = userRepository.findAll().get(0).getId();

        //when
        mockMvc.perform(delete("/user/" + userId));

        //then
        assertThat(articleRepository.findAll().size()).isEqualTo(0);
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void 물품_페이지로딩() throws Exception {
        //given
        articleRegisterProc(0);
        Long articleId = articleRepository.findAll().get(0).getId();

        //when & then
        mockMvc.perform(get("/article/" + articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목 0"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.picture").value("사진"))
                .andExpect(jsonPath("$.category").value("카테고리"))
                .andExpect(jsonPath("$.views").value(1))
                .andExpect(jsonPath("$.sold").value(false))
                .andExpect(jsonPath("$.dealHopeAddress.city").value("희망주소0"))
                .andExpect(jsonPath("$.dealHopeAddress.street").value("희망주소0"))
                .andExpect(jsonPath("$.dealHopeAddress.zipcode").value("희망주소0"))
                .andExpect(jsonPath("$.seller").value("박이름"));
    }

    @Test
    public void 잘못된_물품페이지로딩() throws Exception {
        //given
        articleRegisterProc(0);
        Long articleId = articleRepository.findAll().get(0).getId();

        //when
        MvcResult result = mockMvc.perform(get("/article/" + articleId + 1)).andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).contains("articleNotFound");
    }

    @Test
    public void 주소에_맞는_물품_리스트로딩() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            articleRegisterProc(i);
        }

        /*
        물품 지역 리스트
        idx     address
        0       000
        1       001
        2       002
        3       113
        4       114
        5       115
        6       226
        7       227
        8       228
        9       339
         */

        //when
        mockMvc.perform(get("/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[5]").exists()) // 6개의 검색 결과가 존재해야한다
                .andExpect(jsonPath("$[6]").doesNotExist())
                .andExpect(jsonPath("$[0].title").value("제목 9"))
                .andExpect(jsonPath("$[5].title").value("제목 4")); // 검색 결과는 최신순으로 내림차순한다

        //같은 검색결과를 반환하는지 확인
        mockMvc.perform(get("/address/희망주소1/희망주소1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[2]").exists()) // 3개의 검색 결과가 존재해야한다
                .andExpect(jsonPath("$[3]").doesNotExist())
                .andExpect(jsonPath("$[0].title").value("제목 5"))
                .andExpect(jsonPath("$[2].title").value("제목 3")); // 검색 결과는 최신순으로 내림차순한다

        //단 하나의 단독결과만 반환하는지 확인
        mockMvc.perform(get("/address/희망주소1/희망주소1/희망주소3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists()) // 단 하나의 결과만 출력해야한다
                .andExpect(jsonPath("$[1]").doesNotExist())
                .andExpect(jsonPath("$[0].title").value("제목 3")); // 검색 결과는 최신순으로 내림차순한다

        //없는 주소를 입력시 반환값이 없는지 확인
        mockMvc.perform(get("/address/없는주소"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").doesNotExist()); // 아무 결과도 있어선 안된다

        //영어 url도 확인, 역시 없음
        mockMvc.perform(get("/address/no-address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").doesNotExist()); // 아무 결과도 있어선 안된다
    }

    @Test
    public void 리스트_페이지_분할테스트() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            articleRegisterProc(i);
        }

        // 첫번째 페이지
        mockMvc.perform(get("/search?size=3&page=0&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(("제목 9")))
                .andExpect(jsonPath("$[1].title").value(("제목 8")))
                .andExpect(jsonPath("$[2].title").value(("제목 7")))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 두번째 페이지
        mockMvc.perform(get("/search?size=3&page=1&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(("제목 6")))
                .andExpect(jsonPath("$[1].title").value(("제목 5")))
                .andExpect(jsonPath("$[2].title").value(("제목 4")))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 세번째 페이지
        mockMvc.perform(get("/search?size=3&page=2&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(("제목 3")))
                .andExpect(jsonPath("$[1].title").value(("제목 2")))
                .andExpect(jsonPath("$[2].title").value(("제목 1")))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 마지막 페이지
        mockMvc.perform(get("/search?size=3&page=3&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(("제목 0")))
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    public void 인기순위검색() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            articleRegisterProc(i);
        }
        // 물품 리스트 0 ~ 9
        // 이 중에 1 4 5 8의 조회수를 높혀보자 높은 순서대로 5 8 1 4이다
        watchArticle(1, 4);
        watchArticle(4, 2);
        watchArticle(5, 10);
        watchArticle(8, 7);

        //when
        MvcResult result = mockMvc.perform(get("/hot-articles"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String[] articles = result.getResponse().getContentAsString().split("false},");

        assertThat(articles[0]).contains("제목 5");
        assertThat(articles[1]).contains("제목 8");
        assertThat(articles[2]).contains("제목 1");
        assertThat(articles[3]).contains("제목 4");

        assertThat(Arrays.stream(articles).count()).isEqualTo(6); // page size
    }


    @Test
    public void 제목연관검색() throws Exception {
        //given
        for (int i = 0; i < 5; i++) {
            articleRegisterProc(7777);
        } // 검색 결과가 5개 나오는가?
        for (int i = 0; i < 10; i++) {
            articleRegisterProc(10000);
        } // 검색 결과가 6개로 나뉘어 나오는가?
        for (int i = 0; i < 3; i++) {
            articleRegisterProc(777);
        } // 검색 결과가 777 3개 + 7777 3개 이렇게 나오는가? 아니면 어떻게 나오는가? -> 최신순으로 정렬하자

        String keyword = "7777";
        //when
        this.mockMvc.perform(get("/search?keyword=" + keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists())
                .andExpect(jsonPath("$[2]").exists())
                .andExpect(jsonPath("$[3]").exists())
                .andExpect(jsonPath("$[4]").exists())
                .andExpect(jsonPath("$[5]").doesNotExist())
                .andExpect(jsonPath("$[0].title").value("제목 7777"))
                .andExpect(jsonPath("$[1].title").value("제목 7777"))
                .andExpect(jsonPath("$[2].title").value("제목 7777"))
                .andExpect(jsonPath("$[3].title").value("제목 7777"))
                .andExpect(jsonPath("$[4].title").value("제목 7777"))
                .andReturn();

        keyword = "10000";

        this.mockMvc.perform(get("/search?keyword=" + keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists())
                .andExpect(jsonPath("$[2]").exists())
                .andExpect(jsonPath("$[3]").exists())
                .andExpect(jsonPath("$[4]").exists())
                .andExpect(jsonPath("$[5]").exists())
                .andExpect(jsonPath("$[6]").doesNotExist())
                .andExpect(jsonPath("$[0].title").value("제목 10000"))
                .andExpect(jsonPath("$[1].title").value("제목 10000"))
                .andExpect(jsonPath("$[2].title").value("제목 10000"))
                .andExpect(jsonPath("$[3].title").value("제목 10000"))
                .andExpect(jsonPath("$[4].title").value("제목 10000"))
                .andExpect(jsonPath("$[5].title").value("제목 10000"))
                .andReturn();

        keyword = "777";

        MvcResult result777 = mockMvc.perform(get("/search?keyword=" + keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists())
                .andExpect(jsonPath("$[2]").exists())
                .andExpect(jsonPath("$[3]").exists())
                .andExpect(jsonPath("$[4]").exists())
                .andExpect(jsonPath("$[5]").exists())
                .andExpect(jsonPath("$[6]").doesNotExist())
                .andExpect(jsonPath("$[0].title").value("제목 777"))
                .andExpect(jsonPath("$[1].title").value("제목 777"))
                .andExpect(jsonPath("$[2].title").value("제목 777"))
                .andExpect(jsonPath("$[3].title").value("제목 7777"))
                .andExpect(jsonPath("$[4].title").value("제목 7777"))
                .andExpect(jsonPath("$[5].title").value("제목 7777"))
                .andReturn();
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

    public void watchArticle(int idx, int count) throws Exception { // 조회수 증가
        Long articleId = articleRepository.findAll().get(idx).getId();

        for (int i = 0; i < count; i++) {
            mockMvc.perform(get("/article/" + articleId));
        }
    }
}
