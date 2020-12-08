package com.example.CarDj.models;

public class YoutubeConfig {
    public YoutubeConfig(){

    }
    private static final String API_KEY = "AIzaSyBqECp8c8JgCjbeOq80j3uJpHI148URZyA";
    private static final String API_ADDRESS = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=rating&type=video&videoDefinition=any&videoDuration=short&videoEmbeddable=any&key=";
    public static String getApiAddress(){
        return API_ADDRESS + API_KEY + "&q=";
    }
}

