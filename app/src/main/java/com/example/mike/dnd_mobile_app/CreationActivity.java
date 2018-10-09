package com.example.mike.dnd_mobile_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class CreationActivity extends AppCompatActivity {

    public Button rndButton, customButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char_creation);


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
        alert.setMessage("Name your Character");
        alert.setTitle("Character Name");

        alert.setView(edittext);

        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String YouEditTextValue = edittext.getText().toString();
                DnDCharacter newChar = new DnDCharacter(YouEditTextValue);
                Toast.makeText(CreationActivity.this, "Character Created!", Toast.LENGTH_LONG).show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();


    }
}
