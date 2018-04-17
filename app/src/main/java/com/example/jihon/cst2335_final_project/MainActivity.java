package com.example.jihon.cst2335_final_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

    private String items[] = new String[]{"Multiple Choice Quiz Creator", "Patient Intake Form",
            "Movie Information", "OCTranspo Bus Route App"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView theList = (ListView)findViewById(R.id.theList);
        ArrayAdapter<String> theData =
                new ArrayAdapter<String>(this, R.layout.array_layout, items);
        theList.setAdapter( theData );


        theList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position)
                {
                    case 0:
                        startActivity(new Intent(MainActivity.this, QuizCreator.class));
                        break;
                    case 1:
                        startActivity(new Intent( MainActivity.this, PatientIntake.class  ));
                        break;
                    case 2:
                        startActivity(new Intent( MainActivity.this, Movie.class  ));
                        break;
                    case 3:
                        startActivity(new Intent( MainActivity.this, OCTranspo.class  ));
                        break;
                }
            }
        });
    }
}