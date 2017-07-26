package com.mumineendownloads.mumineenpdf.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.mumineendownloads.mumineenpdf.Model.PDF;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


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
    private static final String KEY_PAGE = "pages";
    private static final String KEY_GO = "go";
    private static final String KEY_CAT = "sub_cat";
    private static final String KEY_AUDIO = "audio";
    private Context context;
    private ArrayList<PDF.PdfBean> downloaded;

    public PDFHelper(Context context){
        super(context, "database.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PDF + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_ALBUM + " TEXT," + KEY_SOURCE + " TEXT," + KEY_SIZE + " TEXT," +
                KEY_PID  + " INTEGER UNIQUE," + KEY_STATUS + " INTEGER," + KEY_PAGE + " INTEGER,"+  KEY_GO + " INTEGER," + KEY_CAT + " TEXT" + ", "+ KEY_AUDIO +" INTEGER)";
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
        values.put(KEY_GO, pdf.getGo());
        values.put(KEY_CAT, pdf.getCat());
            db.insert(TABLE_PDF, null, values);
        }catch (SQLiteConstraintException ignored){

        }
        db.close();
    }

    public PDF.PdfBean getPDF(int pid) {
        ArrayList<PDF.PdfBean> arrayList = new ArrayList<PDF.PdfBean>();
        String selectQuery = "SELECT  * FROM " + TABLE_PDF + " WHERE pid = " + pid;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(1);
                String album1 = cursor.getString(2);
                PDF.PdfBean contact = new PDF.PdfBean();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setTitle(title);
                contact.setAlbum(album1);
                try {
                    contact.setStatus(Integer.parseInt(cursor.getString(6)));
                }catch (NumberFormatException ignored){
                    contact.setStatus(0);
                }
                contact.setSource(cursor.getString(3));
                contact.setSize(cursor.getString(4));
                contact.setPid(Integer.parseInt(cursor.getString(5)));
                String c;
                if(cursor.getString(7)==null){
                    c = "0";
                } else {
                    c = cursor.getString(7);
                }
                contact.setCat(cursor.getString(9));
                contact.setAudio(cursor.getInt(10));
                contact.setPageCount(Integer.parseInt(c));
                if(!isDownloaded(cursor.getInt(5),Integer.parseInt(cursor.getString(4)))){
                    contact.setStatus(Status.STATUS_NULL);
                    updatePDF(contact);
                }else {
                    contact.setStatus(Status.STATUS_DOWNLOADED);
                    updatePDF(contact);
                }
                arrayList.add(contact);
            } while (cursor.moveToNext());
        }
        return arrayList.get(0);
    }

    public ArrayList<PDF.PdfBean> getAllPDFS(String album) {
        List<String> files = Utils.getFiles();
        ArrayList<PDF.PdfBean> arrayList = new ArrayList<PDF.PdfBean>();
        String selectQuery;
        switch (album) {
            case "all":
                selectQuery = "SELECT  * FROM " + TABLE_PDF + " ORDER BY title";
                break;
            case "Quran30":
             selectQuery = "SELECT  * FROM " + TABLE_PDF + " WHERE album in ('Quran30','QuranSurat') ORDER BY title";
                break;
            default:
                selectQuery = "SELECT  * FROM " + TABLE_PDF + " WHERE album = '" + album + "' ORDER BY title";
                break;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(1);
                String album1 = cursor.getString(2);
                if(album.equals("QuranSurat")){
                    title = title.substring(4);
                }
                PDF.PdfBean contact = new PDF.PdfBean();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setTitle(title);
                contact.setAlbum(album1);
//                contact.setStatus(Integer.parseInt(cursor.getString(6)));
                contact.setSource(cursor.getString(3));
                contact.setSize(cursor.getString(4));
                contact.setPid(Integer.parseInt(cursor.getString(5)));
                contact.setCat(cursor.getString(9));
                contact.setAudio(cursor.getInt(10));
                String c;
                if(cursor.getString(7)==null){
                   c = "0";
                } else {
                    c = cursor.getString(7);
                }
                if(files.contains(String.valueOf(contact.getPid()))){
                    contact.setStatus(Status.STATUS_DOWNLOADED);
                }else {
                    contact.setStatus(Status.STATUS_NULL);
                }
                contact.setPageCount(Integer.parseInt(c));
                arrayList.add(contact);
            } while (cursor.moveToNext());
        }return arrayList;
    }

    private boolean isDownloaded(int pid,int size) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mumineen/" + pid + ".pdf");
        return file.exists();
    }

    public int updatePDF(PDF.PdfBean pdf) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, pdf.getStatus());
        if(pdf.getPageCount()!=0) {
            values.put(KEY_PAGE, pdf.getPageCount());
        }
        return db.update(TABLE_PDF, values, KEY_PID + " = ?",
                new String[] { String.valueOf(pdf.getPid()) });
    }

    public void deletePDF(PDF.PdfBean pdf) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PDF, KEY_ID + " = ?",
                new String[] { String.valueOf(pdf.getId()) });
        db.close();
    }

    public void updateOrInsert(final PDF.PdfBean pdf) {
        final SQLiteDatabase db = this.getWritableDatabase();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ContentValues initialValues = new ContentValues();
                initialValues.put(KEY_SIZE, pdf.getSize());
                initialValues.put(KEY_SOURCE, pdf.getSource());
                initialValues.put(KEY_ALBUM, pdf.getAlbum());
                initialValues.put(KEY_TITLE, pdf.getTitle());
                initialValues.put(KEY_PID, pdf.getPid());
                initialValues.put(KEY_CAT,pdf.getCat());
                initialValues.put(KEY_PAGE, pdf.getPageCount());
                initialValues.put(KEY_AUDIO, pdf.getAudio());
                try {
                    int id = (int) db.insertWithOnConflict(TABLE_PDF, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
                    if (id == -1) {
                        db.update(TABLE_PDF, initialValues, "pid = ?", new String[]{String.valueOf(pdf.getPid())});
                    }
                }catch (IllegalStateException ignored){
                }
            }
        });
    }

    public ArrayList<String> getAlbumName(){
        ArrayList<String> arrayList = new ArrayList<>();
        String selectQuery = "SELECT distinct album FROM " + TABLE_PDF + " WHERE " + KEY_STATUS +"  = " + Status.STATUS_DOWNLOADED;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                arrayList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    public ArrayList<String> getAlbums(){
        ArrayList<String> arrayList = new ArrayList<>();
        String selectQuery = "SELECT distinct album FROM " + TABLE_PDF;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                arrayList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    public int getDownloadAlbum() {
        String selectQuery = "SELECT count(distinct album) FROM " + TABLE_PDF + " WHERE " + KEY_STATUS +"  = " + Status.STATUS_DOWNLOADED ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                return cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return 0;
    }



}