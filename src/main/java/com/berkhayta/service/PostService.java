package com.berkhayta.service;

import com.berkhayta.config.JwtManager;
import com.berkhayta.dto.request.CreatePostRequestDto;
import com.berkhayta.dto.response.CommentResponseDto;
import com.berkhayta.dto.response.PostListResponseDto;
import com.berkhayta.entity.Like;
import com.berkhayta.entity.Post;
import com.berkhayta.entity.User;
import com.berkhayta.exception.AuthException;
import com.berkhayta.exception.ErrorType;
import com.berkhayta.repository.PostRepository;
import com.berkhayta.views.VwUserAvatar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final UserService userService;
    private final JwtManager jwtManager;
    private final CommentService commentService;
    public void createPost(CreatePostRequestDto dto) {
        Optional<Long> userId = jwtManager.getAuthId(dto.getToken());
        if(userId.isEmpty()) throw new AuthException(ErrorType.BAD_REQUEST_INVALID_TOKEN);
        repository.save(Post.builder()
                .comment(dto.getComment())
                .commentCount(0L)
                .likeCount(0L)
                .photo(dto.getUrl())
                .sharedDate(System.currentTimeMillis())
                .userId(userId.get())
                .build());
    }

    public List<PostListResponseDto> getPostList(String token) {
        Optional<Long> userId = jwtManager.getAuthId(token);
        if(userId.isEmpty()) throw new AuthException(ErrorType.BAD_REQUEST_INVALID_TOKEN);
        List<Post> postList = repository.findAll();
        List<PostListResponseDto> result = new ArrayList<>();
        List<Long> userIds = postList.stream().map(Post::getUserId).toList();
        List<Long> postIds = postList.stream().map(Post::getId).toList();
        Map<Long, User> mapUserList = userService.findAllByIdsMap(userIds);
        Map<Long,List<CommentResponseDto>> commentList = commentService.getAllCommentListByPostIds(postIds);
        //List<VwUserAvatar> userAvatarList = userService.getUserAvatarList(); // 20K+
        postList.forEach(p->{
           // VwUserAvatar userAvatar = userAvatarList.stream().filter(x-> x.getId().equals(p.getUserId())).findFirst().get();
            result.add(PostListResponseDto.builder()
                    .postId(p.getId())
                    .avatar(mapUserList.get(p.getUserId()).getAvatar())
                    .userName(mapUserList.get(p.getUserId()).getUserName())
                    .userId(p.getUserId())
                    .sharedDate(p.getSharedDate())
                    .comment(p.getComment())
                    .commentCount(p.getCommentCount())
                    .likeCount(p.getLikeCount())
                    .photo(p.getPhoto())
                    .commentList(commentList.get(p.getId()))
                    .build());
        });

        return result.stream().sorted(Comparator.comparingLong(PostListResponseDto::getSharedDate)).toList().reversed();
    }

    public void addComment(Long postId) {
        Optional<Post> postOptional = repository.findById(postId);
        if (postOptional.isPresent()){
            Post post = postOptional.get();

            if (post.getCommentCount()!=null)
                post.setCommentCount(post.getCommentCount()+1);
            else
                post.setCommentCount(1L);


            repository.save(post);
        }
    }
}

