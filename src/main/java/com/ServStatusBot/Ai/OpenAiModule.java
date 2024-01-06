package com.ServStatusBot.Ai;

import com.ServStatusBot.Utils;
import com.ServStatusBot.config.BotConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OpenAiModule {

    private final Utils utils;

    private final BotConfig botConfig;

    private static final String MODEL_GPT_35 = "gpt-3.5-turbo";
    @SneakyThrows
    public String getAnswerFromAi(String userMessage) {

        URL url = new URL("https://api.proxyapi.ru/openai/v1/chat/completions");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + botConfig.getTokenAi());
        connection.setRequestProperty("Accept", "application/json");

        String jsonBody = "{\"model\": \"" + MODEL_GPT_35 + "\", \"messages\": [{\"role\": \"user\", \"content\": \""
                + userMessage + "\"}]}";
        connection.setDoOutput(true);

        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
        writer.write(jsonBody);
        writer.flush();
        writer.close();
        System.out.println(writer);

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (IOException e) {
            // Обработка исключения IOException
            e.printStackTrace();
        }
        return utils.getGtpAnswer(response);
    }
}
