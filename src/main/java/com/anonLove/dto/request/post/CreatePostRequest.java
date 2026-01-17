package com.anonLove.dto.request.post;

import com.anonLove.domain.post.TargetGender;
import com.anonLove.domain.post.VisibilityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Category is required")
    private Integer categoryId;

    private VisibilityType visibilityType;

    private TargetGender targetGender;
}
