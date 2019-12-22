package com.example.magisterka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Chronometer;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.StrictMath.abs;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView xText, yText, zText;
    private Sensor mySensor;
    private SensorManager SM;
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    private float sum;
    private FirebaseDatabase database;
    private float x, y, z, prevx, prevy, prevz;
    private TextView labelTxt;
    private String label;
    private Button changeLabel;
    private Intent changeLabelIntent;
    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLabel();
        changeLabel = findViewById(R.id.changeLabel);
        prevx=prevy=prevz=0;
        sum=0;
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        DatabaseReference ref = database.getReference("phone_acc");


        myRef.setValue("Hello, World11!");
        changeLabelIntent = new Intent(this, LabelListActivity.class);

        xText = findViewById(R.id.dataX);
        yText = findViewById(R.id.dataY);
        zText = findViewById(R.id.dataZ);

        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 2000) {
                    //chronometer.setBase(SystemClock.elapsedRealtime());
                    Toast.makeText(MainActivity.this, "Send!", Toast.LENGTH_SHORT).show();
                    sendValue(sum);
                    sum=0;
                    prevx=prevy=prevz=0;
                }
            }
        });

        changeLabel.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChangeLabelActivity();
            }
        });
    }

    private void setLabel() {
        labelTxt = findViewById(R.id.label);
        myDb = new DBHelper(this);;
        Cursor res = myDb.getAllData();
        while(res.moveToNext()){
            if(res.getInt(2)>0) {
                labelTxt.setText(res.getString(0));
                break;
            }
        }
    }

    private void goToChangeLabelActivity() {
        startActivity(changeLabelIntent);
    }

    private void sendValue(float sum) {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        label = labelTxt.getText().toString();
        DatabaseReference today_ref = database.getReference("phone_acc/"+label).push();
        Acc_value acc_value = new Acc_value(currentTime, currentDate, sum);
        today_ref.setValue(acc_value);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xText.setText("X: " + event.values[0]);
        yText.setText("Y: " + event.values[1]);
        zText.setText("Z: " + event.values[2]);
        sum = sum_algorythm(event, sum);
    }

    private float sum_algorythm(SensorEvent event, float sum) {
        x=event.values[0];
        y= event.values[1];
        z= event.values[2];
        sum = sum + abs(prevx - x) + abs(prevy - y) + abs(prevz - z);
        return sum;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void startChronometer(View v) {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
            startService(new Intent(this, MyService.class));
        }
    }

    public void pauseChronometer(View v) {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
            stopService(new Intent(this, MyService.class));
        }
    }

    public void stopChronometer(View v) {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        stopService(new Intent(this, MyService.class));
    }
}
