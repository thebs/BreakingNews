package com.example.lab12f7.breakingnews;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by lab12f7 on 1/16/2017.
 */

public class News {
    private String details;
    private String des;

    public News(String details, String des){
        this.details = details;
        this.des = des;
    }

    public String getDetails() {
        return details;
    }

    public String getDes() {
        return des;
    }

    @Override
    public String toString() {
        return details;
    }
}
