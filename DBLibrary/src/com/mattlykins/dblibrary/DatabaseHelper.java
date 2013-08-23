package com.mattlykins.dblibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String PACKAGENAME = "com.mattlykins.dblibrary";

    private static String DB_NAME = "ConversionFactors";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     * 
     * @param context
     */
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDatabase();

        if (dbExist) {
            // do nothing - database already exist
        }
        else {

            // By calling this method an empty database will be created into the
            // default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            this.getWritableDatabase();

            try {

                copyDataBase();

            }
            catch (IOException e) {

                Log.d("EXCEPTION", "" + getMethodName(2));
                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     * 
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDatabase() {

        File file = myContext.getDatabasePath(DB_NAME);
        
        return file.exists();
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = myContext.getDatabasePath(DB_NAME).getPath();

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        // Open the database
        String myPath = myContext.getDatabasePath(DB_NAME).getPath();
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
    
    public void Delete_ByID(String tableName,int id)
    {
        myDataBase.delete(tableName, "_id=" + id, null);
    }
    
    public void Update_ByID(String tableName, int ID, String[] colNames, String[] data)
    {
        ContentValues values = new ContentValues();
        for( int i = 0; i < colNames.length; i++)
        {
           values.put(colNames[i], data[i]);
           Log.d("FERRET",ID+ " " + colNames[i] + " " + data[i]);
        }
        
        myDataBase.update(tableName, values, "_id=" + ID, null);
    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    
    public String getSymbolFromID(final String[] tempID)
    {
        Cursor c = myDataBase.rawQuery("SELECT SYMBOL FROM UNITS WHERE ID='?'", tempID);
        if(c != null){
            return c.getString(0);
        }
        else
        {
            return null;
        }
        
    }    

    public Cursor getAllRows(String tableName) {        
        String selectQuery = "SELECT * FROM " + tableName;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        return cursor;
    }
    
    public Cursor sqlQuery(String query){        
        Cursor cursor = myDataBase.rawQuery(query,null);
        return cursor;
    }

    public static String getMethodName(final int depth) {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[1 + depth].getMethodName();
    }
    


}
