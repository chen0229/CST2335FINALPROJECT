package com.example.jihon.cst2335_final_project;

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

public class TrueFalseActivity extends AppCompatActivity {

    protected final static String ACTIVITY_NAME = "TrueFalseActivity";
    ListView tfListView;
    Button mc_button;
    Button numeric_button;
    Button main_button;

    Button tfcreateButton;
    Intent intent;
    QuizDatabaseHelper quizDBHelper;
    SQLiteDatabase db;
    TrueFalseAdapter tfAdapter;
    ArrayList<String> tfList = new ArrayList<>();
    public static final int tfrequestCode = 30;
    ProgressBar progressBar;

    boolean frameLayoutExists;
    private static final String URLString = "http://torunski.ca/CST2335/QuizInstance.xml";
    //private String snackbarMes = "@string/quiz_snackbar";

    EditText questionfield;
    Button addButton;
    Button cancelButton;
    EditText answerfield;
    String question;
    String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_false);

        tfListView = findViewById(R.id.tfListView);
        quizDBHelper = new QuizDatabaseHelper(this);
        db = quizDBHelper.getWritableDatabase();
        tfAdapter = new TrueFalseAdapter(this);
        tfListView.setAdapter(tfAdapter);

        //use AsyncTask to retrive data
        Button parseButton = (Button)findViewById(R.id.tfparseButton);
        parseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar = findViewById(R.id.tfprogressBar);
                progressBar.setVisibility(View.VISIBLE);
                //use AsyncTask to retrive data
               LoadTrueFalseQuery populateList = new LoadTrueFalseQuery();
                populateList.execute(URLString);

            }
        });


        mc_button = (Button) findViewById(R.id.mc_tfbutton);
        numeric_button = (Button)findViewById(R.id.nu_tfbutton) ;
        main_button = (Button)findViewById(R.id.main_tfbutton);
        tfcreateButton = (Button) findViewById(R.id.tfcreateButton);

        //show the true/false questions in the database
        showQuestion();

        mc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(TrueFalseActivity.this, MultipleChoiceActivity.class);
                startActivity(intent);
            }
        });

        numeric_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(TrueFalseActivity.this, NumericActivity.class);
                startActivity(intent);
            }
        });

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(TrueFalseActivity.this, QuizCreator.class);
                startActivity(intent);
            }
        });

        tfcreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTrueFalseItem();
            }
        });

        //listView onclick
        frameLayoutExists = (findViewById(R.id.tf_FrameLayout) != null);

        tfListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = db.rawQuery("SELECT * FROM " + QuizDatabaseHelper.TRUEFALSE_TABLE_NAME + ";", null);
                if(cursor != null){
                    cursor.moveToFirst();
                }
                cursor.moveToPosition(position);

                Bundle bundle = new Bundle();
                bundle.putInt("tfid", cursor.getInt(cursor.getColumnIndex(QuizDatabaseHelper.TFKEY_ID)));
                bundle.putString("tfquestion", cursor.getString(cursor.getColumnIndex(QuizDatabaseHelper.TFQUESTION)));
                bundle.putString("tfanswer", cursor.getString(cursor.getColumnIndex(QuizDatabaseHelper.TFCORRECT_ANSWER)));

                bundle.putBoolean("isLandscape", frameLayoutExists);
                bundle.putInt("tfposition", position);

                if(!frameLayoutExists ){
                    Intent detailIntent = new Intent(TrueFalseActivity.this, TrueFalseDetail.class);
                    detailIntent.putExtras(bundle);
                    startActivityForResult(detailIntent, tfrequestCode);
                }
                else{
                    TrueFalseFragment tfFragment = new TrueFalseFragment();
                    tfFragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.tf_FrameLayout, tfFragment).commit();
                }
            }
        });
    }



    //adapter for quiz question
    protected class TrueFalseAdapter extends ArrayAdapter<String> {
        public TrueFalseAdapter(Context c){
            super(c, 0);
        }
        public int getCount() {
            return tfList.size();
        }

        public String getItem(int position) {
            return tfList.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = TrueFalseActivity.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.activity_quiz_list, null);
            TextView quiz_text = result.findViewById(R.id.quiz_text);
            quiz_text.setText(getItem(position));
            return result;
        }
    }

    public void addTrueFalseItem(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(TrueFalseActivity.this);
        final View logView = getLayoutInflater().inflate(R.layout.true_false_dialog, null);

        builder.setView(logView);
        final AlertDialog dialog = builder.create();

        questionfield = logView.findViewById(R.id.tfquestion);
        addButton = logView.findViewById(R.id.addTFButton);
        cancelButton = logView.findViewById(R.id.cancelTFButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(questionfield.getText().toString().length() < 1){
                    showValidationDialog();
                }
                else {
                    questionfield = logView.findViewById(R.id.tfquestion);
                    answerfield = logView.findViewById(R.id.tfAnswer);
                    question = questionfield.getText().toString();
                    answer = answerfield.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(QuizDatabaseHelper.TFQUESTION, question);
                    values.put(QuizDatabaseHelper.TFCORRECT_ANSWER, answer);

                    db.insert(QuizDatabaseHelper.TRUEFALSE_TABLE_NAME, null, values);

                    Cursor cursor = db.query(QuizDatabaseHelper.TRUEFALSE_TABLE_NAME,
                            new String[]{QuizDatabaseHelper.TFQUESTION}, null, null, null, null, null);
                    int colIndex = cursor.getColumnIndex(QuizDatabaseHelper.TFQUESTION);
                    Cursor c = db.rawQuery("SELECT * FROM " + QuizDatabaseHelper.TRUEFALSE_TABLE_NAME + ";", null);
                    int cnt = c.getCount();

                    if (cnt > 0) {
                        for (cursor.moveToLast(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            String value = cursor.getString(colIndex);
                            tfList.add(value);
                            tfAdapter.notifyDataSetChanged();
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

    public void editTrueFalseItem(int id, int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(TrueFalseActivity.this);
        final View logView = getLayoutInflater().inflate(R.layout.true_false_dialog, null);
        builder.setView(logView);
        final AlertDialog dialog = builder.create();

        final int  questionid = id;
        final int itemPosition = position;
        final EditText questionfield = logView.findViewById(R.id.tfquestion);
        Button addButton = logView.findViewById(R.id.addTFButton);
        Button cancelButton = logView.findViewById(R.id.cancelTFButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(questionfield.getText().toString().length() < 1){
                    showValidationDialog();
                }
                else{
                    EditText questionfield = logView.findViewById(R.id.tfquestion);
                    EditText answerfield = logView.findViewById(R.id.tfAnswer);
                    String question = questionfield.getText().toString();
                    String answer = answerfield.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(QuizDatabaseHelper.TFQUESTION, question);
                    values.put(QuizDatabaseHelper.TFCORRECT_ANSWER, answer );

                    String strId = "QUESTION_ID = " + questionid;
                    db.update(QuizDatabaseHelper.TRUEFALSE_TABLE_NAME, values, strId,null);

                    tfList.set(itemPosition, question);
                    tfAdapter.notifyDataSetChanged();

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
    public void deleteTrueFalseItem(int id, int position) {
        final int itemID = id;
        final int itemPosition = position;
        db.execSQL("DELETE FROM " + QuizDatabaseHelper.TRUEFALSE_TABLE_NAME + " WHERE " + QuizDatabaseHelper.TFKEY_ID + " = " + id + ";");
        tfList.remove(itemPosition);
       tfAdapter.notifyDataSetChanged();
    }

    public void showQuestion(){
        tfList.clear();
        Cursor cursor = db.rawQuery("SELECT * FROM " + QuizDatabaseHelper.TRUEFALSE_TABLE_NAME + ";", null);
        int cnt = cursor.getCount();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            int colIndex = cursor.getColumnIndex(QuizDatabaseHelper.TFQUESTION);
            String value = cursor.getString(colIndex);
            tfList.add(value);
            tfAdapter.notifyDataSetChanged();
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == tfrequestCode && resultCode == 35) {
            int returnValue = data.getIntExtra("id", 0);
            int positionValue = data.getIntExtra("position", 0);
            editTrueFalseItem(returnValue, positionValue);
        }

        if (requestCode == tfrequestCode && resultCode == 45) {
            int returnValue = data.getIntExtra("tfid", 0);
            int positionValue = data.getIntExtra("tfposition", 0);
            deleteTrueFalseItem(returnValue, positionValue);
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
     * Output upon entering a question without a name
     */
    public void showValidationDialog() {
        final android.app.AlertDialog validateDialog = new android.app.AlertDialog.Builder(TrueFalseActivity.this).create();
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

    /**
     * AsyncTask loading the items from the database
     */
    protected class LoadTrueFalseQuery extends AsyncTask<String, Integer, String> {

        //   ProgressBar progressBar = findViewById(R.id.progressBar);
        String getQuestion;
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


                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    String tagName = parser.getName();
                  //  int tagType = parser.getEventType();
             //       if(tagType == XmlPullParser.START_TAG ) {
                    if (tagName.equals("TrueFalseQuestion")) {
                        getQuestion = parser.getAttributeValue(null, "question");
                        publishProgress(35);
                        getAnswer = parser.getAttributeValue(null, "answer");
                        publishProgress(55);
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
            values.put(QuizDatabaseHelper.TFQUESTION, getQuestion);
            values.put(QuizDatabaseHelper.TFCORRECT_ANSWER, getAnswer);

            db.insert(QuizDatabaseHelper.TRUEFALSE_TABLE_NAME, null, values);

            progressBar.setVisibility(View.INVISIBLE);
           // showQuestion();

        }

    }
}
