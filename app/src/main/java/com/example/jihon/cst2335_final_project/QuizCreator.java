package com.example.jihon.cst2335_final_project;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class QuizCreator extends AppCompatActivity {

    protected final static String ACTIVITY_NAME = "QuizCreator";
    ListView quizListView;
    Button create_mc_button;
    Button create_tf_button;
    Button create_numeric_button;
    Intent intent;
    QuizDatabaseHelper quizDBHelper;
    SQLiteDatabase db;


    boolean isTablet;
    private static final String URLString = "http://torunski.ca/CST2335/QuizInstance.xml";
    //private String snackbarMes = "@string/quiz_snackbar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_creator);

        create_mc_button = (Button) findViewById(R.id.create_mc_button);
        create_tf_button = (Button) findViewById(R.id.create_tf_button);
        create_numeric_button = (Button) findViewById(R.id.create_numeric_button);


        Toolbar toolbar = (Toolbar) findViewById(R.id.quiz_toolbar);
        setSupportActionBar(toolbar);

        //start multiple choice questions
        create_mc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizCreator.this, MultipleChoiceActivity.class);
                startActivity(intent);
            }
        });

        //start true/flase questions
        create_tf_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizCreator.this, TrueFalseActivity.class);
                startActivity(intent);
            }
        });

        //start numeric questions
        create_numeric_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizCreator.this, NumericActivity.class);
                startActivity(intent);
            }
        });

    }


    public boolean onCreateOptionsMenu(Menu m) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.quiz_toolbar_menu, m);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem mi){
        String aboutMes = "@string/quiz_aboutMes";
        switch(mi.getItemId()){
            case R.id.activity_one : startActivity(1);
                break;
            case R.id.activity_two : startActivity(2);
                break;
            case R.id.activity_three : startActivity(3);
                break;
            case R.id.home: startActivity(4);
                break;
            case R.id.help :
            final android.app.AlertDialog aboutDialog = new android.app.AlertDialog.Builder(QuizCreator.this).create();
            aboutDialog.setTitle(R.string.quiz_title);
            aboutDialog.setMessage(getResources().getString(R.string.quiz_dialog_message));
            aboutDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            aboutDialog.dismiss();
                        }
                    });
            aboutDialog.show();
            break;

        }

        return true;
    }
    private void startActivity(int activity) {

        switch (activity) {
            case 1:
                intent = new Intent(QuizCreator.this, OCTranspo.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(QuizCreator.this, Movie.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(QuizCreator.this, PatientIntake.class);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(QuizCreator.this, Main2Activity.class);
                startActivity(intent);
                break;

        }
    }


}
