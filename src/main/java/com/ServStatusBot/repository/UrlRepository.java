package com.ServStatusBot.repository;

import com.ServStatusBot.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByUrl(String url);
}
