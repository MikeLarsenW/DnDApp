package com.example.mike.dnd_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Spell extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spell0);

        TextView spelldescription = findViewById(R.id.spellDescription);

        Intent intent = getIntent();
        String name = intent.getStringExtra("SpellName");
        String description = intent.getStringExtra("SpellDescription");
        setTitle(name);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            spelldescription.setText(description);
        }
    }
}
