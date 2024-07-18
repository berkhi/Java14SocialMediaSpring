package com.berkhayta.controller;

import com.berkhayta.dto.request.CreatePostRequestDto;
import com.berkhayta.dto.response.PostListResponseDto;
import com.berkhayta.dto.response.ResponseDto;
import com.berkhayta.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @PostMapping("create-post")
    public ResponseEntity<ResponseDto<Boolean>> createPost(@RequestBody CreatePostRequestDto dto){
        postService.createPost(dto);
        return ResponseEntity.ok(ResponseDto.<Boolean>builder()
                        .data(true)
                        .code(200)
                        .message("post paylaşıldı.")
                .build());
    }

    @GetMapping("get-post-list")
    public ResponseEntity<ResponseDto<List<PostListResponseDto>>> getPostList(String token){
        return ResponseEntity.ok(ResponseDto.<List<PostListResponseDto>>builder()
                        .message("post listesi getirildi.")
                        .code(200)
                        .data(postService.getPostList(token))
                .build());
    }
}
