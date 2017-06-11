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
 * Clase que muestra una gráfica y listas de las puntuaciones de los usuarios.
 */
public class ScoreActivity extends AppCompatActivity {
    /**
     * La gráfica.
     */
    LineChart chart;
    LineDataSet dataSet;
    Toolbar toolbar;
    /**
     * Guarda las mejores puntuaciones del día.
     */
    ArrayList<Score> bestOfDay = new ArrayList<>();
    /**
     * Guarda las mejores puntuaciones de la historia.
     */
    ArrayList<Score> highestScores = new ArrayList<>();
    /**
     * Guarda la puntuación de la última partida.
     */
    ArrayList<Integer> myLastGame = new ArrayList<>();
    JSONObject object;
    JSONArray json_array;
    private int totalScore, lastGameTotalScore;
    TextView tvTotalScore, tvTitle;
    private boolean isMenu, lastGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.toolbarScore);
        setSupportActionBar(toolbar);
        lastGame = !isMenu;
        isMenu = getIntent().getExtras().getBoolean("menu");
        chart = (LineChart) findViewById(R.id.chart);


        if(!isMenu) {
            tvTitle = (TextView) findViewById(R.id.title_toolbar);
            tvTitle.setText(R.string.last_game);
            myLastGame = getIntent().getIntegerArrayListExtra("scores");
            if (!Credentials.invitado)
                ((ImageButton) findViewById(R.id.bt_next)).setVisibility(View.VISIBLE);
        }

        // SUBE El RESULTADO
        for (int i = 0; i < myLastGame.size(); i++) {
            lastGameTotalScore += myLastGame.get(i);
        }
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
                                .appendQueryParameter("score", Integer.toString(lastGameTotalScore))
                                .build()
                                .toString()
                );

            } else {
                Toast.makeText(this, getResources().getString(R.string.no_conex), Toast.LENGTH_SHORT).show();
            }
        }

        // CARGA DATOS
        final ListView listView = (ListView) findViewById(R.id.list_view);
        if (checkConnection()) {
            new CallAPI() {
                @Override
                protected void onPostExecute(final String result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                object = new JSONObject(result);
                                if (!isMenu)
                                    drawChart(object, true);
                                else
                                    drawChart(object, false);

                            // MUESTRA EL SCORE DE LAS 10 ULTIMAS PARTIDAS
                                json_array = object.getJSONArray("last_scores");

                                for (int i = 0; i < json_array.length(); i++) {
                                    bestOfDay.add(new Score(json_array.getJSONObject(i)));
                                }

                                if (!bestOfDay.isEmpty()) {
                                    ScoreAdapter adapter = new ScoreAdapter(bestOfDay);
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
        } else {
            //Toast.makeText(this, getResources().getString(R.string.no_conex), Toast.LENGTH_SHORT).show();
            drawChart(object, true);
            ((ImageView) findViewById(R.id.iv_no_conex)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_no_conex)).setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    /**
     * Acción al pulsar el Botón de Play (Jugar).
     * Abre la activity para jugar
     *
     * @param view la vista
     */
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
                        finish();
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

    /**
     * Acción al pulsar el Botón de Historia.
     * Muestra las mejores puntuaciones de la historia.
     *
     * @param view la vista
     */
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

    /**
     * Acción al pulsar el Botón de cerrar en el toolbar.
     * Cierra la sesión del usuario.
     *
     * @param view la vista
     */
    public void btBackOnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setMessage(R.string.exit_confirm_body);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Credentials.password = null;
                Credentials.user = null;
                startActivity(new Intent(ScoreActivity.this, MainActivity.class));
                finish();
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

    /**
     * Comprueba si hay conexión a Internet.
     *
     * @return true si hay Internet o false si no hay
     */
    public boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void changeChartValues() {
        totalScore = 0;
        if (lastGame) {
            drawChart(object, false);
            tvTitle.setText(R.string.last_scores);
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
                                    drawChart(new JSONObject(result), true);
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

                tvTitle.setText(R.string.last_game);
                lastGame = true;
            }
        }
        tvTotalScore.setText(String.valueOf(totalScore));
        chart.invalidate();
    }

    /**
     * Acción al pulsar el Botón de next (siguiente).
     * Cambia la gráfica de última partida a últimas 10 partidas y viceversa.
     *
     * @param view la vista
     */
    public void btNextOnClick(View view) {
        changeChartValues();
    }

    /**
     * Dibuja una gráfica.
     *
     * @param object objeto JSON
     * @param lastGame true si se ha jugado una partida anteriormente o false si no.
     */
    private void drawChart(JSONObject object, boolean lastGame) {
        try {

            // GRAFICA
            ArrayList<Integer> scores = new ArrayList<>();

            // SI NO ES LA PRIMERA PANTALLA
            if (lastGame) {
                scores = myLastGame;
            }else {
                json_array = object.getJSONArray("your_last_scores");
                ArrayList<Integer> myLastScores = new ArrayList<Integer>();
                for (int i = 0; i < json_array.length(); i++) {
                    myLastScores.add(json_array.getInt(i));
                }

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

            chart.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
