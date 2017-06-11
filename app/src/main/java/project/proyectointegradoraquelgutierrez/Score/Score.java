package project.proyectointegradoraquelgutierrez.Score;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raquel on 07/06/2017.
 */
public class Score {
    private String name;
    private int position, totalScore;

    /**
     * Instancia un nuevo Score
     *
     * @param name       el nombre de usuario
     * @param position   la posicion en el ranking
     * @param totalScore la puntuacion total
     */
    public Score(String name, int position, int totalScore) {
        this.name = name;
        this.position = position;
        this.totalScore = totalScore;
    }

    /**
     * Instantiates a new Score.
     *
     * @param jsonObject un objeto JSON que contiene los datos del usuario, la puntuacion y su posicion
     */
    public Score(JSONObject jsonObject) {
        //"score":500 , "user":"user"
        try {
            totalScore = jsonObject.getInt("score");
            name = jsonObject.getString("user");
            position = jsonObject.getInt("rank");

            System.out.println("Constructor de Score: "
                    + name + " " + position + " " + totalScore);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Recoge el nombre de usuario.
     *
     * @return el nombre de usuario
     */
    public String getUserName() {
        return name;
    }

    /**
     * Pone el nombre de usuario.
     *
     * @param name el nombre de usuario
     */
    public void setUserName(String name) {
        this.name = name;
    }

    /**
     * Recoge la posición en el ranking del usuario.
     *
     * @return la posición en el ranking del usuario
     */
    public int getPosition() {
        return position;
    }

    /**
     * Pone la posición en el ranking del usuario.
     *
     * @param position la posicion en el ranking del usuario
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Recoge la puntuación total.
     *
     * @return la puntuacion total
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Pone la puntuación total,
     *
     * @param totalScore la puntuación total
     */
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}
