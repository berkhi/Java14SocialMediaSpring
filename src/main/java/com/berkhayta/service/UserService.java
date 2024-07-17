package com.berkhayta.service;

import com.berkhayta.config.JwtManager;
import com.berkhayta.dto.request.UserLoginRequestDto;
import com.berkhayta.dto.request.UserSaveRequestDto;
import com.berkhayta.dto.response.SearchUserResponseDto;
import com.berkhayta.entity.User;
import com.berkhayta.exception.AuthException;
import com.berkhayta.exception.ErrorType;
import com.berkhayta.repository.UserRepository;
import com.berkhayta.views.VwUserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final JwtManager jwtManager;
    public User save(UserSaveRequestDto dto) {
        return repository.save(User.builder()
                        .password(dto.getPassword())
                        .email(dto.getEmail())
                        .userName(dto.getUserName())
                .build());
    }

    public Optional<User> login(UserLoginRequestDto dto) {
       return repository.findOptionalByUserNameAndPassword(dto.getUserName(),dto.getPassword());
    }

    public List<SearchUserResponseDto> search(String userName) {
        List<User> userList;
        List<SearchUserResponseDto> result = new ArrayList<>();
        if(Objects.isNull(userName))
            userList = repository.findAll();
        else
            userList = repository.findAllByUserNameContaining(userName);
        userList.forEach(u->
            result.add(SearchUserResponseDto.builder()
                            .userName(u.getUserName())
                            .avatar(u.getAvatar())
                            .email(u.getEmail())
                    .build())
        );
        return result;
    }

    public VwUserProfile getProfileByToken(String token) {
        Optional<Long> authId = jwtManager.getAuthId(token);
        if(authId.isEmpty()) throw new AuthException(ErrorType.BAD_REQUEST_INVALID_TOKEN);
        return repository.getByAuthId(authId.get());
    }

    public void editProfile(User user) {
        repository.save(user);
    }
}
