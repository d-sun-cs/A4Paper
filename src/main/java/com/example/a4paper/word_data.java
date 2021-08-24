package com.example.a4paper;

/**
 * 创建数据源
 */

public class word_data {
    String word;
    int times;

    public word_data(String word, int times) {
        this.word = word;
        this.times = times;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

}
