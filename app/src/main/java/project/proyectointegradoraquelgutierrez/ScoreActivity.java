package project.proyectointegradoraquelgutierrez;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarScore);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().hide();


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
            chart.getAxisLeft().setDrawGridLines(true);
            chart.getAxisLeft().setDrawAxisLine(false);
            chart.getAxisLeft().setDrawLabels(false);
            chart.getXAxis().setEnabled(false);
            //chart.getAxisLeft().setEnabled(false);
            chart.getAxisRight().setEnabled(false);
            chart.setBorderColor(R.color.backgroundColorPrimary);
            LineDataSet dataSet = new LineDataSet(entries, "Score"); // add entries to dataset
            chart.setNoDataText(getResources().getString(R.string.no_data_text));

            dataSet.setDrawVerticalHighlightIndicator(false);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setDrawCircles(false);

            //dataSet.setValueTextColor(R.color.textHint);
            dataSet.setDrawValues(false);
            dataSet.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return Integer.toString((int) value);
                }
            });
            ((TextView) findViewById(R.id.scoreChart)).setText(String.valueOf(totalScore));
            chart.setDrawBorders(false);
            chart.getLegend().setEnabled(false);
            dataSet.setLabel(totalScore + "");
            dataSet.setFillColor(R.color.backgroundColorPrimary);
            //dataSet.setColor();
            //dataSet.setValueTextColor();
            Description descr = new Description();
            descr.setText("");
            chart.setDescription(descr);

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate();


            if (!Credentials.invitado) {
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
    }

    public void btPlayOnClick(View view) {
        startActivity(new Intent(ScoreActivity.this, ShakeActivity.class));
    }

    public void btHistoryOnClick(View view) {

    }

    public void btBackOnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setMessage(R.string.exit_confirm_body);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(ScoreActivity.this, MainActivity.class));
            }
        });
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
