package ru.taksebe.telegram.premium.telegram;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import ru.taksebe.telegram.premium.exceptions.TelegramFileNotFoundException;
import ru.taksebe.telegram.premium.exceptions.TelegramFileUploadByIdException;
import ru.taksebe.telegram.premium.exceptions.TelegramFileUploadException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramApiClient {
    String URL;
    String botToken;

    String startMessagePhotoFileId;
    String startMessagePhotoFilePath;
    String startMessageText;

    String tempFileNamePrefix;

    RestTemplate restTemplate;

    public TelegramApiClient(@Value("${telegram.api-url}") String URL,
                             @Value("${telegram.bot-token}") String botToken,
                             @Value("${message.start.picture-file-id}") String startMessagePhotoFileId,
                             @Value("${message.start.picture-file-path}") String startMessagePhotoFilePath,
                             @Value("${message.start.text}") String startMessageText,
                             @Value("${files.incoming}") String tempFileNamePrefix) {
        this.URL = URL;
        this.botToken = botToken;
        this.tempFileNamePrefix = tempFileNamePrefix;
        this.startMessagePhotoFileId = startMessagePhotoFileId;
        this.startMessagePhotoFilePath = startMessagePhotoFilePath;
        this.startMessageText = startMessageText;
        this.restTemplate = new RestTemplate();
    }

    public void uploadStartPhoto(String chatId) throws IOException {
        try {
            uploadPhotoByFileId(chatId);
        } catch (TelegramFileUploadByIdException e) {
            uploadPhotoAsFile(chatId);
        }
    }

    public void uploadAudio(String chatId, ByteArrayResource value) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("audio", value);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        try {
            restTemplate.exchange(
                    MessageFormat.format("{0}bot{1}/sendAudio?chat_id={2}", URL, botToken, chatId),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
        } catch (Exception e) {
            throw new TelegramFileUploadException();
        }
    }

    public File getVoiceFile(String fileId) {
        try {
            return restTemplate.execute(
                    Objects.requireNonNull(getVoiceTelegramFileUrl(fileId)),
                    HttpMethod.GET,
                    null,
                    clientHttpResponse -> {
                        File ret = File.createTempFile(this.tempFileNamePrefix, ".ogg");
                        StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
                        return ret;
                    });
        } catch (Exception e) {
            throw new TelegramFileNotFoundException();
        }
    }

    private void uploadPhotoByFileId(String chatId) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("photo", this.startMessagePhotoFileId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        try {
            restTemplate.exchange(
                    MessageFormat.format("{0}bot{1}/sendPhoto?chat_id={2}&caption={3}",
                            URL, botToken, chatId, this.startMessageText),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
        } catch (Exception e) {
            throw new TelegramFileUploadByIdException();
        }
    }

    private void uploadPhotoAsFile(String chatId) throws IOException {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("photo", getPhotoResource());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        try {
            restTemplate.exchange(
                    MessageFormat.format("{0}bot{1}/sendPhoto?chat_id={2}&caption={3}",
                            URL, botToken, chatId, this.startMessageText),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
        } catch (Exception e) {
            throw new TelegramFileUploadException();
        }
    }

    private ByteArrayResource getPhotoResource() throws IOException {
        byte[] data = Objects.requireNonNull(this.getClass().getClassLoader()
                .getResourceAsStream(this.startMessagePhotoFilePath)).readAllBytes();

        return new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return "start.jpg";
            }
        };
    }

    private String getVoiceTelegramFileUrl(String fileId) {
        try {
            ResponseEntity<ApiResponse<org.telegram.telegrambots.meta.api.objects.File>> response = restTemplate.exchange(
                    MessageFormat.format("{0}bot{1}/getFile?file_id={2}", URL, botToken, fileId),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<org.telegram.telegrambots.meta.api.objects.File>>() {
                    }
            );
            return Objects.requireNonNull(response.getBody()).getResult().getFileUrl(this.botToken);
        } catch (Exception e) {
            throw new TelegramFileNotFoundException();
        }
    }
}