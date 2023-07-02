package com.example.danguen.post.article;

import com.example.danguen.BaseTest;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.image.exception.PostNotFoundException;
import com.example.danguen.domain.post.dto.response.ResponseArticleDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicArticlePostTest extends BaseTest {

    private final String urlPrefix = "/public/post";

    @DisplayName("물품 정보 요청")
    @Test
    public void successLoadSimpleArticleInfo() throws Exception {
        //given
        LocalDateTime testTime = LocalDateTime.now();
        Thread.sleep(500);

        Long articleId = makeArticle(0, loginUserId);

        //when
        MvcResult result = mockMvc.perform(get(urlPrefix + "/article/" + articleId))
                .andExpect(status().isOk())
                .andReturn();

        //then
        ResponseArticleDto articleDto
                = mapper.readValue(result.getResponse().getContentAsString(), ResponseArticleDto.class);

        assertThat(articleDto.getId()).isEqualTo(articleId);
        assertThat(articleDto.getTitle()).isEqualTo(articleTitle + 0);
        assertThat(articleDto.getContent()).isEqualTo(articleContent);
        assertThat(articleDto.getPrice()).isEqualTo(articlePrice);
        assertThat(articleDto.isSold()).isEqualTo(false);
        assertThat(articleDto.getDealHopeAddress()).isEqualTo(
                new Address(articleCity + 0, articleStreet + 0, articleZipcode + 0)
        );
        assertThat(articleDto.getWriter()).isEqualTo(loginUserName);
        assertThat(articleDto.getWrittenTime()).isAfter(testTime);
    }

    @DisplayName("존재하지 않는 중고물품 정보 요청")
    @Test
    public void failLoadNonExistArticleInfo() throws Exception {
        //when
        MvcResult result = mockMvc.perform(get(urlPrefix + "/article/999999999"))
                .andExpect(status().isNotFound())
                .andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).contains(PostNotFoundException.message);
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
    int articleSize = 10;


    @DisplayName("주소 없이 주소별 중고물품 리스트 요청")
    @Test
    public void successAddressSearchArticleListWithOutAddress() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeArticle(i, loginUserId);
        }

        int[] expectPostIndex = new int[]{9, 8, 7, 6, 5, 4};

        //when
        MvcResult result = mockMvc.perform(get(urlPrefix + "/address"))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = mappingResponse(result, ResponseArticleSimpleDto.class);

        // then
        assertThat(responseList.size()).isEqualTo(6);
        for (int i = 0; i < responseList.size(); i++) {
            ResponseArticleSimpleDto res = responseList.get(i);

            assertThat(res.getTitle()).isEqualTo(articleTitle + expectPostIndex[i]);
            assertThat(res.getPrice()).isEqualTo(articlePrice);
            assertThat(res.getLikeCount()).isEqualTo(0);
            assertThat(res.getChatCount()).isEqualTo(0); //todo
            assertThat(res.getCommentCount()).isEqualTo(0);
            assertThat(res.getDealHopeAddress()).isEqualTo(
                    new Address(
                            articleCity + expectPostIndex[i] / 3,
                            articleStreet + expectPostIndex[i] / 3,
                            articleZipcode + expectPostIndex[i])
            );
        }
    }

    @DisplayName("도시만을 포함하는 주소별 중고물품 리스트 요청")
    @Test
    public void successAddressSearchArticleListWithCity() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeArticle(i, loginUserId);
        }

        int[] expectPostIndex = new int[]{5, 4, 3};

        //when
        MvcResult result = mockMvc.perform(get(urlPrefix + "/address/희망주소1"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = mappingResponse(result, ResponseArticleSimpleDto.class);

        // then
        assertThat(responseList.size()).isEqualTo(3);
        for (int i = 0; i < responseList.size(); i++) {
            ResponseArticleSimpleDto res = responseList.get(i);

            assertThat(res.getTitle()).isEqualTo(articleTitle + expectPostIndex[i]);
            assertThat(res.getPrice()).isEqualTo(articlePrice);
            assertThat(res.getLikeCount()).isEqualTo(0);
            assertThat(res.getChatCount()).isEqualTo(0); //todo
            assertThat(res.getCommentCount()).isEqualTo(0);
            assertThat(res.getDealHopeAddress()).isEqualTo(
                    new Address(
                            "희망주소1",
                            articleStreet + expectPostIndex[i] / 3,
                            articleZipcode + expectPostIndex[i])
            );
        }
    }

    @DisplayName("도시, 도로명만을 포함하는 주소별 중고물품 리스트 요청")
    @Test
    public void successAddressSearchArticleListWithCityAndStreet() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeArticle(i, loginUserId);
        }

        int[] expectPostIndex = new int[]{2, 1, 0};

        //when
        MvcResult result = mockMvc.perform(get(urlPrefix + "/address/희망주소0/희망주소0"))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = mappingResponse(result, ResponseArticleSimpleDto.class);

        // then
        assertThat(responseList.size()).isEqualTo(3);
        for (int i = 0; i < responseList.size(); i++) {
            ResponseArticleSimpleDto res = responseList.get(i);

            assertThat(res.getTitle()).isEqualTo(articleTitle + expectPostIndex[i]);
            assertThat(res.getPrice()).isEqualTo(articlePrice);
            assertThat(res.getLikeCount()).isEqualTo(0);
            assertThat(res.getChatCount()).isEqualTo(0); //todo
            assertThat(res.getCommentCount()).isEqualTo(0);
            assertThat(res.getDealHopeAddress()).isEqualTo(
                    new Address(
                            "희망주소0",
                            "희망주소0",
                            articleZipcode + expectPostIndex[i])
            );
        }
    }

    @DisplayName("모든 주소를 포함하는 주소별 중고물품 리스트 요청")
    @Test
    public void successAddressSearchArticleListWithFullAddress() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeArticle(i, loginUserId);
        }

        int[] expectPostIndex = new int[]{2};

        //when
        MvcResult result = mockMvc.perform(get(urlPrefix + "/address/희망주소0/희망주소0/희망주소2"))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = mappingResponse(result, ResponseArticleSimpleDto.class);

        // then
        assertThat(responseList.size()).isEqualTo(1);
        for (int i = 0; i < responseList.size(); i++) {
            ResponseArticleSimpleDto res = responseList.get(i);

            assertThat(res.getTitle()).isEqualTo(articleTitle + expectPostIndex[i]);
            assertThat(res.getPrice()).isEqualTo(articlePrice);
            assertThat(res.getLikeCount()).isEqualTo(0);
            assertThat(res.getChatCount()).isEqualTo(0); //todo
            assertThat(res.getCommentCount()).isEqualTo(0);
            assertThat(res.getDealHopeAddress()).isEqualTo(
                    new Address(
                            "희망주소0",
                            "희망주소0",
                            "희망주소2")
            );
        }

    }

    @DisplayName("존재하지않는 주소별 중고물품 리스트 요청")
    @Test
    public void successAddressSearchArticleListWithNonExistAddress() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeArticle(i, loginUserId);
        }

        //when
        MvcResult result = mockMvc.perform(get(urlPrefix + "/address/none/exist/address"))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = mappingResponse(result, ResponseArticleSimpleDto.class);

        // then
        assertThat(responseList).isEmpty();
    }

    @DisplayName("중고물품 리스트 결과 페이지 분할")
    @Test
    public void successLoadArticleListSplit() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeArticle(i, loginUserId);
        }

        // 첫번째 페이지
        mockMvc.perform(get(urlPrefix + "/search?size=3&page=0&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((articleTitle + 9)))
                .andExpect(jsonPath("$[1].title").value((articleTitle + 8)))
                .andExpect(jsonPath("$[2].title").value((articleTitle + 7)))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 두번째 페이지
        mockMvc.perform(get(urlPrefix + "/search?size=3&page=1&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((articleTitle + 6)))
                .andExpect(jsonPath("$[1].title").value((articleTitle + 5)))
                .andExpect(jsonPath("$[2].title").value((articleTitle + 4)))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 세번째 페이지
        mockMvc.perform(get(urlPrefix + "/search?size=3&page=2&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((articleTitle + 3)))
                .andExpect(jsonPath("$[1].title").value((articleTitle + 2)))
                .andExpect(jsonPath("$[2].title").value((articleTitle + 1)))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 마지막 페이지
        mockMvc.perform(get(urlPrefix + "/search?size=3&page=3&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((articleTitle + 0)))
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @DisplayName("조회수 증가")
    @Test
    public void successIncreaseArticleView() throws Exception {
        Long articleId = makeArticle(0, loginUserId);

        mockMvc.perform(get(urlPrefix + "/article/" + articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.views").value(1));
    }

    @DisplayName("인기 순위별 리스트 요청")
    @Test
    public void successLoadPopularArticleList() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            makeArticle(i, loginUserId);
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
        MvcResult result = mockMvc.perform(get(urlPrefix + "/hot-articles"))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = mappingResponse(result, ResponseArticleSimpleDto.class);

        //then
        assertThat(responseList.size()).isEqualTo(6); // 페이지 사이즈만큼 출력
        for (int i = 0; i < 4; i++) {
            assertThat(responseList.get(i).getTitle()).isEqualTo(articleTitle + targetIndex[i]);
        }
    }

    @DisplayName("검색어 전체를 포함하는 제목을 가진 중고물풀 리스트 검색")
    @Test
    public void successSearchArticleWithFullPartOfKeyword() throws Exception {
        //given
        int keyword = 12345;
        for (int i = 0; i < 5; i++) {
            makeArticle(12345, loginUserId);
        }
        for (int i = 0; i < 10; i++) {
            makeArticle(55555, loginUserId);
        }
        for (int i = 0; i < 3; i++) {
            makeArticle(992345555, loginUserId);
        }

        //when
        MvcResult result = mockMvc.perform(get(urlPrefix + "/search?keyword=" + keyword))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = mappingResponse(result, ResponseArticleSimpleDto.class);

        //then
        assertThat(responseList.size()).isEqualTo(5);
        for (var res : responseList) {
            assertThat(res.getTitle()).isEqualTo(articleTitle + keyword);
        }
    }

    @DisplayName("검색어 일부를 포함하는 제목을 가진 중고물풀 리스트 검색")
    @Test
    public void successSearchArticleWithPartOfKeyword() throws Exception {
        //given
        int keyword = 234;
        for (int i = 0; i < 5; i++) {
            makeArticle(12345, loginUserId);
        }
        for (int i = 0; i < 10; i++) {
            makeArticle(55555, loginUserId);
        }
        for (int i = 0; i < 3; i++) {
            makeArticle(992345555, loginUserId);
        }

        //when
        MvcResult result = mockMvc.perform(get(urlPrefix + "/search?keyword=" + keyword))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = mappingResponse(result, ResponseArticleSimpleDto.class);

        //then
        assertThat(responseList.size()).isEqualTo(6);
        for (var res : responseList) {
            assertThat(res.getTitle()).contains(String.valueOf(keyword));
        }
    }

    public void watchArticle(int idx, int count) throws Exception { // 조회수 증가
        Long articleId = postRepository.findAll().get(idx).getId();

        for (int i = 0; i < count; i++) {
            mockMvc.perform(get(urlPrefix + "/article/" + articleId));
        }
    }
}
