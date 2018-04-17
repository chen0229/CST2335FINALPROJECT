package com.example.jihon.cst2335_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class NumericActivity extends AppCompatActivity {

    protected final static String ACTIVITY_NAME = "NumericActivity";
    ListView numericListView;
    Button mc_button;
    Button tf_button;
    Button main_button;

    Button nucreateButton;
    Intent intent;
    QuizDatabaseHelper quizDBHelper;
    SQLiteDatabase db;
    NumericAdapter numericAdapter;
    ArrayList<String> numericList = new ArrayList<>();
    public static final int numericRequestCode = 50;
    ProgressBar progressBar;

    boolean frameLayoutExists;
    private static final String URLString = "http://torunski.ca/CST2335/QuizInstance.xml";
    //private String snackbarMes = "@string/quiz_snackbar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numeric);

        numericListView = findViewById(R.id.numericListView);
        quizDBHelper = new QuizDatabaseHelper(this);
        db = quizDBHelper.getWritableDatabase();
        numericAdapter = new NumericAdapter(this);
        numericListView.setAdapter(numericAdapter);

        Button parseButton = (Button)findViewById(R.id.nuparseButton);
        parseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar = findViewById(R.id.nuprogressBar);
                progressBar.setVisibility(View.VISIBLE);
                //use AsyncTask to retrive data
               LoadNumericQuery populateList = new LoadNumericQuery();
                populateList.execute(URLString);

            }
        });

        //show the numeric questions in the database
        showQuestion();

        tf_button = (Button) findViewById(R.id.tf_nubutton);
        mc_button = (Button)findViewById(R.id.mc_nubutton) ;
        main_button = (Button)findViewById(R.id.main_nubutton);
        nucreateButton = (Button) findViewById(R.id.nucreateButton);

        tf_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(NumericActivity.this, TrueFalseActivity.class);
                startActivity(intent);
            }
        });

        mc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(NumericActivity.this, MultipleChoiceActivity.class);
                startActivity(intent);
            }
        });

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(NumericActivity.this, QuizCreator.class);
                startActivity(intent);
            }
        });

        nucreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumericItem();
            }
        });

        //listView onclick
        frameLayoutExists = (findViewById(R.id.numeric_FrameLayout) != null);

        numericListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = db.rawQuery("SELECT * FROM " + QuizDatabaseHelper.NUMERIC_TABLE_NAME + ";", null);
                if(cursor != null){
                    cursor.moveToFirst();
                }
                cursor.moveToPosition(position);

                Bundle bundle = new Bundle();
                bundle.putInt("nuid", cursor.getInt(cursor.getColumnIndex(QuizDatabaseHelper.NUKEY_ID)));
                bundle.putString("nuquestion", cursor.getString(cursor.getColumnIndex(QuizDatabaseHelper.NUQUESTION)));
                bundle.putString("nuchoice1", cursor.getString(cursor.getColumnIndex(QuizDatabaseHelper.NUCHOICE1)));
                bundle.putString("nuanswer", cursor.getString(cursor.getColumnIndex(QuizDatabaseHelper.NUCORRECT_ANSWER)));

                bundle.putBoolean("isLandscape", frameLayoutExists);
                bundle.putInt("nuposition", position);

                if(!frameLayoutExists ){
                    Intent detailIntent = new Intent(NumericActivity.this, NumericDetail.class);
                    detailIntent.putExtras(bundle);
                    startActivityForResult(detailIntent, numericRequestCode);
                }
                else{
                    NumericFragment numericFragment = new NumericFragment();
                    numericFragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.numeric_FrameLayout, numericFragment).commit();
                }
            }
        });
    }



    //adapter for quiz question
    protected class NumericAdapter extends ArrayAdapter<String> {
        public NumericAdapter(Context c){
            super(c, 0);
        }
        public int getCount() {
            return numericList.size();
        }

        public String getItem(int position) {
            return numericList.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = NumericActivity.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.activity_quiz_list, null);
            TextView quiz_text = result.findViewById(R.id.quiz_text);
            quiz_text.setText(getItem(position));
            return result;
        }
    }

    public void addNumericItem(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(NumericActivity.this);
        final View logView = getLayoutInflater().inflate(R.layout.numeric_dialog, null);

        builder.setView(logView);
        final AlertDialog dialog = builder.create();

        final EditText questionfield = logView.findViewById(R.id.numQuestion);
        Button addButton = logView.findViewById(R.id.addNUButton);
        Button cancelButton = logView.findViewById(R.id.cancelNUButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(questionfield.getText().toString().length() < 1){
                    showValidationDialog();
                }
                else{
                    EditText questionfield = logView.findViewById(R.id.numQuestion);
                    EditText choice1field = logView.findViewById(R.id.numchoice1);
                    EditText answerfield = logView.findViewById(R.id.num_answer);

                    String question = questionfield.getText().toString();
                    String choice1 = choice1field.getText().toString();
                    String answer = answerfield.getText().toString();



                    ContentValues values = new ContentValues();
                    values.put(QuizDatabaseHelper.NUQUESTION, question);
                    values.put(QuizDatabaseHelper.NUCHOICE1, choice1);
                    values.put(QuizDatabaseHelper.NUCORRECT_ANSWER, answer );

                    db.insert(QuizDatabaseHelper.NUMERIC_TABLE_NAME, null, values);

                    numericList.add("test");
                    numericAdapter.notifyDataSetChanged();

//                    Cursor cursor = db.query(QuizDatabaseHelper.NUMERIC_TABLE_NAME,
//                            new String[]{QuizDatabaseHelper.NUQUESTION}, null, null, null, null, null);
//                    int colIndex = cursor.getColumnIndex(QuizDatabaseHelper.NUQUESTION);
//
//                    Cursor c = db.rawQuery("SELECT * FROM " + QuizDatabaseHelper.NUMERIC_TABLE_NAME + ";", null);
//                    int cnt = c.getCount();
//                    if(cnt > 0){
//                        for(cursor.moveToLast(); !cursor.isAfterLast(); cursor.moveToNext()){
//                            String value = cursor.getString(colIndex);
//                            Log.i("VALUES!", value);
//                           numericList.add(value);
//                           numericAdapter.notifyDataSetChanged();
//                        }
//
//                    }
//                    c.close();
                    dialog.dismiss();
                    //showQuestion();
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

    public void editNumericItem(int id, int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(NumericActivity.this);
        final View logView = getLayoutInflater().inflate(R.layout.numeric_dialog, null);
        builder.setView(logView);
        final AlertDialog dialog = builder.create();

        final int  questionid = id;
        final int itemPosition = position;
        final EditText questionfield = logView.findViewById(R.id.numQuestion);
        Button addButton = logView.findViewById(R.id.addNUButton);
        Button cancelButton = logView.findViewById(R.id.cancelNUButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(questionfield.getText().toString().length() < 1){
                    showValidationDialog();
                }
                else{
                    EditText questionfield = logView.findViewById(R.id.numQuestion);
                    EditText choice1field = logView.findViewById(R.id.numchoice1);
                    EditText answerfield = logView.findViewById(R.id.num_answer);

                    String question = questionfield.getText().toString();
                    String choice1 = choice1field.getText().toString();
                    String answer = answerfield.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(QuizDatabaseHelper.NUQUESTION, question);
                    values.put(QuizDatabaseHelper.NUCHOICE1, choice1);
                    values.put(QuizDatabaseHelper.NUCORRECT_ANSWER, answer );

                    String strId = "QUESTION_ID = " + questionid;
                    db.update(QuizDatabaseHelper.NUMERIC_TABLE_NAME, values, strId,null);

                    numericList.set(itemPosition, question);
                    numericAdapter.notifyDataSetChanged();

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
    public void deleteNumericItem(int id, int position) {
        final int itemID = id;
        final int itemPosition = position;
        db.execSQL("DELETE FROM " + QuizDatabaseHelper.NUMERIC_TABLE_NAME + " WHERE " + QuizDatabaseHelper.NUKEY_ID + " = " + id + ";");
        numericList.remove(itemPosition);
        numericAdapter.notifyDataSetChanged();
    }
    /**
     * Output upon entering a question without a name
     */
    public void showValidationDialog() {
        final android.app.AlertDialog validateDialog = new android.app.AlertDialog.Builder(NumericActivity.this).create();
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

    public void showQuestion(){
       //numericList.clear();
        Cursor cursor = db.rawQuery("SELECT * FROM " + QuizDatabaseHelper.NUMERIC_TABLE_NAME + ";", null);
        int cnt = cursor.getCount();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            int colIndex = cursor.getColumnIndex(QuizDatabaseHelper.NUQUESTION);
            String value = cursor.getString(colIndex);
           numericList.add(value);
            numericAdapter.notifyDataSetChanged();
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == numericRequestCode && resultCode == 15) {
            int returnValue = data.getIntExtra("nuid", 0);
            int positionValue = data.getIntExtra("nuposition", 0);
            editNumericItem(returnValue, positionValue);
        }

        if (requestCode == numericRequestCode && resultCode == 25) {
            int returnValue = data.getIntExtra("nuid", 0);
            int positionValue = data.getIntExtra("nuposition", 0);
            deleteNumericItem(returnValue, positionValue);
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
     * AsyncTask loading the items from the database
     */
    protected class LoadNumericQuery extends AsyncTask<String, Integer, String> {

        //   ProgressBar progressBar = findViewById(R.id.progressBar);
        String getQuestion;
        String getChoice1;
        String getAnswer;

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

                while (parser.next() != XmlPullParser.END_DOCUMENT) {

                    String tagName = parser.getName();
                   // int tagType = parser.getEventType();
                   // if(tagType == XmlPullParser.START_TAG ) {
                        if (tagName.equals("NumericQuestion")) {
                            getQuestion = parser.getAttributeValue(null, "question");
                            publishProgress(35);
                            getAnswer = parser.getAttributeValue(null, "accurancy");
                            publishProgress(55);
                            getChoice1 = parser.getAttributeValue(null, "answer");
                            publishProgress(75);
                        }
                    }
                    //call publishProgress() to show that progress is completed
                    publishProgress(100);

            } catch (Exception e) {
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
            values.put(QuizDatabaseHelper.NUQUESTION, getQuestion);
            values.put(QuizDatabaseHelper.NUCHOICE1, getChoice1);
            values.put(QuizDatabaseHelper.NUCORRECT_ANSWER, getAnswer);

            db.insert(QuizDatabaseHelper.NUMERIC_TABLE_NAME, null, values);

            progressBar.setVisibility(View.INVISIBLE);
          //  showQuestion();
        }
    }

}
