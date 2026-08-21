package ru.practicum.gateway.user;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.user.dto.UpdateUserDto;
import ru.practicum.gateway.user.dto.UserRequestDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";


    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))

                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()))
                .build()
        );
    }

    public ResponseEntity<Object> getUser(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> createUser(UserRequestDto requestDto) {
        return post("", requestDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UpdateUserDto updateUserDto) {
        return patch("/" + userId, updateUserDto);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId);
    }
}
