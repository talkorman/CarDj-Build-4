package com.example.CarDj.view_play_list;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.CarDj.R;
import com.example.CarDj.models.SongData;
import com.example.CarDj.models.State;
import com.example.CarDj.models.audio_view_models.AppState;
import com.example.CarDj.models.play_lists.PlayListViewModel;
import com.example.CarDj.models.play_lists.SongsDataSource;

import java.util.ArrayList;
import java.util.List;


public class PlayListFragment extends Fragment {

    private RecyclerView rvPlayList;
    private AppState appState;
    private List<SongData> songs;

    public static PlayListFragment newInstance(){
        PlayListFragment playListFragment = new PlayListFragment();
        return playListFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        appState = new ViewModelProvider(requireActivity()).get(AppState.class);
        View root =  inflater.inflate(R.layout.fragment_play_list, container, false);
        rvPlayList = root.findViewById(R.id.rvPlayList);
        rvPlayList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        SongsDataSource ds = new SongsDataSource(getContext());
        ds.getSongs().observe(getViewLifecycleOwner(), s->{
            songs = s;
            PlayListAdapter adapter = new PlayListAdapter(s, getContext());
            rvPlayList.setAdapter(adapter);
        });
        appState.getSongToList().observe(getViewLifecycleOwner(), song->{
            ds.add(song);
            appState.getCurrentState().postValue(State.OPENING);
        });
        appState.getCurrentState().observe(getViewLifecycleOwner(), state->{
            if(state == State.DISPLAY_PLAYLIST){
            appState.getSongsPlayList().postValue(songs);
            }
            if(state == State.DELETE_ALL){
                ds.deleteAll();
                appState.getCurrentState().postValue(State.OPENING);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appState = new ViewModelProvider(requireActivity()).get(AppState.class);
    }
}
