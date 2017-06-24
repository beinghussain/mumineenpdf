package com.mumineendownloads.mumineenpdf.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.Model.PDFDownload;


public class DownloadDBHelper extends SQLiteOpenHelper {
    private static final String KEY_ID = "id";
    private static final String KEY_PID = "pid";
    private static final String KEY_STATUS = "status";
    private static final String TABLE_NAME = "downloadManager";

    private final Context context;

    public DownloadDBHelper(Context context) {
        super(context, "database.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"  + KEY_PID  + " INTEGER UNIQUE," + KEY_STATUS + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void deletePDFDownload(PDFDownload pdf) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_PID + " = ?",
                new String[] { String.valueOf(pdf.getPid()) });
        db.close();
    }

    public void updateOrInsert(final PDFDownload pdf) {
        final SQLiteDatabase db = this.getWritableDatabase();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ContentValues initialValues = new ContentValues();
                initialValues.put(KEY_STATUS, pdf.getStatus());
                initialValues.put(KEY_PID, pdf.getPid());

                int id = (int) db.insertWithOnConflict(TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == -1) {
                    db.update(TABLE_NAME, initialValues, "pid = ?", new String[] {String.valueOf(pdf.getPid())});
                }
            }
        });

    }
}
