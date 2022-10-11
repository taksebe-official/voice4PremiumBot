package ru.taksebe.telegram.premium.utils;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class Converter {
    private final FFmpeg ffmpeg;

    public Converter(@Value("${ffmpeg.path}") String ffmpegPath) throws IOException {
        this.ffmpeg = new FFmpeg(new File(ffmpegPath).getPath());
    }

    public void convertOggToMp3(String inputPath, String targetPath) throws IOException {
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(inputPath)
                .overrideOutputFiles(true)
                .addOutput(targetPath)
                .setAudioCodec("libmp3lame")
                .setAudioBitRate(32768)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(this.ffmpeg);
        executor.createJob(builder).run();

        try {
            executor.createTwoPassJob(builder).run();
        } catch (IllegalArgumentException ignored) {//отлавливаем и игнорируем ошибку, возникающую из-за отсутствия видеоряда (конвертер предназначен для видео)
        }
    }
}