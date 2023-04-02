package com.ServStatusBot.service;

import com.ServStatusBot.config.BotConfig;
import com.ServStatusBot.model.Url;
import com.ServStatusBot.model.User;
import com.ServStatusBot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    @Autowired
    private final UserService userService;

    @Autowired
    private final UserRepository userRepository;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String command = update.getMessage().getText();

            if (command.equals("старт")) {
                sendMessage(chatId, "Введите добавить адрес интервал проверки в секундах");
            } else if (command.contains("добавить")) {
                List<String> words = List.of(update.getMessage().getText().split(" "));
                User user = new User();
                Url url = new Url();
                url.setUrl(words.get(1));
                url.setInterval(Long.valueOf(words.get(2)));
                user.setChatId(update.getMessage().getChatId());
                user.setUrl(Collections.singletonList(url));
                user.setUserName(update.getMessage().getChat().getFirstName());
                userService.saveUser(user);

            } else if (command.equals("показать")) {
                List<User> users = userRepository.findAllByChatId(chatId);
                Map<String, Long> result = new HashMap<>();
                int size = userRepository.findAllByChatId(chatId).size();
                for (User user : users) {
                    result.put(user.getUrl().get(0).getUrl(), user.getUrl().get(0).getInterval());
                }
                sendMessage(chatId, String.valueOf(result));
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
