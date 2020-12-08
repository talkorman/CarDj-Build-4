package com.example.CarDj.view_search_results;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.CarDj.R;
import com.example.CarDj.models.SongData;
import com.example.CarDj.models.SongsDataResults;
import com.example.CarDj.models.State;
import com.example.CarDj.models.audio_view_models.AppState;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class ResultsFragment extends Fragment {
    private RecyclerView rvSongs;
    private static ArrayList<SongData> resultsToShow = new ArrayList<>();


   public static ResultsFragment newInstance(){
    ResultsFragment resultsFragment = new ResultsFragment();
       return resultsFragment;
   }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_results_view, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppState appState = new ViewModelProvider(requireActivity()).get(AppState.class);
        appState.getSearchWords().observe(getViewLifecycleOwner(), (search)->{
            System.out.println("got search words in fragment");
            SongsDataResults sdr = new SongsDataResults(search);
            sdr.readSearch(appState.getSearchResults());
        });
        appState.getSearchResults().observe(getViewLifecycleOwner(), (results)->{
            System.out.println("got search results in fragment");
            appState.setAppState(State.SHOW_RESULTS);
            rvSongs = view.findViewById(R.id.view_list);
            rvSongs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            ResultsAdapter adapter = null;
            try {
                adapter = new ResultsAdapter(results, getContext());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();

            }
            rvSongs.setAdapter(adapter);
        });


    }
}

