package com.example.danguen;


import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.image.exception.ArticleNotFoundException;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.repository.ArticlePostRepository;
import com.example.danguen.domain.user.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ArticlePostTest extends BaseTest {

    @Autowired
    ArticlePostRepository articlePostRepository;

    @DisplayName("이미지 없이 중고물품 등록")
    @WithMockUser
    @Test
    public void successSaveArticleWithOutImages() throws Exception {
        //given
        RequestArticleSaveOrUpdateDto dto = RequestArticleSaveOrUpdateDto.builder()
                .title(articleTitle)
                .content(articleContent)
                .price(articlePrice)
                .category(articleCategory)
                .dealHopeAddress(new Address(articleCity, articleStreet, articleZipcode))
                .build();

        String dtoJson = mapper.writeValueAsString(dto);
        MockMultipartFile request = new MockMultipartFile(
                "request",
                "request",
                "application/json",
                dtoJson.getBytes(StandardCharsets.UTF_8)
        );

        //when
        mockMvc.perform(multipart("/article")
                        .file(request)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        //then
        ArticlePost articlePost = articlePostRepository.findAll().get(0);

        assertThat(articlePost.getTitle()).isEqualTo(articleTitle + 0);
        assertThat(articlePost.getCategory()).isEqualTo(articleCategory);
        assertThat(articlePost.getContent()).isEqualTo(articleContent);
        assertThat(articlePost.getImages().size()).isEqualTo(1);
        assertThat(articlePost.getDealHopeAddress().getCity()).isEqualTo(articleCity + 0);
        assertThat(articlePost.getDealHopeAddress().getStreet()).isEqualTo(articleStreet + 0);
        assertThat(articlePost.getDealHopeAddress().getZipcode()).isEqualTo(articleZipcode + 0);
        assertThat(articlePost.getPrice()).isEqualTo(10000);
        assertThat(articlePost.getSeller().getName()).isEqualTo(sessionName);
        assertThat(articlePost.getSeller().getEmail()).isEqualTo(sessionEmail);
        assertThat(articlePost.getImages().size()).isEqualTo(1);
    }

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
        assertThat(imageRepository.findAll().size()).isEqualTo(3);
    }

    @DisplayName("이미지 수정 없이 물품 정보 수정")
    @WithMockUser
    @Test
    public void successUpdateArticleInfoWithOutImages() throws Exception {
        //given
        Long articleId = makeMockArticle(0, getSessionUser()).getId();

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

        //when
        //todo update 메서드 파라미터 변경으로 다른 방안 모색
        mockMvc.perform(put("/article/" + articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        //then
        ArticlePost articlePost = articlePostRepository.getReferenceById(articleId);
        assertThat(articlePost.getTitle()).isEqualTo("new" + articleTitle);
        assertThat(articlePost.getCategory()).isEqualTo("new" + articleCategory);
        assertThat(articlePost.getContent()).isEqualTo("new" + articleContent);
        assertThat(articlePost.getDealHopeAddress().getCity()).isEqualTo("new" + articleCity);
        assertThat(articlePost.getDealHopeAddress().getStreet()).isEqualTo("new" + articleStreet);
        assertThat(articlePost.getDealHopeAddress().getZipcode()).isEqualTo("new" + articleZipcode);
        assertThat(articlePost.getPrice()).isEqualTo(30000);
    }

    @DisplayName("이미지를 포함한 물품 정보 수정")
    @WithMockUser
    @Test
    public void successUpdateArticleInfoWithImages() throws Exception {
        //TODO 이미지 포함 테스트 시도
    }

    @DisplayName("중고물품 삭제")
    @WithMockUser
    @Test
    public void successDeleteArticle() throws Exception {
        //given
        User user = getSessionUser();
        ArticlePost articlePost = makeMockArticle(0, user);

        //when
        mockMvc.perform(delete("/article/" + articlePost.getId()))
                .andExpect(status().isOk());

        //then
        assertThat(articlePostRepository.findAll().size()).isEqualTo(0);
        assertThat(user.getSellArticlePosts().size()).isEqualTo(0);
    }

    @DisplayName("물품 정보 요청")
    @WithAnonymousUser
    @Test
    public void successLoadSimpleArticleInfo() throws Exception {
        //given
        //todo 작성 시간 검증
        LocalDateTime testTime = LocalDateTime.now();

        Long articleId = makeMockArticle(0, getSessionUser()).getId();

        //when
        ResultActions resultActions = mockMvc.perform(get("/article/" + articleId))
                .andExpect(status().isOk());
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(articleId))
                .andExpect(jsonPath("$.title").value(articleTitle + 0))
                .andExpect(jsonPath("$.content").value(articleContent))
                .andExpect(jsonPath("$.price").value(articlePrice))
                .andExpect(jsonPath("$.view").value(1))
                .andExpect(jsonPath("$.isSold").value(false))
                .andExpect(jsonPath("$.dealHopeAddress.city").value(articleCity + 0))
                .andExpect(jsonPath("$.dealHopeAddress.street").value(articleStreet + 0))
                .andExpect(jsonPath("$.dealHopeAddress.zipcode").value(articleZipcode + 0))
                .andExpect(jsonPath("$.seller").value(sessionName));

    }

    @DisplayName("중고물품 등록자 회원탈퇴시 게시글 자동삭제")
    @WithMockUser
    @Test
    public void successAutoDeleteArticleWhenSellerWithdrawal() throws Exception {
        //given
        User otherUser = getOtherUser();
        makeMockArticle(0, otherUser);

        //when
        mockMvc.perform(delete("/user/" + otherUser.getId()));

        //then
        assertThat(articlePostRepository.findAll().size()).isEqualTo(0);
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @DisplayName("존재하지 않는 중고물품 정보 요청")
    @WithAnonymousUser
    @Test
    public void failLoadNonExistArticleInfo() throws Exception {
        //when
        MvcResult result = mockMvc.perform(get("/article/999999999")).andReturn();

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
    int pageSize = 6;


    @DisplayName("주소 없이 주소별 중고물품 리스트 요청")
    @WithAnonymousUser
    @Test
    public void successAddressSearchArticleListWithOutAddress() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeMockArticle(i, getSessionUser());
        }

        //when
        MvcResult result = mockMvc.perform(get("/address"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        List<ResponseArticleSimpleDto> responseList = mapper.readValue(responseBody, new TypeReference<>() {
        });

        // then
        assertThat(responseList.size()).isEqualTo(6);

        for (int i = 1; i <= pageSize; i++) {
            ResponseArticleSimpleDto res = responseList.get(i);

            assertThat(res.getTitle()).isEqualTo(articleTitle + (articleSize - i));
            assertThat(res.getPrice()).isEqualTo(articlePrice);
            assertThat(res.getViews()).isEqualTo(0);
            assertThat(res.getLikes()).isEqualTo(0);
            assertThat(res.getDealHopeAddress()).isEqualTo(
                    new Address(articleCity + i / 3, articleStreet + i / 3, articleZipcode + i)
            );
        }
    }

    @DisplayName("도시만을 포함하는 주소별 중고물품 리스트 요청")
    @Test
    public void successAddressSearchArticleListWithCity() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeMockArticle(i, getSessionUser());
        }

        //when
        MvcResult result = mockMvc.perform(get("/address/희망주소1"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        List<ResponseArticleSimpleDto> responseList = mapper.readValue(responseBody, new TypeReference<>() {
        });

        // then
        assertThat(responseList.size()).isEqualTo(3);

        assertThat(responseList.get(0).getTitle()).isEqualTo(articleTitle + 5);
        assertThat(responseList.get(1).getTitle()).isEqualTo(articleTitle + 4);
        assertThat(responseList.get(2).getTitle()).isEqualTo(articleTitle + 3);

        for (int i = 1; i <= responseList.size(); i++) {
            ResponseArticleSimpleDto res = responseList.get(i);

            assertThat(res.getPrice()).isEqualTo(articlePrice);
            assertThat(res.getViews()).isEqualTo(0);
            assertThat(res.getLikes()).isEqualTo(0);
            assertThat(res.getDealHopeAddress()).isEqualTo(
                    new Address("희망주소1", articleStreet + i / 3, articleZipcode + i)
            );
        }
    }

    @DisplayName("도시, 도로명만을 포함하는 주소별 중고물품 리스트 요청")
    @WithAnonymousUser
    @Test
    public void successAddressSearchArticleListWithCityAndStreet() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeMockArticle(i, getSessionUser());
        }

        //when
        MvcResult result = mockMvc.perform(get("/address/희망주소0/희망주소0"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        List<ResponseArticleSimpleDto> responseList = mapper.readValue(responseBody, new TypeReference<>() {
        });

        // then
        assertThat(responseList.size()).isEqualTo(3);

        assertThat(responseList.get(0).getTitle()).isEqualTo(articleTitle + 2);
        assertThat(responseList.get(1).getTitle()).isEqualTo(articleTitle + 1);
        assertThat(responseList.get(2).getTitle()).isEqualTo(articleTitle + 0);

        for (int i = 1; i <= responseList.size(); i++) {
            ResponseArticleSimpleDto res = responseList.get(i);

            assertThat(res.getPrice()).isEqualTo(articlePrice);
            assertThat(res.getViews()).isEqualTo(0);
            assertThat(res.getLikes()).isEqualTo(0);
            assertThat(res.getDealHopeAddress()).isEqualTo(
                    new Address("희망주소1", "희망주소1", articleZipcode + i)
            );
        }
    }

    @DisplayName("모든 주소를 포함하는 주소별 중고물품 리스트 요청")
    @WithAnonymousUser
    @Test
    public void successAddressSearchArticleListWithFullAddress() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeMockArticle(i, getSessionUser());
        }

        //when
        MvcResult result = mockMvc.perform(get("/address/희망주소0/희망주소0/희망주소2"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        List<ResponseArticleSimpleDto> responseList = mapper.readValue(responseBody, new TypeReference<>() {
        });

        // then
        assertThat(responseList.size()).isEqualTo(1);

        assertThat(responseList.get(0).getTitle()).isEqualTo(articleTitle + 2);

        for (int i = 1; i <= responseList.size(); i++) {
            ResponseArticleSimpleDto res = responseList.get(i);

            assertThat(res.getPrice()).isEqualTo(articlePrice);
            assertThat(res.getViews()).isEqualTo(0);
            assertThat(res.getLikes()).isEqualTo(0);
            assertThat(res.getDealHopeAddress()).isEqualTo(
                    new Address("희망주소1", "희망주소1", "희망주소0")
            );
        }
    }

    @DisplayName("존재하지않는 주소별 중고물품 리스트 요청")
    @WithAnonymousUser
    @Test
    public void successAddressSearchArticleListWithNonExistAddress() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeMockArticle(i, getSessionUser());
        }

        //when
        MvcResult result = mockMvc.perform(get("/address/none/exist/address"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        List<ResponseArticleSimpleDto> responseList = mapper.readValue(responseBody, new TypeReference<>() {
        });

        // then
        assertThat(responseList.size()).isEqualTo(0);
    }

    @DisplayName("중고물품 리스트 결과 페이지 분할")
    @WithAnonymousUser
    @Test
    public void successLoadArticleListSplit() throws Exception {
        //given
        for (int i = 0; i < articleSize; i++) {
            makeMockArticle(i, getSessionUser());
        }

        // 첫번째 페이지
        mockMvc.perform(get("/search?size=3&page=0&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((articleTitle + 9)))
                .andExpect(jsonPath("$[1].title").value((articleTitle + 8)))
                .andExpect(jsonPath("$[2].title").value((articleTitle + 7)))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 두번째 페이지
        mockMvc.perform(get("/search?size=3&page=1&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((articleTitle + 6)))
                .andExpect(jsonPath("$[1].title").value((articleTitle + 5)))
                .andExpect(jsonPath("$[2].title").value((articleTitle + 4)))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 세번째 페이지
        mockMvc.perform(get("/search?size=3&page=2&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((articleTitle + 3)))
                .andExpect(jsonPath("$[1].title").value((articleTitle + 2)))
                .andExpect(jsonPath("$[2].title").value((articleTitle + 1)))
                .andExpect(jsonPath("$[3]").doesNotExist());

        // 마지막 페이지
        mockMvc.perform(get("/search?size=3&page=3&keyword="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((articleTitle + 0)))
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
            assertThat(articles.get(i).getTitle()).isEqualTo(articleTitle + targetIndex[i]);
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
                .andExpect(jsonPath("$[0].title").value(articleTitle + 7777))
                .andExpect(jsonPath("$[1].title").value(articleTitle + 7777))
                .andExpect(jsonPath("$[2].title").value(articleTitle + 7777))
                .andExpect(jsonPath("$[3].title").value(articleTitle + 7777))
                .andExpect(jsonPath("$[4].title").value(articleTitle + 7777))
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
                .andExpect(jsonPath("$[0].title").value(articleTitle + 10000))
                .andExpect(jsonPath("$[1].title").value(articleTitle + 10000))
                .andExpect(jsonPath("$[2].title").value(articleTitle + 10000))
                .andExpect(jsonPath("$[3].title").value(articleTitle + 10000))
                .andExpect(jsonPath("$[4].title").value(articleTitle + 10000))
                .andExpect(jsonPath("$[5].title").value(articleTitle + 10000))
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
                .andExpect(jsonPath("$[0].title").value(articleTitle + 777))
                .andExpect(jsonPath("$[1].title").value(articleTitle + 777))
                .andExpect(jsonPath("$[2].title").value(articleTitle + 777))
                .andExpect(jsonPath("$[3].title").value(articleTitle + 7777))
                .andExpect(jsonPath("$[4].title").value(articleTitle + 7777))
                .andExpect(jsonPath("$[5].title").value(articleTitle + 7777))
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

        assertThat(result.getResponse().get.get(objKey) instanceof ArrayList).isTrue();
        resultList = ((ArrayList<?>) result.getModelAndView().getModel().get(objKey)).stream().map(obj -> (T) obj).collect(Collectors.toList());

        return resultList;
    }
}
