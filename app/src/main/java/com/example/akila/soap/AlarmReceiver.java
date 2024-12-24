package com.example.akila.soap;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {

    Context context;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.i("ALARM", "Service Started");
        ReceiveMails receiveMails = new ReceiveMails();
        receiveMails.execute("a", "b", "c");
    }

    @SuppressLint("StaticFieldLeak")
    class ReceiveMails extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            MailDataBaseHelper mDbHelper = new MailDataBaseHelper(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM mailDB", null);
            if (cursor.moveToLast()) {
                do {
                    if (!cursor.getString(5).equals("1")) {
                        String dateCumtom = cursor.getString(4).substring(8, 10) + "/" + cursor.getString(4).substring(4, 7) + "/" + cursor.getString(4).substring(30, 34);
                        AlarmReceiver.this.makeNotification(Integer.valueOf(cursor.getString(0)), cursor.getString(1) + " (" + dateCumtom + ")", cursor.getString(3));
                    }
                } while (cursor.moveToPrevious());
            }
            cursor.close();

            String userName = context.getString(R.string.mail_user_name);
            String password = context.getString(R.string.mail_password);
            checkMailOutlook(userName, password);
            return null;
        }

        private void checkMailOutlook(String userName, String password) {

            MailDataBaseHelper mDbHelper = new MailDataBaseHelper(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            try {
                String protocol = "imaps";
                Properties props = new Properties();
                props.setProperty("mail.store.protocol", protocol);

                props.setProperty("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.setProperty("mail.imaps.socketFactory.fallback", "false");
                props.setProperty("mail.imaps.port", "993");
                props.setProperty("mail.imaps.socketFactory.port", "993");

                Session session = Session.getDefaultInstance(props, null);
                Store store = session.getStore(protocol);
                store.connect("imap-mail.outlook.com", userName, password);
                Folder inbox = store.getFolder("INBOX");
                UIDFolder uf = (UIDFolder) inbox;
                inbox.open(Folder.READ_WRITE);

                Message messages[] = inbox.getMessages();
                Log.i("MAIL_SYNC", "Total Mails in MailBox = " + String.valueOf(messages.length));

                Cursor cursor = db.rawQuery(MailDataBaseHelper.getMaxID(), null);
                long lastSyncMailUID = 0;
                if (cursor.moveToFirst()) lastSyncMailUID = cursor.getLong(0);
                Log.i("DB", "Last Synced Mail UID = " + String.valueOf(lastSyncMailUID));
                cursor.close();

                Log.i("SYNC","Last synced Mail position in MailBox = " + getClosestUIDPosition(messages, (int) lastSyncMailUID, uf));

                for (int i = getClosestUIDPosition(messages, (int) lastSyncMailUID, uf); i < messages.length; i++) {
                    Message individualMsg = messages[i];
                    if (individualMsg.getSubject().equals("Error Notification of CEB Payment Web Service")) {
                        Log.i("MAIL", "ID = " + uf.getUID(messages[i]));
                        db.execSQL(MailDataBaseHelper.addMail(uf.getUID(messages[i]), individualMsg.getSubject(), individualMsg.getFrom()[0].toString(), individualMsg.getContent().toString(), individualMsg.getReceivedDate()));
                    }

                }

                inbox.close(false);
                store.close();

                Log.i("MAIL", "Synced");
            } catch (Exception exp) {
                exp.printStackTrace();
            }

        }

    }

    private void makeNotification(int notifyID, String title, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            CharSequence name = "my_channel_01";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Create a notification and set the notification channel.
            Notification notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId(CHANNEL_ID)
                    .build();

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(notifyID, notification);
        } else {
            Notification notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            mNotificationManager.notify(notifyID, notification);
        }
    }

    private int getClosestUIDPosition(Message a[], int x, UIDFolder uf) throws MessagingException {

        int low = 0;

        int high = a.length - 1;

        if (high < 0)
            throw new IllegalArgumentException("The array cannot be empty");

        while (low < high) {
            int mid = (low + high) / 2;
            assert (mid < high);
            int d1 = Math.abs((int) uf.getUID(a[mid]) - x);
            int d2 = Math.abs((int) uf.getUID(a[mid + 1]) - x);
            if (d2 <= d1) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return high;
    }

}
