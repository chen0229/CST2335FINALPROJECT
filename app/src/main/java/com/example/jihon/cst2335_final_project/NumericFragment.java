package com.example.jihon.cst2335_final_project;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/*
 * Use the {@link MultipleChoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NumericFragment extends Fragment {

    private Activity myActivity;
    private View view;
    private EditText question_text;
    private EditText choice1_text;
    private EditText correctAnswer;
    private Button editButton;
    private Button closeButton;
    private Button deleteButton;

    private SQLiteDatabase writableDB;
    protected QuizDatabaseHelper quizDBHelper;
    protected QuizCreator quizCreator;
    Bundle bundle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bundle = this.getArguments();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.numeric_detail, container, false);
        quizDBHelper = new QuizDatabaseHelper(getActivity());
        final int id = getArguments().getInt("nuid");
        final String question = bundle.getString("nuquestion");
        final String choice1 = bundle.getString("nuchoice1");
        final String answer = bundle.getString("nuanswer");

        final Boolean frameLayoutExists = getArguments().getBoolean("isLandscape");
        final int position = bundle.getInt("nuposition");

        question_text = view.findViewById(R.id.numquestion_text);
        choice1_text = view.findViewById(R.id.numchoice1_text);
        correctAnswer = view.findViewById(R.id.numcorectAnswer);
        editButton = view.findViewById(R.id.editNUButton);
        closeButton = view.findViewById(R.id.closeNUButton);
        deleteButton = view.findViewById(R.id.deleteNUButton);

        question_text.setText(question);
        choice1_text.setText(choice1);
        correctAnswer.setText(answer);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NumericFragment.this.getActivity(), QuizCreator.class );
                startActivityForResult(intent, 0);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //landscape
                if(frameLayoutExists == true){

                    myActivity.getFragmentManager().beginTransaction().remove(NumericFragment.this).commit();
                    ((NumericActivity) myActivity).editNumericItem(id, position);
                }
                //portrait
                else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("nuid", id);
                    bundle.putInt("nuposition", position);
                    Intent resultIntent = new Intent().putExtras(bundle);
                    myActivity.setResult(15, resultIntent);
                    myActivity.finish();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //landscape
                if(frameLayoutExists){
                    myActivity.getFragmentManager().beginTransaction().remove(NumericFragment.this).commit();
                    ((NumericActivity) myActivity).deleteNumericItem(id, position);
                }
                //portrait
                else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("nuid", id);
                    bundle.putInt("nuposition", position);
                    Intent resultIntent = new Intent().putExtras(bundle);
                    myActivity.setResult(25, resultIntent);
                    myActivity.finish();
                }
            }
        });

        return view;
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.myActivity = activity;
    }
}


