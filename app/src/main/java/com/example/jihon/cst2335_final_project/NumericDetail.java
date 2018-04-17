package com.example.jihon.cst2335_final_project;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by jihon on 2018-04-16.
 */

public class NumericDetail extends Activity {

    protected final String ACTIVITY_NAME = "NumericDetail";
    protected Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_quiz_detail);
        Bundle activityBundle = getIntent().getExtras();

        if (activityBundle != null) {
            Log.i(ACTIVITY_NAME, "R.string.content_detatail");
        }
        Bundle info = getIntent().getExtras();
        NumericFragment quizFragment = new NumericFragment();
        quizFragment.setArguments(info);
        getFragmentManager().beginTransaction()
                .replace(R.id.quiz_frameLayout, quizFragment).commit();

    }
}
