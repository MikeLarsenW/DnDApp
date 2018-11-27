package com.example.mike.dnd_mobile_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MainScreen extends AppCompatActivity {

    ImageView addTile, viewTile, spellTile, diceTile, settingTile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        addTile = (ImageView)findViewById(R.id.createCharacterTile);
        viewTile = (ImageView)findViewById(R.id.viewCharacterTile);
        spellTile = (ImageView)findViewById(R.id.spellTile);
        diceTile = (ImageView)findViewById(R.id.diceTile);
        settingTile = (ImageView)findViewById(R.id.settingsTile);
    }


    public void onClick(View view)
    {
        Intent intent;
        switch (view.getId())
        {
            case R.id.createCharacterTile:
                intent = new Intent(this, CreationActivity.class);
                startActivity(intent); break;
            case R.id.viewCharacterTile:
                intent = new Intent(this, CharacterList.class);
                startActivity(intent); break;
            case R.id.spellTile:
                intent = new Intent(this, Spelltable.class);
                startActivity(intent); break;
            case R.id.diceTile:
                intent = new Intent(this, DiceActivity.class);
                startActivity(intent); break;
            case R.id.settingsTile:
                intent = new Intent(this, null);
                startActivity(intent); break;
        }
    }
}
