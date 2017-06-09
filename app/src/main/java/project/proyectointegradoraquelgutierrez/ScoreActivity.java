package project.proyectointegradoraquelgutierrez;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import project.proyectointegradoraquelgutierrez.Score.Score;
import project.proyectointegradoraquelgutierrez.Score.ScoreAdapter;

/**
 *
 */
public class ScoreActivity extends AppCompatActivity {
    LineChart chart;
    LineDataSet dataSet;
    Toolbar toolbar;
    ArrayList<Score> lastScores = new ArrayList<>();
    ArrayList<Score> highestScores = new ArrayList<>();
    ArrayList<Integer> myLastScores = new ArrayList<>();
    ArrayList<Integer> myLastScoreSaved = new ArrayList<>();
    JSONObject object;
    JSONArray json_array;
    private int totalScore;
    TextView tvTotalScore, tvTitle;
    private boolean isMenu, lastGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.toolbarScore);
        setSupportActionBar(toolbar);
        isMenu = getIntent().getExtras().getBoolean("menu");
        lastGame = !isMenu;


        if(!isMenu) {
            tvTitle = (TextView) findViewById(R.id.title_toolbar);
            tvTitle.setText(R.string.last_game);
            if (!Credentials.invitado)
                ((ImageButton) findViewById(R.id.bt_next)).setVisibility(View.VISIBLE);
        }

        if (checkConnection()) {
            new CallAPI() {
                @Override
                protected void onPostExecute(final String result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                object = new JSONObject(result);

                                json_array = object.getJSONArray("your_last_scores");
                                for (int i = 0; i < json_array.length(); i++) {
                                    myLastScores.add(json_array.getInt(i));
                                }

                                // GRAFICA

                                chart = (LineChart) findViewById(R.id.chart);
                                ArrayList<Integer> scores = new ArrayList<>();

                                // SI NO ES LA PRIMERA PANTALLA
                                if (!isMenu) {
                                    scores = getIntent().getIntegerArrayListExtra("scores");
                                    myLastScoreSaved = scores;
                                }else {
                                    // Si no hay 10 partidas jugadas lo rellena con ceros al principio.
                                    for(int i = 0; i < 10 - myLastScores.size(); i++)
                                        scores.add(0);
                                    for (int i = 0; i < myLastScores.size(); i++)
                                        scores.add(myLastScores.get(i));
                                }
                                totalScore = 0;
                                List<Entry> entries = new ArrayList<Entry>();

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


                                chart.setNoDataText(getResources().getString(R.string.no_data_text));
                                dataSet = new LineDataSet(entries, "Score"); // add entries to dataset

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
                                tvTotalScore = (TextView) findViewById(R.id.scoreChart);
                                tvTotalScore.setText(String.valueOf(totalScore));
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


                                // MUESTRA EL SCORE DE LAS 10 ULTIMAS PARTIDAS
                                json_array = object.getJSONArray("last_scores");

                                for (int i = 0; i < json_array.length(); i++) {
                                    lastScores.add(new Score(json_array.getJSONObject(i)));
                                }

                                if (!lastScores.isEmpty()) {
                                    ListView listView =(ListView) findViewById(R.id.list_view);
                                    ScoreAdapter adapter = new ScoreAdapter(lastScores);
                                    listView.setAdapter(adapter);

                                } else {
                                    //Toast.makeText(this, getResources().getString(R.string.no_conex), Toast.LENGTH_SHORT).show();
                                    ((ImageView) findViewById(R.id.iv_no_conex)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.tv_no_conex)).setVisibility(View.VISIBLE);
                                }

                                chart.invalidate();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }.execute(
                Uri.parse("http://www.iesmurgi.org:85/raquel2017/ranking.php")
                    .buildUpon()
                    .appendQueryParameter("user", Credentials.user)
                    .toString()
            );
        }

        // SUBE El RESULTADO
        if (!Credentials.invitado || !isMenu) {
            if (checkConnection()) {
                new CallAPI() {
                    @Override
                    protected void onPostExecute(final String result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!result.equals("ok"))
                                    if (!Credentials.invitado)
                                        Toast.makeText(ScoreActivity.this, getResources().getString(R.string.cant_upload), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }.execute(
                        Uri.parse("http://www.iesmurgi.org:85/raquel2017/upload_result.php")
                                .buildUpon()
                                .appendQueryParameter("user", Credentials.user)
                                .appendQueryParameter("pass", Credentials.password)
                                .appendQueryParameter("score", Integer.toString(totalScore))
                                .build()
                                .toString()
                );

            } else {
                Toast.makeText(this, getResources().getString(R.string.no_conex), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void btPlayOnClick(View view) {
        if (checkConnection()) {
            startActivity(new Intent(ScoreActivity.this, ShakeActivity.class));
        } else {
            if (!Credentials.invitado) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
                builder.setTitle(R.string.continue_without_conex);
                builder.setMessage(R.string.no_conex_description);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ScoreActivity.this, ShakeActivity.class));
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
    }

    public void btHistoryOnClick(View view) {
        final Dialog dialog = new Dialog(ScoreActivity.this);
        dialog.setContentView(R.layout.list_view_history);
        dialog.setTitle(getResources().getString(R.string.highest_scores));
        if (json_array != null) {
            if(highestScores.size() == 0) {
                try {
                    json_array = object.getJSONArray("highest_scores");

                    for (int i = 0; i < json_array.length(); i++) {
                        highestScores.add(new Score(json_array.getJSONObject(i)));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else return;

        ListView listView = (ListView) dialog.findViewById(R.id.list_view_history);
        if (highestScores.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.no_games_yet), Toast.LENGTH_SHORT).show();
            return;
        }
        ScoreAdapter adapter = new ScoreAdapter(highestScores);
        listView.setAdapter(adapter);
        dialog.show();
        //Toast.makeText(this, getResources().getString(R.string.no_conex), Toast.LENGTH_SHORT).show();

    }

    public void btBackOnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setMessage(R.string.exit_confirm_body);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Credentials.password = null;
                Credentials.user = null;
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

    public boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void changeChartValues() {
        totalScore = 0;
        List<Entry> entries = new ArrayList<Entry>();
        if (lastGame) {
            for (int i = 0; i < myLastScoreSaved.size(); i++) {
                entries.add(new Entry(i, myLastScoreSaved.get(i)));
                totalScore += myLastScoreSaved.get(i);
            }
            dataSet.setValues(entries);
            tvTitle.setText(R.string.last_game);
            lastGame = false;
        } else {
            if (checkConnection()) {
                new CallAPI() {
                    @Override
                    protected void onPostExecute(final String result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    myLastScores.clear();

                                    object = new JSONObject(result);

                                    json_array = object.getJSONArray("your_last_scores");
                                    for (int i = 0; i < json_array.length(); i++) {
                                        myLastScores.add(json_array.getInt(i));
                                    }

                                    // GRAFICA

                                    chart = (LineChart) findViewById(R.id.chart);
                                    ArrayList<Integer> scores = new ArrayList<>();

                                    // SI NO ES LA PRIMERA PANTALLA


                                    // Si no hay 10 partidas jugadas lo rellena con ceros al principio.
                                    for (int i = 0; i < 10 - myLastScores.size(); i++)
                                        scores.add(0);
                                    for (int i = 0; i < myLastScores.size(); i++)
                                        scores.add(myLastScores.get(i));

                                    totalScore = 0;
                                    List<Entry> entries = new ArrayList<Entry>();

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


                                    chart.setNoDataText(getResources().getString(R.string.no_data_text));
                                    dataSet = new LineDataSet(entries, "Score"); // add entries to dataset

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
                                    tvTotalScore = (TextView) findViewById(R.id.scoreChart);
                                    tvTotalScore.setText(String.valueOf(totalScore));
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


                                    // MUESTRA EL SCORE DE LAS 10 ULTIMAS PARTIDAS
                                    json_array = object.getJSONArray("last_scores");

                                    for (int i = 0; i < json_array.length(); i++) {
                                        lastScores.add(new Score(json_array.getJSONObject(i)));
                                    }

                                    if (!lastScores.isEmpty()) {
                                        ListView listView = (ListView) findViewById(R.id.list_view);
                                        ScoreAdapter adapter = new ScoreAdapter(lastScores);
                                        listView.setAdapter(adapter);

                                    } else {
                                        //Toast.makeText(this, getResources().getString(R.string.no_conex), Toast.LENGTH_SHORT).show();
                                        ((ImageView) findViewById(R.id.iv_no_conex)).setVisibility(View.VISIBLE);
                                        ((TextView) findViewById(R.id.tv_no_conex)).setVisibility(View.VISIBLE);
                                    }

                                    chart.invalidate();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }.execute(
                        Uri.parse("http://www.iesmurgi.org:85/raquel2017/ranking.php")
                                .buildUpon()
                                .appendQueryParameter("user", Credentials.user)
                                .toString()
                );

                tvTitle.setText(R.string.last_scores);
                lastGame = true;
            }
        }
        tvTotalScore.setText(String.valueOf(totalScore));
        chart.invalidate();
    }

    public void btNextOnClick(View view) {
        changeChartValues();
    }
}
