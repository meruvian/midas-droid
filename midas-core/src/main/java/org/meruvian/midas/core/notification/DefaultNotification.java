package org.meruvian.midas.core.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by ludviantoovandi on 03/09/14.
 */
public abstract class DefaultNotification {
    private Context context;
    private int id;

    private NotificationCompat.Builder builder;

    public DefaultNotification(Context context, int id) {
        this.context = context;
        this.id = id;

        builder = new NotificationCompat.Builder(context);
    }

    public abstract String title();

    public abstract String contentText();

    public abstract int smallIcon();

    public void setLargeIcon(int res) {
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), res));
    }

    public void setContentInfo(String info) {
        builder.setContentInfo(info);
    }

    public void setTime(long time) {
        builder.setWhen(time);
    }

    public void setIntent(Intent intent) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
    }

    public void setActivity(Class activity) {
        Intent intent = new Intent(context, activity);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(activity);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
    }

    public void setBigView(String title, String[] contents) {
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(title);
        for (String content : contents) {
            inboxStyle.addLine(content);
        }

        builder.setStyle(inboxStyle);
    }

    public void show() {
        builder.setContentTitle(title());
        builder.setContentText(contentText());
        builder.setSmallIcon(smallIcon());
        builder.setTicker(contentText());
        builder.setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//        Notification notification = builder.build();
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        notification.defaults = Notification.DEFAULT_ALL;

        manager.notify(id, builder.build());
    }
}
