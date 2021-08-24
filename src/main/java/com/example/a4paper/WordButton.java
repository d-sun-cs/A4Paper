package com.example.a4paper;

import org.litepal.crud.DataSupport;

/**
 * 这个类是使用github上开源库时要用的
 * 利用这个开源库可以很方便的实现数据库的操作
 */
public class WordButton extends DataSupport {
    int id;
    int left;
    int top;
    int right;
    int bottom;
    String word;
    String interpret;
    //int time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getInterpret() {
        return interpret;
    }

    public void setInterpret(String interpret) {
        this.interpret = interpret;
    }

    /*public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }*/

}
