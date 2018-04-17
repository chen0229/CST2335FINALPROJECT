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
public class TrueFalseFragment extends Fragment {

    private Activity myActivity;
    private View view;
    private EditText question_text;
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
        view = inflater.inflate(R.layout.true_false_detail, container, false);
        quizDBHelper = new QuizDatabaseHelper(getActivity());
        final int id = getArguments().getInt("tfid");
        final String question = bundle.getString("tfquestion");
        final String answer = bundle.getString("tfanswer");

        final Boolean frameLayoutExists = getArguments().getBoolean("isLandscape");
        final int position = bundle.getInt("tfposition");

        question_text = view.findViewById(R.id.tfquestion_text);
        correctAnswer = view.findViewById(R.id.tfcorectAnswer);
        editButton = view.findViewById(R.id.edittfButton);
        closeButton = view.findViewById(R.id.closetfButton);
        deleteButton = view.findViewById(R.id.deletetfButton);

        question_text.setText(question);
        correctAnswer.setText(answer);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrueFalseFragment.this.getActivity(), QuizCreator.class );
                startActivityForResult(intent, 0);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //landscape
                if(frameLayoutExists == true){

                    myActivity.getFragmentManager().beginTransaction().remove(TrueFalseFragment.this).commit();
                    ((TrueFalseActivity) myActivity).editTrueFalseItem(id, position);
                }
                //portrait
                else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("tfid", id);
                    bundle.putInt("tfposition", position);
                    Intent resultIntent = new Intent().putExtras(bundle);
                    myActivity.setResult(35, resultIntent);
                    myActivity.finish();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //landscape
                if(frameLayoutExists){
                    myActivity.getFragmentManager().beginTransaction().remove(TrueFalseFragment.this).commit();
                    ((TrueFalseActivity) myActivity).deleteTrueFalseItem(id, position);
                }
                //portrait
                else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("tfid", id);
                    bundle.putInt("tfposition", position);
                    Intent resultIntent = new Intent().putExtras(bundle);
                    myActivity.setResult(45, resultIntent);
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


