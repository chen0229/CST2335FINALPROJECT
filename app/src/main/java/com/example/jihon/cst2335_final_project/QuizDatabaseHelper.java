package com.example.jihon.cst2335_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by jihon on 2018-03-31.
 */

public class QuizDatabaseHelper extends SQLiteOpenHelper {

    protected final String ACTIVITY_NAME = "QuizDatabaseHelper";
    public static final String DATABASE_NAME = "quiz.db";
    public static final String MULTIPLE_CHOICE_TABLE_NAME = "MultiChoiceQuestion";
    public static final String TRUEFALSE_TABLE_NAME = "TrueFalseQuestion";
    public static final String NUMERIC_TABLE_NAME = "NumericQuestion";
    public static final int VERSION_NUM = 1;
    public static final String MCKEY_ID = "mcid";
    public static final String MCQUESTION = "mcquestion";
    public static final String MCCHOICE1 = "mcchoice1";
    public static final String MCCHOICE2 = "mcchoice2";
    public static final String MCCHOICE3 = "mcchoice3";
    public static final String MCCHOICE4= "mcchoice4";
    public static final String MCCORRECT_ANSWER = "mcanswer";
    public static final String TFKEY_ID = "tfid";
    public static final String TFQUESTION = "tfquestion";
    public static final String TFCORRECT_ANSWER = "tfanswer";
    public static final String NUKEY_ID = "nuid";
    public static final String NUQUESTION = "nuquestion";
    public static final String NUCHOICE1 = "nuchoice1";
    public static final String NUCORRECT_ANSWER = "nuanswer";


    public QuizDatabaseHelper(Context ctx){

        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    //create question table if database doesn't exist yet, db is the database object
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable1 = "CREATE TABLE " + MULTIPLE_CHOICE_TABLE_NAME + "( " + MCKEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MCQUESTION + " TEXT, " + MCCHOICE1 + " TEXT, " + MCCHOICE2 + " TEXT, "+ MCCHOICE3 + " TEXT, "
                + MCCHOICE4 + " TEXT, " + MCCORRECT_ANSWER + " TEXT )";
        String createTable2 = "CREATE TABLE " + TRUEFALSE_TABLE_NAME + "( " + TFKEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TFQUESTION + " TEXT, " +  TFCORRECT_ANSWER + " TEXT )";
        String createTable3 = "CREATE TABLE " + NUMERIC_TABLE_NAME + "( " + NUKEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NUQUESTION + " TEXT, " + NUCHOICE1 + " TEXT, " +  NUCORRECT_ANSWER + " TEXT )";

        try {
            db.execSQL(createTable1);
            db.execSQL(createTable2);
            db.execSQL(createTable3);
        }catch(SQLException e){
            Log.e(ACTIVITY_NAME, e.getMessage());
        }
    }

    //handle upgrading the data, or drop the table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + MULTIPLE_CHOICE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TRUEFALSE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NUMERIC_TABLE_NAME);
        onCreate(db);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MULTIPLE_CHOICE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TRUEFALSE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NUMERIC_TABLE_NAME);
        onCreate(db);
    }
}


