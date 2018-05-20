package com.example.subhamdhakal.eavesdrop;

public class StoreData {

    String songName;
    String songGenre;
    String songArtist;
    String songPitch;


    public StoreData(){

    }



    public StoreData(String songName, String songGenre, String songArtist, String songPitch) {
        this.songName = songName;
        this.songGenre = songGenre;
        this.songArtist=songArtist;
        this.songPitch = songPitch;
    }
    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setSongGenre(String songGenre) {
        this.songGenre = songGenre;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public void setSongPitch(String songPitch) {
        this.songPitch = songPitch;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongGenre() {
        return songGenre;
    }
    public String getArtist() {
        return songArtist;
    }

    public String getSongPitch() {
        return songPitch;
    }


}
