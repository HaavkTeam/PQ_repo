package org.pqproject.backend.service;

import org.pqproject.backend.pojo.ReturnSpeech;
import org.pqproject.backend.pojo.Speech;
import org.pqproject.backend.pojo.Spit;
import org.pqproject.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.pqproject.backend.mapper.SpeechMapper;
import org.pqproject.backend.mapper.userMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SpeechServiceImpl implements SpeechService {
    @Autowired
    private SpeechMapper speechMapper;

    @Autowired
    private userMapper userMapper;

    public ReturnSpeech getSpeechById(String speechId){
        Speech speech = speechMapper.getSpeechById(speechId);
        return transformToReturnSpeech(speech); // Transform Speech to ReturnSpeech
    };

    public List<ReturnSpeech> getAllSpeeches() {
        if (speechMapper.getAllSpeeches().size() == 0) {
            List<ReturnSpeech> speeches = new ArrayList<>();
            Speech defaultSpeech = new Speech();
            defaultSpeech.createDefaultSpeech();
            speeches.add(transformToReturnSpeech(defaultSpeech)); // Return a default speech if no speeches are found
            return speeches; // Return an empty list if no speeches are found
        }
        else {
            List<ReturnSpeech> returnSpeeches = new ArrayList<>();
            List<Speech> speeches = speechMapper.getAllSpeeches();
            for (Speech speech : speeches) {
                returnSpeeches.add(transformToReturnSpeech(speech)); // Transform each Speech to ReturnSpeech
            }
            return returnSpeeches; // Return the list of transformed speeches
        }
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

    public boolean joinSpeech(String speechId, String userId) {
        Speech existingSpeech = speechMapper.getSpeechById(speechId);
        if (existingSpeech == null) {
            return false; // Speech with this ID does not exist
        } else {
            speechMapper.joinSpeech(speechId, userId);
            return true; // User joined the speech successfully
        }
    }

    public List<ReturnSpeech> getMySpeeches(String userId) {
        List<String> speeches = speechMapper.getMySpeeches(userId);
        List<Speech> mySpeeches = new ArrayList<>();
        for (String speechId : speeches) {
            Speech speech = speechMapper.getSpeechById(speechId);
            if (speech != null) {
                mySpeeches.add(speech);
            }
        }
        if (mySpeeches.isEmpty()) {
            Speech defaultSpeech = new Speech();
            defaultSpeech.createDefaultSpeech();
            mySpeeches.add(defaultSpeech); // Return a default speech if no speeches are found
        }
        List<ReturnSpeech> myReturnSpeeches = new ArrayList<>();
        for (Speech speech : mySpeeches) {
            myReturnSpeeches.add(transformToReturnSpeech(speech)); // Transform each Speech to ReturnSpeech
        }
        return myReturnSpeeches;
    }

    public List<ReturnSpeech> getSpeechesBySpeaker(String speaker) {
        List<Speech> speeches = speechMapper.getSpeechesBySpeaker(speaker);
        if (speeches.isEmpty()) {
            Speech defaultSpeech = new Speech();
            defaultSpeech.createDefaultSpeech();
            speeches.add(defaultSpeech); // Return a default speech if no speeches are found
        }
        List<ReturnSpeech> myReturnSpeeches = new ArrayList<>();
        for (Speech speech : speeches) {
            myReturnSpeeches.add(transformToReturnSpeech(speech)); // Transform each Speech to ReturnSpeech
        }
        return myReturnSpeeches;
    }

    public List<ReturnSpeech> getSpeechesByOrganizer(String organizer) {
        List<Speech> speeches = speechMapper.getSpeechesByOrganizer(organizer);
        if (speeches.isEmpty()) {
            Speech defaultSpeech = new Speech();
            defaultSpeech.createDefaultSpeech();
            speeches.add(defaultSpeech); // Return a default speech if no speeches are found
        }
        List<ReturnSpeech> myReturnSpeeches = new ArrayList<>();
        for (Speech speech : speeches) {
            myReturnSpeeches.add(transformToReturnSpeech(speech)); // Transform each Speech to ReturnSpeech
        }
        return myReturnSpeeches;
    }

    public List<String> getAudienceBySpeechId(String speechId) {
        Speech existingSpeech = speechMapper.getSpeechById(speechId);
        if (existingSpeech == null) {
            return new ArrayList<>(); // Return an empty list if the speech does not exist
        } else {
            // Retrieve the audience members for the given speech ID
            List<String> audienceNames = new ArrayList<>();
            List<String> audienceIds = speechMapper.getUsersBySpeechId(speechId);
            for (String audience : audienceIds) {
                User user = userMapper.getUserById(audience);
                if (user != null) {
                    audienceNames.add(user.getUsername()); // Add the username to the list
                } else {
                    audienceNames.add("未知用户"); // Default username if user not found
                }
            }
            return audienceNames; // Retrieve all audience members for the given speech ID
        }
    }
    public boolean spikeSpeech(Spit spit) {
        Speech existingSpeech = speechMapper.getSpeechById(spit.getSpeechId());
        String spitId = getUppercaseLetterAndNumber(8);
        spit.setSpitId(spitId); // Set a unique spit ID
        if (existingSpeech == null) {
            return false; // Speech with this ID does not exist
        } else {
            speechMapper.spikeSpeech(spit);
            return true; // Spit added successfully
        }
    }

    public List<Spit> getSpitsBySpeechId(String speechId) {
        Speech existingSpeech = speechMapper.getSpeechById(speechId);
        if (existingSpeech == null) {
            return new ArrayList<>(); // Return an empty list if the speech does not exist
        } else {
            return speechMapper.getSpitsBySpeechId(speechId); // Retrieve all spits for the given speech ID
        }
    }

    public ReturnSpeech transformToReturnSpeech(Speech speech) {
        User speaker = userMapper.getUserById(speech.getSpeaker());
        if (speaker == null) {
            speaker = new User();
            speaker.setUsername("未知演讲者"); // Default username if speaker not found
        }
        User organizer = userMapper.getUserById(speech.getOrganizer());
        if (organizer == null) {
            organizer = new User();
            organizer.setUsername("未知组织者"); // Default username if organizer not found
        }
        ReturnSpeech returnSpeech = new ReturnSpeech();
        returnSpeech.createReturnSpeech(speech, speaker.getUsername(), organizer.getUsername());
        return returnSpeech; // Transform Speech to ReturnSpeech
    }

    public  String getUppercaseLetterAndNumber(Integer length) {
        String uid = "";
        for (Integer i = 0; i < length; i++) {
            int index = (int) Math.round(Math.random() * 1);
            String randChar = "";
            switch (index) {
                case 0:
                    //大写字符
                    randChar = String.valueOf((char) Math.round(Math.random() * 25 + 97));
                    break;
                default:
                    //数字
                    randChar = String.valueOf(Math.round(Math.random() * 9));
                    break;
            }
            uid = uid.concat(randChar);
        }
        return uid;
    }
}
