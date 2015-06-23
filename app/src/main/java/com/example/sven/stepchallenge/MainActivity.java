package com.example.sven.stepchallenge;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    //UI
    private TextView stepsTodayValue;
    private TextView dailyGoalValue;
    private TextView distanceRunValue;
    private Button resetButton;
    private ProgressBar progressBar;

    //Notification
    NotificationCompat.Builder notification;
    private static final int uniqueID = 45612;

    //Sensorvariables
    private SensorManager sensorManager;
    private float currentvectorSum;
    private int numSteps;
    boolean inStep;

    //Preferences
    int goalPref;
    float lengthPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tryGetPref();
        getUI();

        //Setup sensor variables
        numSteps = 0;
        currentvectorSum = 0;

        //notification setup
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        enableAccelerometerListening();
    }

    public void getUI(){
        stepsTodayValue = (TextView) findViewById(R.id.stepsTodayValue);
        dailyGoalValue = (TextView) findViewById(R.id.dailyGoalValue);
        resetButton = (Button) findViewById(R.id.resetButton);
        distanceRunValue = (TextView) findViewById(R.id.distanceRunValue);
        distanceRunValue.setText(String.valueOf(lengthPref * numSteps) + " m");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(goalPref);
        progressBar.setProgress(numSteps);
    }

    private void enableAccelerometerListening(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }


    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            currentvectorSum = (x*x + y*y + z*z);
            if(currentvectorSum < 100 && inStep==false){
                inStep = true;
            }
            if(currentvectorSum > 125 && inStep==true){
                inStep = false;
                numSteps++;
                stepNumberChanged();
                if(numSteps == goalPref){
                    goalReachedNotify();
                }

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void stepNumberChanged(){
        progressBar.setProgress(numSteps);
        dailyGoalValue.setText(numSteps + " of " + String.valueOf(goalPref));
        stepsTodayValue.setText(String.valueOf(numSteps));
        distanceRunValue.setText(String.valueOf(Math.round(lengthPref * numSteps*100)/100.0) + " m");
    }

    public void goalReachedNotify(){
        //Build the notification
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setTicker("This is the ticker");
        notification.setDefaults(Notification.DEFAULT_VIBRATE);
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Congrats! Daily goal reached!");
        notification.setContentText("You successfully achieved your goal of " + goalPref + " steps!");

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());
    }

    public void tryGetPref(){
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        try{
            goalPref = Integer.parseInt(sharedPref.getString("goal", ""));
            lengthPref = Float.parseFloat(sharedPref.getString("length", ""));
        }catch(Exception e){}
    }


    @Override
    protected void onResume() {
        tryGetPref();
        stepNumberChanged();
        progressBar.setMax(goalPref);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    public void resetSteps(View v){
        progressBar.setProgress(0);
        dailyGoalValue.setText("0 of " + goalPref);
        distanceRunValue.setText("0 m");
        numSteps = 0;
        stepsTodayValue.setText(String.valueOf(numSteps));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settings = new Intent(this, Settings.class);
                startActivity(settings);
                return true;
            case R.id.menu_help:
                Intent help = new Intent(this, Help.class);
                startActivity(help);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
