package com.example.jihon.cst2335_final_project;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by jihon on 2018-04-16.
 */

public class TrueFalseDetail extends Activity {
    protected final String ACTIVITY_NAME = "TrueFalseDetail";
    protected Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_quiz_detail);
        Bundle activityBundle = getIntent().getExtras();

        if (activityBundle != null) {
            Log.i(ACTIVITY_NAME, "R.string.content_detatail");
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
         TrueFalseFragment quizFragment = new TrueFalseFragment();
        quizFragment.setArguments(activityBundle);
        ft.replace(R.id.quiz_frameLayout, quizFragment);
        ft.commit();
    }
}
