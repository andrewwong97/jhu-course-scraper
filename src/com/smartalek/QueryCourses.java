package com.smartalek;

import com.jaunt.*;
import java.lang.String;
import java.util.ArrayList;


public class QueryCourses {
    public static void main(String[] args) {
        queryCourses();
    }
    /**
     * Send a GET request to ISIS API to grab all EN courses for current semester
     * Parse JSON data from API, then populate an array list of all possible courses
     * @return an ArrayList of all possible courses
     */
    private static void queryCourses() {
        UserAgent ua = new UserAgent();
        String key = "current?key=tDxBrZz15UY2cJ258US4qYEuLONUBS4S";
        try {
            ua.sendGET("https://isis.jhu.edu/api/classes/Whiting%20School%20of%20Engineering/" + key);
        } catch (ResponseException e) {
            e.printStackTrace();
        }
        System.out.println(ua.getSource());
    }
}
