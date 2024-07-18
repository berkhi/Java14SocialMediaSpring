package com.berkhayta.service;

import com.berkhayta.config.JwtManager;
import com.berkhayta.dto.request.CreatePostRequestDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final com.berkhayta.service.UserService userService;
    private final JwtManager jwtManager;
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
        Map<Long, User> mapUserList = userService.findAllByIdsMap(userIds);
        //List<VwUserAvatar> userAvatarList = userService.getUserAvatarList(); // 20K+
        postList.forEach(p->{
           // VwUserAvatar userAvatar = userAvatarList.stream().filter(x-> x.getId().equals(p.getUserId())).findFirst().get();
            result.add(PostListResponseDto.builder()
                    .avatar(mapUserList.get(p.getUserId()).getAvatar())
                    .userName(mapUserList.get(p.getUserId()).getUserName())
                    .userId(p.getUserId())
                    .sharedDate(p.getSharedDate())
                    .comment(p.getComment())
                    .commentCount(p.getCommentCount())
                    .likeCount(p.getLikeCount())
                    .photo(p.getPhoto())
                    .build());
        });

        return result;
    }
}

