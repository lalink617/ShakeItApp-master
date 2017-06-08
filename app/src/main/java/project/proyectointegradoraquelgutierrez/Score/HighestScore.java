package project.proyectointegradoraquelgutierrez.Score;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raquel on 08/06/2017.
 */

public class HighestScore {
    private String name;
    private int position, totalScore;
    private String date;

    public HighestScore(String name, int totalScore, String date) {
        this.name = name;
        this.position = position;
        this.totalScore = totalScore;
        this.date = date;
    }

    public HighestScore(JSONObject jsonObject) {
        //"score":500 , "user":"user", "date":"08/06/2017"
        try {
            totalScore = jsonObject.getInt("score");
            name = jsonObject.getString("user");
            date = jsonObject.getString("date");


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getUserName() {
        return name;
    }

    public void setUserName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
