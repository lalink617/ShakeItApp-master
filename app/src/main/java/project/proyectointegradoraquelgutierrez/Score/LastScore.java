package project.proyectointegradoraquelgutierrez.Score;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raquel on 07/06/2017.
 */

public class LastScore {
    private String name;
    private int position, totalScore;

    public LastScore(String name, int position, int totalScore) {
        this.name = name;
        this.position = position;
        this.totalScore = totalScore;
    }

    public LastScore(JSONObject jsonObject) {
        //"score":500 , "user":"user"
        try {
            totalScore = jsonObject.getInt("score");
            name = jsonObject.getString("user");

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
}
