package com.example.danguen.domain.post.service;

import com.example.danguen.config.exception.MissingSessionPrincipalDetailsException;
import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.image.exception.ArticleNotFoundException;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.repository.PostRepository;
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

    @Transactional(readOnly = true)
    public boolean isUsersCreation(Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new MissingSessionPrincipalDetailsException();
        }
        PrincipalUserDetails principalUserDetails = (PrincipalUserDetails) authentication.getPrincipal();

        Post post = getPostById(postId);

        return post.getWriter().getId().equals(principalUserDetails.getUserId());
    }
}
