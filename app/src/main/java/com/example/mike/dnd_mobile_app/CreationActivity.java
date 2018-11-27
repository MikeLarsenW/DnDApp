package com.example.mike.dnd_mobile_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class CreationActivity extends AppCompatActivity {

    public Button rndButton, customButton;
    public static final String userPREFERENCES = "CharsCreated" ;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char_creation);
        sharedpreferences = getSharedPreferences(userPREFERENCES, MODE_PRIVATE);


        rndButton = findViewById(R.id.Button_Random);
        rndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRandomChar();
            }
        });

        customButton = findViewById(R.id.Button_Custom);
        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openActivity2();
            }
        });
    }

    public void openActivity2()
    {
        Intent intent = new Intent(this, CharCreationActivityTwo.class);
        startActivity(intent);
    }

    public void createRandomChar()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(this);
        alert.setTitle("Character Name");
        alert.setMessage("Name your Character");
        alert.setView(edittext);

        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String Name = edittext.getText().toString();
                if(Name != null && !Name.isEmpty()) {
                    if (!sharedpreferences.contains(Name)) {
                        DnDCharacter newChar = new DnDCharacter(Name);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String json = gson.toJson(newChar);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(Name,json);
                        editor.commit();
                        Log.d("JSON DEBUG", json);

                        Toast.makeText(CreationActivity.this, "Character Created!", Toast.LENGTH_LONG).show();
                        Intent toMainScreen = new Intent(getApplicationContext(), MainScreen.class);
                        startActivity(toMainScreen);
                    } else {
                        characterNameExists(Name);
                    }
                }else{
                    emptyCharacterName();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });

        alert.show();
    }

    public void characterNameExists(String Name){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Whoops...");
        alert.setMessage("Character with name '" + Name + "' already exists. Change character name in order to create.");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });
        alert.show();
    }

    public void emptyCharacterName(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Whoops...");
        alert.setMessage("Character's must have a name to be created.");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        menu.findItem(R.id.menu_create).setEnabled(false);
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
                intent = new Intent(this, CharacterList.class);
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
}
