package com.example.danguen.domain.post.service;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleDto;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.post.entity.Article;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface ArticleService {
    /**
     * 조회할 게시글 번호를 통해 특정 게시글을 조회
     *
     * @param articleId 조회할 게시글 번호
     * @return 조회된 게시글 정보
     */
    ResponseArticleDto getArticle(Long articleId);

    /**
     * 주소 정보에 따라 게시글 목록을 페이지로 표시
     *
     * @param pageable 사용자에게 표시할 페이지 정보
     * @param address  조회에 필요한 주소 정보
     * @return 주소 정보와 일치하는 게시글 목록
     */
    List<ResponseArticleSimpleDto> getArticleByAddressPage(Pageable pageable, Address address);

    /**
     * 게시글의 인기정도에 따라 게시글 목록을 페이지로 분류하여 표시
     *
     * @param pageable 사용자에게 표시할 페이지 정보
     * @return 상위 인기 게시글 목록
     */
    List<ResponseArticleSimpleDto> getHotArticlePage(Pageable pageable);

    /**
     * 검색 키워드에 따라 제목이나 내용이 키워드를 포함하는 게시글 목록을 페이지로 분류하여 표시
     *
     * @param pageable 사용자에게 표시할 페이지 정보
     * @param title    검색 키워드
     * @return 검색 결과 게시글 목록
     */
    List<ResponseArticleSimpleDto> getSearchArticlePage(Pageable pageable, String title);

    /**
     * 특정 사용자가 관심가진 유저들이 등록한 게시글을 페이지로 분류하여 표시
     *
     * @param pageable 사용자에게 표시할 페이지 정보
     * @param userId   특정 사용자
     * @return 관심 유저들이 등록한 게시글 목록
     */
    List<ResponseArticleSimpleDto> getInterestPage(Pageable pageable, Long userId);

    /**
     * 게시글의 정보를 받아 새로운 게시글을 등록
     *
     * @param request 게시글의 텍스트 정보를 가진 DTO
     * @param userId  게시글을  등록한 유저의 ID
     * @return 저장된 Article의 ID
     * @throws IOException 이미지의 transferTo 메소드 에러처리
     */

    Long save(RequestArticleSaveOrUpdateDto request, Long userId) throws IOException;

    /**
     * 게시글 업데이트 정보를 받아 기존 게시글의 정보를 수정
     *
     * @param request   게시글 수정 정보를 가진 DTO
     * @param articleId 수정할 게시글 ID
     */
    void update(RequestArticleSaveOrUpdateDto request, Long articleId);

    /**
     * ID에 맞는 게시글을 삭제
     *
     * @param articleId 삭제할 게시글 ID
     */
    void delete(Long articleId);

    /**
     * 특정 ID를 통해 DB 에서 Article Entity 반환
     *
     * @param articleId 가져올 Article ID
     * @return DB 에서 가져온 Article Entity
     */
    Article getArticleFromDB(Long articleId);
}
