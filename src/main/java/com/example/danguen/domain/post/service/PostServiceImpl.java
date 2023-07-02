package com.example.danguen.domain.post.service;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.image.exception.PostNotFoundException;
import com.example.danguen.domain.post.dto.request.RequestPostSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.dto.response.ResponsePostDto;
import com.example.danguen.domain.post.dto.response.ResponsePostSimpleDto;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.entity.PostKind;
import com.example.danguen.domain.post.repository.PostRedisRepository;
import com.example.danguen.domain.post.repository.PostRepository;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = "post")
@Transactional
@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private final UserServiceImpl userService;
    private final PostRepository postRepository;
    private final PostRedisRepository redisRepository;

    @Override
    public ResponsePostDto getPostDto(Long postId) {
        Post post = getPostById(postId);
        post.addViewCount();

        return ResponsePostDto.toResponse(post);
    }

    //@Cacheable(key = "'all'")
    public List<ResponsePostSimpleDto> getNotices() {
        System.out.println("!111!!!!");
        return postRepository.findAll().stream().map(ResponsePostSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Override
    public void giveLike(Long postId, Long userId) {
        Post post = getPostById(postId);

        //todo like
    }

    @Override
    public Long save(RequestPostSaveOrUpdateDto request, Long userId, PostKind.Kind kind) {
        Post post = request.toEntity(kind);

        User user = userService.getUserById(userId);
        user.addPost(post);

        Post savedPost = postRepository.save(post);

        redisRepository.save(ResponsePostSimpleDto.toResponse(savedPost));

        return savedPost.getId();
    }

    @Override
    public void update(RequestPostSaveOrUpdateDto request, Long postId) {
        Post post = getPostById(postId);

        post.update(request);
    }

    @Override
    public void delete(Long postId) {
        Post post = getPostById(postId);
        User user = post.getWriter();

        if (post instanceof ArticlePost) {
            user.removeSellArticle((ArticlePost) post);
        }

        post.getComments().stream()
                .filter(comment -> comment.getWriter().isPresent())
                .forEach(comment -> comment.getWriter().get().removeComment(comment));

        postRepository.delete(post);
    }

    @Override
    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
    }
}
