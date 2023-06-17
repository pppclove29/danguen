package com.example.danguen.domain.post.service;

import com.example.danguen.config.exception.MissingSessionPrincipalDetailsException;
import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.image.exception.ArticleNotFoundException;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.repository.PostRepository;
import com.example.danguen.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {
    //TODO 인터페이스 구현
    private final PostRepository postRepository;

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(ArticleNotFoundException::new);
    }

    @Transactional
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
}
