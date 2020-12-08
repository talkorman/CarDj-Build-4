package com.example.CarDj.models.audio_view_models;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.CarDj.models.SongData;
import com.example.CarDj.models.SongsDataResults;
import com.example.CarDj.models.State;
import com.example.CarDj.models.play_lists.SongsDataSource;

import java.util.ArrayList;
import java.util.List;

public class AppState extends ViewModel {
    private final MutableLiveData<State> currentState = new MutableLiveData<>();
    private final MutableLiveData<String> searchWords = new MutableLiveData<>();
    private final MutableLiveData<String> userSpokenText = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<SongData>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<StringBuilder> deviceTextToSay = new MutableLiveData<>();
    private final MutableLiveData<String> songId = new MutableLiveData<>();
    private final MutableLiveData<SongData> songToList = new MutableLiveData<>();
    private final MutableLiveData<List<SongData>> songsPlayList = new MutableLiveData<>();
    private String wordsText;


    public MutableLiveData<List<SongData>> getSongsPlayList(){return songsPlayList;}
    public MutableLiveData<SongData> getSongToList(){return songToList;}
    public MutableLiveData<String> getSongId(){ return songId;}
    public String getWordsText() {
        return wordsText;
    }
    public MutableLiveData<String> getSearchWords(){ return searchWords;}
    public MutableLiveData<StringBuilder>getDeviceTextToSay() {
        return deviceTextToSay;
    }
    public MutableLiveData<String> getUserSpokenText() {
        return userSpokenText;
    }
    public MutableLiveData<ArrayList<SongData>> getSearchResults(){ return searchResults;}
    public void setAppState(State state) {
        this.currentState.postValue(state);;
    }
    public MutableLiveData<State> getCurrentState(){
        return currentState;
    }
    public void getWordsText(MutableLiveData<String> callback){
        callback.postValue(wordsText);
    }
    public void delay(int seconds, State state){
        System.out.println("call to change state");
        Handler h = new Handler();
        Runnable time = new Runnable() {
            @Override
            public void run() {
                setAppState(state);
                System.out.println("perform state change");
            }
        };
        h.postDelayed(time, seconds);
    }
}

