package org.pqproject.backend.controller;

import org.pqproject.backend.pojo.Speech;
import org.pqproject.backend.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/speech")
public class SpeechController {
    @Autowired
    private SpeechService speechService;

    @RequestMapping("/getSpeechById")
    public Speech getSpeechById(@RequestParam String speechId) {
        return speechService.getSpeechById(speechId); // Return the speech details as a string
    }

    @RequestMapping("/getAllSpeeches")
    public List<Speech> getAllSpeeches() {
        return speechService.getAllSpeeches(); // Return the list of all speeches
    }

    @RequestMapping("/addSpeech")
    public String addSpeech(@RequestBody Speech speech) {
        if (speechService.addSpeech(speech)) {
            return "演讲添加成功"; // Return a success message
        } else {
            return "演讲添加失败，演讲ID已存在"; // Return an error message
        }
    }

    @RequestMapping("/updateSpeech")
    public String updateSpeech(@RequestBody Speech speech) {
        if (speechService.updateSpeech(speech)) {
            return "演讲更新成功"; // Return a success message
        } else {
            return "演讲更新失败，演讲ID不存在"; // Return an error message
        }
    }

    @RequestMapping("/deleteSpeech")
    public String deleteSpeech(@RequestParam String speechId) {
        if (speechService.deleteSpeech(speechId)) {
            return "演讲删除成功"; // Return a success message
        } else {
            return "演讲删除失败，演讲ID不存在"; // Return an error message
        }
    }

    @RequestMapping("/test")
    public String test() {
        return "测试成功"; // Return a success message for testing purposes
    }

    @RequestMapping("/startSpeech")
    public String startSpeech(@RequestParam String speechId) {
        speechService.startSpeech(speechId, new Date()); // Start the speech with the current time
        return "演讲已开始"; // Return a success message indicating the speech has started
    }

    @RequestMapping("/endSpeech")
    public String endSpeech(@RequestParam String speechId) {
        speechService.endSpeech(speechId, new Date()); // End the speech with the current time
        return "演讲已结束"; // Return a success message indicating the speech has ended
    }
}
