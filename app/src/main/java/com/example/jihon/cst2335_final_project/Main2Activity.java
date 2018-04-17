package com.example.jihon.cst2335_final_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    private Intent intent;

    private Button movieButton;
    private Button transpoButton;
    private Button quizButton;
    private Button patientButton;

    private final static String version = "VERSION 1.0";
    private final static String author = "Malcolm Roy";
    private final static String author2 = "Jihong Chen";
    private final static String author3 = "Huijuan Dai";
    private final static String author4 = "Chenxiao Cui";

    private String choice = "";

    private FloatingActionButton goButton;

    private TextView movieText;
    private TextView transpoText;
    private TextView quizText;
    private TextView patientText;

    private boolean transpoShowing;
    private boolean movieShowing;
    private boolean quizShowing;
    private boolean patientShowing;

    private RelativeLayout mainLayout;

    private final static String movieAppDesc = "Movie Information Selected \n\nView description," +
            " length, actors, ratings, genre and poster";
    private final static String transpoAppDesc = "OC Transpo Route Info selected\n\n View destination, latitude, longitude, " +
            "gps speed, start time and adjusted schedule time";
    private final static String patientAppDesc = "Patient Intake Form Selected\n\n Enter new patient" +
            " information for use in doctor office, dentist or optometrist";
    private final static String quizAppDesc = "Multiple Choice Quiz Creator Selected\n\nCreate various" +
            " questions types: multiple choice, numeric or true/false";
    private final static String helpInfo = "This app includes 4 seperate activities, choose 1 to see description, then hit the" +
            " launch button to start.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mainLayout = findViewById(R.id.mainLayout);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }catch(NullPointerException npointer){

        }

        movieText  = new TextView(getApplicationContext());
        transpoText = new TextView(getApplicationContext());
        quizText = new TextView(getApplicationContext());
        patientText = new TextView(getApplicationContext());

        transpoShowing = false;
        movieShowing = false;
        quizShowing = false;
        patientShowing = false;

        //setup buttons with text size and text color
        movieButton = findViewById(R.id.movieButton);
        movieButton.setTextSize(12);
        movieButton.setTextColor(Color.parseColor("#382559"));
        transpoButton = findViewById(R.id.transpoButton);
        transpoButton.setTextSize(12);
        transpoButton.setTextColor(Color.parseColor("#382559"));
        quizButton = findViewById(R.id.quizButton);
        quizButton.setTextSize(12);
        quizButton.setTextColor(Color.parseColor("#382559"));
        patientButton = findViewById(R.id.patientButton);
        patientButton.setTextSize(12);
        patientButton.setTextColor(Color.parseColor("#382559"));

        //main launch button
        goButton = findViewById(R.id.goButton);

        addMovieButtonFunctionality();
        addTranspoButtonFunctionality();
        addQuizButtonFunctionality();
        addPatientButtonFunctionality();

        addGoButtonFunctionality();

        goButton.hide();

    }

    private Dialog createHelpDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.help);
        builder.setMessage(helpInfo);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                // do nothing
            }
        });
        // Create the AlertDialog
        return builder.create();

    }

    private void addGoButtonFunctionality(){
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(choice){
                    case "transpo" : startActivity(1);
                        break;
                    case "movie" : startActivity(2);
                        break;
                    case "patient" : startActivity(3);
                        break;
                    case "quiz" : startActivity(4);
                        break;
                }
            }
        });
    }

    private void addPatientButtonFunctionality(){
        patientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choice.equals("patient") || patientShowing){
                    flipPatientButtonOff();
                }else{
                    flipPatientButtonOn();
                }
            }
        });
    }

    private void flipPatientButtonOff(){
        choice = "";
        goButton.hide();
        patientButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.blue_high_light8));
        patientShowing = false;
        patientButton.setText(null);
    }

    private void flipPatientButtonOn(){

        patientButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_inner));

        patientText.setText(patientAppDesc);

        patientButton.setText(patientText.getText());

        if (movieShowing){
            flipMovieButtonOff();
        }
        if (transpoShowing){
            flipTranspoButtonOff();
        }
        if (quizShowing){
            flipQuizButtonOff();
        }
        choice = "patient";
        patientShowing = true;
        goButton.show();
    }

    private void addQuizButtonFunctionality(){
        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choice.equals("quiz") || quizShowing){
                    flipQuizButtonOff();

                }else {
                    flipQuizButtonOn();

                }
            }
        });
    }

    private void flipQuizButtonOn(){

        quizButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_inner));

        quizText.setText(quizAppDesc);

        quizButton.setText(quizText.getText());

        if (movieShowing){
            flipMovieButtonOff();
        }
        if (transpoShowing){
            flipTranspoButtonOff();
        }
        if (patientShowing){
            flipPatientButtonOff();
        }
        choice = "quiz";
        quizShowing = true;
        goButton.show();
    }

    private void flipQuizButtonOff(){
        choice = "";
        goButton.hide();
        quizButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.blue_high_light3));
        quizShowing = false;
        quizButton.setText(null);
    }

    private void addMovieButtonFunctionality(){
        movieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (choice.equals("movie") || movieShowing){
                    flipMovieButtonOff();

                }else {
                    flipMovieButtonOn();

                }
            }
        });
    }

    private void flipMovieButtonOff(){
        choice = "";
        goButton.hide();
        movieButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.blue_high_light9));
        movieShowing = false;
        movieButton.setText(null);
    }

    private void flipMovieButtonOn(){

        movieButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_inner));

        movieText.setText(movieAppDesc);

        movieButton.setText(movieText.getText());

        if (transpoShowing){
            flipTranspoButtonOff();
        }
        if (quizShowing){
            flipQuizButtonOff();
        }
        if (patientShowing){
            flipPatientButtonOff();
        }
        choice = "movie";
        movieShowing = true;
        goButton.show();
    }

    private void addTranspoButtonFunctionality(){
        transpoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (choice.equals("transpo") || transpoShowing){
                    flipTranspoButtonOff();

                } else{
                    flipTranspoButtonOn();
                }
            }
        });
    }

    private void flipTranspoButtonOn(){

        transpoButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_inner));

        transpoText.setText(transpoAppDesc);

        transpoButton.setText(transpoText.getText());

        if (movieShowing){
            flipMovieButtonOff();
        }
        if (quizShowing){
            flipQuizButtonOff();
        }
        if (patientShowing){
            flipPatientButtonOff();
        }
        choice = "transpo";
        transpoShowing = true;
        goButton.show();
    }

    private void flipTranspoButtonOff(){
        choice = "";
        goButton.hide();
        transpoButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.blue_high_light2));
        transpoShowing = false;
        transpoButton.setText(null);
    }

    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.toolbar_menu, m);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem mi){

        switch(mi.getItemId()){
            case R.id.about : Snackbar.make(mainLayout, version+"  BY:  "+author+
                    ",\n "+author2+", "+author3+", "+author4, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
                break;
            case R.id.help : createHelpDialog().show();
                break;
            case R.id.activity_one : startActivity(1);
                break;
            case R.id.activity_two : startActivity(2);
                break;
            case R.id.activity_three : startActivity(3);
                break;
            case R.id.activity_four : startActivity(4);
                break;

        }

        return true;
    }

    private void startActivity(int activity){

        switch(activity){
            case 1 : intent = new Intent(Main2Activity.this, OCTranspo.class);
                startActivity(intent);
                break;
            case 2 : intent = new Intent(Main2Activity.this, Movie.class);
                startActivity(intent);
                break;
            case 3 : intent = new Intent(Main2Activity.this, PatientIntake.class);
                startActivity(intent);
                break;
            case 4 : intent = new Intent(Main2Activity.this, QuizCreator.class);
                startActivity(intent);
                break;

        }

    }

}
