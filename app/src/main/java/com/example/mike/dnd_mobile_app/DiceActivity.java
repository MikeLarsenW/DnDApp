package com.example.mike.dnd_mobile_app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tbouron.shakedetector.library.ShakeDetector;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class DiceActivity extends AppCompatActivity{

    //Define variables, views, and sensors
    Spinner s;
    Button btn;
    TextView result1TV;
    TextView result2TV;
    TextView result3TV;
    TextView result4TV;
    TextView result5TV;
    TextView result6TV;
    int diceType = 0;
    int diceAmt = 1;
    int[] rolls;
    Switch accel;
    SeekBar numOfDice;
    TextView numberTV;
    TextView[] listOfTV = new TextView[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);



        accel = (Switch)findViewById(R.id.accelerometerSwitch);

        //Initialize various items
        rolls = new int[6];
        btnInit();
        spinnerInit();
        shakeInit();
        seekBarInit();
        initResults();

    }

    public void updateResultViews(int amount)
    {
        for(int i = 0; i < amount - 1; i++)
        {
            listOfTV[i].setVisibility(View.VISIBLE);
        }
        if(amount == 6)
        {
            for(int i = 0; i < 6; i++)
            {
                listOfTV[i].setVisibility(View.VISIBLE);
            }
        }
        else if(amount < 6)
        {
            for(int j = (amount); j < 6; j++)
            {
                System.out.println("Number is:" + j);
                listOfTV[j].setVisibility(View.INVISIBLE);
            }
        }
    }

    //Initialize textviews for results
    public void initResults()
    {
        result1TV = (TextView)findViewById(R.id.result1);
        result2TV = (TextView)findViewById(R.id.result2);
        result3TV = (TextView)findViewById(R.id.result3);
        result4TV = (TextView)findViewById(R.id.result4);
        result5TV = (TextView)findViewById(R.id.result5);
        result6TV = (TextView)findViewById(R.id.result6);
        listOfTV = new TextView[]{result1TV, result2TV, result3TV, result4TV, result5TV, result6TV};


    }
    //Initialize Seekbar
    public void seekBarInit()
    {
        numOfDice = (SeekBar)findViewById(R.id.numOfDice);
        numberTV = (TextView)findViewById(R.id.num);
        //Update amount of dice to be rolled with the seekbar
        numOfDice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                diceAmt = progress;
                updateResultViews(diceAmt);
                numberTV.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    //Function is implemented using an external library 'Shake Detector'
    //https://github.com/tbouron/ShakeDetector
    public void shakeInit()
    {
        ShakeDetector.create(this, new ShakeDetector.OnShakeListener() {
            @Override
            public void OnShake() {
                roll();
            }
        });

        ShakeDetector.updateConfiguration(2.5f, 1);
    }
    //Initializes the roll button and set onClickListener
        //Button will call the roll() function when pressed
    public void btnInit()
    {
        btn = (Button)findViewById(R.id.rollButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roll();
            }
        });
    }

    //Initializes the spinner elements and gets the selected item
    //changes the dice type based on selection
    public void spinnerInit()
    {
        Spinner spinner = (Spinner) findViewById(R.id.diceSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.diceSpinnerElements, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                diceType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    //Generate a random number based on the type of dice that is selected
    public void roll()
    {
        for(int i = 0; i < diceAmt; i++)
        {

            switch(diceType)
            {
                case 0:
                    rolls[i] = (int)(0);
                    break;

                case 1:
                    rolls[i] = (int)(Math.random() * 4 + 1);
                    break;

                case 2:
                    rolls[i] = (int)(Math.random() * 6 + 1);
                    break;

                case 3:
                    rolls[i] = (int)(Math.random() * 8 + 1);
                    break;

                case 4:
                    rolls[i] = (int)(Math.random() * 10 + 1);
                    break;

                case 5:
                    rolls[i] = (int)(Math.random() * 12 + 1);
                    break;

                case 6:
                    rolls[i] = (int)(Math.random() * 20 + 1);
                    break;

            }
        }
        for(int i = 0; i < 6; i++)
        {
            listOfTV[i].setText("You Rolled a " + rolls[i]);
        }

    }
}
