package com.example.mike.dnd_mobile_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CharCreationActivityTwo extends AppCompatActivity {

private EditText Text_Name, Text_Strength, Text_Dexterity, Text_Constitution, Text_Intelligence, Text_Wisdom, Text_Charisma;
private Button BtnCreate, BtnCancel;
private ImageView image;
private static final int REQUEST_TAKE_PHOTO = 1;
private static final int SELECTED_PIC = 2;
private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char_creation_two);

        image = findViewById(R.id.Image_Char);

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
        int Intelligence = Integer.parseInt(Text_Intelligence.getText().toString());
        int Wisdom = Integer.parseInt(Text_Wisdom.getText().toString());
        int Charisma = Integer.parseInt(Text_Charisma.getText().toString());
        DnDCharacter newChar = new DnDCharacter(Name, Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma);
        Toast.makeText(this, "Character Created!", Toast.LENGTH_LONG).show();
    }

    String[] picOptions = {"Camera","Gallery", "Remove photo"};
    public void setImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Character photo");
        builder.setItems(picOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        takePicture();
                        break;
                    case 1:
                        pickPicture();
                        break;
                    case 2:
                        resetPicture();
                        break;
                }
            }
        });
        builder.show();
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private void pickPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, SELECTED_PIC);
    }

    protected void resetPicture(){
        image.setImageResource(R.drawable.randomchar);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
        }
        if (requestCode == SELECTED_PIC && resultCode == RESULT_OK)
            try {
                if (bitmap != null) {
                    bitmap.recycle();
                }
                InputStream stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
