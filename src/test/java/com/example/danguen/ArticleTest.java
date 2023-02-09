package com.example.danguen;


import com.example.danguen.config.exception.ArticleNotFoundException;
import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.image.Image;
import com.example.danguen.domain.model.post.article.Article;
import com.example.danguen.domain.model.post.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.model.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ArticleTest extends BaseTest {

    @WithMockUser
    @Test
    public void 정상적인_물품_등록하기() throws Exception {
        //given & when
        articleSaveProc(0);

        //then
        Article article = articleRepository.findAll().get(0);
        Image image = imageRepository.findAll().get(0);

        assertThat(article.getTitle()).isEqualTo(title + 0);
        assertThat(article.getCategory()).isEqualTo(category);
        assertThat(article.getContent()).isEqualTo(articleContent);
        assertThat(article.getImages().size()).isEqualTo(1);
        assertThat(article.getDealHopeAddress().getCity()).isEqualTo(hopeCity + 0);
        assertThat(article.getDealHopeAddress().getStreet()).isEqualTo(hopeStreet + 0);
        assertThat(article.getDealHopeAddress().getZipcode()).isEqualTo(hopeZipcode + 0);
        assertThat(article.getPrice()).isEqualTo(10000);
        assertThat(article.getSeller().getName()).isEqualTo(sessionName);
        assertThat(article.getSeller().getEmail()).isEqualTo(sessionEmail);
        assertThat(article.getImages().get(0).getName()).isEqualTo(image.getName());
    }

    @WithMockUser
    @Test
    public void 물품_정보수정() throws Exception {
        //given
        articleSaveProc(0);

        RequestArticleSaveOrUpdateDto dto = new RequestArticleSaveOrUpdateDto();
        dto.setTitle("new제목");
        dto.setCategory("new카테고리");
        dto.setContent("new내용");
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
        articleSaveProc(0);
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
        articleSaveProc(0);
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
        articleSaveProc(0);
        Long articleId = articleRepository.findAll().get(0).getId();

        //when & then
        mockMvc.perform(get("/article/" + articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title + 0))
                .andExpect(jsonPath("$.content").value(articleContent))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.category").value(category))
                .andExpect(jsonPath("$.views").value(1))
                .andExpect(jsonPath("$.sold").value(false))
                .andExpect(jsonPath("$.dealHopeAddress.city").value(hopeCity + 0))
                .andExpect(jsonPath("$.dealHopeAddress.street").value(hopeStreet + 0))
                .andExpect(jsonPath("$.dealHopeAddress.zipcode").value(hopeZipcode + 0))
                .andExpect(jsonPath("$.seller").value(sessionName));
    }

    @Test
    public void 잘못된_물품페이지로딩() throws Exception {
        //given
        articleSaveProc(0);
        Long articleId = articleRepository.findAll().get(0).getId();

        //when
        MvcResult result = mockMvc.perform(get("/article/" + articleId + 1)).andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).contains(ArticleNotFoundException.message);
    }

    @Test
    public void 주소에_맞는_물품_리스트로딩() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            articleSaveProc(i);
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
                .andExpect(jsonPath("$[0].title").value(title + 9))
                .andExpect(jsonPath("$[5].title").value(title + 4)); // 검색 결과는 최신순으로 내림차순한다

        //같은 검색결과를 반환하는지 확인
        mockMvc.perform(get("/address/희망주소1/희망주소1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[2]").exists()) // 3개의 검색 결과가 존재해야한다
                .andExpect(jsonPath("$[3]").doesNotExist())
                .andExpect(jsonPath("$[0].title").value(title + 5))
                .andExpect(jsonPath("$[2].title").value(title + 3)); // 검색 결과는 최신순으로 내림차순한다

        //단 하나의 단독결과만 반환하는지 확인
        mockMvc.perform(get("/address/희망주소1/희망주소1/희망주소3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists()) // 단 하나의 결과만 출력해야한다
                .andExpect(jsonPath("$[1]").doesNotExist())
                .andExpect(jsonPath("$[0].title").value(title + 3)); // 검색 결과는 최신순으로 내림차순한다

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
            articleSaveProc(i);
        }

        // 첫번째 페이지
        mockMvc.perform(get("/search?size=3&page=0&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((title + 9)))
                .andExpect(jsonPath("$[1].title").value((title + 8)))
                .andExpect(jsonPath("$[2].title").value((title + 7)))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 두번째 페이지
        mockMvc.perform(get("/search?size=3&page=1&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((title + 6)))
                .andExpect(jsonPath("$[1].title").value((title + 5)))
                .andExpect(jsonPath("$[2].title").value((title + 4)))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 세번째 페이지
        mockMvc.perform(get("/search?size=3&page=2&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((title + 3)))
                .andExpect(jsonPath("$[1].title").value((title + 2)))
                .andExpect(jsonPath("$[2].title").value((title + 1)))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 마지막 페이지
        mockMvc.perform(get("/search?size=3&page=3&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((title + 0)))
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    public void 인기순위검색() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            articleSaveProc(i);
        }
        // 물품 리스트 0 ~ 9
        // 이 중에 1 4 5 8의 조회수를 높혀보자 높은 순서대로 5 8 1 4이다
        watchArticle(1, 4);
        watchArticle(4, 2);
        watchArticle(5, 10);
        watchArticle(8, 7);

        //when
        mockMvc.perform(get("/hot-articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(title + 5))
                .andExpect(jsonPath("$[1].title").value(title + 8))
                .andExpect(jsonPath("$[2].title").value(title + 1))
                .andExpect(jsonPath("$[3].title").value(title + 4));
    }


    @Test
    public void 제목연관검색() throws Exception {
        //given
        for (int i = 0; i < 5; i++) {
            articleSaveProc(7777);
        } // 검색 결과가 5개 나오는가?
        for (int i = 0; i < 10; i++) {
            articleSaveProc(10000);
        } // 검색 결과가 6개로 나뉘어 나오는가?
        for (int i = 0; i < 3; i++) {
            articleSaveProc(777);
        } // 검색 결과가 777 3개 + 7777 3개 이렇게 나오는가? 아니면 어떻게 나오는가? -> 최신순으로 정렬하자

        String keyword = "7777";
        //when
        mockMvc.perform(get("/search?keyword=" + keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists())
                .andExpect(jsonPath("$[2]").exists())
                .andExpect(jsonPath("$[3]").exists())
                .andExpect(jsonPath("$[4]").exists())
                .andExpect(jsonPath("$[5]").doesNotExist())
                .andExpect(jsonPath("$[0].title").value(title + 7777))
                .andExpect(jsonPath("$[1].title").value(title + 7777))
                .andExpect(jsonPath("$[2].title").value(title + 7777))
                .andExpect(jsonPath("$[3].title").value(title + 7777))
                .andExpect(jsonPath("$[4].title").value(title + 7777))
                .andReturn();

        keyword = "10000";

        mockMvc.perform(get("/search?keyword=" + keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists())
                .andExpect(jsonPath("$[2]").exists())
                .andExpect(jsonPath("$[3]").exists())
                .andExpect(jsonPath("$[4]").exists())
                .andExpect(jsonPath("$[5]").exists())
                .andExpect(jsonPath("$[6]").doesNotExist())
                .andExpect(jsonPath("$[0].title").value(title + 10000))
                .andExpect(jsonPath("$[1].title").value(title + 10000))
                .andExpect(jsonPath("$[2].title").value(title + 10000))
                .andExpect(jsonPath("$[3].title").value(title + 10000))
                .andExpect(jsonPath("$[4].title").value(title + 10000))
                .andExpect(jsonPath("$[5].title").value(title + 10000))
                .andReturn();

        keyword = "777";

        mockMvc.perform(get("/search?keyword=" + keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists())
                .andExpect(jsonPath("$[2]").exists())
                .andExpect(jsonPath("$[3]").exists())
                .andExpect(jsonPath("$[4]").exists())
                .andExpect(jsonPath("$[5]").exists())
                .andExpect(jsonPath("$[6]").doesNotExist())
                .andExpect(jsonPath("$[0].title").value(title + 777))
                .andExpect(jsonPath("$[1].title").value(title + 777))
                .andExpect(jsonPath("$[2].title").value(title + 777))
                .andExpect(jsonPath("$[3].title").value(title + 7777))
                .andExpect(jsonPath("$[4].title").value(title + 7777))
                .andExpect(jsonPath("$[5].title").value(title + 7777))
                .andReturn();
    }

    @WithMockUser
    @Test
    public void 관심유저_게시글출력() throws Exception {
        //given
        User other = makeUserProc("홍길동", "hong@namver.com");

        for (int i = 0; i < 3; i++) {
            noneSessionsArticleSaveProc(other, i);
        }

        other = makeUserProc("삼순이", "sam@toto.com");

        for (int i = 0; i < 10; i++) {
            noneSessionsArticleSaveProc(other, i);
        }

        other = makeUserProc("고구마", "potato@vege.com");

        for (int i = 0; i < 4; i++) {
            noneSessionsArticleSaveProc(other, i);
        }

        User user = userRepository.findByEmail(sessionEmail).get();

        // 홍길동과 고구마를 관심유저로 등록하자
        Long interestUserId1 = userRepository.findByEmail("hong@namver.com").get().getId();
        Long interestUserId2 = userRepository.findByEmail("potato@vege.com").get().getId();

        mockMvc.perform(put("/user/iuser/" + interestUserId1));
        mockMvc.perform(put("/user/iuser/" + interestUserId2));

        //when
        mockMvc.perform(get("/interest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("제목 홍길동"))
                .andExpect(jsonPath("$[1].title").value("제목 홍길동"))
                .andExpect(jsonPath("$[2].title").value("제목 홍길동"))
                .andExpect(jsonPath("$[3].title").value("제목 고구마"))
                .andExpect(jsonPath("$[4].title").value("제목 고구마"))
                .andExpect(jsonPath("$[5].title").value("제목 고구마"))
                .andExpect(jsonPath("$[0].price").value("0"))
                .andExpect(jsonPath("$[1].price").value("10000"))
                .andExpect(jsonPath("$[2].price").value("20000"))
                .andExpect(jsonPath("$[3].price").value("0"))
                .andExpect(jsonPath("$[4].price").value("10000"))
                .andExpect(jsonPath("$[5].price").value("20000"));
    }

    public void watchArticle(int idx, int count) throws Exception { // 조회수 증가
        Long articleId = articleRepository.findAll().get(idx).getId();

        for (int i = 0; i < count; i++) {
            mockMvc.perform(get("/article/" + articleId));
        }
    }
}
