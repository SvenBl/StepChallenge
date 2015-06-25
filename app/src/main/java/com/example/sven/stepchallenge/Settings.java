package com.example.sven.stepchallenge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Settings extends ActionBarActivity {

    EditText dailyGoalField;
    EditText stepLengthField;
    String goalPref;
    String lengthPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dailyGoalField = (EditText) findViewById(R.id.dailyGoalField);
        stepLengthField = (EditText) findViewById(R.id.stepLengthField);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        try{
            goalPref = sharedPref.getString("goal", "");
            lengthPref = sharedPref.getString("length", "");
            stepLengthField.setText(lengthPref);
            dailyGoalField.setText(goalPref);
        }catch(Exception e){}

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    public void setPreferences(View view){

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("goal", dailyGoalField.getText().toString());
        editor.putString("length", stepLengthField.getText().toString());
        editor.apply();

        Toast.makeText(this, "Preferences have been saved", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.menu_help:
                Intent help = new Intent(this, Help.class);
                startActivity(help);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}