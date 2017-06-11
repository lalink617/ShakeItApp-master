package project.proyectointegradoraquelgutierrez.Score;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import project.proyectointegradoraquelgutierrez.Credentials;
import project.proyectointegradoraquelgutierrez.R;

/**
 * Infla las listas de los ranking con el ArrayList pasado por cabecera
 */
public class ScoreAdapter extends BaseAdapter {
    private ArrayList<Score> users;

    /**
     * Copia las scores pasadas por cabecera a una variable global
     *
     * @param userScores Los usuarios con sus posiciones y puntuaciones
     */
    public ScoreAdapter(ArrayList<Score> userScores) {
        users = userScores;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Score getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View listItemView = view;
        if (listItemView == null) {
            Context cnt = viewGroup.getContext();
            listItemView = LayoutInflater.from(cnt).inflate(
                    R.layout.list_item, viewGroup, false);
        }

        TextView position = (TextView) listItemView.findViewById(R.id.tvPosition);
        TextView name = (TextView) listItemView.findViewById(R.id.tvName);
        TextView score = (TextView) listItemView.findViewById(R.id.tvScore);

        if (users.get(i).getUserName().equals(Credentials.user)) {
            name.setTextColor(ContextCompat.getColor(viewGroup.getContext(), R.color.colorAccent));
            score.setTextColor(ContextCompat.getColor(viewGroup.getContext(), R.color.colorAccent));
        } else {
            name.setTextColor(ContextCompat.getColor(viewGroup.getContext(), R.color.textColorPrimary));
            score.setTextColor(ContextCompat.getColor(viewGroup.getContext(), R.color.textColorPrimary));
        }


        name.setText(users.get(i).getUserName());
        position.setText(String.valueOf(users.get(i).getPosition()));
        score.setText(String.valueOf(users.get(i).getTotalScore()));

        return listItemView;

    }
}
