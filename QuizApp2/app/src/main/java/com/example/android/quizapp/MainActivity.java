package com.example.android.quizapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    private RadioGroup rd;
    private RadioGroup rd1;
    private CheckBox aaCheckBox;
    private CheckBox bbCheckBox;
    private CheckBox ccCheckBox;
    private CheckBox ddCheckBox;
    private EditText collectionEditText;
    private static int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rd = (RadioGroup) findViewById(R.id.rdGroup);
        rd1= (RadioGroup) findViewById(R.id.rd1Group);

        aaCheckBox = (CheckBox) findViewById(R.id.aa_CheckBox);
        bbCheckBox = (CheckBox) findViewById(R.id.bb_CheckBox);
        ccCheckBox = (CheckBox) findViewById(R.id.cc_CheckBox);
        ddCheckBox = (CheckBox) findViewById(R.id.dd_CheckBox);
        Button Submit = (Button) findViewById(R.id.Submit);
        aaCheckBox.setOnClickListener(this);
        bbCheckBox.setOnClickListener(this);
        ccCheckBox.setOnClickListener(this);
        ddCheckBox.setOnClickListener(this);
        assert Submit != null;
        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox);
        Submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                int ans1Score = checkAns1();
                int ans2Score = checkAns2();
                int ans3Score = checkAns3();
                int ans4Score = checkAns4();
                displayQuizScore(calcScore(ans1Score, ans2Score, ans3Score, ans4Score));
            }
        });
    }

    public void displayQuizScore(int score) {
        TextView scoreView = (TextView) findViewById(R.id.quizScore);
        scoreView.setText(String.valueOf(score));
    }
    public void resetQuestions(View view){
        rd.clearCheck();
        rd1.clearCheck();
        aaCheckBox.setChecked(false);
        bbCheckBox.setChecked(false);
        ccCheckBox.setChecked(false);
        ddCheckBox.setChecked(false);
       collectionEditText.setText("");
        score = 0;
        displayQuizScore(score);
    }
    private static int calcScore(int ans1,int ans2Score, int ans3Score, int ans4Score) {
        score = 0;
        score = score + ans1 + ans2Score +  ans3Score +ans4Score;
        return score;
    }
    private int checkAns1() {
        int checkedId = rd.getCheckedRadioButtonId();
        if (checkedId == R.id.c_radio_button) {
            return 1;
        }
        Toast.makeText(MainActivity.this, "Ans for Q1 is wrong",
                Toast.LENGTH_SHORT).show();
        return 0;
    }

    private int checkAns2() {
        if (aaCheckBox.isChecked() && bbCheckBox.isChecked() && !ccCheckBox.isChecked() && !ddCheckBox.isChecked()){
            return 1;
        } else {
            Toast.makeText(MainActivity.this,"Ans for Q2 is wrong", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    private int checkAns3(){
            String correctAnswer = "Collection of views";
        collectionEditText = (EditText) findViewById(R.id.collection_Edittext);
        String hasCollection = collectionEditText.getText().toString();
        if (hasCollection.equals(correctAnswer)) {
           return 1;
        }
        Toast.makeText(MainActivity.this, "Ans for Q3 is wrong",
                    Toast.LENGTH_SHORT).show();
        return 0;
    }
    private int checkAns4(){
        int checkedId1 = rd1.getCheckedRadioButtonId();
        if (checkedId1 == R.id.dddd_radio_button ) {
            return 1;
        }
        Toast.makeText(MainActivity.this, "Ans for Q4 is wrong",
                Toast.LENGTH_SHORT).show();
        return 0;
    }

    @Override
    public void onClick(View v) {

    }
}