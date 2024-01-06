package com.ServStatusBot.service;

import com.ServStatusBot.Ai.OpenAiModule;
import com.ServStatusBot.Utils;
import com.ServStatusBot.config.BotConfig;
import com.ServStatusBot.config.SSLVerificationDisabler;
import com.ServStatusBot.model.Url;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
@EnableRetry
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    private final OpenAiModule openAiModule;

    private final SSLVerificationDisabler sslVerificationDisabler;

    @Autowired
    private final UserService userService;

    @Autowired
    private final Utils utils;

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
            String name = update.getMessage().getChat().getFirstName();
//            var textList = Arrays.stream(update.getMessage().getText().split(" ")).toList();
//            var userUrl = textList.get(1);
//            long interval = Long.parseLong(textList.get(2));

            if (command.equals("старт")) {
//                sendMessage(chatId, "Введите добавить адрес интервал проверки в секундах");

            } else if (command.contains("добавить")) {
//                utils.createUserAndUrl(chatId, userUrl, name, interval, false);

            } else if (command.equals("показать")) {
//                List<Url> urls = userService.findByChatId(chatId).getUrls();
//                Map<String, Long> result = new HashMap<>();
//                for (var url : urls) {
//                    result.put(url.getUrl(), url.getInterval());
//                }
//                sendMessage(chatId, String.valueOf(result));

//            } else if (command.contains("проверить")) {
 //               isUrlWorks(chatId, userUrl);

            } else if (command.contains("следить")) {
//                startMonitoring(interval, userUrl, chatId);
                sendMessage(chatId, "работаю");

            } else if (command.startsWith("вопрос")) {
                String message = update.getMessage().getText();
                int spaceIndex = message.indexOf(' ');
                sendMessage(chatId, openAiModule.getAnswerFromAi(message.substring(spaceIndex + 1)));
            }
            else {
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

    @SneakyThrows
    private void isUrlWorks(long chatId, String url) {

        try {
            sslVerificationDisabler.disableSSLVerification();
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url)
                    .openConnection();
            var responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                sendMessage(chatId, "НЕ ДОСТУПЕН!!!");
            } else {
                sendMessage(chatId, "РЕСУРС ДОСТУПЕН");
            }

        } catch (final MalformedURLException e) {
            throw new IllegalStateException(e);
        } catch (final IOException e) {
            sendMessage(chatId, "НЕ ДОСТУПЕН");
        }
    }

    private void startMonitoring(long interval, String url, Long chatId) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    sslVerificationDisabler.disableSSLVerification();
                    HttpsURLConnection connection = (HttpsURLConnection) new URL(url)
                            .openConnection();
                    var responseCode = connection.getResponseCode();
                    if (responseCode != 200) {
                        sendMessage(chatId, "Сервис: " + url + " " + "НЕ ДОСТУПЕН!");
                        wait(500);
                        sendMessage(chatId, "Ожидание 5 мин");
                        Thread.sleep(300000);
                    }

                } catch (final IOException e) {
                    sendMessage(chatId, "Ошибка " + url);
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, interval * 1000);
    }
}
