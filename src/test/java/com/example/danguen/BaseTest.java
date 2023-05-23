package com.example.danguen;

import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.comment.repository.CommentRepository;
import com.example.danguen.domain.image.repository.ImageRepository;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.repository.PostRepository;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class BaseTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext ctx;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    ObjectMapper mapper;

    private User sessionUser;
    String sessionName = "박이름";
    String sessionEmail = "email@temp.com";

    private User otherUser;
    String noneSessionName = "김기타";
    String noneSessionEmail = "other@temp.com";

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();

        sessionUser = makeMockUser(sessionName, sessionEmail);
        registerUserToSession(sessionUser);

        otherUser = makeMockUser(noneSessionName, noneSessionEmail);
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

    String userCity = "서울시";
    String userStreet = "길로";
    String userZipcode = "1234";


    public User makeMockUser(String name, String email) {
        Address address = new Address(userCity, userStreet, userZipcode);
        User user = mock(User.class);
        when(user.getName()).thenReturn(name);
        when(user.getEmail()).thenReturn(email);
        when(user.getAddress()).thenReturn(address);
        userRepository.save(user);

        return user;
    }

    public User getSessionUser() {
        return sessionUser;
    }

    public User getOtherUser() {
        return otherUser;
    }


    String articleTitle = "제목 ";
    String articleCategory = "카테고리";
    String articleContent = "게시글 내용";
    int articlePrice = 10000;
    String articleCity = "희망주소";
    String articleStreet = "희망주소";
    String articleZipcode = "희망주소";

    public ArticlePost makeMockArticle(int idx, User user) { // 중고 물품 등록
        Address address = new Address(articleCity + idx / 3, articleStreet + idx / 3, articleZipcode + idx);

        ArticlePost articlePost = mock(ArticlePost.class);
        when(articlePost.getTitle()).thenReturn(articleTitle + idx);
        when(articlePost.getContent()).thenReturn(articleContent);
        when(articlePost.getPrice()).thenReturn(articlePrice);
        when(articlePost.getCategory()).thenReturn(articleCategory);
        when(articlePost.getDealHopeAddress()).thenReturn(address);
        when(articlePost.getSeller()).thenReturn(user);

        return postRepository.save(articlePost);
    }

    public void makeMockArticleImage() {

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
