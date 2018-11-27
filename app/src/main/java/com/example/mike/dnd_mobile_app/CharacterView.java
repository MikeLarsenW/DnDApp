package com.example.mike.dnd_mobile_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CharacterView extends AppCompatActivity {

    private Button Btn_DeleteChar;
    private EditText Text_Strength, Text_Dexterity, Text_Constitution, Text_Intelligence, Text_Wisdom, Text_Charisma;
    TextView Text_Name;
    LinearLayout saveCancelButtons;
    ImageView Btn_EditChar, CharImage;
    String getKey = "";
    String strength, dexterity, constitution, intelligence, wisdom, charisma;
    public static final String userPREFERENCES = "CharsCreated" ;
    SharedPreferences sharedpreferences;
    DnDCharacter storedCharacter;
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int SELECTED_PHOTO = 1;
    private Bitmap bitmap;
    private String bitmapToString = "";
    String mCurrentPhotoPath = "";
    Boolean clickable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_view);
        sharedpreferences = getSharedPreferences(userPREFERENCES, MODE_PRIVATE);

        CharImage = findViewById(R.id.charImage);
        Text_Name = findViewById(R.id.charName);
        Text_Strength = findViewById(R.id.EditText_Strength);
        Text_Dexterity = findViewById(R.id.EditText_Dexterity);
        Text_Constitution = findViewById(R.id.EditText_Constitution);
        Text_Intelligence = findViewById(R.id.EditText_Intelligence);
        Text_Wisdom = findViewById(R.id.EditText_Wisdom);
        Text_Charisma = findViewById(R.id.EditText_Charisma);
        saveCancelButtons = findViewById(R.id.hiddenLayout);
        Btn_EditChar = findViewById(R.id.Button_EditCharacter);
        Btn_DeleteChar = findViewById(R.id.Button_DeleteChar);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            getKey = extras.getString("key");
            Gson gson = new Gson();
            String json = sharedpreferences.getString(getKey, "");
            storedCharacter = gson.fromJson(json, DnDCharacter.class);
            setImage();
            Text_Name.setText(storedCharacter.getName());

            getCharacterValues();
            setCharacterValues();
        }
    }

    public void onEditStatsClicked(View view){
        clickable = true;
        CharImage.setClickable(true);
        Text_Strength.setEnabled(true);
        Text_Dexterity.setEnabled(true);
        Text_Constitution.setEnabled(true);
        Text_Intelligence.setEnabled(true);
        Text_Wisdom.setEnabled(true);
        Text_Charisma.setEnabled(true);
        Btn_EditChar.setEnabled(false);
        Btn_DeleteChar.setVisibility(View.GONE);
        saveCancelButtons.setVisibility(View.VISIBLE);
    }

    public void onCancelClicked(View view){
        clickable = false;
        CharImage.setClickable(false);
        Text_Strength.setEnabled(false);
        Text_Dexterity.setEnabled(false);
        Text_Constitution.setEnabled(false);
        Text_Intelligence.setEnabled(false);
        Text_Wisdom.setEnabled(false);
        Text_Charisma.setEnabled(false);
        Btn_EditChar.setEnabled(true);
        Btn_DeleteChar.setVisibility(View.VISIBLE);
        saveCancelButtons.setVisibility(View.GONE);
        Log.d("CANCEL BUTTON CHAR VIEW", "onCancelClicked: set original image");
        setImage();
        Log.d("CANCEL BUTTON CHAR VIEW", "onCancelClicked: set original image 22222222");
        setCharacterValues();
    }

    public void onSaveClicked(View view){
        clickable = false;
        int Strength = ifIntNull(Text_Strength.getText().toString());
        int Dexterity = ifIntNull(Text_Dexterity.getText().toString());
        int Constitution = ifIntNull(Text_Constitution.getText().toString());
        int Intelligence = ifIntNull(Text_Intelligence.getText().toString());
        int Wisdom = ifIntNull(Text_Wisdom.getText().toString());
        int Charisma = ifIntNull(Text_Charisma.getText().toString());

        storedCharacter.setCharImage(bitmapToString);
        storedCharacter.setStrength(Strength);
        storedCharacter.setDexterity(Dexterity);
        storedCharacter.setConstitution(Constitution);
        storedCharacter.setIntelligence(Intelligence);
        storedCharacter.setWisdom(Wisdom);
        storedCharacter.setCharisma(Charisma);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(storedCharacter);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(getKey,json);
        editor.apply();

        Text_Strength.setEnabled(false);
        Text_Dexterity.setEnabled(false);
        Text_Constitution.setEnabled(false);
        Text_Intelligence.setEnabled(false);
        Text_Wisdom.setEnabled(false);
        Text_Charisma.setEnabled(false);
        Btn_EditChar.setEnabled(true);
        Btn_DeleteChar.setVisibility(View.VISIBLE);
        saveCancelButtons.setVisibility(View.GONE);
        getCharacterValues();
        setCharacterValues();
        Toast.makeText(this, "Character Updated!", Toast.LENGTH_LONG).show();
    }

    public void onDeleteClicked(View view){
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle("Delete");
        deleteDialog.setMessage("Are you sure you want to delete '" + storedCharacter.getName() + "'");
        deleteDialog.setCancelable(false);
        deleteDialog.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), storedCharacter.getName()+" has been deleted.", Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.remove(getKey);
                        editor.apply();
                        Intent intent = new Intent(getBaseContext(), CharacterList.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }});
        deleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }});
        AlertDialog alert11 = deleteDialog.create();
        alert11.show();
    }

    public void pickImage(View view){
        if(clickable){
            String[] picOptions = {"Camera","Gallery", "Remove photo"};
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
    }

    public void getCharacterValues(){
        strength = ifNull(storedCharacter.getStrength());
        dexterity = ifNull(storedCharacter.getDexterity());
        constitution = ifNull(storedCharacter.getConstitution());
        intelligence = ifNull(storedCharacter.getIntelligence());
        wisdom = ifNull(storedCharacter.getWisdom());
        charisma = ifNull(storedCharacter.getCharisma());
    }

    public void setCharacterValues(){
        Text_Strength.setText(strength);
        Text_Dexterity.setText(dexterity);
        Text_Constitution.setText(constitution);
        Text_Intelligence.setText(intelligence);
        Text_Wisdom.setText(wisdom);
        Text_Charisma.setText(charisma);
    }

    public String ifNull(int value){
        if(value == -1)
            return "";
        else
            return Integer.toString(value);
    }

    public int ifIntNull(String value){
        if(value != null && !value.isEmpty())
            return Integer.parseInt(value);
        else
            return -1;
    }

    public void setImage(){
        String previouslyEncodedImage = storedCharacter.getCharImage();
        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            CharImage.setImageBitmap(bitmap);
        }else {
            CharImage.setImageResource(R.drawable.randomchar);
        }
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

    public void pickPicture() {
        Intent getPictureIntent = new Intent();
        getPictureIntent.setType("image/*");
        getPictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        getPictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(getPictureIntent, SELECTED_PHOTO);
    }

    public void resetPicture(){
        CharImage.setImageResource(R.drawable.randomchar);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                if (bitmap != null) {
                    bitmap.recycle();
                }
                bitmap = rotateImage(setReducedImageSize());
                CharImage.setImageBitmap(bitmap);
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
                    Bitmap rotatedImage = rotateBitmap(bitmap);
                    CharImage.setImageBitmap(rotatedImage);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    rotatedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    bitmapToString = Base64.encodeToString(b, Base64.DEFAULT);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if (resultCode == RESULT_CANCELED){
            Log.d("canceled clicked", "onActivityResult: CANCEL");
        }
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
        int targetImageViewWidth = CharImage.getWidth();
        int targetImageViewHeight = CharImage.getHeight();

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

    public Bitmap rotateBitmap(Bitmap bitmap)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
