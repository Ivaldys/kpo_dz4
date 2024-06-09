package ru.task.reg.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "session")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Session {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "token")
    String token;

    @Column(name = "created")
    LocalDateTime created;

}
