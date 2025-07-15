package org.pqproject.backend.service;

import org.pqproject.backend.pojo.Speech;
import org.springframework.beans.factory.annotation.Autowired;
import org.pqproject.backend.mapper.SpeechMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SpeechSerivceImpl implements SpeechService {
    @Autowired
    private SpeechMapper speechMapper;

    public  Speech getSpeechById(String speechId){
        return speechMapper.getSpeechById(speechId);
    };

    public List<Speech> getAllSpeeches() {
        if (speechMapper.getAllSpeeches().size() == 0) {
            List<Speech> speeches = new ArrayList<>();
            Speech defaultSpeech = new Speech();
            defaultSpeech.createDefaultSpeech();
            speeches.add(defaultSpeech);
            return speeches; // Return an empty list if no speeches are found
        }
        return speechMapper.getAllSpeeches();
    }

    public boolean addSpeech(Speech speech)
    {
        System.out.println(speech.toString());
        Speech existingSpeech = speechMapper.getSpeechById(speech.getSpeechId());
        if (existingSpeech != null) {
            return false; // Speech with this ID already exists
        }
        else {
            speechMapper.addSpeech(speech);
            return true; // Speech added successfully
        }
    }
    public boolean updateSpeech(Speech speech) {
        Speech existingSpeech = speechMapper.getSpeechById(speech.getSpeechId());
        if (existingSpeech == null) {
            return false; // Speech with this ID does not exist
        }
        else {
            speechMapper.updateSpeech(speech);
            return true; // Speech updated successfully
        }
    }

    public boolean deleteSpeech(String speechId) {
        Speech existingSpeech = speechMapper.getSpeechById(speechId);
        if (existingSpeech == null) {
            return false; // Speech with this ID does not exist
        } else {
            speechMapper.deleteSpeech(speechId);
            return true; // Speech deleted successfully
        }
    }

    public void startSpeech(String speechId, Date startTime) {
        speechMapper.startSpeech(speechId, startTime);
    }

    public void endSpeech(String speechId, Date endTime) {
        speechMapper.endSpeech(speechId, endTime);
    }

}
