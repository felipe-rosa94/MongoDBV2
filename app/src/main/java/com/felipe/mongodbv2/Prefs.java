package com.felipe.mongodbv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by felipe on 05/04/2018.
 */

public class Prefs {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "prefs.sqb";
    private static final String DATABASE_TABLE = "prefs";

    private static final String _ID = "Id";
    private static final String _DATABASE_NAME = "DatabaseName";
    private static final String _COLLECTIONS = "Collections";
    private static final String _API_KEY = "ApiKey";

    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " ("
                    + _ID + " integer primary key autoincrement, "
                    + _DATABASE_NAME + " text not null, "
                    + _COLLECTIONS + " text, "
                    + _API_KEY + " text "
                    + ");";

    public class Struc {
        public int Id = 1;
        public String DatabaseName = "";
        public String Collections = "";
        public String ApiKey = "";
    }

    public final Struc Fields = new Struc();

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public Prefs(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            db.execSQL("insert into prefs (Id,DatabaseName,Collections,ApiKey) values (1,\"\",\"\",\"\");");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Database", "Atualizando da versão " + oldVersion
                    + " para "
                    + newVersion + ". Isto destruirá todos os dados.");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE + ";");
            onCreate(db);
        }
    }

    //---opens the database
    public void open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        getId(1);
    }

    //---closes the database
    public void close() {
        DBHelper.close();
    }

    private ContentValues getArgs() {
        ContentValues args = new ContentValues();

        args.put(_ID, Fields.Id);
        args.put(_DATABASE_NAME, Fields.DatabaseName);
        args.put(_COLLECTIONS, Fields.Collections);
        args.put(_API_KEY, Fields.ApiKey);
        return args;
    }

    public void getFields(Cursor c) {
        if (!c.isAfterLast() && !c.isBeforeFirst()) {
            int i = 0;
            Fields.Id = c.getInt(i++);
            Fields.DatabaseName = c.getString(i++);
            Fields.Collections = c.getString(i++);
            Fields.ApiKey = c.getString(i++);

        }
    }

    public boolean emtpyTable() {
        return db.delete(Prefs.DATABASE_TABLE, null, null) > 0;
    }

    //---insert a record
    public long insert() {
        return db.insert(DATABASE_TABLE, null, getArgs());
    }

    //---updates a record
    public boolean update(long Id) {
        return db.update(DATABASE_TABLE, getArgs(), _ID + "=" + Id, null) > 0;
    }

    //---deletes a particular record
    public boolean delete(long Id) {
        return db.delete(DATABASE_TABLE, _ID + "=" + Id, null) > 0;
    }

    //---retrieves all the records
    public Cursor getAll() {
        return getWhere(null);
    }

    //---retrieves a record
    public Cursor getId(long Id) throws SQLException {
        Cursor c = getWhere(_ID + "=" + Id);
        return c;
    }

    //---retrieves a record set
    public Cursor getWhere(String Where) throws SQLException {
        Cursor c =
                db.query(DATABASE_TABLE, new String[]{
                                _ID,
                                _DATABASE_NAME,
                                _COLLECTIONS,
                                _API_KEY},
                        Where,
                        null,
                        null,
                        null,
                        _ID);

        if (c != null) {
            c.moveToFirst();
            getFields(c);
        }
        return c;
    }
}