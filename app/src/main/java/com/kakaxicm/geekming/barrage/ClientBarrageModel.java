package com.kakaxicm.geekming.barrage;

/**
 * Created by chenming on 2018/8/30
 */
public class ClientBarrageModel extends BarrageModel {
    public String content;
    public int textColor;
    public String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
