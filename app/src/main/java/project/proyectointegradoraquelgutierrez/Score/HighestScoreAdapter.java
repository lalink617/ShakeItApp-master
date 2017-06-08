package project.proyectointegradoraquelgutierrez.Score;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import project.proyectointegradoraquelgutierrez.R;

public class HighestScoreAdapter extends BaseAdapter {
    private ArrayList<HighestScore> users;

    public HighestScoreAdapter(ArrayList<HighestScore> userScores) {
        users = userScores;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public HighestScore getItem(int i) {
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
        TextView date = (TextView) listItemView.findViewById(R.id.tvUserScore);

        name.setText(users.get(i).getUserName());
        date.setText(users.get(i).getDate());
        position.setText(String.valueOf(users.get(i).getPosition()));
        score.setText(String.valueOf(users.get(i).getTotalScore()));

        return listItemView;

    }
}
