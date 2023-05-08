package com.ServStatusBot.service;

import com.ServStatusBot.config.BotConfig;
import com.ServStatusBot.model.Url;
import com.ServStatusBot.model.User;
import com.ServStatusBot.repository.UrlRepository;
import com.ServStatusBot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    @Autowired
    private final UserService userService;

    @Autowired
    private final UrlService urlService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UrlRepository urlRepository;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        List<String> userText = List.of(update.getMessage().getText().split(" "));

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String command = update.getMessage().getText();

            if (command.equals("старт")) {
                sendMessage(chatId, "Введите добавить адрес интервал проверки в секундах");
            } else if (command.contains("добавить")) {
                if (userRepository.findByChatId(chatId).isEmpty()) {
                    User user = new User();
                    Url url = new Url();
                    List<Url> urls = new ArrayList<>();
                    url.setUrl(userText.get(1));
                    url.setInterval(Long.valueOf(userText.get(2)));
                    user.setChatId(update.getMessage().getChatId());
                    user.setUserName(update.getMessage().getChat().getFirstName());
                    urls.add(url);
                    user.setUrls(urls);
                    urlService.saveUrl(url);
                    userService.saveUser(user);

                } else {
                    User currentUser = userRepository.findByChatId(chatId).get();
                    List<Url> currentUserUrls = currentUser.getUrls();
                    Url newUrl = new Url();
                    newUrl.setUrl(userText.get(1));
                    newUrl.setInterval(Long.valueOf(userText.get(2)));
                    currentUserUrls.add(newUrl);
                    currentUser.setUrls(currentUserUrls);
                    urlService.saveUrl(newUrl);
                    userService.saveUser(currentUser);
                }

            } else if (command.equals("показать")) {
                List<Url> urls = userRepository.findByChatId(chatId).get().getUrls();
                Map<String, Long> result = new HashMap<>();
                for (var url : urls) {
                    result.put(url.getUrl(), url.getInterval());
                }
                sendMessage(chatId, String.valueOf(result));
            } else if (command.contains("проверить")) {
                String url = userText.get(1);
                isUrlWorks(chatId, url);
            } else {
                sendMessage(chatId, "Ты пишешь какую-то дичь");
            }
        }

    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Привет, " + name;
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void isUrlWorks(long chatId, String url) throws IOException {

        try {
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.connect();
            sendMessage(chatId, "WORKS");
        } catch (final MalformedURLException e) {
            throw new IllegalStateException(e);
        } catch (final IOException e) {
            sendMessage(chatId, "NOT WORKS");
        }
    }


    public void addUrlToCheck(Long chatId) {


    }

}
