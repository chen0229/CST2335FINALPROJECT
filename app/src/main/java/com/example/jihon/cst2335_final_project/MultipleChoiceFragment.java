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
public class MultipleChoiceFragment extends Fragment {

    private Activity myActivity;
    private EditText question_text;
    private EditText choice1_text;
    private EditText choice2_text;
    private EditText choice3_text;
    private EditText choice4_text;
    private EditText correctAnswer;
    private Button editButton;
    private Button closeButton;
    private Button deleteButton;

    private SQLiteDatabase writableDB;
    protected QuizDatabaseHelper quizDBHelper;
    protected QuizCreator quizCreator;
    Bundle bundle;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.multiple_choice_detail, container, false);
       // quizDBHelper = new QuizDatabaseHelper(getActivity());
        //writableDB = quizDBHelper.getWritableDatabase();

        final int id = getArguments().getInt("mcid");
        final String question = getArguments().getString("mcquestion");
        final String choice1 = getArguments().getString("mcchoice1");
        final String choice2 = getArguments().getString("mcchoice2");
        final String choice3 = getArguments().getString("mcchoice3");
        final String choice4 = getArguments().getString("mcchoice4");
        final String answer = getArguments().getString("mcanswer");

        final Boolean frameLayoutExists = getArguments().getBoolean("isLandscape");
        final int position = getArguments().getInt("mcposition");

        question_text = view.findViewById(R.id.mcquestion_text);
        choice1_text = view.findViewById(R.id.mcchoice1_text);
        choice2_text = view.findViewById(R.id.mcchoice2_text);
        choice3_text = view.findViewById(R.id.mcchoice3_text);
        choice4_text = view.findViewById(R.id.mcchoice4_text);
        correctAnswer = view.findViewById(R.id.mccorectAnswer);
        editButton = view.findViewById(R.id.editMCButton);
        closeButton = view.findViewById(R.id.closeMCButton);
        deleteButton = view.findViewById(R.id.deleteMCButton);

        question_text.setText(question);
        choice1_text.setText(choice1);
        choice2_text.setText(choice2);
        choice3_text.setText(choice3);
        choice4_text.setText(choice4);
        correctAnswer.setText(answer);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MultipleChoiceFragment.this.getActivity(), QuizCreator.class );
                startActivityForResult(intent, 0);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //landscape
                if(frameLayoutExists == true){

                    myActivity.getFragmentManager().beginTransaction().remove(MultipleChoiceFragment.this).commit();
                    ((MultipleChoiceActivity) myActivity).editMultiChoiceItem(id, position);
                }
                //portrait
                else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("mcid", id);
                    bundle.putInt("mcposition", position);
                    Intent resultIntent = new Intent().putExtras(bundle);
                    myActivity.setResult(10, resultIntent);
                    myActivity.finish();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //landscape
                if(frameLayoutExists == true){
                    myActivity.getFragmentManager().beginTransaction().remove(MultipleChoiceFragment.this).commit();
                    ((MultipleChoiceActivity) myActivity).deleteMultiChoiceItem(id, position);
                }
                //portrait
                else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("mcid", id);
                    bundle.putInt("mcposition", position);
                    Intent resultIntent = new Intent().putExtras(bundle);
                    myActivity.setResult(20, resultIntent);
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


