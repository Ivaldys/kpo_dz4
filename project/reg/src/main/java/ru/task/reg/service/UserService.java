package ru.task.reg.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.task.reg.DTO.UserLoginDTO;
import ru.task.reg.DTO.UserRegisterDTO;
import ru.task.reg.entity.User;
import ru.task.reg.exception.IllegalEmailException;
import ru.task.reg.exception.IllegalNicknameException;
import ru.task.reg.exception.IllegalPasswordException;
import ru.task.reg.repository.UserRepo;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@Setter
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;

    public User save(User user) {
        return userRepo.save(user);
    }

    public User addNew(UserRegisterDTO registerDTO) throws
            IllegalPasswordException, IllegalEmailException, IllegalNicknameException {
        if (existByEmail(registerDTO.email())) {
            throw new IllegalArgumentException("User with email " + registerDTO.email() + " already exists");
        }
        if (!passwordValidation(registerDTO.password())) {
            throw new IllegalPasswordException();
        }
        if (!validateEmail(registerDTO.email())) {
            throw new IllegalEmailException();
        }

        if (!validateNickname(registerDTO.nickname())) {
            throw new IllegalNicknameException();
        }
        User newUser = new User();
        newUser.setPassword(registerDTO.password());
        newUser.setEmail(registerDTO.email());
        newUser.setNickname(registerDTO.nickname());
        newUser.setCreated(LocalDateTime.now());
        return userRepo.save(newUser);
    }

    public boolean authenticate(UserLoginDTO loginDTO) {
        if (!existByEmail(loginDTO.email())) {
            throw new NoSuchElementException();
        }
        User user = findUserByEmail(loginDTO.email());
        return user.getPassword().equals(loginDTO.password());
    }

    public User findUserByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow();
    }

    public boolean existByEmail(String email) {
        return userRepo.existsByEmail(email);
    }


    private boolean checkDigits(String password) {
        for (char s : password.toCharArray()) {
            if (s > 47 && s < 58) {
                return true;
            }
        }
        return false;
    }

    private boolean checkSpecialCharacters(String password) {
        for (char s : password.toCharArray()) {
            if (s > 32 && s < 48 || s == '_') {
                return true;
            }
        }
        return false;
    }

    private boolean checkLowerAndUpperCase(String password) {
        return !password.toLowerCase().equals(password) && !password.toUpperCase().equals(password);
    }

    private boolean passwordValidation(String password) {
        return password.length() >= 8 &&
                checkDigits(password) &&
                checkSpecialCharacters(password) &&
                checkLowerAndUpperCase(password);
    }
    private boolean validateEmail(String email) {
        return email.length() > 4 && email.contains("@");
    }

    private boolean validateNickname(String nickname) {
        return !nickname.isBlank();
    }
}

