package com.ServStatusBot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

import static jakarta.persistence.TemporalType.TIMESTAMP;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long interval;

    @Lob
    @NotNull
    private Long chatId;

    @Lob
    @NotNull
    private String userLink;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;
}
