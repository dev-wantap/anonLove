package com.anonLove.service.post;

import com.anonLove.domain.comment.Comment;
import com.anonLove.domain.post.Category;
import com.anonLove.domain.post.Post;
import com.anonLove.domain.post.TargetGender;
import com.anonLove.domain.user.User;
import com.anonLove.dto.request.post.CreatePostRequest;
import com.anonLove.dto.request.post.UpdatePostRequest;
import com.anonLove.dto.response.comment.CommentResponse;
import com.anonLove.dto.response.post.CreatePostResponse;
import com.anonLove.dto.response.post.PostDetailResponse;
import com.anonLove.dto.response.post.PostListResponse;
import com.anonLove.exception.CustomException;
import com.anonLove.exception.ErrorCode;
import com.anonLove.repository.CategoryRepository;
import com.anonLove.repository.CommentRepository;
import com.anonLove.repository.PostRepository;
import com.anonLove.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;

    public Page<PostListResponse> getPosts(Integer categoryId, Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        TargetGender userGender = TargetGender.valueOf(user.getGender().name());

        Page<Post> posts = postRepository.findVisiblePosts(
                categoryId,
                user.getUniversity(),
                userGender,
                pageable
        );

        return posts.map(PostListResponse::from);
    }

    public PostDetailResponse getPostDetail(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        User viewer = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!post.isVisibleTo(viewer)) {
            throw new CustomException(ErrorCode.POST_NOT_ACCESSIBLE);
        }

        boolean isMine = post.isAuthor(userId);

        // 댓글 조회
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        List<CommentResponse> commentResponses = comments.stream()
                .map(comment -> CommentResponse.from(comment, userId))
                .collect(Collectors.toList());

        return PostDetailResponse.from(post, isMine, commentResponses);
    }

    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Post post = Post.builder()
                .user(user)
                .category(category)
                .title(request.getTitle())
                .content(request.getContent())
                .visibilityType(request.getVisibilityType())
                .targetGender(request.getTargetGender())
                .build();

        Post savedPost = postRepository.save(post);
        return new CreatePostResponse(savedPost.getId());
    }

    @Transactional
    public void updatePost(Long postId, UpdatePostRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.isAuthor(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        post.update(
                request.getTitle(),
                request.getContent(),
                request.getVisibilityType(),
                request.getTargetGender()
        );
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.isAuthor(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        postRepository.delete(post);
    }
}