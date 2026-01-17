package com.anonLove.service.comment;

import com.anonLove.domain.comment.Comment;
import com.anonLove.domain.post.Post;
import com.anonLove.domain.user.User;
import com.anonLove.dto.request.comment.CreateCommentRequest;
import com.anonLove.dto.request.comment.UpdateCommentRequest;
import com.anonLove.dto.response.comment.CommentResponse;
import com.anonLove.dto.response.comment.CreateCommentResponse;
import com.anonLove.exception.CustomException;
import com.anonLove.exception.ErrorCode;
import com.anonLove.repository.CommentRepository;
import com.anonLove.repository.PostRepository;
import com.anonLove.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AiFilterClient aiFilterClient;

    @Transactional
    public CreateCommentResponse createComment(Long postId, CreateCommentRequest request, Long userId) {
        // TODO:
        // 1. 입력 유효성 검사(request.getContent() null/빈문자열/최대길이)
        // 2. 포스트 및 사용자 존재 여부 확인
        // 3. 권한/차단 상태 확인(차단된 사용자라면 예외)
        // 4. AI 필터 호출(비동기 또는 sync) + 장애처리(circuit breaker, timeout)
        // 5. 필터 결과 저장
        // 6. 댓글 저장 및 댓글 수 업데이트
        // 7. 이벤트 발행(알림/검색 색인 등)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // AI 필터링
        boolean isFiltered = aiFilterClient.checkToxic(request.getContent());

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(request.getContent())
                .isFiltered(isFiltered)
                .build();

        Comment savedComment = commentRepository.save(comment);

        log.info("Comment created: commentId={}, postId={}, isFiltered={}",
                savedComment.getId(), postId, isFiltered);

        return CreateCommentResponse.from(savedComment);
    }

    public List<CommentResponse> getCommentsByPost(Long postId, Long viewerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return comments.stream()
                .map(comment -> CommentResponse.from(comment, viewerId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateComment(Long commentId, UpdateCommentRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.isAuthor(userId)) {
            throw new CustomException(ErrorCode.NOT_COMMENT_AUTHOR);
        }

        // AI 필터링 재검사
        boolean isFiltered = aiFilterClient.checkToxic(request.getContent());

        comment.update(request.getContent(), isFiltered);

        log.info("Comment updated: commentId={}, isFiltered={}", commentId, isFiltered);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.isAuthor(userId)) {
            throw new CustomException(ErrorCode.NOT_COMMENT_AUTHOR);
        }

        commentRepository.delete(comment);

        log.info("Comment deleted: commentId={}", commentId);
    }
}
