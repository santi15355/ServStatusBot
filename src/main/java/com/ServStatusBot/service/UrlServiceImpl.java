package com.ServStatusBot.service;

import com.ServStatusBot.model.Url;
import com.ServStatusBot.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlServiceImpl implements UrlService {

    @Autowired
    private UrlRepository urlRepository;
    @Override
    public Url saveUrl(Url url) {
        return urlRepository.save(url);
    }
}
