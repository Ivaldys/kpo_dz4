package ru.task.reg.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.task.reg.entity.Session;
import ru.task.reg.entity.User;
import ru.task.reg.repository.SessionRepo;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionService {
    SessionRepo sessionRepo;

    public Session newSession(User user, String token) {
        Session session = new Session();
        session.setUser(user);
        session.setCreated(LocalDateTime.now());
        session.setToken(token);
        return sessionRepo.save(session);
    }

    public Session findSessionByToken(String token) {
        return sessionRepo.findByToken(token).orElseThrow();
    }

}
