package com.example.attendify_1;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;

public class Global_Data_Structure extends Application {

    public static HashMap<String, String> data = new HashMap<>();

    public static HashMap<String, String> getData() {
        return data;
    }

    public static HashMap<String, String> allStudents = new HashMap<>();
}