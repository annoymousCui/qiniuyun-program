package com.group.voice.controller;

import com.group.common.result.Result;
import com.group.voice.service.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 语音控制器
 */
@RestController
@RequestMapping("/voice")
@CrossOrigin
public class VoiceController {
    
    @Autowired
    private VoiceService voiceService;
    
    /**
     * 语音转文字
     */
    @PostMapping("/speech-to-text")
    public Result<Map<String, Object>> speechToText(@RequestParam("file") MultipartFile file) {
        try {
            String text = voiceService.speechToText(file);
            Map<String, Object> data = new HashMap<>();
            data.put("text", text);
            return Result.success("转换成功", data);
        } catch (Exception e) {
            return Result.error("语音转文字失败：" + e.getMessage());
        }
    }
    
    /**
     * 文字转语音
     */
    @PostMapping("/text-to-speech")
    public Result<Map<String, Object>> textToSpeech(@RequestParam String text,
                                                   @RequestParam(required = false, defaultValue = "zh") String language) {
        try {
            String audioUrl = voiceService.textToSpeech(text, language);
            Map<String, Object> data = new HashMap<>();
            data.put("audioUrl", audioUrl);
            return Result.success("转换成功", data);
        } catch (Exception e) {
            return Result.error("文字转语音失败：" + e.getMessage());
        }
    }
    
}



