package com.example.mike.dnd_mobile_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CharCreationActivityTwo extends AppCompatActivity {

private EditText Text_Name, Text_Strength, Text_Dexterity, Text_Constitution, Text_Intelligence, Text_Wisdom, Text_Charisma;
private Button BtnCreate, BtnCancel;
private ImageView image;
private static final int REQUEST_TAKE_PHOTO = 0;
private static final int SELECTED_PHOTO = 1;
private Bitmap bitmap;
private String bitmapToString = "";
String mCurrentPhotoPath = "";
public static final String userPREFERENCES = "CharsCreated";
SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char_creation_two);
        sharedpreferences = getSharedPreferences(userPREFERENCES, MODE_PRIVATE);

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
        if(Name != null && !Name.isEmpty()){
            if(!sharedpreferences.contains(Name)){
                int Strength = ifNull(Text_Strength.getText().toString());
                int Dexterity = ifNull(Text_Dexterity.getText().toString());
                int Constitution = ifNull(Text_Constitution.getText().toString());
                int Intelligence = ifNull(Text_Intelligence.getText().toString());
                int Wisdom =ifNull(Text_Wisdom.getText().toString());
                int Charisma = ifNull(Text_Charisma.getText().toString());
                DnDCharacter newChar = new DnDCharacter(bitmapToString, Name, Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(newChar);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Name,json);
                editor.commit();
                Toast.makeText(this, "Character Created!", Toast.LENGTH_LONG).show();
                //closeActivity();
                Intent toMainScreen = new Intent(getApplicationContext(), MainScreen.class);
                startActivity(toMainScreen);
            } else {
                characterNameExists(Name);
            }
        }else {
            emptyCharacterName();
        }
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

    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.mike.dnd_mobile_app",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void pickPicture() {
        Intent getPictureIntent = new Intent();
        getPictureIntent.setType("image/*");
        getPictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        getPictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(getPictureIntent, SELECTED_PHOTO);
    }

    private void resetPicture(){
        image.setImageResource(R.drawable.randomchar);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                if (bitmap != null) {
                    bitmap.recycle();
                }
                bitmap = rotateImage(setReducedImageSize());
                image.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                bitmapToString = Base64.encodeToString(b, Base64.DEFAULT);
            }
            if (requestCode == SELECTED_PHOTO) {
                try {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    InputStream stream = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(stream);
                    stream.close();
                    //Bitmap rotatedImage = rotateBitmap(bitmap);
                    //image.setImageBitmap(rotatedImage);
                    image.setImageBitmap(bitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    bitmapToString = Base64.encodeToString(b, Base64.DEFAULT);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

    public int ifNull(String value){
        if(value != null && !value.isEmpty())
            return Integer.parseInt(value);
        else
            return -1;
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public Bitmap setReducedImageSize() {
        int targetImageViewWidth = image.getWidth();
        int targetImageViewHeight = image.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    public Bitmap rotateImage(Bitmap bitmap){
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(mCurrentPhotoPath);
        }catch (IOException e){
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            default:
        }
        return Bitmap.createBitmap(bitmap ,0 ,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /*public Bitmap rotateBitmap(Bitmap bitmap)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }*/

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
}