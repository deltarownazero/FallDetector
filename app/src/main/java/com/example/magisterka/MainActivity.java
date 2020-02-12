package com.example.magisterka;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
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
    private LocationManager locationManager;
    private LocationListener listener;
    private TextView locationText;
    private TextView distanceText;
    private TextView speedText;
    private Button b;
    double distance = 0;
    double speed = 0;
    private double prevLongitude = 0, prevLatitude = 0, longitude, latitude;
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    int numberOfSend = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLabel();
        changeLabel = findViewById(R.id.changeLabel);
        prevx = prevy = prevz = 0;
        sum = 0;
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        DatabaseReference ref = database.getReference("phone_acc");


        myRef.setValue("Hello, World11!");
        changeLabelIntent = new Intent(this, LabelListActivity.class);

        xText = findViewById(R.id.dataX);
        yText = findViewById(R.id.dataY);
        zText = findViewById(R.id.dataZ);

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        chronometerConfigure();


        changeLabel.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChangeLabelActivity();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        findLastLocation();
        locationService();
    }

    @SuppressLint("MissingPermission")
    private void locationService() {

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 90, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListener);
    }


    private void findLastLocation() {
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        String provider = locationManager.getBestProvider(criteria, false);
        if (checkSelfPermission(permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("error with permission");
            return;
        }else{
            ActivityCompat.requestPermissions(this, new String[] {
                    permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION
            }, 0);

        }
        Location location = locationManager.getLastKnownLocation(provider);
        setLocation(location);

    }

    private void setLocation(Location location) {
        if(prevLatitude == 0){                                  //pierwszy raz
            prevLatitude = location.getLatitude();
            prevLongitude = location.getLongitude();
        }
        else{
            prevLatitude = latitude;
            prevLongitude = longitude;
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        locationText = findViewById(R.id.location);
        locationText.setText(String.valueOf((double) Math.round(location.getLongitude()*100)/100)+" "+String.valueOf((double) Math.round(location.getLatitude()*100)/100));
        distanceText = findViewById(R.id.distance);
        distanceText.setText(String.valueOf(getDistance())+" km");
        speedText = findViewById(R.id.speed);
        speedText.setText(String.valueOf((double) Math.round(speed*100)/100)+" m/s");
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

    private void chronometerConfigure() {
        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 2000) {
                    //chronometer.setBase(SystemClock.elapsedRealtime());
                    Toast.makeText(MainActivity.this, "Send!", Toast.LENGTH_SHORT).show();
                    sendValue(sum, getSpeed());
                    sum = 0;
                    prevx = prevy = prevz = 0;
                    findLastLocation();
                }
            }
        });
    }

    private double getSpeed() {

            double prevDistance = distance;
            distance = getDistance();
            speed = ((distance - prevDistance)*1000) / 2000;

        return speed;
    }

    private double getDistance() {

        if(prevLongitude != 0){     //Haversine formula.
            double latDistance = Math.toRadians(prevLatitude - latitude);
            double lngDistance = Math.toRadians(prevLongitude - longitude);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(prevLatitude)) * Math.cos(Math.toRadians(latitude))
                    * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            distance = distance + (double) Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c*100)  / 100;
        }
        return distance;
    }


    private void goToChangeLabelActivity() {
        startActivity(changeLabelIntent);
    }

    private void sendValue(float sum, double distance) {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        label = labelTxt.getText().toString();
        DatabaseReference today_ref = database.getReference("phone_acc/"+label).push();
        Acc_value acc_value = new Acc_value(currentTime, currentDate, sum, speed);
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
