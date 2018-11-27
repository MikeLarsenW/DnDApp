//Group 6
//DnD App
//Implemented by Quinten Whitaker

package com.example.mike.dnd_mobile_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class DiceActivity extends AppCompatActivity{

    //Define variables, views, and sensors
    Spinner s;
    Button btn;
    int diceType = 1;
    int diceAmt = 1;
    int[] rolls;
    Switch accel;
    SeekBar numOfDice;
    TextView numberTV;
    boolean switchVal;
    Vibrator v;
    FlexboxLayout diceLayout;
    LayoutInflater li;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);

        //Initialize various items
        rolls = new int[6];
        btnInit();
        spinnerInit();
        shakeInit();
        seekBarInit();
        initResults();
        initSwitch();
        initVibrator();
        updateResultViews(1);
        numberTV.setText(Integer.toString(1));
        diceAmt = 1;
        diceType = 1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        menu.findItem(R.id.menu_dice).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId())
        {
            case R.id.menu_create:
                intent = new Intent(this, CreationActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_view:
                intent = new Intent(this, CharacterView.class);
                startActivity(intent);
                break;
            case R.id.menu_spells:
                intent = new Intent(this, Spelltable.class);
                startActivity(intent);
                break;
            case R.id.menu_settings:
                intent = new Intent(this, null);
                startActivity(intent);
                break;
            case R.id.menu_dice:
                intent = new Intent(this, DiceActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Initialize vibrator
    public void initVibrator()
    {
        v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }
    //Initialize switch
    //Set on checked change listener
    public void initSwitch()
    {
        accel = (Switch)findViewById(R.id.accelerometerSwitch);
        accel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchVal = isChecked;
            }
        });
    }
    //This function dynamically updates the layout with dice images depending on the number of dice selected
    public void updateResultViews(int amount)
    {
        diceLayout.removeAllViews();

        int diceName = R.drawable.d6;
        switch(diceType)
        {
            case 1: diceName = R.drawable.d4; break;
            case 2: diceName = R.drawable.d6; break;
            case 3: diceName = R.drawable.d8; break;
            case 4: diceName = R.drawable.d10; break;
            case 5: diceName = R.drawable.d12; break;
            case 6: diceName = R.drawable.d20; break;
        }

        for(int i = 0; i < amount; i++)
        {
            View dice = li.inflate(R.layout.dice_image_layout, null);
            ImageView imageView = (ImageView)dice.findViewById(R.id.imageView);
            TextView textView = (TextView)dice.findViewById(R.id.diceNumTV);

            imageView.setImageResource(diceName);

            diceLayout.addView(dice);

        }
    }

    //Initialize flexboxlayout for results
    public void initResults()
    {
        li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        diceLayout = (FlexboxLayout)findViewById(R.id.flexBoxLayout);
        diceLayout.setFlexWrap(FlexWrap.WRAP);
        diceLayout.setJustifyContent(JustifyContent.SPACE_BETWEEN);
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
                if(switchVal)
                {
                    roll();
                }

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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.diceSpinnerElements, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(1);
        spinner.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                diceType = position;
                updateResultViews(diceAmt);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    //Generate a random number based on the type of dice that is selected
    public void roll()
    {
        v.vibrate(50);
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

        for(int i = 0; i < diceAmt; i++)
        {
            View dice = diceLayout.getChildAt(i);

            TextView textView = (TextView)dice.findViewById(R.id.diceNumTV);
            textView.setText(Integer.toString(rolls[i]));
            ImageView imageView = (ImageView)dice.findViewById(R.id.imageView);
            dice.setRotation(0);
            dice.animate().rotation(360).start();

        }

    }
}
