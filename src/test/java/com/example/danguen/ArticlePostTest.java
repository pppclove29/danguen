package com.example.danguen;


import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.image.exception.ArticleNotFoundException;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.repository.ArticlePostRepository;
import com.example.danguen.domain.user.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//todo anonymousUser의 secured 접근에 대한 테스트 진행
//todo admin의 작업에 대한 anonymousUser, normal User, admin User의 접근 테스트 진행
public class ArticlePostTest extends BaseTest {

    @Autowired
    ArticlePostRepository articlePostRepository;

    @DisplayName("이미지 없이 중고물품 등록")
    @WithMockUser
    @Test
    public void failSaveArticleWithOutImages() throws Exception {
        //given
        Address dealAddress = new Address(articleCity, articleStreet, articleZipcode);

        RequestArticleSaveOrUpdateDto dto = RequestArticleSaveOrUpdateDto.builder()
                .title(articleTitle)
                .content(articleContent)
                .price(articlePrice)
                .category(articleCategory)
                .dealHopeAddress(dealAddress)
                .build();

        //when & then
        mockMvc.perform(multipart("/secured/article")
                        .param("request", mapper.writeValueAsString(dto))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is4xxClientError());
    }

    @Transactional
    @DisplayName("이미지가 포함된 중고물품 등록")
    @WithMockUser
    @Test
    public void successSaveArticleWithImages() throws Exception {
        //given
        RequestArticleSaveOrUpdateDto dto = RequestArticleSaveOrUpdateDto.builder()
                .title(articleTitle)
                .content(articleContent)
                .price(articlePrice)
                .category(articleCategory)
                .dealHopeAddress(new Address(articleCity, articleStreet, articleZipcode))
                .build();

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
        mockMvc.perform(multipart("/secured/article")
                        .file(image1)
                        .file(image2)
                        .file(image3)
                        .flashAttr("request", dto)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        //then
        ArticlePost articlePost = articlePostRepository.findAll().get(0);

        assertThat(articlePost.getImages().size()).isEqualTo(3);
    }

    @DisplayName("물품 정보 수정")
    @WithMockUser
    @Test
    public void successUpdateArticleInfo() throws Exception {
        //given
        Long articleId = makeArticle(0, sessionUserId);

        RequestArticleSaveOrUpdateDto dto = RequestArticleSaveOrUpdateDto.builder()
                .title("new" + articleTitle)
                .content("new" + articleContent)
                .price(30000)
                .category("new" + articleCategory)
                .dealHopeAddress(new Address(
                        "new" + articleCity,
                        "new" + articleStreet,
                        "new" + articleZipcode))
                .build();

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "input.png",
                "image/png",
                new FileInputStream("src/test/java/testImage/input.png"));


        //when
        mockMvc.perform(multipart(HttpMethod.PUT, "/secured/article/" + articleId)
                        .file(image)
                        .flashAttr("request", dto)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        //then
        ArticlePost articlePost = articleService.getArticleById(articleId);
        assertThat(articlePost.getTitle()).isEqualTo("new" + articleTitle);
        assertThat(articlePost.getCategory()).isEqualTo("new" + articleCategory);
        assertThat(articlePost.getContent()).isEqualTo("new" + articleContent);
        assertThat(articlePost.getDealHopeAddress()).isEqualTo(
                new Address("new" + articleCity, "new" + articleStreet, "new" + articleZipcode)
        );
        assertThat(articlePost.getPrice()).isEqualTo(30000);
    }

    @Transactional
    @DisplayName("중고물품 삭제")
    @WithMockUser
    @Test
    public void successDeleteArticle() throws Exception {
        //given
        Long articleId = makeArticle(0, sessionUserId);

        //when
        mockMvc.perform(delete("/secured/article/" + articleId))
                .andExpect(status().isOk());

        //then
        User user = userService.getUserById(sessionUserId);

        assertThat(articlePostRepository.findAll()).isEmpty();
        assertThat(user.getSellArticlePosts()).isEmpty();
    }

    @DisplayName("물품 정보 요청")
    @WithAnonymousUser
    @Test
    public void successLoadSimpleArticleInfo() throws Exception {
        //given
        LocalDateTime testTime = LocalDateTime.now();
        Thread.sleep(500);

        Long articleId = makeArticle(0, sessionUserId);

        //when
        MvcResult result = mockMvc.perform(get("/public/article/" + articleId))
                .andExpect(status().isOk())
                .andReturn();

        //then
        ResponseArticleDto articleDto = toArticleDto(result);

        assertThat(articleDto.getId()).isEqualTo(articleId);
        assertThat(articleDto.getTitle()).isEqualTo(articleTitle + 0);
        assertThat(articleDto.getContent()).isEqualTo(articleContent);
        assertThat(articleDto.getPrice()).isEqualTo(articlePrice);
        assertThat(articleDto.isSold()).isEqualTo(false);
        assertThat(articleDto.getDealHopeAddress()).isEqualTo(
                new Address(articleCity + 0, articleStreet + 0, articleZipcode + 0)
        );
        assertThat(articleDto.getSeller()).isEqualTo(sessionName);
        assertThat(articleDto.getWrittenTime()).isAfter(testTime);
    }

    @DisplayName("중고물품 등록자 회원탈퇴시 게시글 자동삭제")
    @WithMockUser
    @Test
    public void successAutoDeleteArticleWhenSellerWithdrawal() throws Exception {
        //given
        makeArticle(0, noneSessionUserId);

        //when
        mockMvc.perform(delete("/secured/user/" + noneSessionUserId));

        //then
        assertThat(articlePostRepository.findAll()).isEmpty();
    }

    @DisplayName("존재하지 않는 중고물품 정보 요청")
    @WithAnonymousUser
    @Test
    public void failLoadNonExistArticleInfo() throws Exception {
        //when
        MvcResult result = mockMvc.perform(get("/public/article/999999999")).andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).contains(ArticleNotFoundException.message);
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
    @WithAnonymousUser
    @Test
    public void successAddressSearchArticleListWithOutAddress() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeArticle(i, sessionUserId);
        }

        int[] expectPostIndex = new int[]{9, 8, 7, 6, 5, 4};

        //when
        MvcResult result = mockMvc.perform(get("/public/address"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        List<ResponseArticleSimpleDto> responseList = mapper.readValue(responseBody, new TypeReference<>() {
        });

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
            makeArticle(i, sessionUserId);
        }

        int[] expectPostIndex = new int[]{5, 4, 3};

        //when
        MvcResult result = mockMvc.perform(get("/public/address/희망주소1"))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = toSimpleArticleList(result);

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
    @WithAnonymousUser
    @Test
    public void successAddressSearchArticleListWithCityAndStreet() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeArticle(i, sessionUserId);
        }

        int[] expectPostIndex = new int[]{2, 1, 0};

        //when
        MvcResult result = mockMvc.perform(get("/public/address/희망주소0/희망주소0"))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = toSimpleArticleList(result);

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
    @WithAnonymousUser
    @Test
    public void successAddressSearchArticleListWithFullAddress() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeArticle(i, sessionUserId);
        }

        int[] expectPostIndex = new int[]{2};

        //when
        MvcResult result = mockMvc.perform(get("/public/address/희망주소0/희망주소0/희망주소2"))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = toSimpleArticleList(result);

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
    @WithAnonymousUser
    @Test
    public void successAddressSearchArticleListWithNonExistAddress() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeArticle(i, sessionUserId);
        }

        //when
        MvcResult result = mockMvc.perform(get("/public/address/none/exist/address"))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = toSimpleArticleList(result);

        // then
        assertThat(responseList).isEmpty();
    }

    @DisplayName("중고물품 리스트 결과 페이지 분할")
    @WithAnonymousUser
    @Test
    public void successLoadArticleListSplit() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeArticle(i, sessionUserId);
        }

        // 첫번째 페이지
        mockMvc.perform(get("/public/search?size=3&page=0&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((articleTitle + 9)))
                .andExpect(jsonPath("$[1].title").value((articleTitle + 8)))
                .andExpect(jsonPath("$[2].title").value((articleTitle + 7)))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 두번째 페이지
        mockMvc.perform(get("/public/search?size=3&page=1&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((articleTitle + 6)))
                .andExpect(jsonPath("$[1].title").value((articleTitle + 5)))
                .andExpect(jsonPath("$[2].title").value((articleTitle + 4)))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 세번째 페이지
        mockMvc.perform(get("/public/search?size=3&page=2&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((articleTitle + 3)))
                .andExpect(jsonPath("$[1].title").value((articleTitle + 2)))
                .andExpect(jsonPath("$[2].title").value((articleTitle + 1)))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 마지막 페이지
        mockMvc.perform(get("/public/search?size=3&page=3&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((articleTitle + 0)))
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @DisplayName("조회수 증가")
    @WithAnonymousUser
    @Test
    public void successIncreaseArticleView() throws Exception {
        Long articleId = makeArticle(0, sessionUserId);

        mockMvc.perform(get("/public/article/" + articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.views").value(1));
    }

    @DisplayName("인기 순위별 리스트 요청")
    @WithAnonymousUser
    @Test
    public void successLoadPopularArticleList() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            makeArticle(i, sessionUserId);
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
        MvcResult result = mockMvc.perform(get("/public/hot-articles"))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = toSimpleArticleList(result);

        //then
        assertThat(responseList.size()).isEqualTo(6); // 페이지 사이즈만큼 출력
        for (int i = 0; i < 4; i++) {
            assertThat(responseList.get(i).getTitle()).isEqualTo(articleTitle + targetIndex[i]);
        }
    }

    @DisplayName("검색어 전체를 포함하는 제목을 가진 중고물풀 리스트 검색")
    @WithAnonymousUser
    @Test
    public void successSearchArticleWithFullPartOfKeyword() throws Exception {
        //given
        int keyword = 12345;
        for (int i = 0; i < 5; i++) {
            makeArticle(12345, sessionUserId);
        }
        for (int i = 0; i < 10; i++) {
            makeArticle(55555, sessionUserId);
        }
        for (int i = 0; i < 3; i++) {
            makeArticle(992345555, sessionUserId);
        }

        //when
        MvcResult result = mockMvc.perform(get("/public/search?keyword=" + keyword))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = toSimpleArticleList(result);

        //then
        assertThat(responseList.size()).isEqualTo(5);
        for (var res : responseList) {
            assertThat(res.getTitle()).isEqualTo(articleTitle + keyword);
        }
    }

    @DisplayName("검색어 일부를 포함하는 제목을 가진 중고물풀 리스트 검색")
    @WithAnonymousUser
    @Test
    public void successSearchArticleWithPartOfKeyword() throws Exception {
        //given
        int keyword = 234;
        for (int i = 0; i < 5; i++) {
            makeArticle(12345, sessionUserId);
        }
        for (int i = 0; i < 10; i++) {
            makeArticle(55555, sessionUserId);
        }
        for (int i = 0; i < 3; i++) {
            makeArticle(992345555, sessionUserId);
        }

        //when
        MvcResult result = mockMvc.perform(get("/public/search?keyword=" + keyword))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = toSimpleArticleList(result);

        //then
        assertThat(responseList.size()).isEqualTo(6);
        for (var res : responseList) {
            assertThat(res.getTitle()).contains(String.valueOf(keyword));
        }
    }

    @DisplayName("관심 유저의 중고물품 리스트 출력")
    @WithMockUser
    @Test
    public void successLoadInterestUsersArticleList() throws Exception {
        //given
        List<User> interestUserList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            User newUser = makeUser("노관심" + i, "nonInterest" + i + "@email.com");
            makeArticle(i, newUser.getId());
        }
        for (int i = 0; i < 3; i++) {
            User newUser = makeUser("김관심" + i, "interest" + i + "@email.com");
            makeArticle(i, newUser.getId());

            interestUserList.add(newUser);
        }
        setInterestUsers(interestUserList);

        //when
        MvcResult result = mockMvc.perform(get("/public/interest"))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = toSimpleArticleList(result);

        //then
        assertThat(responseList.size()).isEqualTo(3);
        for (var res : responseList) {
            assertThat(res.getSeller()).contains("김관심");
        }
    }
    //todo like, chat

    public void watchArticle(int idx, int count) throws Exception { // 조회수 증가
        Long articleId = articlePostRepository.findAll().get(idx).getId();

        for (int i = 0; i < count; i++) {
            mockMvc.perform(get("/public/article/" + articleId));
        }
    }

    public ResponseArticleDto toArticleDto(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseBody = result.getResponse().getContentAsString();

        return mapper.readValue(responseBody, new TypeReference<>() {
        });
    }

    public List<ResponseArticleSimpleDto> toSimpleArticleList(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseBody = result.getResponse().getContentAsString();

        return mapper.readValue(responseBody, new TypeReference<>() {
        });
    }
}
