package util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseCartHandler extends SQLiteOpenHelper {

    private static String DB_NAME = "bkks_db";
    private static int DB_VERSION = 3;
    private SQLiteDatabase db;

    public static final String CART_TABLE = "wertb";

    public static final String COLUMN_ID = "product_id";
    public static final String COLUMN_CID = "cart_id";
    public static final String COLUMN_QTY = "qty";
    public static final String COLUMN_IMAGE = "product_image";
    public static final String COLUMN_CAT_ID = "cat_id";
    public static final String COLUMN_NAME = "product_name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_MRP = "mrp";
    public static final String COLUMN_UNIT_PRICE = "unit_price";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_STOCK = "stock";
    public static final String COLUMN_INCREMENT = "increament";
    public static final String COLUMN_REWARDS = "rewards";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "product_description";
    public static final String COLUMN_SID = "sid";
    public static final String COLUMN_BOOK_CLASS="book_class";
    public static final String COLUMN_SUBJECT="subject";
    public static final String COLUMN_LANGUAGE="language";







    public DatabaseCartHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db=db;
        String exe = "CREATE TABLE IF NOT EXISTS " + CART_TABLE
                + "(" + COLUMN_CID + " integer primary key, "
                + COLUMN_ID + " integer NOT NULL,"
                + COLUMN_QTY + " DOUBLE NOT NULL,"
                + COLUMN_IMAGE + " TEXT NOT NULL, "
                + COLUMN_CAT_ID + " TEXT NOT NULL, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_PRICE + " DOUBLE NOT NULL, "
                + COLUMN_MRP + " DOUBLE NOT NULL, "
                + COLUMN_UNIT_PRICE + " DOUBLE NOT NULL, "
                + COLUMN_UNIT + " TEXT NOT NULL, "
                + COLUMN_DESC + " TEXT, "
                + COLUMN_STOCK + " TEXT, "
                + COLUMN_INCREMENT + " TEXT, "
                + COLUMN_REWARDS + " TEXT, "
                + COLUMN_SID + " TEXT NOT NULL, "
                + COLUMN_BOOK_CLASS + " TEXT , "
                + COLUMN_SUBJECT + " TEXT  , "
                + COLUMN_LANGUAGE + " TEXT , "
                + COLUMN_TITLE + " TEXT "

                + ")";

        db.execSQL(exe);

    }

    public boolean setCart(HashMap<String, String> map, Float Qty) {
        db = getWritableDatabase();
        if (isInCart(map.get(COLUMN_CID))) {
            db.execSQL("update " + CART_TABLE + " set " + COLUMN_QTY + " = '" + Qty + "'," + COLUMN_PRICE + " = '" + map.get(COLUMN_PRICE) + "' where " + COLUMN_CID + "=" + map.get(COLUMN_CID));
            return false;
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CID, map.get(COLUMN_CID));
            values.put(COLUMN_ID, map.get(COLUMN_ID));
            values.put(COLUMN_QTY, Qty);
            values.put(COLUMN_CAT_ID, map.get(COLUMN_CAT_ID));
            values.put(COLUMN_IMAGE, map.get(COLUMN_IMAGE));
            values.put(COLUMN_NAME, map.get(COLUMN_NAME));
            values.put(COLUMN_PRICE, map.get(COLUMN_PRICE));
            values.put(COLUMN_MRP, map.get(COLUMN_MRP));
            values.put(COLUMN_UNIT_PRICE, map.get(COLUMN_UNIT_PRICE));
            values.put(COLUMN_UNIT, map.get(COLUMN_UNIT));
            values.put(COLUMN_STOCK, map.get(COLUMN_STOCK));
        //    values.put(COLUMN_INCREMENT, map.get(COLUMN_INCREMENT));
            values.put(COLUMN_REWARDS, map.get(COLUMN_REWARDS));
            values.put(COLUMN_TITLE, map.get(COLUMN_TITLE));
            values.put(COLUMN_SID, map.get(COLUMN_SID));
            values.put(COLUMN_DESC, map.get(COLUMN_DESC));
            values.put(COLUMN_BOOK_CLASS, map.get(COLUMN_BOOK_CLASS));
            values.put(COLUMN_SUBJECT, map.get(COLUMN_SUBJECT));
            values.put(COLUMN_LANGUAGE, map.get(COLUMN_LANGUAGE));


            db.insert(CART_TABLE, null, values);

            return true;
        }
    }

    public boolean updateCart(HashMap<String, String> map, Float Qty)
    {
        db = getWritableDatabase();
        if (isInCart(map.get(COLUMN_CID))) {
            db.execSQL("update " + CART_TABLE + " set " + COLUMN_QTY + " = '" + Qty + "'," + COLUMN_PRICE + " = '" + map.get(COLUMN_PRICE) + "' where " + COLUMN_CID + "=" + map.get(COLUMN_CID));
            return true;
        }
        return false;


    }

    public boolean isInCart(String id) {
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE + " where " + COLUMN_CID + " = " + id;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) return true;

        return false;
    }


    public String getCartItemQty(String id) {

        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE + " where " + COLUMN_CID + " = " + id;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COLUMN_QTY));

    }

    public String getInCartItemQty(String id) {
        if (isInCart(id)) {
            db = getReadableDatabase();
            String qry = "Select *  from " + CART_TABLE + " where " + COLUMN_CID + " = " + id;
            Cursor cursor = db.rawQuery(qry, null);
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(COLUMN_QTY));
        } else {
            return "0.0";
        }
    }

    public int getCartCount() {
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        return cursor.getCount();
    }

    public String getTotalAmount() {
        db = getReadableDatabase();
        String qry = "Select SUM(" + COLUMN_PRICE + ") as total_amount  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        String total = cursor.getString(cursor.getColumnIndex("total_amount"));
        if (total != null) {

            return total;
        } else {
            return "0";
        }
    }

    public String getTotalRewards() {
        db = getReadableDatabase();
        String qry = "Select SUM(" + COLUMN_REWARDS + ") as total_amount  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        String total = cursor.getString(cursor.getColumnIndex("total_amount"));
        if (total != null) {

            return total;
        } else {
            return "0";
        }
    }

    public String getTotalMRP() {
        db = getReadableDatabase();
        String qry = "Select SUM(" + COLUMN_MRP + ") as total_mrp  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        String total = cursor.getString(cursor.getColumnIndex("total_mrp"));
        if (total != null) {

            return total;
        } else {
            return "0";
        }
    }


    public ArrayList<HashMap<String, String>> getCartAll() {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(COLUMN_CID, cursor.getString(cursor.getColumnIndex(COLUMN_CID)));
            map.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            map.put(COLUMN_QTY, cursor.getString(cursor.getColumnIndex(COLUMN_QTY)));
            map.put(COLUMN_IMAGE, cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
            map.put(COLUMN_CAT_ID, cursor.getString(cursor.getColumnIndex(COLUMN_CAT_ID)));
            map.put(COLUMN_NAME, cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            map.put(COLUMN_PRICE, cursor.getString(cursor.getColumnIndex(COLUMN_PRICE)));
            map.put(COLUMN_MRP, cursor.getString(cursor.getColumnIndex(COLUMN_MRP)));
            map.put(COLUMN_UNIT_PRICE, cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_PRICE)));
            map.put(COLUMN_UNIT, cursor.getString(cursor.getColumnIndex(COLUMN_UNIT)));
            map.put(COLUMN_STOCK, cursor.getString(cursor.getColumnIndex(COLUMN_STOCK)));
            map.put(COLUMN_INCREMENT, cursor.getString(cursor.getColumnIndex(COLUMN_INCREMENT)));
            map.put(COLUMN_REWARDS, cursor.getString(cursor.getColumnIndex(COLUMN_REWARDS)));
            map.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            map.put(COLUMN_DESC, cursor.getString(cursor.getColumnIndex(COLUMN_DESC)));
            map.put(COLUMN_SID,cursor.getString(cursor.getColumnIndex( COLUMN_SID ) ));
            map.put(COLUMN_BOOK_CLASS,cursor.getString(cursor.getColumnIndex( COLUMN_BOOK_CLASS ) ));
            map.put(COLUMN_LANGUAGE,cursor.getString(cursor.getColumnIndex( COLUMN_LANGUAGE ) ));
            map.put(COLUMN_SUBJECT,cursor.getString(cursor.getColumnIndex( COLUMN_SUBJECT ) ));

            list.add(map);
            cursor.moveToNext();
        }
        return list;
    }

    public ArrayList<HashMap<String, String>> getCartProduct(int product_id) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE+ " where " + COLUMN_ID + " = " + product_id;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(COLUMN_CID, cursor.getString(cursor.getColumnIndex(COLUMN_CID)));
            map.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            map.put(COLUMN_QTY, cursor.getString(cursor.getColumnIndex(COLUMN_QTY)));
            map.put(COLUMN_IMAGE, cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
            map.put(COLUMN_CAT_ID, cursor.getString(cursor.getColumnIndex(COLUMN_CAT_ID)));
            map.put(COLUMN_NAME, cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            map.put(COLUMN_PRICE, cursor.getString(cursor.getColumnIndex(COLUMN_PRICE)));
            map.put(COLUMN_MRP, cursor.getString(cursor.getColumnIndex(COLUMN_MRP)));
            map.put(COLUMN_UNIT_PRICE, cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_PRICE)));
         //   map.put(COLUMN_UNIT_VALUE, cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_VALUE)));
            map.put(COLUMN_UNIT, cursor.getString(cursor.getColumnIndex(COLUMN_UNIT)));
            map.put(COLUMN_STOCK, cursor.getString(cursor.getColumnIndex(COLUMN_STOCK)));
            map.put(COLUMN_INCREMENT, cursor.getString(cursor.getColumnIndex(COLUMN_INCREMENT)));
            map.put(COLUMN_REWARDS, cursor.getString(cursor.getColumnIndex(COLUMN_REWARDS)));
            map.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            map.put(COLUMN_DESC, cursor.getString(cursor.getColumnIndex(COLUMN_DESC)));
            map.put(COLUMN_SID,cursor.getString(cursor.getColumnIndex( COLUMN_SID ) ));
            map.put(COLUMN_BOOK_CLASS,cursor.getString(cursor.getColumnIndex( COLUMN_BOOK_CLASS ) ));
            map.put(COLUMN_LANGUAGE,cursor.getString(cursor.getColumnIndex( COLUMN_LANGUAGE ) ));
            map.put(COLUMN_SUBJECT,cursor.getString(cursor.getColumnIndex( COLUMN_SUBJECT ) ));

            list.add(map);
            cursor.moveToNext();
        }
        return list;
    }

    public String getColumnRewards() {
        db = getReadableDatabase();
        String qry = "SELECT rewards FROM "+ CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        String reward = cursor.getString(cursor.getColumnIndex("rewards"));
        if (reward != null) {

            return reward;
        } else {
            return "0";
        }
    }

    public String getFavConcatString() {
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        String concate = "";
        for (int i = 0; i < cursor.getCount(); i++) {
            if (concate.equalsIgnoreCase("")) {
                concate = cursor.getString(cursor.getColumnIndex(COLUMN_CID));
            } else {
                concate = concate + "_" + cursor.getString(cursor.getColumnIndex(COLUMN_CID));
            }
            cursor.moveToNext();
        }
        return concate;
    }

    public void clearCart() {
        db = getReadableDatabase();
        db.execSQL("delete from " + CART_TABLE);
    }

    public void removeItemFromCart(String id) {
        db = getReadableDatabase();
        db.execSQL("delete from " + CART_TABLE + " where " + COLUMN_CID + " = " + id);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getUnitPrice(String id) {
        db = getReadableDatabase();
        String qry = "Select " + COLUMN_UNIT_PRICE + " from " + CART_TABLE + " where " + COLUMN_CID + " = " + id;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        String total = cursor.getString(cursor.getColumnIndex("unit_price"));
        if (total != null) {

            return total;
        } else {
            return "0";
        }
    }
}
