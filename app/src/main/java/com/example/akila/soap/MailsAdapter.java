package com.example.akila.soap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MailsAdapter extends RecyclerView.Adapter<MailsAdapter.MyViewHolder> {

    private List<Mails> mailList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, year, genre, uid, body;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tvSender);
            genre = view.findViewById(R.id.tvSubject);
            year = view.findViewById(R.id.tvDate);
            uid = view.findViewById(R.id.UID);
            body = view.findViewById(R.id.tvBody);
        }

        @Override
        public void onClick(View view) {
            Log.d("RECYCLE_VIEW", "onClick " + getPosition());
        }
    }

    public MailsAdapter(List<Mails> mailList) {
        this.mailList = mailList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mail_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Mails mails = mailList.get(position);
        holder.title.setText(mails.getTitle());
        holder.genre.setText(mails.getGenre());
        holder.year.setText(mails.getYear());
        holder.uid.setText(mails.getUID());
        holder.body.setText(String.format("#%s", mails.getBody()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                Log.i("RECYCLEVIEW", "Long Pressed " + mails.getUID());

                MailDataBaseHelper mDbHelper = new MailDataBaseHelper(v.getContext());
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                alertDialog.setTitle("Done");

                Cursor cursor = db.rawQuery(MailDataBaseHelper.getFlag(Long.valueOf(mails.getUID())), null);
                Log.i("DB",String.valueOf(cursor.getCount()));
                cursor.moveToNext();
                int flag = Integer.valueOf(cursor.getString(0));
                cursor.close();

                if (flag == 1) {
                    alertDialog.setMessage("Are you sure you want to set this item New?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MailDataBaseHelper mDbHelper = new MailDataBaseHelper(v.getContext());
                            SQLiteDatabase db = mDbHelper.getWritableDatabase();
                            db.execSQL(MailDataBaseHelper.removeFlag(Long.valueOf(mails.getUID())));
                            dialog.dismiss();
                        }
                    });
                } else {
                    alertDialog.setMessage("Are you sure you want to set this item Done?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MailDataBaseHelper mDbHelper = new MailDataBaseHelper(v.getContext());
                            SQLiteDatabase db = mDbHelper.getWritableDatabase();
                            db.execSQL(MailDataBaseHelper.setFlag(Long.valueOf(mails.getUID())));
                            dialog.dismiss();
                        }
                    });
                }


                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();


                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mailList.size();
    }

}
