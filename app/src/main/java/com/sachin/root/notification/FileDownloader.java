package com.sachin.root.notification;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by root on 1/18/15.
 */
public class FileDownloader {

    private static final int BUFFER_SIZE = 1024*5;

    public static final int DOWNLOADING = 1;
    public static final int PAUSE = 2;
    public static final int CANCEL = 3;
    public static final int FAILED = 4;

    private HttpURLConnection connection;
    private InputStream inputStream;
    private long Downloaded = 0, TotalSize;
    private String Url;
    private String FileToSave;
    private int Status;
    private FileOutputStream OutputFile;
    private boolean InternetExists = true;
    private View view;
    private ProgressListener Listener;

    public FileDownloader(Context context,String Url,String fileToSave) {

        this.Url = Url;
        this.FileToSave = fileToSave;
        Downloaded = 0;
        view = new View(context);


    }


    public void start(final ProgressListener listener)
    {
        Status = DOWNLOADING;
        Listener = listener;
        URL url = null;

        try {
            url = new URL(Url);
            Downloaded = 0;
            connection = (HttpURLConnection) url.openConnection();
            new asyncTask().execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void resume(ProgressListener listener)
    {
        Status = DOWNLOADING;
        Listener = listener;
        File localFile = new File(FileToSave);
        if(localFile.exists())
        {
            Downloaded = localFile.length();
        }
        try {
            URL url = new URL(Url);
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setRequestProperty("Range","bytes="+Downloaded+"-");
        new asyncTask().execute();
    }

    public void pause()
    {
        Status = PAUSE;
    }

    public interface ProgressListener{


        public static final int REASON_NO_INTERNET_CONNECTION = 1;
        public static final int REASON_CONNECTION_TIMEOUT = 2;
        public static final int REASON_IO_EXCEPTION = 3;
        public static final int REASON_INVALID_RESPONSE_CODE = 4;

        /**
         * Called when downloading starts.
         *
         */
        public void onProgressStart();

        /**
         * Called on progress update.
         * @param downloaded - size of downloaded content.
         */
        public void onProgressUpdate(long downloaded,long total);

        /**
         * Called when file download process completes.
         * @param downloadedFile
         */
        public void onProgressComplete(File downloadedFile);

        /**
         * Called when download process fails
         * @param reason
         */
        public void onProgressFailed(int reason);

        /**
         * Called when download process is paused.
         * @param downloaded - downloaded file size.
         * @param reason - reason to pause downloaded.
         */
        public void onProgressPause(long downloaded, int reason);
    }

    class asyncTask extends AsyncTask<Integer,Long,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Listener.onProgressStart();
        }

        @Override
        protected String doInBackground(Integer... params) {

            try {
                connection.connect();
                if(connection.getResponseCode()/100 != 2)
                {
                    Status = FAILED;
                }

                inputStream = new BufferedInputStream(connection.getInputStream());
                OutputFile = Downloaded==0?new FileOutputStream(FileToSave):new FileOutputStream(FileToSave,true);

                BufferedInputStream in = new BufferedInputStream(inputStream);
                TotalSize = Downloaded + connection.getContentLength();

                byte[] buffer = new byte[BUFFER_SIZE];
                int readBytes;

                while(Status == DOWNLOADING && (readBytes = inputStream.read(buffer,0,BUFFER_SIZE)) != -1 )
                {
                    OutputFile.write(buffer, 0 ,readBytes);
                    Downloaded += readBytes;
                    publishProgress(Downloaded,TotalSize);
                    //listener.onProgressUpdate(Downloaded,TotalSize);
                    if(!InternetExists || Status == PAUSE)
                    {

                        Status = PAUSE;
                        break;
                    }
                }
                if(Status == DOWNLOADING)
                {

                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(OutputFile!=null)
                        OutputFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if(inputStream!=null)
                        inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(connection!=null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Listener.onProgressComplete(new File(FileToSave));
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            Listener.onProgressUpdate(values[0],values[1]);
            super.onProgressUpdate(values);
        }
    }
}
