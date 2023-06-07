package com.example.danguen;

import com.example.danguen.config.jwt.JwtAuthenticationToken;
import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
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
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.repository.UserRepository;
import com.example.danguen.domain.user.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.FileInputStream;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class BaseTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext ctx;

    @Autowired
    UserServiceImpl userService;
    @Autowired
    ArticleServiceImpl articleService;
    @Autowired
    ArticleImageService articleImageService;
    @Autowired
    CommentServiceImpl commentService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    ObjectMapper mapper;

    Long sessionUserId;
    String sessionName = "박이름";
    String sessionEmail = "email@temp.com";

    Long noneSessionUserId;
    String noneSessionName = "김기타";
    String noneSessionEmail = "other@temp.com";

    @BeforeEach
    public void init() throws Exception {
        System.out.println("Base Init");
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
        System.out.println("Base Clear");
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

    Address userAddress = new Address("서울시", "길로", "1234");

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

    String articleTitle = "제목 ";
    String articleCategory = "카테고리";
    String articleContent = "게시글 내용";
    int articlePrice = 10000;
    String articleCity = "희망주소";
    String articleStreet = "희망주소";
    String articleZipcode = "희망주소";

    public Long makeArticle(int idx, Long userId) throws Exception { // 중고 물품 등록
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

    public void makeArticleImage(ArticlePost articlePost) throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "images",
                "input1.png",
                "image/png",
                new FileInputStream("src/test/java/testImage/input.png"));

        imageRepository.save(
                ArticleImage.builder()
                        .uuid("uuid")
                        .articlePost(articlePost)
                        .build()
        );
    }

    String commentContent = "댓글 내용";

    public void makeComment(Long postId, Long userId) { // 댓글 등록
        RequestCommentSaveDto dto = new RequestCommentSaveDto();

        dto.setContent(commentContent);
        dto.setKind(postRepository.getReferenceById(postId).getKind());

        commentService.save(dto, postId, userId);
    }
}


