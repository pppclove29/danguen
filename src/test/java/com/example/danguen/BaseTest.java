package com.example.danguen;

import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.comment.repository.CommentRepository;
import com.example.danguen.domain.image.entity.ArticleImage;
import com.example.danguen.domain.image.entity.UserImage;
import com.example.danguen.domain.image.exception.ArticleNotFoundException;
import com.example.danguen.domain.image.repository.ImageRepository;
import com.example.danguen.domain.image.service.ArticleImageService;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.repository.PostRepository;
import com.example.danguen.domain.post.service.ArticleServiceImpl;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.repository.UserRepository;
import com.example.danguen.domain.user.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .build();

        User user = makeUser(sessionName, sessionEmail);
        sessionUserId = user.getId();
        registerUserToSession(user);

        noneSessionUserId = makeUser(noneSessionName, noneSessionEmail).getId();
    }

    @AfterEach
    public void clear() {
        commentRepository.deleteAll();
        imageRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }


    public void registerUserToSession(User user) {
        PrincipalUserDetails userDetails = new PrincipalUserDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()));
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

    String articleTitle = "제목 ";
    String articleCategory = "카테고리";
    String articleContent = "게시글 내용";
    int articlePrice = 10000;
    String articleCity = "희망주소";
    String articleStreet = "희망주소";
    String articleZipcode = "희망주소";
@Transactional
    public Long makeArticle(int idx, Long sellerId) throws Exception { // 중고 물품 등록
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

        Long articleId = articleService.save(dto, sellerId);
        ArticlePost articlePost = articleService.getArticleById(articleId);

        makeArticleImage(articlePost);

        return articleId;
    }

    public void makeArticleImage(ArticlePost articlePost) throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "images",
                "input1.png",
                "image/png",
                new FileInputStream("src/test/java/testImage/input.png"));

        Stream.of(image)
                .map(uuid ->
                        ArticleImage.builder()
                                .uuid("uuid")
                                .articlePost(articlePost)
                                .build())
                .forEach(imageRepository::save);
    }

    String commentContent = "댓글 내용";

    public void makeMockComment(Post post, User user) { // 댓글 등록
        Comment comment = mock(Comment.class);

        when(comment.getWriter()).thenReturn(user);
        when(comment.getPost()).thenReturn(post);
        when(comment.getContent()).thenReturn(commentContent);

        commentRepository.save(comment);
    }
}
