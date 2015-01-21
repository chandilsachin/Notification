package com.sachin.root.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

/**
 * Created by root on 1/17/15.
 */
public class NotificationManager extends NotificationCompat.Builder {

    public int PROGRESS_MAX = 100;
    public int PROGRESS_MIN = 0;

    private static int id = 1;
    private int NotificationId;
    private Intent TargetIntent;
    private int SmallIcon;
    private Context context;
    private android.app.NotificationManager sNotification;
    private String TitleOnCompletion;
    private String TitleOnProgress;
    private String ContentOnCompletion;
    private String ContentOnProgress;
    private String TitleOnWaiting;
    private String ContentOnWaiting;


    public NotificationManager(Context context)
    {
        super(context);
        NotificationId = id++;
        this.context = context;
        sNotification = (android.app.NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        //setDefaults(NotificationCompat.DEFAULT_ALL);
        setAutoCancel(true);
    }

    /**
     * Initialises Notification Builder.
     * @param context - context of current activity.
     * @param Title - Title.
     * @param ContentText - Content text for notification
     * @param AutoCancel - Cancelable by user.
     * @param SmallIcon - Icon resource to be used on NotificationBar.
     */
    public NotificationManager(Context context, String Title, String ContentText, boolean AutoCancel, int SmallIcon)
    {
        super(context);
        this.context = context;
        NotificationId = id++;
        sNotification = (android.app.NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        setSmallIcon(SmallIcon);
        setContentTitle(Title);
        setContentText(ContentText);
        setAutoCancel(AutoCancel);
        //setDefaults(NotificationCompat.DEFAULT_ALL);
    }

    /**
     * Returns id that is beind used by current object for notification.
     * @return
     */
    public int getId()
    {
        return NotificationId;
    }

    /**
     * Sets icon for Notification drawer.
     * @param largeIcon - icon resource
     */
    public void setLargeIcon(int largeIcon)
    {
        setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon));
    }

    /**
     * Sets intent to start when user clicks notification.
     * @param targetIntent - target intent.
     * @param flag - flag for intent FLAG_ONE_SHOT, FLAG_NO_CREATE,FLAG_CANCEL_CURRENT,FLAG_UPDATE_CURRENT.
     */
    public void setContentIntent(Intent targetIntent, int flag)
    {
        PendingIntent intentToFire = PendingIntent.getActivity(context,0,targetIntent,flag);
        setContentIntent(intentToFire);
    }

    /**
     * Sets intent to start when user clicks notification canceling current intent if any.
     * @param targetIntent - target intent.
     */
    public void setContentIntent(Intent targetIntent)
    {
        PendingIntent intentToFire = PendingIntent.getActivity(context,0,targetIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        setContentIntent(intentToFire);
    }

    /**
     * Fires Notification
     * @return id being used by current notification object.
     */
    public int fireNotification()
    {
        sNotification.cancel(NotificationId);
        sNotification.notify(NotificationId, build());
        return NotificationId;
    }

    public int fireProgressNotification()
    {
        sNotification.cancel(NotificationId);
        setOngoing(true);
        if(TitleOnProgress!=null)
        setContentTitle(TitleOnProgress);
        if(ContentOnProgress!=null)
        setContentText(ContentOnProgress);
        updateProgress(0);
        return NotificationId;
    }

    public void updateProgress(int progress)
    {
        if(progress > PROGRESS_MAX)
        {
            progressComplete();
            return;
        }
        if(ContentOnProgress != null)
        setContentText(ContentOnProgress);
        if(TitleOnProgress != null)
            setContentTitle(TitleOnProgress);
        setProgress(PROGRESS_MAX,progress,false);
        sNotification.notify(NotificationId, build());
    }

    public void progressComplete()
    {
        if(TitleOnCompletion!=null)
            setContentTitle(TitleOnCompletion);
        if(ContentOnCompletion!=null)
            setContentText(ContentOnCompletion);
        setProgress(0, 0, false);
        setOngoing(false);
        sNotification.notify(NotificationId, build());
    }

    public void fireIndeterminateProgress()
    {
        sNotification.cancel(NotificationId);
        if(TitleOnProgress!=null)
            setContentTitle(TitleOnProgress);
        if(ContentOnProgress!=null)
            setContentText(ContentOnProgress);
        setProgress(0, 0, true);
        sNotification.notify(NotificationId,build());
    }

    /**
     * Returns percentage value.
     * @param progress - current progress
     * @param total - total value.
     */
    public int generateProgress(int progress,int total)
    {
        return (progress * PROGRESS_MAX)/total;
    }

    /**
     * Returns percentage value.
     * @param progress - current progress
     * @param total - total value.
     */
    public int generateProgress(long progress,long total)
    {
        return (int)((progress * PROGRESS_MAX) / total);
    }

    /**
     * Dismiss the Notification Bar
     */
    public void dismiss()
    {
        sNotification.cancel(NotificationId);
    }

    public int getProgressMax() {
        return PROGRESS_MAX;
    }

    public int getProgressMin() {
        return PROGRESS_MIN;
    }

    public void setPROGRESS_MAX(int PROGRESS_MAX) {
        this.PROGRESS_MAX = PROGRESS_MAX;
    }

    public void setPROGRESS_MIN(int PROGRESS_MIN) {
        this.PROGRESS_MIN = PROGRESS_MIN;
    }

    public String getContentOnWaiting() {
        return ContentOnWaiting;
    }

    public void setContentOnWaiting(String contentOnWaiting) {
        ContentOnWaiting = contentOnWaiting;
    }

    public String getTitleOnWaiting() {
        return TitleOnWaiting;
    }

    public void setTitleOnWaiting(String titleOnWaiting) {
        TitleOnWaiting = titleOnWaiting;
    }

    public String getContentOnProgress() {
        return ContentOnProgress;
    }

    public void setContentOnProgress(String contentOnProgress) {
        ContentOnProgress = contentOnProgress;
    }

    public String getContentOnCompletion() {
        return ContentOnCompletion;
    }

    public void setContentOnCompletion(String contentOnCompletion) {
        ContentOnCompletion = contentOnCompletion;
    }

    public String getTitleOnProgress() {
        return TitleOnProgress;
    }

    public void setTitleOnProgress(String titleOnProgress) {
        TitleOnProgress = titleOnProgress;
    }

    public String getTitleOnCompletion() {
        return TitleOnCompletion;
    }

    public void setTitleOnCompletion(String titleOnCompletion) {
        TitleOnCompletion = titleOnCompletion;
    }
}
