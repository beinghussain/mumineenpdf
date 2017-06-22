package com.mumineendownloads.mumineenpdf.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.mumineendownloads.mumineenpdf.Constants;
import com.mumineendownloads.mumineenpdf.Model.PDF;

import java.io.File;
import java.util.ArrayList;


public class PDFHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "pdfManager";
    private static final String TABLE_PDF = "pdf";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SIZE = "size";
    private static final String KEY_ALBUM = "album";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_PID = "pid";
    private static final String KEY_STATUS = "status";
    private static final String KEY_DOWNLOADS = "downloads";
    private static final String KEY_VIEWED = "viewed";


    private Context context;



    public PDFHelper(Context context){
        super(context, "database.db", null, 1);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PDF + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT UNIQUE,"
                + KEY_ALBUM + " TEXT," + KEY_SOURCE + " TEXT," + KEY_SIZE + " TEXT," + KEY_PID  + " INTEGER," + KEY_STATUS + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PDF);
        onCreate(db);
    }

    public void addPDF(PDF.PdfBean pdf) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, pdf.getTitle());
        values.put(KEY_ALBUM, pdf.getAlbum());
        values.put(KEY_SOURCE, pdf.getSource());
        values.put(KEY_SIZE, pdf.getSize());
        values.put(KEY_PID, pdf.getPid());
        values.put(KEY_PID, pdf.getStatus());

            db.insert(TABLE_PDF, null, values);
        }catch (SQLiteConstraintException ignored){

        }
        db.close();
    }

    public PDF.PdfBean getPDF(int pid) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PDF, new String[] { KEY_ID,
                        KEY_TITLE, KEY_ALBUM, KEY_SOURCE, KEY_SIZE, KEY_SIZE }, KEY_PID+ "=?",
                new String[] { String.valueOf(pid) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new PDF.PdfBean(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                Integer.parseInt(cursor.getString(5)),
                Integer.parseInt(cursor.getString(5)));
    }

    public ArrayList<PDF.PdfBean> getAllPDFS(String album) {
        ArrayList<PDF.PdfBean> arrayList = new ArrayList<PDF.PdfBean>();
        String selectQuery = "SELECT  * FROM " + TABLE_PDF + " WHERE album = '" + album + "' ORDER BY title";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PDF.PdfBean contact = new PDF.PdfBean();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setTitle(cursor.getString(1));
                contact.setAlbum(cursor.getString(2));
                if(isDownloaded(cursor.getString(1))){
                    if(cursor.getString(6) != null) {
                        contact.setStatus(Integer.parseInt(cursor.getString(6)));
                    }
                }else {
                    contact.setStatus(Constants.STATUS_NULL);
                }
                contact.setSource(cursor.getString(3));
                contact.setSize(cursor.getString(4));
                contact.setPid(Integer.parseInt(cursor.getString(5)));

                arrayList.add(contact);
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    private boolean isDownloaded(String string) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Mumineen/"+string+".pdf");
        return file.exists();
    }

    public int getPDFCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PDF;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
        assert cursor != null;
        return cursor.getCount();
    }

    public int updatePDF(PDF.PdfBean pdf) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, pdf.getStatus());
        return db.update(TABLE_PDF, values, KEY_PID + " = ?",
                new String[] { String.valueOf(pdf.getPid()) });
    }

    public void deleteContact(PDF.PdfBean pdf) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PDF, KEY_ID + " = ?",
                new String[] { String.valueOf(pdf.getId()) });
        db.close();
    }
}