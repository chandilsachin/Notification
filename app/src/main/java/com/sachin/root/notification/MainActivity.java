package com.sachin.root.notification;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;

import static com.sachin.root.notification.FileDownloader.ProgressListener;


public class MainActivity extends ActionBarActivity {

    NotificationManager notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.btnFireNotification);

        //notification.setContentIntent(new Intent(this,InterstitialActivity.class));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notification = new NotificationManager(getApplicationContext(),"Test","This is a text notification",true,R.drawable.ic_launcher);
                notification.setContentOnCompletion("Completed");
                notification.fireProgressNotification();
            }
        });

        Button dismiss = (Button) findViewById(R.id.btnDismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification.dismiss();
            }
        });

        Button update = (Button) findViewById(R.id.btnUpdateProgress);
        update.setOnClickListener(new View.OnClickListener() {
            int progress = 0;
            @Override
            public void onClick(View v) {
                notification.updateProgress(progress+=5);
            }
        });

        final FileDownloader downloader = new FileDownloader(this,"https://androidnetworktester.googlecode.com/files/10mb.txt", Environment.getExternalStorageDirectory()+"/downloadTest.jpg");


        Button download = (Button) findViewById(R.id.btnStartDownload);
        download.setOnClickListener(new View.OnClickListener() {
            int progress = 0;
            @Override
            public void onClick(View v) {
                notification = new NotificationManager(getApplicationContext(),"Test","This is a text notification",true,R.drawable.ic_launcher);
                notification.setContentOnCompletion("Completed");
                downloader.start(listener);
            }
        });

        Button pause = (Button) findViewById(R.id.btnPauseDownload);
        pause.setOnClickListener(new View.OnClickListener() {
            int progress = 0;
            @Override
            public void onClick(View v) {
                downloader.pause();
            }
        });

        Button resume = (Button) findViewById(R.id.btnResumeDownload);
        resume.setOnClickListener(new View.OnClickListener() {
            int progress = 0;
            @Override
            public void onClick(View v) {
                downloader.resume(listener);
            }
        });
    }

    ProgressDialog dialog;

    public void showProgressBar()
    {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Downloading...");
        dialog.setMessage("Please wait...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.show();
    }

    public void updateProgress(int progress)
    {
        dialog.setProgress(progress);
    }

    public void hideProgressBar()
    {
        dialog.dismiss();
    }

    long totalSize;
    ProgressListener listener = new ProgressListener() {

        @Override
        public void onProgressStart() {
            showProgressBar();
        }

        @Override
        public void onProgressUpdate(long downloaded,long total) {
            //notification.setContentOnProgress("Downloaded "+(downloaded/1000)+"kb of "+(total/1000)+"kb");
            //notification.updateProgress(notification.generateProgress(downloaded,total));
            updateProgress((int)((downloaded*100)/total));
        }

        @Override
        public void onProgressComplete(File downloadedFile) {

            //notification.progressComplete();
            hideProgressBar();
        }

        @Override
        public void onProgressFailed(int reason) {
            //notification.setContentText("Failed");
            //notification.fireNotification();
            dialog.setMessage("Failed");
        }

        @Override
        public void onProgressPause(long downloaded, int reason) {
            dialog.setMessage("Paused");
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
