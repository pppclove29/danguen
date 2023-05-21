package com.example.danguen;


import com.example.danguen.domain.image.exception.ArticleNotFoundException;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.image.entity.Image;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ArticlePostTest extends BaseTest {

    @DisplayName("정상적인 물품 등록하기")
    @WithMockUser
    @Test
    public void 정상적인_물품_등록하기() throws Exception {
        //given & when
        articleSaveProc(0);

        //then
        ArticlePost articlePost = articlePostRepository.findAll().get(0);
        Image image1 = articleImageRepository.findAll().get(0);

        assertThat(articlePost.getTitle()).isEqualTo(title + 0);
        assertThat(articlePost.getCategory()).isEqualTo(category);
        assertThat(articlePost.getContent()).isEqualTo(articleContent);
        assertThat(articlePost.getImages().size()).isEqualTo(1);
        assertThat(articlePost.getDealHopeAddress().getCity()).isEqualTo(hopeCity + 0);
        assertThat(articlePost.getDealHopeAddress().getStreet()).isEqualTo(hopeStreet + 0);
        assertThat(articlePost.getDealHopeAddress().getZipcode()).isEqualTo(hopeZipcode + 0);
        assertThat(articlePost.getPrice()).isEqualTo(10000);
        assertThat(articlePost.getSeller().getName()).isEqualTo(sessionName);
        assertThat(articlePost.getSeller().getEmail()).isEqualTo(sessionEmail);
        assertThat(articlePost.getImages().get(0).getName()).isEqualTo(image1.getName());
        assertThat(articlePost.getImages().size()).isEqualTo(1);
    }

    @WithMockUser
    @Test
    public void 이미지_다수_포함_물품등록() throws Exception {
        //given
        RequestArticleSaveOrUpdateDto dto = RequestArticleSaveOrUpdateDto.builder()
                .title(title)
                .content(articleContent)
                .price(price)
                .category(category)
                .dealHopeAddress(new Address(hopeCity, hopeStreet, hopeZipcode))
                .build();

        String dtoJson = new ObjectMapper().writeValueAsString(dto);
        MockMultipartFile request = new MockMultipartFile(
                "request",
                "request",
                "application/json",
                dtoJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile image1 = new MockMultipartFile(
                "images",
                "input1.png",
                "image/png",
                new FileInputStream("src/test/java/testImage/input.png"));

        MockMultipartFile image2 = new MockMultipartFile(
                "images",
                "input2.png",
                "image/png",
                new FileInputStream("src/test/java/testImage/input.png"));

        MockMultipartFile image3 = new MockMultipartFile(
                "images",
                "input3.png",
                "image/png",
                new FileInputStream("src/test/java/testImage/input.png"));

        //when
        mockMvc.perform(multipart("/article")
                        .file(image1)
                        .file(image2)
                        .file(image3)
                        .file(request)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        //then
        ArticlePost articlePost = articlePostRepository.findAll().get(0);

        assertThat(articlePost.getImages().size()).isEqualTo(3);
        assertThat(articlePost.getImages().get(0).getName()).isEqualTo("input1.png");
        assertThat(articlePost.getImages().get(1).getName()).isEqualTo("input2.png");
        assertThat(articlePost.getImages().get(2).getName()).isEqualTo("input3.png");
        assertThat(articleImageRepository.findAll().size()).isEqualTo(3); // article 3
    }

    @WithMockUser
    @Test
    public void 물품_정보수정() throws Exception {
        //given
        articleSaveProc(0);

        RequestArticleSaveOrUpdateDto dto = RequestArticleSaveOrUpdateDto.builder()
                .title("new제목")
                .content("new내용")
                .price(30000)
                .category("new카테고리")
                .dealHopeAddress(new Address("new희망주소1", "new희망주소2", "new희망주소3"))
                .build();

        Long articleId = articlePostRepository.findAll().get(0).getId();

        //when
        mockMvc.perform(put("/article/" + articleId).contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(dto))).andExpect(status().isOk());

        //then
        ArticlePost articlePost = articlePostRepository.findById(articleId).get();
        assertThat(articlePost.getTitle()).isEqualTo("new제목");
        assertThat(articlePost.getCategory()).isEqualTo("new카테고리");
        assertThat(articlePost.getContent()).isEqualTo("new내용");
        assertThat(articlePost.getDealHopeAddress().getCity()).isEqualTo("new희망주소1");
        assertThat(articlePost.getDealHopeAddress().getStreet()).isEqualTo("new희망주소2");
        assertThat(articlePost.getDealHopeAddress().getZipcode()).isEqualTo("new희망주소3");
        assertThat(articlePost.getPrice()).isEqualTo(30000);
        assertThat(articlePost.getSeller().getEmail()).isEqualTo("email@temp.com");
    }

    @WithMockUser
    @Test
    public void 물품_삭제() throws Exception {
        //given
        articleSaveProc(0);
        Long articleId = articlePostRepository.findAll().get(0).getId();


        //when
        mockMvc.perform(delete("/article/" + articleId));

        //then
        User user = userRepository.findAll().get(0);

        assertThat(articlePostRepository.findAll().size()).isEqualTo(0);
        assertThat(user.getSellArticlePosts().size()).isEqualTo(0);
    }

    @Test
    public void 등록자_회원탈퇴시_중고물품_처리테스트() throws Exception {
        //given
        articleSaveProc(0);
        Long userId = userRepository.findAll().get(0).getId();

        //when
        mockMvc.perform(delete("/user/" + userId));

        //then
        assertThat(articlePostRepository.findAll().size()).isEqualTo(0);
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void 물품_페이지로딩() throws Exception {
        //given
        articleSaveProc(0);
        Long articleId = articlePostRepository.findAll().get(0).getId();

        //when
        MvcResult result = mockMvc.perform(get("/article/" + articleId))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(result.getModelAndView().getModel().get("article")).isInstanceOf(ResponseArticleDto.class);

        ResponseArticleDto article = (ResponseArticleDto) result.getModelAndView().getModel().get("article");

        assertThat(article.getTitle()).isEqualTo(title + 0);
        assertThat(article.getContent()).isEqualTo(articleContent);
        assertThat(article.getPrice()).isEqualTo(10000);
        assertThat(article.getCategory()).isEqualTo(category);
        assertThat(article.getViews()).isEqualTo(1);
        assertThat(article.isSold()).isEqualTo(false);
        assertThat(article.getDealHopeAddress().getCity()).isEqualTo(hopeCity + 0);
        assertThat(article.getDealHopeAddress().getStreet()).isEqualTo(hopeStreet + 0);
        assertThat(article.getDealHopeAddress().getZipcode()).isEqualTo(hopeZipcode + 0);
        assertThat(article.getSeller()).isEqualTo(sessionName);
    }

    @Test
    public void 잘못된_물품페이지로딩() throws Exception {
        //given
        articleSaveProc(0);
        Long articleId = articlePostRepository.findAll().get(0).getId();

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
        MvcResult result;
        List<ResponseArticleSimpleDto> articles;
        String objKey = "articles";

        result = mockMvc.perform(get("/address"))
                .andExpect(status().isOk())
                .andReturn(); // 검색 결과는 최신순으로 내림차순한다

        articles = objToList(result, objKey);

        assertThat(articles.size()).isEqualTo(6);
        assertThat(articles.get(0).getTitle()).isEqualTo(title + 9);
        assertThat(articles.get(5).getTitle()).isEqualTo(title + 4);

        //같은 검색결과를 반환하는지 확인
        result = mockMvc.perform(get("/address/희망주소1/희망주소1"))
                .andExpect(status().isOk())
                .andReturn();

        articles = objToList(result, objKey);

        assertThat(articles.size()).isEqualTo(3);
        assertThat(articles.get(0).getTitle()).isEqualTo(title + 5);
        assertThat(articles.get(2).getTitle()).isEqualTo(title + 3);

        //단 하나의 단독결과만 반환하는지 확인
        result = mockMvc.perform(get("/address/희망주소1/희망주소1/희망주소3"))
                .andExpect(status().isOk())
                .andReturn();

        articles = objToList(result, objKey);

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title + 3);

        //없는 주소를 입력시 반환값이 없는지 확인
        result = mockMvc.perform(get("/address/없는주소"))
                .andExpect(status().isOk())
                .andReturn();

        articles = objToList(result, objKey);

        assertThat(articles.size()).isEqualTo(0);

        //영어 url도 확인, 역시 없음
        result = mockMvc.perform(get("/address/no-address"))
                .andExpect(status().isOk())
                .andReturn();

        articles = objToList(result, objKey);

        assertThat(articles.size()).isEqualTo(0);
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
        // 즉 인기리스트 순서는 5 8 1 4 순으로 나와야한다
        int[] viewCount = new int[]{10, 7, 4, 2};
        int[] targetIndex = new int[]{5, 8, 1, 4};

        for (int i = 0; i < 4; i++) {
            watchArticle(targetIndex[i], viewCount[i]);
        }

        //when
        MvcResult result = mockMvc.perform(get("/hot-articles"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<ResponseArticleSimpleDto> articles = objToList(result, "articles");

        assertThat(articles.size()).isEqualTo(6); // 페이지 사이즈만큼 출력
        for (int i = 0; i < 4; i++) {
            assertThat(articles.get(i).getTitle()).isEqualTo(title + targetIndex[i]);
            assertThat(articles.get(i).getViews()).isEqualTo(viewCount[i]);
        }
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

        // 홍길동과 고구마를 관심유저로 등록하자
        Long interestUserId1 = userRepository.findByEmail("hong@namver.com").get().getId();
        Long interestUserId2 = userRepository.findByEmail("potato@vege.com").get().getId();

        mockMvc.perform(put("/user/iuser/" + interestUserId1));
        mockMvc.perform(put("/user/iuser/" + interestUserId2));

        //when
        MvcResult result = mockMvc.perform(get("/interest"))
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
                .andExpect(jsonPath("$[5].price").value("20000"))
                .andReturn();

        //then
        System.out.println(result.getResponse().getContentAsString());
    }

    public void watchArticle(int idx, int count) throws Exception { // 조회수 증가
        Long articleId = articlePostRepository.findAll().get(idx).getId();

        for (int i = 0; i < count; i++) {
            mockMvc.perform(get("/article/" + articleId));
        }
    }

    public <T> List<T> objToList(MvcResult result, String objKey) {
        List<T> resultList;

        assertThat(result.getModelAndView().getModel().get(objKey) instanceof ArrayList).isTrue();
        resultList = ((ArrayList<?>) result.getModelAndView().getModel().get(objKey)).stream().map(obj -> (T) obj).collect(Collectors.toList());

        return resultList;
    }
}
