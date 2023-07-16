package com.example.danguen;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.security.config.BeanIds;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.auth0.jwt.JWT;
import com.example.danguen.config.jwt.JwtProperties;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.comment.repository.CommentRepository;
import com.example.danguen.domain.comment.service.CommentServiceImpl;
import com.example.danguen.domain.image.entity.PostImage;
import com.example.danguen.domain.image.entity.UserImage;
import com.example.danguen.domain.image.repository.ImageRepository;
import com.example.danguen.domain.image.service.UserImageService;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.request.RequestPostSaveOrUpdateDto;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.repository.ArticlePostRepository;
import com.example.danguen.domain.post.repository.PostRepository;
import com.example.danguen.domain.post.service.ArticleServiceImpl;
import com.example.danguen.domain.post.service.PostServiceImpl;
import com.example.danguen.domain.user.entity.Role;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.repository.UserRepository;
import com.example.danguen.domain.user.service.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
public abstract class BaseTest {
	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	private WebApplicationContext ctx;

	@Autowired
	protected UserServiceImpl userService;
	@Autowired
	protected UserImageService userImageService;
	@Autowired
	protected PostServiceImpl postServiceImpl;
	@Autowired
	protected ArticleServiceImpl articleService;
	@Autowired
	protected CommentServiceImpl commentService;

	@Autowired
	protected UserRepository userRepository;
	@Autowired
	protected PostRepository postRepository;
	@Autowired
	protected ArticlePostRepository articlePostRepository;
	@Autowired
	protected CommentRepository commentRepository;
	@Autowired
	protected ImageRepository imageRepository;

	@Autowired
	protected ObjectMapper mapper;

	protected Long loginUserId;
	protected String loginUserName = "박이름";
	protected String loginUserEmail = "email@temp.com";

	protected Long otherUserId;
	protected String otherUserName = "김기타";
	protected String otherUserEmail = "other@temp.com";

	@Order(0)
	@BeforeEach
	public void baseInit() throws ServletException {
		System.out.println("Base Init");
		DelegatingFilterProxy delegateProxyFilter = new DelegatingFilterProxy();
		delegateProxyFilter.init(new MockFilterConfig(ctx.getServletContext(), BeanIds.SPRING_SECURITY_FILTER_CHAIN));

		mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilter(delegateProxyFilter)
				.addFilter(new CharacterEncodingFilter("UTF-8", true)).build();

		loginUserId = makeUser(loginUserName, loginUserEmail).getId();
		otherUserId = makeUser(otherUserName, otherUserEmail).getId();
	}

	@AfterEach
	public void clear() {
		commentRepository.deleteAll();
		imageRepository.deleteAll();
		postRepository.deleteAll();
		userRepository.deleteAll();
	}

	protected Address userAddress = new Address("서울시", "길로", "1234");

	public User makeUser(String name, String email) {
		User user = userService.save(name, email);

		imageRepository.save(UserImage.builder().user(user).uuid("uuid").build());

		return user;
	}

	public void setInterestUsers(List<User> interestUsers) {
		for (var iUser : interestUsers)
			userService.addInterestUser(loginUserId, iUser.getId());
	}

	protected String articleTitle = "제목 ";
	protected String articleCategory = "카테고리";
	protected String articleContent = "게시글 내용";
	protected int articlePrice = 10000;
	protected String articleCity = "희망주소";
	protected String articleStreet = "희망주소";
	protected String articleZipcode = "희망주소";

	public Long makeArticle(int idx, Long userId) { // 중고 물품 등록
		Long articleId = articleService.save(makeArticleDto(idx), userId);

		try {
			makeArticleImage(articleId);
		} catch (IOException e) {
			System.out.println("테스트 이미지 생성 에러");
		}

		return articleId;
	}

	public RequestArticleSaveOrUpdateDto makeArticleDto(int idx) {
		RequestPostSaveOrUpdateDto postDto = new RequestPostSaveOrUpdateDto(articleTitle + idx, articleContent);
		return new RequestArticleSaveOrUpdateDto(postDto, articlePrice, articleCategory,
				new Address(articleCity + idx / 3, articleStreet + idx / 3, articleZipcode + idx));
	}

	public void makeArticleImage(Long articleId) throws IOException {
		ArticlePost articlePost = articleService.getArticleById(articleId);

		PostImage postImage = PostImage.builder().uuid("testUUID").post(articlePost).build();

		imageRepository.save(postImage);
	}

	public <T> List<T> mappingResponse(MvcResult result, Class<T> responseType)
			throws UnsupportedEncodingException, JsonProcessingException {
		String responseBody = result.getResponse().getContentAsString();
		List<LinkedHashMap<String, Object>> resultList = mapper.readValue(responseBody, new TypeReference<>() {
		});

		List<T> mappedList = new ArrayList<>();
		for (LinkedHashMap<String, Object> resultMap : resultList) {
			T mappedObject = mapper.convertValue(resultMap, responseType);
			mappedList.add(mappedObject);
		}

		return mappedList;
	}

	protected String commentContent = "댓글 내용";

	public Long makeComment(Long postId, Long userId) { // 댓글 등록
		RequestCommentSaveDto dto = new RequestCommentSaveDto();

		dto.setContent(commentContent);
		dto.setKind(postServiceImpl.getPostById(postId).getKind());

		return commentService.saveInPost(dto, postId, userId).getId();
	}

	public String makeJwtValue(Role role) {
		String value;
		switch (role) {
		case ADMIN:
			userService.changeRole(loginUserId, Role.ADMIN);
		case USER:
			value = JwtProperties.PREFIX
					+ JWT.create().withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
							.withClaim("userName", loginUserName).withClaim("userId", loginUserId)
							.sign(JwtProperties.ALGORITHM);
			break;
		case ANONYMOUS:
			value = "";
			break;
		default:
			throw new RuntimeException("도달하면 안됩니다");
		}

		return value;
	}
}
