package com.example.mike.dnd_mobile_app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class CharCreationActivityTwo extends AppCompatActivity {

private EditText Text_Name, Text_Strength, Text_Dexterity, Text_Constitution, Text_Intelligence, Text_Wisdom, Text_Charisma;
private Button BtnCreate, BtnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char_creation_two);

        ImageView image = findViewById(R.id.Image_Char);

        Text_Name = findViewById(R.id.EditText_Name);

        Text_Strength = findViewById(R.id.EditText_Strength);
        Text_Dexterity = findViewById(R.id.EditText_Dextirity);
        Text_Constitution = findViewById(R.id.EditText_Constitution);
        Text_Intelligence = findViewById(R.id.EditText_Intelligence);
        Text_Wisdom = findViewById(R.id.EditText_Wisdom);
        Text_Charisma = findViewById(R.id.EditText_Charisma);

        BtnCancel = findViewById(R.id.Button_Cancel);
        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

        BtnCreate = findViewById(R.id.Button_Create);
        BtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChar();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });
    }


    public void closeActivity()
    {
        this.finish();
    }

    public void createChar()
    {
        String Name = Text_Name.getText().toString();
        int Strength = Integer.parseInt(Text_Strength.getText().toString());
        int Dexterity = Integer.parseInt(Text_Dexterity.getText().toString());
        int Constitution = Integer.parseInt(Text_Constitution.getText().toString());
        int  Intelligence = Integer.parseInt(Text_Intelligence.getText().toString());
        int Wisdom = Integer.parseInt(Text_Wisdom.getText().toString());
        int Charisma = Integer.parseInt(Text_Charisma.getText().toString());
        DnDCharacter newChar = new DnDCharacter(Name, Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma);
        Toast.makeText(this, "Character Created!", Toast.LENGTH_LONG).show();
    }

    String[] picOptions = {"Gallery", "Camera", "Remove photo"};
    public void setImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Character photo");
        builder.setItems(picOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "Choose pic from gallery", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "Take pic with camera", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Remove pic", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        builder.show();
    }

}


