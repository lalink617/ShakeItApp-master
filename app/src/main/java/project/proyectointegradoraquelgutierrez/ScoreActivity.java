package project.proyectointegradoraquelgutierrez;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

import project.proyectointegradoraquelgutierrez.Score.ScoreAdapter;
import project.proyectointegradoraquelgutierrez.Score.UserScore;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);


        LineChart chart = (LineChart) findViewById(R.id.chart);
        ListView listView = (ListView) findViewById(R.id.list_view);
        ArrayList<UserScore> userScores = new ArrayList<>();
        userScores.add(new UserScore("Raquel", 1, 600));
        userScores.add(new UserScore("Daniel", 2, 500));
        userScores.add(new UserScore("María", 3, 450));
        userScores.add(new UserScore("María", 4, 430));
        userScores.add(new UserScore("María", 5, 410));
        ScoreAdapter adapter = new ScoreAdapter(userScores);
        listView.setAdapter(adapter);

        if (!getIntent().getExtras().getBoolean("menu")) {

            List<Entry> entries = new ArrayList<Entry>();
            ArrayList<Integer> scores = getIntent().getIntegerArrayListExtra("scores");

            int totalScore = 0;

            for (int i = 0; i < scores.size(); i++) {
                entries.add(new Entry(i, scores.get(i)));
                totalScore += scores.get(i);
            }

            LineDataSet dataSet = new LineDataSet(entries, "Score"); // add entries to dataset
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(true);
            dataSet.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return Integer.toString((int) value);
                }
            });
            //dataSet.setColor();
            //dataSet.setValueTextColor();

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate();



            new CallAPI() {
                @Override
                protected void onPostExecute(final String result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!result.equals("ok")) {
                                Toast.makeText(ScoreActivity.this, getResources().getString(R.string.bad_login), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }.execute(
                    Uri.parse("http://192.168.3.44/check_login.php")
                            .buildUpon()
                            .appendQueryParameter("user", Credentials.user)
                            .appendQueryParameter("pass", Credentials.password)
                            .appendQueryParameter("score", Integer.toString(totalScore))
                            .build()
                            .toString()
            );
        }

    }

    public void btPlayOnClick(View view) {
        startActivity(new Intent(ScoreActivity.this, ShakeActivity.class));
    }

    public void btHistoryOnClick(View view) {

    }
}
