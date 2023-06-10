package com.example.danguen;

import com.example.danguen.config.jwt.JwtAuthenticationToken;
import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.comment.repository.CommentRepository;
import com.example.danguen.domain.comment.service.CommentServiceImpl;
import com.example.danguen.domain.image.entity.ArticleImage;
import com.example.danguen.domain.image.entity.UserImage;
import com.example.danguen.domain.image.repository.ImageRepository;
import com.example.danguen.domain.image.service.ArticleImageService;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.repository.PostRepository;
import com.example.danguen.domain.post.service.ArticleServiceImpl;
import com.example.danguen.domain.post.service.PostService;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.repository.UserRepository;
import com.example.danguen.domain.user.service.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class BaseTest {
    //todo json 객체 검증 말고 json을 dto로 변환해서 검증하도록

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    protected UserServiceImpl userService;
    @Autowired
    protected PostService postService;
    @Autowired
    protected ArticleServiceImpl articleService;
    @Autowired
    protected ArticleImageService articleImageService;
    @Autowired
    protected CommentServiceImpl commentService;

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PostRepository postRepository;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected ImageRepository imageRepository;

    @Autowired
    protected ObjectMapper mapper;

    protected Long sessionUserId;
    protected String sessionName = "박이름";
    protected String sessionEmail = "email@temp.com";

    protected Long noneSessionUserId;
    protected String noneSessionName = "김기타";
    protected String noneSessionEmail = "other@temp.com";

    @Order(0)
    @BeforeEach
    public void baseInit() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .build();

        User user = makeUser(sessionName, sessionEmail);
        sessionUserId = user.getId();
        registerUserToSession(user);

        noneSessionUserId = makeUser(noneSessionName, noneSessionEmail).getId();
    }

    @Transactional
    @AfterEach
    public void clear() {
        //todo delete articleImage
        commentRepository.deleteAll();
        imageRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }


    public void registerUserToSession(User user) {
        PrincipalUserDetails principalUserDetails = new PrincipalUserDetails(user);

        Authentication authentication
                = new JwtAuthenticationToken(null, principalUserDetails, principalUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected Address userAddress = new Address("서울시", "길로", "1234");

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User makeUser(String name, String email) {
        User user = userService.save(name, email);

        imageRepository.save(UserImage.builder()
                .user(user)
                .uuid("uuid")
                .build()
        );

        return user;
    }

    public void setInterestUsers(List<User> interestUsers) {
        for (var iUser : interestUsers)
            userService.addInterestUser(sessionUserId, iUser.getId());
    }

    protected String articleTitle = "제목 ";
    protected String articleCategory = "카테고리";
    protected String articleContent = "게시글 내용";
    protected int articlePrice = 10000;
    protected String articleCity = "희망주소";
    protected String articleStreet = "희망주소";
    protected String articleZipcode = "희망주소";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long makeArticle(int idx, Long userId) { // 중고 물품 등록
        RequestArticleSaveOrUpdateDto dto = RequestArticleSaveOrUpdateDto.builder()
                .title(articleTitle + idx)
                .content(articleContent)
                .price(articlePrice)
                .category(articleCategory)
                .dealHopeAddress(
                        new Address(
                                articleCity + idx / 3,
                                articleStreet + idx / 3,
                                articleZipcode + idx)
                )
                .build();
        ArticlePost post = dto.toEntity();
        post.setSeller(userRepository.getReferenceById(userId));

        ArticlePost articlePost = postRepository.save(post);

        makeArticleImage(articlePost);

        return articlePost.getId();
    }

    public void makeArticleImage(ArticlePost articlePost) {
        imageRepository.save(
                ArticleImage.builder()
                        .uuid("uuid")
                        .articlePost(articlePost)
                        .build()
        );
    }

    public <T> List<T> mappingResponse(MvcResult result, Class<T> responseType) throws UnsupportedEncodingException, JsonProcessingException {
        String responseBody = result.getResponse().getContentAsString();
        List<LinkedHashMap<String, Object>> resultList = mapper.readValue(responseBody, new TypeReference<List<LinkedHashMap<String, Object>>>() {});

        List<T> mappedList = new ArrayList<>();
        for (LinkedHashMap<String, Object> resultMap : resultList) {
            T mappedObject = mapper.convertValue(resultMap, responseType);
            mappedList.add(mappedObject);
        }

        return mappedList;
    }

    protected String commentContent = "댓글 내용";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long makeComment(Long postId, Long userId) { // 댓글 등록
        RequestCommentSaveDto dto = new RequestCommentSaveDto();

        dto.setContent(commentContent);
        dto.setKind(postRepository.findById(postId).get().getKind());

        return commentService.saveInPost(dto, postId, userId).getId();
    }
    @PersistenceContext
    EntityManager entityManager;

    @Transactional(propagation =Propagation.REQUIRES_NEW)
    public void saveEntity(){
        System.out.println("parent before "+commentRepository.findAll().size());
        commentRepository.save(new Comment());
        entityManager.flush();

        System.out.println("parent after "+commentRepository.findAll().size());
    }
}


