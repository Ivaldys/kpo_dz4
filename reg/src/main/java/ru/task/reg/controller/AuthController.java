package ru.task.reg.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.task.reg.DTO.UserLoginDTO;
import ru.task.reg.DTO.UserRegisterDTO;
import ru.task.reg.auth.JwtUtil;
import ru.task.reg.entity.Session;
import ru.task.reg.entity.User;
import ru.task.reg.exception.IllegalEmailException;
import ru.task.reg.exception.IllegalNicknameException;
import ru.task.reg.exception.IllegalPasswordException;
import ru.task.reg.service.SessionService;
import ru.task.reg.service.UserService;

import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    JwtUtil jwtUtil;
    UserService userService;
    ObjectMapper objectMapper;
    SessionService sessionService;

    @Operation(summary = "зарегистрировать нового пользователя")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDTO registerDTO) {
        User saved = null;
        try {
            saved = userService.addNew(registerDTO);
        } catch (IllegalArgumentException exception) {
            log.error(exception.getMessage());
            return new ResponseEntity<>("User with given email already exists", HttpStatusCode.valueOf(400));
        } catch (IllegalPasswordException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("Illegal password. It should contain upper and lower case letters, special characters and digits, also password size should be greater than 7.", HttpStatusCode.valueOf(400));
        } catch (IllegalNicknameException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("Field nickname can not be empty", HttpStatusCode.valueOf(400));
        } catch (IllegalEmailException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("Illegal email address", HttpStatusCode.valueOf(400));
        }
        try {
            return new ResponseEntity<>(objectMapper.writeValueAsString(saved), HttpStatusCode.valueOf(201));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "войти в систему и получить токен")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO loginDTO) {
        boolean isAuthenticated = false;
        try {
            isAuthenticated = userService.authenticate(loginDTO);
        } catch (NoSuchElementException exception) {
            log.warn("There is no user with email: {}", loginDTO.email());
        }
        if (isAuthenticated) {
            String token = jwtUtil.generateToken(loginDTO.email());
            sessionService.newSession(userService.findUserByEmail(loginDTO.email()), token);
            return new ResponseEntity<>(token, HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>("Wrong password", HttpStatusCode.valueOf(403));
    }

    @Operation(summary = "информация о пользователе")
    @GetMapping("/info")
    public ResponseEntity<String> info(@RequestHeader("Authorization") String token) throws JsonProcessingException {
        if (!jwtUtil.validateToken(token)) {
            return new ResponseEntity<>("Given token is not valid", HttpStatusCode.valueOf(403));
        }
        Session session = null;
        try {
            session = sessionService.findSessionByToken(token);
        } catch (NoSuchElementException exception) {
            return new ResponseEntity<>("There is no actual session", HttpStatusCode.valueOf(403));
        }
        return new ResponseEntity<>(objectMapper.writeValueAsString(session.getUser()), HttpStatusCode.valueOf(200));
    }
}
