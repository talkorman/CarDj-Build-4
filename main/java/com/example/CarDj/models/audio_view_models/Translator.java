package com.example.CarDj.models.audio_view_models;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.CarDj.R;
import com.example.CarDj.models.State;

public class Translator extends Fragment {
    private int countSayResults;
    private StringBuilder voice;
    private StringBuilder searchingString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_translate, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppState appState = new ViewModelProvider(requireActivity()).get(AppState.class);
        appState.getUserSpokenText().observe(getViewLifecycleOwner(), (s)->{
            voice = new StringBuilder();
            searchingString = new StringBuilder();
            String words = s.toLowerCase();

            System.out.println(words);
            State state = appState.getCurrentState().getValue();
            if (words.contains("i want")) {
                appState.setAppState(State.ASKING_FOR_SEARCH);
                countSayResults = 0;
                voice.setLength(0);
                searchingString.setLength(0);
                searchingString.append(words);
                searchingString.delete(0, 7);
                String item = searchingString.toString();
                item.replace(".", "/").replace(",", "/").replace(" ", "/");
                voice.append(item);
                System.out.println(item);
                appState.getDeviceTextToSay().postValue(voice);
                appState.getSearchWords().postValue(item);
                return;
            }
            if(words.contains("no") && state == State.DEVICE_ALREADY_SAID_RESULT){
                appState.getCurrentState().postValue(State.USER_SAID_NO);
                return;
            }
            if(words.contains("yes") && state == State.DEVICE_ALREADY_SAID_RESULT){
                appState.getCurrentState().postValue(State.USER_SAID_YES);
                return;
            }
            if(words.contains("ok") && state == State.DEVICE_ALREADY_SAID_RESULT){
                appState.getCurrentState().postValue(State.ADDTO_PLAYLIST);
                return;
            }
            if(words.contains("go") && state == State.OPENING){
                appState.getCurrentState().postValue(State.DISPLAY_PLAYLIST);
                return;
            }
            if(words.contains("delete all")){
                appState.getCurrentState().postValue(State.DELETE_ALL);
            }
        });
    }




/*
    public void translateSpokenText(String userSpokeText, MutableLiveData<Integer> currentState, int resultSize) {
       // ArrayList<SongData> resultsObjects = AppState.getInstance().resultsObjects;
        String words = userSpokeText.toLowerCase();
        int state = currentState.getValue();
        //AppState stateUpdate = AppState.getInstance();

        if(state == AppState.GREETING && words.contains("")){
            countSayResults = 0;
            //UserSay.getInstance().activateUserSay(true);
            return;
        }
        if( words.contains("cut")){
            System.out.println("cancel");
            countSayResults = 0;
            //closePlayer();
            //recognizer.startListening(intent);
            currentState.postValue(AppState.REJECT);
            //stateUpdate.setAppState(AppState.GREETING);
            return;
        }
        if(state == AppState.DEVICE_SAY_RESULT && words.contains("yes")){
            System.out.println("yes");
            //DeviceSay.getInstance().textToSpeech.stop();
            currentState.postValue(AppState.USER_SAID_YES);
            return;
        }
        if(state == AppState.DEVICE_SAY_RESULT && words.contains("no")){
            System.out.println("no");
                currentState.postValue(AppState.USER_SAID_NO);
               return;
            }
        if((state == AppState.DEVICE_SAY_RESULT ||
                state == AppState.DISPLAYING_SONG) && words.contains("to play list")){
            currentState.postValue(AppState.ADDTO_PLAYLIST);
            //countSayResults = 0;
            //AppState.getInstance().setAppState(AppState.ADDTO_PLAYLIST);
            //resultsObjects.get(countSayResults).setSongId(songId);
            //dataSource.add(resultsObjects.get(countSayResults));
            //showPlayList();
            //
            //appState.setAppState(AppState.GREETING);
            return;
        }
        if(state == AppState.DEVICE_SAY_RESULT && (words.contains("number one") ||
                words.contains("number two") || words.contains("number three") ||
                words.contains("number four") || words.contains("number five"))){
            String text = words.substring(0, 6);
            System.out.println("number chosen: " + text);
            switch (text){
                case "one" : countSayResults = 0;
                break;
                case "two" : countSayResults = 1;
                break;
                case "three" : countSayResults = 2;
                break;
                case "four" : countSayResults = 3;
                break;
                case "five" : countSayResults = 4;
                break;
            }
            currentState.postValue(AppState.USER_SAY_RESULT_NUMBER);
            //appState.setAppState(AppState.DEVICE_SAY_OPTION);
            //tellOptions(resultsObjects);
            return;
        }
        if(state == AppState.DEVICE_SAY_RESULT){
            countSayResults = 0;
            currentState.postValue(AppState.REPEAT_RESULTS);
        }
        if(words == "detete all"){
            currentState.postValue(AppState.DELETE_ALL);
        }
    }

 */
}
