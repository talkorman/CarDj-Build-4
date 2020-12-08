package com.example.CarDj.models.audio_view_models;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.CarDj.R;
import com.example.CarDj.models.State;

import java.util.ArrayList;

public class UserSayFragment extends Fragment {

    private ImageButton btnUserSay;
    private SpeechRecognizer recognizer;
    private Intent intent;
    private AppState appState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_user_audio, container, false);
        btnUserSay = root.findViewById(R.id.btnUserSay);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            appState = new ViewModelProvider(requireActivity()).get(AppState.class);
        appState.getCurrentState().observe(getViewLifecycleOwner(), (state)-> {
            if (state == State.SEARCH || state == State.SHOW_RESULTS || state == State.REPEAT_RESULTS || state == State.USER_DIDNT_CHOSE){
                recognizer.stopListening();
                return;
            }
            if (state == State.DEVICE_ALREADY_SAID_RESULT || state == State.OPENING) {
                recognizer.startListening(intent);
                return;
            }
        }
        );
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                recognizer.stopListening();
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    String string = matches.get(0);
                    appState.getUserSpokenText().postValue(string);
                }
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
            }
            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
                System.out.println("End of speach");
            }

            @Override
            public void onError(int error) {
                if (error == 1 || error == 5) {
                   if(appState.getCurrentState().getValue() == State.OPENING){
                       recognizer.startListening(intent);
                   }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                //recognizer.stopListening();
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });
        btnUserSay.setOnClickListener((v) -> {
                recognizer.startListening(intent);
        });

    }
}
