package com.example.a4paper;

public class statistics_word {
    private  String word;
    private  int click_time;

    public statistics_word(String word, int click_time) {
        this.word = word;
        this.click_time = click_time;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getClick_time() {
        return click_time;
    }

    public void setClick_time(int click_time) {
        this.click_time = click_time;
    }
}
