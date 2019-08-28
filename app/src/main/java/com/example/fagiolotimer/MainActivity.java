package com.example.fagiolotimer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private TextView timer, status;
    private ImageView startButton, resetButton, alarmOff;
    private Button startBreak;
    private long time = 0, startFrom = 0;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private boolean running = false;
    private boolean defaultTime = true;
    private final long HALF_HOUR = 30*60*1000, FIVE_MINUTES = 5*60*1000;
    private long timerSet, timerSetPause;
    private String seconds, minutes, hours;
    private CountDownTimer ctimer;
    private long testt=5000;
    private Uri notification;
    private static String MYIMP = "preference";
    private SharedPreferences timeSet;
    private NotificationManager noti;
    private Intent background;
    private boolean pause = false;
    private Ringtone r;
    private AudioManager am;
    private int ringtonepref;
    private ProgressBar progCirc;
    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = (TextView) findViewById(R.id.timer);
        status = (TextView) findViewById(R.id.rest);
        alarmOff = (ImageView) findViewById(R.id.alarm_off);
        startButton = (ImageView) findViewById(R.id.start_button);
        resetButton = (ImageView) findViewById(R.id.reset_button);
        startBreak = (Button) findViewById(R.id.pausa_inizia);
        progCirc = (ProgressBar) findViewById(R.id.prog_circ);
        startBreak.setVisibility(View.INVISIBLE);
        alarmOff.setVisibility(View.INVISIBLE);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ringtonepref = am.getRingerMode();
        noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(notification == null)
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if(notification == null)
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        Bundle newTimerSet = getIntent().getExtras();
        timeSet = getSharedPreferences(MYIMP, Context.MODE_PRIVATE);
        if(newTimerSet != null){
            timerSet = newTimerSet.getLong("new_timer");
            timerSetPause = newTimerSet.getLong("new_timer_pause");
            SharedPreferences.Editor edit = timeSet.edit();
            edit.putLong("tempoImpostato", timerSet);
            edit.putLong("pausaImpostato", timerSetPause);
            edit.commit();

        }
        else if(defaultTime){
            timerSet = timeSet.getLong("tempoImpostato", 0);
            timerSetPause = timeSet.getLong("pausaImpostato", 0);
            if(timerSet == 0){
                timerSet = HALF_HOUR;
            }
            if (timerSetPause == 0) {
                timerSetPause = FIVE_MINUTES;
            }
        }
        timeString(timerSet);
        progCirc.setMax((int) timerSet);
        progCirc.setProgress((int) timerSet);
        background = new Intent(MainActivity.this, BackgroundService.class);
        startService(background);
    }

    public void startTimer(View view){
        alarmOff.setVisibility(View.INVISIBLE);
        startBreak.setVisibility(View.INVISIBLE);
        progress = (int) timerSet;
        progCirc.setMax((int) timerSet);
        if(!running) {
            if (time == 0) {
                ctimer = new CountDownTimer(timerSet, 1000){
                    public void onTick(long millisUntilFinished){
                        timeString(millisUntilFinished);
                        time = millisUntilFinished;
                        progCirc.setProgress((int) millisUntilFinished);
                    }

                    public void onFinish(){
                        timeString(timerSet);
                        startButton.setImageResource(R.drawable.pause_button_dark);
                        fine();
                        progCirc.setProgress(0);

                    }
                };
            }
            else{
                startFrom = time;
                ctimer = new CountDownTimer(startFrom, 1000){
                    public void onTick(long millisUntilFinished){
                        timeString(millisUntilFinished);
                        time = millisUntilFinished;
                        progCirc.setProgress((int) millisUntilFinished);
                    }

                    public void onFinish(){
                        timeString(timerSet);
                        startButton.setImageResource(R.drawable.pause_button_dark);
                        fine();
                        progCirc.setProgress(0);
                    }
                };
            }
            running = true;
            ctimer.start();
            startButton.setImageResource(R.drawable.pause_button_dark);
        }
        else{
            ctimer.cancel();
            setString(hours, minutes, seconds);
            running = false;
            startButton.setImageResource(R.drawable.start_button_dark);
        }
    }

    public void iniziaPausa(View view){
        startBreak.setVisibility(View.INVISIBLE);
        noti.cancel(NOTIFICATION_ID);
        pausetime();
    }

    public void alarmOff(View view){
        //mp.stop();
        r.stop();
        am.setRingerMode(ringtonepref);
        alarmOff.setVisibility(View.INVISIBLE);
    }

    private void pausetime(){
        am.setRingerMode(ringtonepref);
        progress = (int) timerSetPause;
        progCirc.setMax((int) timerSetPause);
        progCirc.setProgress((int) timerSetPause);
        if(!pause){
            startButton.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.VISIBLE);
            status.setText("Be productive!");
            timeString(timerSet);
        }
        else{
            startButton.setVisibility(View.INVISIBLE);
            resetButton.setVisibility(View.INVISIBLE);
            ctimer = new CountDownTimer(timerSetPause, 1000){
                public void onTick(long millisUntilFinished){
                    timeString(millisUntilFinished);
                    progress = (int) millisUntilFinished;
                    progCirc.setProgress(progress);
                }

                public void onFinish(){
                    timeString(timerSetPause);
                    fine();
                    progCirc.setProgress(0);
                }
            }.start();
            status.setText("Stand up and relax for a bit!");
        }
    }

    private void timeString(long value){
        seconds = Long.toString(TimeUnit.MILLISECONDS.toSeconds(value)%60);
        minutes = Long.toString(TimeUnit.MILLISECONDS.toMinutes(value)%60);
        hours = Long.toString(TimeUnit.MILLISECONDS.toHours(value));
        if(seconds.length()==1)
                seconds = "0" + seconds;
        if(minutes.length()==1)
            minutes = "0" + minutes;
        if(hours.length()==1)
            hours = "0" + hours;
        setString(hours, minutes, seconds);
    }

    private void setString(String hours, String minutes, String seconds){
        String msg = "";
        boolean space = false;
        if(!hours.equals("00")) {
            msg += hours;
            space = true;
        }
        if (space) {
            msg += ":";
        }
        msg += minutes+":"+seconds;
        timer.setText(msg);
        if(space){
            timer.setTextSize(35);
        }
        else{
            timer.setTextSize(50);
        }
    }

    public void resetTimer(View view){
        progCirc.setProgress((int) timerSet);
        if(running){
            ctimer.cancel();
            timeString(timerSet);
        }
        else {
            timeString(timerSet);
        }
        time = 0;
        running = false;
        startButton.setImageResource(R.drawable.start_button_dark);
    }

    public void fine(){
        alarmOff.setVisibility(View.VISIBLE);
        //mp.start();
        if(ringtonepref == AudioManager.RINGER_MODE_SILENT || ringtonepref == AudioManager.RINGER_MODE_VIBRATE)
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        r.play();
        startBreak.setVisibility(View.VISIBLE);
        ctimer.cancel();
        time = 0;
        running = false;
        if(!pause) {
            startBreak.setText("Take a break!");
            startButton.setImageResource(R.drawable.start_button_dark);
            deliverNotification(this);
            pause = true;
        }
        else{
            startBreak.setText("Go back to work!");
            deliverNotificationPausa(this);
            pause = false;
        }
    }

    private void deliverNotification(Context context){
        Intent notifica = new Intent(context, MainActivity.class);
        notifica.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifica.setAction(Intent.ACTION_MAIN);
        notifica.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, notifica, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Take a break")
                .setContentText("You should take a break!")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        noti.notify(NOTIFICATION_ID, builder.build());
    }

    private void deliverNotificationPausa(Context context){
        Intent notifica = new Intent(context, MainActivity.class);
        notifica.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifica.setAction(Intent.ACTION_MAIN);
        notifica.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, notifica, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Back to work")
                .setContentText("Go and be productive!")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        noti.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings){
            defaultTime = false;
            Intent settings = new Intent(this, Settings.class);
            settings.putExtra("time_set", timerSet);
            settings.putExtra("time_set_pause", timerSetPause);
            startActivity(settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
