package ru.task.reg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.task.reg.entity.Session;

import java.util.Optional;

@Repository
public interface SessionRepo extends JpaRepository<Session, Integer> {
    Optional<Session> findByToken(String token);
}
