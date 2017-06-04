package project.proyectointegradoraquelgutierrez;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ShakeActivity extends AppCompatActivity implements SensorEventListener{
    private long last_update = 0;
    private long startTime;
    private int average = 0, valueCount = 0;
    private float lastValueX = 0, lastValueY = 0, lastValueZ = 0;
    private float currentValueX = 0, currentValueY = 0, currentValueZ = 0;
    private ArrayList<Integer> evolution = new ArrayList<>();
    private static final short startR=226, startG=244, startB=199, endR=255, endG=78, endB=80;
    private long lastColorChangeTime = 0;
    private int maxSinceLastChange = 0;

    private static int weightedAverage(double t) {
        if(t > 1) t = 1;
        if(t < 0) t = 0;
        return Color.rgb(
                (int) (startR*(1-t) + endR*t),
                (int) (startG*(1-t) + endG*t),
                (int) (startB*(1-t) + endB*t)
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ShakeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(evolution.size() < 10) evolution.add(valueCount == 0 ? 0 : average/valueCount);
                        Intent i = new Intent(ShakeActivity.this, ScoreActivity.class);
                        i.putIntegerArrayListExtra("scores", evolution);
                        startActivity(i);
                    }
                });
            }
        }, 10000);

        startTime = System.currentTimeMillis();
        lastColorChangeTime = startTime;
    }

    // Método que hace medias
    private int updateScore(float currentHour) {
        float time_difference = currentHour - last_update;
        if (time_difference > 0) {
            int move = (int) (Math.sqrt((currentValueX-lastValueX) * (currentValueX-lastValueX) + (currentValueY-lastValueY) * (currentValueY-lastValueY) + (currentValueZ-lastValueZ) * (currentValueZ-lastValueZ))*10);

            lastValueX = currentValueX;
            lastValueY = currentValueY;
            lastValueZ = currentValueZ;

            long currentTime = System.currentTimeMillis();

            // update background color
            maxSinceLastChange = Math.max(maxSinceLastChange, move);
            if((currentTime-lastColorChangeTime) > 250) {
                findViewById(R.id.relative_layout).setBackgroundColor(weightedAverage(maxSinceLastChange/400.0));
                lastColorChangeTime = currentTime;
                maxSinceLastChange = 0;
            }


            long secondsElapsed = (currentTime-startTime)/1000;
            if(secondsElapsed == evolution.size()) {
                average += move;
                valueCount++;
            } else {
                evolution.add(valueCount == 0 ? 0 : average/valueCount);
                valueCount = 1;
                average = move;
                ((TextView) findViewById(R.id.countdown)).setText(String.valueOf(10 - secondsElapsed));
            }

            return move;
        }
        return 0;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        synchronized (this) {
            long currentHour = sensorEvent.timestamp;
            currentValueX = sensorEvent.values[0];
            currentValueY = sensorEvent.values[1];
            currentValueZ = sensorEvent.values[2];

            if (lastValueX == 0 && lastValueY == 0 && lastValueZ == 0) {
                last_update = currentHour;

                lastValueX = currentValueX;
                lastValueY = currentValueY;
                lastValueZ = currentValueZ;
            }
            updateScore(currentHour);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onStop() {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
