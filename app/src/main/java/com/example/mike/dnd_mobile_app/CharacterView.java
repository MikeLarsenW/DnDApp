package com.example.mike.dnd_mobile_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class CharacterView extends AppCompatActivity {

    private Button Btn_EditStats, Btn_DeleteChar;
    private TextView Text_Strength, Text_Dexterity, Text_Constitution, Text_Intelligence, Text_Wisdom, Text_Charisma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_view);

        Text_Strength = findViewById(R.id.TextView_Strength);
        Text_Dexterity = findViewById(R.id.TextView_Dexterity);
        Text_Constitution = findViewById(R.id.TextView_Constitution);
        Text_Intelligence = findViewById(R.id.TextView_Intelligence);
        Text_Wisdom = findViewById(R.id.TextView_Wisdom);
        Text_Charisma = findViewById(R.id.TextView_Charisma);


    }
}
