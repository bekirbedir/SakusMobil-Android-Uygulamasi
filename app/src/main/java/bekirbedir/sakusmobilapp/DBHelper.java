package bekirbedir.sakusmobilapp;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Created by bekir on 21.1.2018.
 */


public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME   = "SAKUS_MOBIL_DB";
    // Contacts table name
    private static final String TABLE_FAVORITE_BUS = "favorite_bus";
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_FAVORITE_BUS + "(id INTEGER PRIMARY KEY,bus_number TEXT" + ")";
        Log.d("DBHelper", "SQL : " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE_BUS);
        onCreate(db);
    }
    public void insertBus(String busNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("bus_number", busNumber);
        db.insert(TABLE_FAVORITE_BUS, null, values);
        db.close();
    }
    public List<Otobus> getFavoriteBusList() {
        List<Otobus> FavoritebusList = new ArrayList<Otobus>();
        SQLiteDatabase db = this.getWritableDatabase();

        // String sqlQuery = "SELECT  * FROM " + TABLE_COUNTRIES;
        // Cursor cursor = db.rawQuery(sqlQuery, null);

        Cursor cursor = db.query(true,TABLE_FAVORITE_BUS, new String[]{"id", "bus_number"}, null,null ,"bus_number", null ,"id DESC","7");
        while (cursor.moveToNext()) {
            FavoritebusList.add(new Otobus(cursor.getString(1)));
        }
        if(FavoritebusList.isEmpty())
        {
            FavoritebusList.add(new Otobus("4"));
        }
        return FavoritebusList;
    }
    public String getLastBus() {
      String getLastBus= "";
        SQLiteDatabase db = this.getWritableDatabase();

        // String sqlQuery = "SELECT  * FROM " + TABLE_COUNTRIES;
        // Cursor cursor = db.rawQuery(sqlQuery, null);

        Cursor cursor = db.query(true,TABLE_FAVORITE_BUS, new String[]{"id", "bus_number"}, null,null ,"bus_number", null ,"id DESC","1");
        while (cursor.moveToNext()) {
            getLastBus =cursor.getString(1);
        }
        if(getLastBus == "" || getLastBus == null)
        {
            getLastBus = "4";
        }
        return getLastBus;
    }


}