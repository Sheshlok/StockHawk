package com.sam_chordas.android.stockhawk.utilities;

/**
 * Created by sheshloksamal on 20/07/16.
 */

import android.database.Cursor;

/**
 * Created by sheshloksamal on 12/03/16.
 * @see  <a href="https://github.com/square/sqlbrite/blob/master/sample%2Fsrc%2Fmain%2Fjava%2Fcom%2Fexample%2Fsqlbrite%2Ftodo%2Fdb%2FDb.java">
 *     SqlBrite Sample db>Db.java</a>
 */
public final class DbUtils {

    public static final int BOOLEAN_FALSE = 0;
    public static final int BOOLEAN_TRUE = 1;

    public static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    public static boolean getBoolean(Cursor cursor, String columnName) {
        return getInt(cursor, columnName) == BOOLEAN_TRUE;
    }

    public static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    public static long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
    }

    public static double getDouble(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(columnName));
    }

    private DbUtils() {
        throw new AssertionError("No instances.");
    }
}

