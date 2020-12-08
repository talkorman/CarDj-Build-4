package com.example.CarDj.models.audio_view_models;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.CarDj.R;
import com.example.CarDj.models.SongData;
import com.example.CarDj.models.State;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Locale;

public class DeviceSayFragment extends Fragment {
    private EditText etText;
    private ImageButton btnUserSay;
    private boolean greet = false;
    private TextToSpeech textToSpeech;
    private int resultNumber;
    private ArrayList<SongData> results;
    private AppState appState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       final View root =  inflater.inflate(R.layout.fragment_device_audio, container, false);
        etText = root.findViewById(R.id.etText);

    return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            appState = new ViewModelProvider(requireActivity()).get(AppState.class);
        textToSpeech = new TextToSpeech(getContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS){
                            int lang = textToSpeech.setLanguage(Locale.US);
                            if(lang==TextToSpeech.LANG_MISSING_DATA||lang==TextToSpeech.LANG_NOT_SUPPORTED){
                                Toast.makeText(getContext(), "Language not supported", Toast.LENGTH_LONG).show();
                            }
                            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                @Override
                                public void onStart(String utteranceId) {
                                    System.out.println("device speaking");
                                }

                                @Override
                                public void onDone(String utteranceId) {
                                    System.out.println("device complete speak");
                                    State state = appState.getCurrentState().getValue();
                                    System.out.println("state seen from device after device speak:" + state);

                                    if(state == State.GREETING){
                                        System.out.println("rejecting");
                                        appState.setAppState(State.REJECT);
                                        return;
                                    }
                                    if(state == State.ASKING_FOR_SEARCH){
                                        appState.getCurrentState().postValue(State.SEARCH);
                                        return;
                                    }
                                    if(state == State.SHOW_RESULTS || state == State.USER_SAID_NO){
                                            resultNumber++;
                                        appState.getCurrentState().postValue(State.DEVICE_ALREADY_SAID_RESULT);
                                        System.out.println("device said results in device");
                                        return;
                                    }
                                    if(state == State.REPEAT_RESULTS){
                                        appState.getCurrentState().postValue(State.SHOW_RESULTS);
                                        appState.getSearchResults().postValue(results);
                                        return;
                                    }
                                }

                                @Override
                                public void onError(String utteranceId) {

                                }
                            });
                        }
                    }
                });
        appState.getDeviceTextToSay().observe(getViewLifecycleOwner(), (text)->{
            StringBuilder fullText = new StringBuilder();
            if(appState.getCurrentState().getValue() == State.ASKING_FOR_SEARCH){
                fullText.append("I'm going to search for ").append(text);
            }
            speak(fullText.toString());
            etText.setText(text.toString());
        });
        appState.getCurrentState().observe(getViewLifecycleOwner(), (state)->{
            if(state == State.GREETING)greeting();
            else if(state == State.USER_SPEAK)textToSpeech.stop();
            else if(state == State.USER_SAID_NO)sayResults();
            else if(state == State.USER_SAID_YES)appState.getSongId().postValue(results.get(resultNumber).getVideoId());
            else if(state == State.ADDTO_PLAYLIST)toPlayList();
            else if(state == State.DISPLAY_PLAYLIST)speak("playing play list");
            else if(state == State.USER_DIDNT_CHOSE){
                System.out.println("Device know that user didn't chose");
                speak("I repeat");
                appState.getCurrentState().postValue(State.REPEAT_RESULTS);
                resultNumber = 0;
            }else if(state == State.DELETE_ALL)speak("I'm deleting all the play list");
        });
        appState.getSearchResults().observe(getViewLifecycleOwner(), (results)->{
                System.out.println(appState.getCurrentState());
            System.out.println("got search result in device");
            this.results = results;
            resultNumber = 0;
            sayResults();
        });

    }
    public void sayResults(){
        System.out.println("device say a result number " + resultNumber);
        if(resultNumber < results.size()){
            String words = results.get(resultNumber).getTitle();
            speak(words);
        }else{
            System.out.println("result eccsess the limit");
            speak("please chose again");
            resultNumber = 0;
            appState.getCurrentState().postValue(State.OPENING);
        }
    }
    public void speak(String s){
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null ,TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
    }
    public void greeting(){
        LocalTime morning = LocalTime.of(4, 0);
        LocalTime noon = LocalTime.of(12, 0);
        LocalTime evening = LocalTime.of(17, 0);
        LocalTime night = LocalTime.of(21, 0);
        LocalTime midnight = LocalTime.of(0, 0);
        LocalTime currentTime = LocalTime.now();
        String time = new String();
        if(currentTime.isAfter(morning))time = "morning";
        if(currentTime.isAfter(noon))time = "afternoon";
        if(currentTime.isAfter(evening))time = "evening";
        else if(currentTime.isAfter(evening) || currentTime.isBefore(morning))time = "night";

        System.out.println(time);
       //speak("Hello");
        speak("Good " + time + "!, My name is Miri. I will search for you and play your favorate songs. " +
                "Please just tell me which song do you like and please " +
                "concentrate on the road. have a safty drive and a pleasent jurney!");
    }
    public void toPlayList(){
        appState.getSongToList().postValue(results.get(resultNumber - 1));
        speak("Added to play list");
        resultNumber = 0;
    }
}