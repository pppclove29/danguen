package com.example.danguen;


import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.Address;
import com.example.danguen.domain.article.Article;
import com.example.danguen.domain.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.infra.ArticleRepository;
import com.example.danguen.domain.infra.UserRepository;
import com.example.danguen.domain.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

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
    public void 임의유저생성및세션등록() {
        Address address = new Address("서울시", "서울구", "서울로");

        User user = User.builder().name("박이름").email("email@temp.com").picture("picture").address(address).build();

        userRepository.save(user);

        PrincipalUserDetails userDetails = new PrincipalUserDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()));

        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print()).build();
    }

    @AfterEach
    void 초기화() {
        jdbcTemplate.update("set FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.update("truncate table users");
        jdbcTemplate.update("truncate table article");
        jdbcTemplate.update("set FOREIGN_KEY_CHECKS = 1");
    }

    @WithMockUser
    @Test
    public void 정상적인물품등록하기() throws Exception {
        //given & when
        articleRegisterProc();

        //then
        Article article = articleRepository.findAll().get(0);

        assertThat(article.getTitle()).isEqualTo("제목");
        assertThat(article.getCategory()).isEqualTo("카테고리");
        assertThat(article.getContent()).isEqualTo("내용");
        assertThat(article.getPicture()).isEqualTo("사진");
        assertThat(article.getDealHopeAddress().getCity()).isEqualTo("희망주소1");
        assertThat(article.getDealHopeAddress().getStreet()).isEqualTo("희망주소2");
        assertThat(article.getDealHopeAddress().getZipcode()).isEqualTo("희망주소3");
        assertThat(article.getPrice()).isEqualTo(10000);
        assertThat(article.getSeller().getEmail()).isEqualTo("email@temp.com");
    }

    @WithMockUser
    @Test
    public void 물품정보수정() throws Exception {
        //given
        articleRegisterProc();

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
    public void 물품삭제() throws Exception {
        //given
        articleRegisterProc();
        Long articleId = articleRepository.findAll().get(0).getId();

        //when
        mockMvc.perform(delete("/article/" + articleId));

        //then
        User user = userRepository.findAll().get(0);

        assertThat(articleRepository.findAll().size()).isEqualTo(0);
        assertThat(user.getSellArticles().size()).isEqualTo(0);
    }

    @Test
    public void 물품페이지로딩() throws Exception {
        //given
        articleRegisterProc();
        Long articleId = articleRepository.findAll().get(0).getId();

        //when & then
        mockMvc.perform(get("/article/" + articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.picture").value("사진"))
                .andExpect(jsonPath("$.category").value("카테고리"))
                .andExpect(jsonPath("$.views").value(1))
                .andExpect(jsonPath("$.sold").value(false))
                .andExpect(jsonPath("$.dealHopeAddress.city").value("희망주소1"))
                .andExpect(jsonPath("$.dealHopeAddress.street").value("희망주소2"))
                .andExpect(jsonPath("$.dealHopeAddress.zipcode").value("희망주소3"))
                .andExpect(jsonPath("$.seller").value("박이름"));
    }

    @Test
    public void 잘못된물품페이지로딩() throws Exception {
        //given
        articleRegisterProc();
        Long articleId = articleRepository.findAll().get(0).getId();

        //when & then

        MvcResult result = mockMvc.perform(get("/article/" + articleId + 1)).andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("articleNotFound");
    }

    @Test
    public void 주소에맞는물품리스트로딩() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            RequestArticleSaveOrUpdateDto dto = articleRegisterProc();
            dto.setDealHopeAddress(new Address("희망주소" + i / 3, "희망주소" + i / 3, "희망주소" + i / 3));
        }
        /*
        물품 지역 리스트
        000
        111
        222
        3
         */

        // 검색 로직 미완성으로 보류
        //    @GetMapping("/articles/{city}/{street}/{zipcode}")
        //    public List<ResponseArticleDto> getArticlePage(@PageableDefault(sort = "createdTime", size = 6, direction = Sort.Direction.DESC) Pageable pageable,
        //                                                   @PathVariable(required = false) String city,
        //                                                   @PathVariable(required = false) String street,
        //                                                   @PathVariable(required = false) String zipcode) {
    }

    @Test
    public void 인기순위검색() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            articleRegisterProc();
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
        String str = result.getResponse().getContentAsString();
        String[] strs = str.split(",{");
    }

//    @GetMapping("/hot-articles")
//    public List<ResponseArticleDto> getHotArticlePage(@PageableDefault(size = 6) Pageable pageable) {
//    @GetMapping("/search")
//    public List<ResponseArticleDto> getSearchPage(@PageableDefault(size = 6) Pageable pageable,
//                                                  @RequestParam("keyword") String title) {

    public RequestArticleSaveOrUpdateDto articleRegisterProc() throws Exception {
        RequestArticleSaveOrUpdateDto dto = new RequestArticleSaveOrUpdateDto();
        dto.setTitle("제목");
        dto.setCategory("카테고리");
        dto.setContent("내용");
        dto.setPicture("사진");
        dto.setDealHopeAddress(new Address("희망주소1", "희망주소2", "희망주소3"));
        dto.setPrice(10000);

        //when
        mockMvc.perform(post("/article").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(dto))).andExpect(status().isOk());

        return dto;
    }

    public void watchArticle(int idx, int count) throws Exception { // 조회수 증가
        Long articleId = articleRepository.findAll().get(idx).getId();

        for (int i = 0; i < count; i++) {
            mockMvc.perform(get("/article/" + articleId));
        }
    }
}
