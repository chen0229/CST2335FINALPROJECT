package com.example.jihon.cst2335_final_project;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MultipleChoiceActivity extends AppCompatActivity {

    protected final static String ACTIVITY_NAME = "MultipleChoiceActivity";
    ListView mcListView;
    Button tf_button;
    Button numeric_button;
    Button main_button;

    Button mccreateButton;
    Intent intent;
    QuizDatabaseHelper quizDBHelper;
    SQLiteDatabase db;
    MultipleChoiceAdapter mcAdapter;
    ArrayList<String> mcList = new ArrayList<>();
    public static final int mcrequestCode = 10;


    ProgressBar progressBar;
    boolean frameLayoutExists;
    private Fragment mcFragment;
    private static final String URLString = "http://torunski.ca/CST2335/QuizInstance.xml";

    /** Parent Layout used to display SnackBar and Alert notifications */
    private View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice);

        mcListView = findViewById(R.id.mcListView);
        quizDBHelper = new QuizDatabaseHelper(this);
        db = quizDBHelper.getWritableDatabase();
        mcAdapter = new MultipleChoiceAdapter(this);
        mcListView.setAdapter(mcAdapter);

        Button parseButton = (Button)findViewById(R.id.mcparseButton);
        parseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar = findViewById(R.id.mcprogressBar);
                progressBar.setVisibility(View.VISIBLE);
                //use AsyncTask to retrive data
                LoadMultiChoicesQuery  populateList = new LoadMultiChoicesQuery();
                populateList.execute(URLString);
            }
        });

        tf_button = (Button) findViewById(R.id.tf_mcbutton);
        numeric_button = (Button) findViewById(R.id.numeric_mcbutton);
        main_button = (Button) findViewById(R.id.main_mcbutton);
        mccreateButton = (Button) findViewById(R.id.mccreateButton);

        //show the multiple choice questions in the database
        showQuestion();

        tf_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MultipleChoiceActivity.this, TrueFalseActivity.class);
                startActivity(intent);
            }
        });

        numeric_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MultipleChoiceActivity.this, NumericActivity.class);
                startActivity(intent);
            }
        });

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MultipleChoiceActivity.this, QuizCreator.class);
                startActivity(intent);
            }
        });

        mccreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMultipleChoice();
            }
        });

        //listView onclick
        frameLayoutExists = (findViewById(R.id.mc_FrameLayout) != null);

        mcListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = db.rawQuery("SELECT * FROM " + QuizDatabaseHelper.MULTIPLE_CHOICE_TABLE_NAME + ";", null);
                if (cursor != null) {
                    cursor.moveToFirst();
                }
                cursor.moveToPosition(position);

                Bundle bundle = new Bundle();
                bundle.putInt("mcid", cursor.getInt(cursor.getColumnIndex(QuizDatabaseHelper.MCKEY_ID)));
                bundle.putString("mcquestion", cursor.getString(cursor.getColumnIndex(QuizDatabaseHelper.MCQUESTION)));
                bundle.putString("mcchoice1", cursor.getString(cursor.getColumnIndex(QuizDatabaseHelper.MCCHOICE1)));
                bundle.putString("mcchoice2", cursor.getString(cursor.getColumnIndex(QuizDatabaseHelper.MCCHOICE2)));
                bundle.putString("mcchoice3", cursor.getString(cursor.getColumnIndex(QuizDatabaseHelper.MCCHOICE3)));
                bundle.putString("mcchoice4", cursor.getString(cursor.getColumnIndex(QuizDatabaseHelper.MCCHOICE4)));
                bundle.putString("mcanswer", cursor.getString(cursor.getColumnIndex(QuizDatabaseHelper.MCCORRECT_ANSWER)));


                bundle.putBoolean("isLandscape", frameLayoutExists);
                bundle.putInt("mcposition", position);

                if (!frameLayoutExists) {
                    Intent detailIntent = new Intent(MultipleChoiceActivity.this, MultipleChoiceDetail.class);
                    detailIntent.putExtras(bundle);
                    startActivityForResult(detailIntent, mcrequestCode);
                } else {
                    mcFragment = new MultipleChoiceFragment();
                    mcFragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.mc_FrameLayout, mcFragment).commit();
                }
            }
        });
    }


    //adapter for quiz question
    protected class MultipleChoiceAdapter extends ArrayAdapter<String> {
        public MultipleChoiceAdapter(Context c) {
            super(c, 0);
        }

        public int getCount() {
            return mcList.size();
        }

        public String getItem(int position) {
            return mcList.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = MultipleChoiceActivity.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.activity_quiz_list, null);
            TextView quiz_text = result.findViewById(R.id.quiz_text);
            quiz_text.setText(getItem(position));
            return result;
        }
    }

    public void addMultipleChoice() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MultipleChoiceActivity.this);
        final View logView = getLayoutInflater().inflate(R.layout.multiple_choice_dialog, null);

        builder.setView(logView);
        final AlertDialog dialog = builder.create();

        final EditText questionfield = logView.findViewById(R.id.mcQuestion);
        Button cancelButton = logView.findViewById(R.id.cancelMCButton);
        Button addButton = logView.findViewById(R.id.addMCButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionfield.getText().toString().length() < 1) {
                    showValidationDialog();
                } else {
                    EditText questionfield = logView.findViewById(R.id.mcQuestion);
                    EditText choice1field = logView.findViewById(R.id.mcChoice1);
                    EditText choice2field = logView.findViewById(R.id.mcChoice2);
                    EditText choice3field = logView.findViewById(R.id.mcChoice3);
                    EditText choice4field = logView.findViewById(R.id.mcChoice4);
                    EditText answerfield = logView.findViewById(R.id.mcAnswer);

                    String question = questionfield.getText().toString();
                    String choice1 = choice1field.getText().toString();
                    String choice2 = choice2field.getText().toString();
                    String choice3 = choice3field.getText().toString();
                    String choice4 = choice4field.getText().toString();
                    String answer = answerfield.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(QuizDatabaseHelper.MCQUESTION, question);
                    values.put(QuizDatabaseHelper.MCCHOICE1, choice1);
                    values.put(QuizDatabaseHelper.MCCHOICE2, choice2);
                    values.put(QuizDatabaseHelper.MCCHOICE3, choice3);
                    values.put(QuizDatabaseHelper.MCCHOICE4, choice4);
                    values.put(QuizDatabaseHelper.MCCORRECT_ANSWER, answer);

                    db.insert(QuizDatabaseHelper.MULTIPLE_CHOICE_TABLE_NAME, null, values);

                    Cursor cursor = db.query(QuizDatabaseHelper.MULTIPLE_CHOICE_TABLE_NAME,
                            new String[]{QuizDatabaseHelper.MCQUESTION}, null, null, null, null, null);
                    int colIndex = cursor.getColumnIndex(QuizDatabaseHelper.MCQUESTION);

                    Cursor c = db.rawQuery("SELECT * FROM " + QuizDatabaseHelper.MULTIPLE_CHOICE_TABLE_NAME + ";", null);
                    int cnt = c.getCount();
                    if (cnt > 0) {
                        for (cursor.moveToLast(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            String value = cursor.getString(colIndex);
                            mcList.add(value);
                            mcAdapter.notifyDataSetChanged();
                        }
                        c.close();
                    }
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.addQuestionMessage,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void showQuestion(){
        mcList.clear();
        Cursor cursor = db.rawQuery("SELECT * FROM " + QuizDatabaseHelper.MULTIPLE_CHOICE_TABLE_NAME + ";", null);
        int cnt = cursor.getCount();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            int colIndex = cursor.getColumnIndex(QuizDatabaseHelper.MCQUESTION);
            String value = cursor.getString(colIndex);
            mcList.add(value);
            mcAdapter.notifyDataSetChanged();
            cursor.moveToNext();
        }
        cursor.close();
    }


    public void editMultiChoiceItem(int id, int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MultipleChoiceActivity.this);
        final View logView = getLayoutInflater().inflate(R.layout.multiple_choice_dialog, null);
        builder.setView(logView);
        final AlertDialog dialog = builder.create();

        final int  questionid = id;
        final int itemPosition = position;
        final EditText questionfield = logView.findViewById(R.id.mcQuestion);
        Button addButton = logView.findViewById(R.id.addMCButton);
        Button cancelButton = logView.findViewById(R.id.cancelMCButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(questionfield.getText().toString().length() < 1){
                    showValidationDialog();
                }
                else{
                    EditText questionfield = logView.findViewById(R.id.mcQuestion);
                    EditText choice1field = logView.findViewById(R.id.mcChoice1);
                    EditText choice2field = logView.findViewById(R.id.mcChoice2);
                    EditText choice3field = logView.findViewById(R.id.mcChoice3);
                    EditText choice4field = logView.findViewById(R.id.mcChoice4);
                    EditText answerfield = logView.findViewById(R.id.mcAnswer);

                    String question = questionfield.getText().toString();
                    String choice1 = choice1field.getText().toString();
                    String choice2 = choice2field.getText().toString();
                    String choice3 = choice3field.getText().toString();
                    String choice4 = choice4field.getText().toString();
                    String answer = answerfield.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(QuizDatabaseHelper.MCQUESTION, question);
                    values.put(QuizDatabaseHelper.MCCHOICE1, choice1);
                    values.put(QuizDatabaseHelper.MCCHOICE2, choice2);
                    values.put(QuizDatabaseHelper.MCCHOICE3, choice3);
                    values.put(QuizDatabaseHelper.MCCHOICE4, choice4);
                    values.put(QuizDatabaseHelper.MCCORRECT_ANSWER, answer );

                    String strId = "QUESTION_ID = " + questionid;
                    db.update(QuizDatabaseHelper.MULTIPLE_CHOICE_TABLE_NAME, values, strId,null);

                    mcList.set(itemPosition, question);
                    mcAdapter.notifyDataSetChanged();

                    Toast.makeText(getApplicationContext(), R.string.editQuestionMessage,
                            Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    public void deleteMultiChoiceItem(int id, int position) {
        final int itemID = id;
        final int itemPosition = position;
        db.execSQL("DELETE FROM " + QuizDatabaseHelper.MULTIPLE_CHOICE_TABLE_NAME + " WHERE " + QuizDatabaseHelper.MCKEY_ID + " = " + id + ";");
        mcList.remove(itemPosition);
        mcAdapter.notifyDataSetChanged();

       /// Snackbar.make(parentLayout,
       //         getResources().getString(R.string.quiz_snackbar),
         //       Snackbar.LENGTH_LONG).show();
    }

    /**
     * Output upon entering a question without a name
     */
    public void showValidationDialog() {
        final android.app.AlertDialog validateDialog = new android.app.AlertDialog.Builder(MultipleChoiceActivity.this).create();
        validateDialog.setTitle(R.string.validateDialogTitle);
        validateDialog.setMessage(getResources().getString(R.string.validateMessage));
        validateDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        validateDialog.dismiss();
                    }
                });
        validateDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == mcrequestCode && resultCode == 10) {
            int returnValue = data.getIntExtra("mcid", 0);
            int positionValue = data.getIntExtra("mcposition", 0);
            editMultiChoiceItem(returnValue, positionValue);
        }

        if (requestCode == mcrequestCode && resultCode == 20) {
            int returnValue = data.getIntExtra("mcid", 0);
            int positionValue = data.getIntExtra("mcposition", 0);
            deleteMultiChoiceItem(returnValue, positionValue);
        }
    }

    /**
     * Closes database and activity
     */
    public void onDestroy() {
        db.close();
        super.onDestroy();
        //Log.i(ACTIVITY_NAME, "In onDestroy()");
    }
    /**
     * AsyncTask loading the items from an http server
     */
    protected class LoadMultiChoicesQuery extends AsyncTask<String, Integer, String> {

     //   ProgressBar progressBar = findViewById(R.id.progressBar);
        String getQuestion;
        String getChoice1;
        String getChoice2;
        String getChoice3;
        String getChoice4;
        String getAnswer;
        ArrayList<String> answers = new ArrayList<>();

        @Override
        protected String doInBackground(String... urls) {
            Log.i(ACTIVITY_NAME, " In doInBackGround");
            progressBar.setProgress(25);
            try {
                //set up connection
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();

                //instantiate the parser
                XmlPullParser parser = Xml.newPullParser();
                //the inputStream object is the one we get from the HttpURLConnection
                InputStream stream = conn.getInputStream();
                parser.setInput(stream, null);


                boolean isMultipleChoiceQuestion = false;
                boolean isAnswer = false;

                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    switch (parser.getEventType()) {
                        case XmlPullParser.START_TAG:
                            String tagName = parser.getName();
                            if (tagName.equals("MultipleChoiceQuestion")) {
                                isMultipleChoiceQuestion = true;
                                getQuestion = parser.getAttributeValue(null, "question");
                                publishProgress(35);
                                getAnswer = parser.getAttributeValue(null, "correct");
                                publishProgress(45);
                            }
                            if (tagName.equals("Answer"))
                                isAnswer = true;
                            break;
                        case XmlPullParser.TEXT:
                            String text = parser.getText();
                            if (isAnswer)
                                answers.add(text);
                            publishProgress(65);
                            break;
                        case XmlPullParser.END_TAG:
                            String tagName2 = parser.getName();
                            if (tagName2.equals("MultipleChoiceQuestion"))
                                isMultipleChoiceQuestion = false;
                            if (tagName2.equals("Answer"))
                                isAnswer = false;
                    }
                }

                //call publishProgress() to show that progress is completed
                publishProgress(100);

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... value) {
            Log.i(ACTIVITY_NAME, "in onProgressUpdate");

            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);

        }

        @Override
        protected void onPostExecute(String result) {

            ContentValues values = new ContentValues();
            values.put(QuizDatabaseHelper.MCQUESTION, getQuestion);
            values.put(QuizDatabaseHelper.MCCHOICE1, answers.get(0));
            values.put(QuizDatabaseHelper.MCCHOICE2, answers.get(1));
            values.put(QuizDatabaseHelper.MCCHOICE3, answers.get(2));
            values.put(QuizDatabaseHelper.MCCHOICE4, answers.get(3));
            values.put(QuizDatabaseHelper.MCCORRECT_ANSWER, getAnswer);

            db.insert(QuizDatabaseHelper.MULTIPLE_CHOICE_TABLE_NAME, null, values);

            progressBar.setVisibility(View.INVISIBLE);
            showQuestion();
        }
    }



}
