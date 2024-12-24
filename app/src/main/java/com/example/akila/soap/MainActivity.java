package com.example.akila.soap;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Mails> mailList = new ArrayList<>();
    private RecyclerView recyclerView;
    public MailsAdapter mAdapter;

    Button butNewItems, butDoneItems, butMarkAllDone;
    TextView tvTitle;

    boolean view_New_Items = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMailAdapter();
        initRecyclerView();

        startAlarmManager();

        initTextViews();
        initButtons();
    }

    private void startAlarmManager() {
        if (isMyServiceRunning(AlarmReceiver.class))
            Log.i("SERVICE", "Service is Running");
        else {
            Log.i("SERVICE", "Service is Not Running");
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager alarmMgr = (AlarmManager) this.getSystemService(ALARM_SERVICE);
            if (alarmMgr != null) {
                alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 3 * 60000, alarmIntent);
            }
        }

    }

    private void initMailAdapter() {

        mAdapter = new MailsAdapter(mailList);
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void initTextViews() {

        tvTitle = findViewById(R.id.tvTitle);
    }

    private void initButtons() {
        butNewItems = findViewById(R.id.butNewItems);
        butDoneItems = findViewById(R.id.butDoneItems);
        butMarkAllDone = findViewById(R.id.butMarkAllDone);

        butNewItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_New_Items = true;
                prepareMailData();
            }
        });

        butDoneItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_New_Items = false;
                prepareMailData();
            }
        });

        butMarkAllDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                alertDialog.setTitle("Done");
                alertDialog.setMessage("Are you sure you want to mark ALL ITEMS Done?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MailDataBaseHelper mDbHelper = new MailDataBaseHelper(v.getContext());
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();
                        db.execSQL(MailDataBaseHelper.setAllDone());
                        dialog.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareMailData();
    }

    public void prepareMailData() {


        mailList.clear();
        int i = 0;
        MailDataBaseHelper mDbHelper = new MailDataBaseHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM mailDB", null);
        if (cursor.moveToLast()) {
            do {
                //Log.i("DB_DATA", String.format("Row: %d, Values: %s", cursor.getPosition(), cursor.getString(5)));

                if (view_New_Items) {
                    if (!cursor.getString(5).equals("1")) {
                        String dateCumtom = cursor.getString(4).substring(8, 10) + "/" + cursor.getString(4).substring(4, 7) + "/" + cursor.getString(4).substring(30, 34);

                        Mails mails = new Mails(cursor.getString(2), cursor.getString(1), dateCumtom, cursor.getString(0), cursor.getString(3));
                        mailList.add(mails);
                        i++;
                    }
                } else {
                    if (cursor.getString(5).equals("1")) {
                        String dateCumtom = cursor.getString(4).substring(8, 10) + "/" + cursor.getString(4).substring(4, 7) + "/" + cursor.getString(4).substring(30, 34);

                        Mails mails = new Mails(cursor.getString(2), cursor.getString(1), dateCumtom, cursor.getString(0), cursor.getString(3));
                        mailList.add(mails);
                        i++;
                    }
                }

            } while (cursor.moveToPrevious());
        }
        cursor.close();
        Log.i("MAIL", "Count = " + String.valueOf(i));
        mAdapter.notifyDataSetChanged();

        if (view_New_Items) {
            tvTitle.setText("New Items (" + mAdapter.getItemCount() + ")");
            butMarkAllDone.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setText("Done Items (" + mAdapter.getItemCount() + ")");
            butMarkAllDone.setVisibility(View.INVISIBLE);
        }

        if (cursor.getCount() == 0)
            getSupportActionBar().setTitle("SOAP (Sync in progress... Come Back Later)");
        else getSupportActionBar().setTitle("SOAP");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        prepareMailData();

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
