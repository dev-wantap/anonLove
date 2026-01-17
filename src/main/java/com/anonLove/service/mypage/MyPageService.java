package com.anonLove.service.mypage;
import com.anonLove.domain.comment.Comment;
import com.anonLove.domain.post.Post;
import com.anonLove.dto.response.MyPageCommentResponse;
import com.anonLove.dto.response.MyPagePostResponse;
import com.anonLove.repository.CommentRepository;
import com.anonLove.repository.PostRepository;
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
public class MyPageService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public List<MyPagePostResponse> getMyPosts(Long userId) {
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return posts.stream()
                .map(post -> {
                    long commentCount = commentRepository.countByPostId(post.getId());
                    return MyPagePostResponse.from(post, (int) commentCount);
                })
                .collect(Collectors.toList());
    }

    public List<MyPageCommentResponse> getMyComments(Long userId) {
        List<Comment> comments = commentRepository.findByUserIdWithPost(userId);

        return comments.stream()
                .map(MyPageCommentResponse::from)
                .collect(Collectors.toList());
    }
}