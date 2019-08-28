package com.example.fagiolotimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import java.util.concurrent.TimeUnit;

public class Settings extends AppCompatActivity {
    private NumberPicker hours, minutes, seconds, hours_pause, minutes_pause, seconds_pause;
    private Button apply;
    private String seconds_l, minutes_l, hours_l;
    private long new_hours, new_minutes, new_seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setHomeButtonEnabled(true);
        hours = (NumberPicker) findViewById(R.id.hours_timer);
        minutes = (NumberPicker) findViewById(R.id.minutes_timer);
        seconds = (NumberPicker) findViewById(R.id.seconds_timer);
        hours_pause = (NumberPicker) findViewById(R.id.hours_timer_pause);
        minutes_pause = (NumberPicker) findViewById(R.id.minutes_timer_pause);
        seconds_pause = (NumberPicker) findViewById(R.id.seconds_timer_pause);
        apply = (Button) findViewById(R.id.apply_changes);
        Bundle value = getIntent().getExtras();
        hours.setMinValue(00);
        hours.setMaxValue(99);
        hours_pause.setMinValue(00);
        hours_pause.setMaxValue(99);
        minutes.setMinValue(00);
        minutes.setMaxValue(60);
        minutes_pause.setMinValue(00);
        minutes_pause.setMaxValue(60);
        seconds.setMinValue(00);
        seconds.setMaxValue(60);
        seconds_pause.setMinValue(00);
        seconds_pause.setMaxValue(60);
        if(value != null){
            Long time = value.getLong("time_set");
            setActualTime(time, hours, minutes, seconds);
            Long time_pause = value.getLong("time_set_pause");
            setActualTime(time_pause, hours_pause, minutes_pause, seconds_pause);
        }
        else{
            hours.setValue(00);
            minutes.setValue(00);
            seconds.setValue(00);
            hours_pause.setValue(00);
            minutes_pause.setValue(00);
            seconds_pause.setValue(00);
        }
    }

    public void applyChanges(View view){
        new_hours = (long) hours.getValue();
        new_minutes = (long) minutes.getValue();
        new_seconds = (long) seconds.getValue();
        new_hours = new_hours*60*60*1000;
        new_minutes = new_minutes*60*1000;
        new_seconds = new_seconds*1000;
        Long new_time = new_hours + new_minutes + new_seconds;
        Intent backtomain = new Intent(this, MainActivity.class);
        backtomain.putExtra("new_timer", new_time);
        new_hours = (long) hours_pause.getValue();
        new_minutes = (long) minutes_pause.getValue();
        new_seconds = (long) seconds_pause.getValue();
        new_hours = new_hours*60*60*1000;
        new_minutes = new_minutes*60*1000;
        new_seconds = new_seconds*1000;
        Long new_time_pause = new_hours + new_minutes + new_seconds;
        backtomain.putExtra("new_timer_pause", new_time_pause);
        startActivity(backtomain);
    }

    private void setActualTime(Long value, NumberPicker hours_t, NumberPicker minutes_t, NumberPicker seconds_t){
        seconds_l = Long.toString(TimeUnit.MILLISECONDS.toSeconds(value)%60);
        minutes_l = Long.toString(TimeUnit.MILLISECONDS.toMinutes(value)%60);
        hours_l = Long.toString(TimeUnit.MILLISECONDS.toHours(value));
        if(seconds_l.length()==1)
            seconds_l = "0" + seconds_l;
        if(minutes_l.length()==1)
            minutes_l = "0" + minutes_l;
        if(hours_l.length()==1)
            hours_l = "0" + hours_l;
        int sec = Integer.parseInt(seconds_l);
        int min = Integer.parseInt(minutes_l);
        int hou = Integer.parseInt(hours_l);
        hours_t.setValue(hou);
        minutes_t.setValue(min);
        seconds_t.setValue(sec);
    }
}
