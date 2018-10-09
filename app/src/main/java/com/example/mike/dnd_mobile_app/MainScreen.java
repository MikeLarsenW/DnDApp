package com.example.mike.dnd_mobile_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainScreen extends AppCompatActivity {

    Button btn,btn2,btn3,btn4,btn5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        btn = (Button)findViewById(R.id.diceScreenButton);
        btn2 = (Button)findViewById(R.id.spellTableButton);
        btn3 = (Button)findViewById(R.id.charactersScreenButton);
        btn4 = (Button)findViewById(R.id.settingsScreenButton);
        btn5 = (Button)findViewById(R.id.createCharacterScreenButton);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), DiceActivity.class);
                startActivity(intent);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Spelltable.class);
                startActivity(intent);
            }
        });

        /**
         * TODO As each feature gets added the intent to navigate to each page should be added
         */
        btn4.setEnabled(false);
//        btn3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(), CreationActivity.class);
//                startActivity(intent);
//            }
//        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CharacterView.class);
                startActivity(intent);
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CreationActivity.class);
                startActivity(intent);
            }
        });
    }
}
