package project.proyectointegradoraquelgutierrez;

/**
 * Created by Raquel on 07/06/2017.
 */

public class UserScore {
    private String name;
    private int position, totalScore;

    public UserScore(String name, int position, int totalScore) {
        this.name = name;
        this.position = position;
        this.totalScore = totalScore;
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
