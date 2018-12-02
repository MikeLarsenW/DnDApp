package com.example.mike.dnd_mobile_app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.gson.Gson;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.example.mike.dnd_mobile_app.CharacterList.userPREFERENCES;

public class SettingsActivity extends AppCompatActivity {
    Button deleteAll;
    ImageButton syncButton;

    SharedPreferences sharedPreferences;

    CharacterAdapter adapter;
    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);

    ArrayList<DnDCharacter> dndCharacters;
    ArrayList<String> char_list = new ArrayList<>();

    private String sharedPrefPath;
    private String TAG = "error";

    private DriveResourceClient mDriveResourceClient;
    private DriveClient mDriveClient;
    GoogleSignInAccount account;

    final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        syncButton = (ImageButton) findViewById(R.id.sync);
        deleteAll = (Button) findViewById(R.id.deleteAll);

        sharedPreferences = getSharedPreferences(userPREFERENCES, MODE_PRIVATE);

        char_list = getAllCharacters();
        Collections.sort(char_list);

        adapter = new CharacterAdapter(this, char_list);

        dndCharacters = getReadableList();

        // If there are no Characters stored disable the buttons
        if (adapter.getItemCount() == 0) {
            deleteAll.setEnabled(false);
            syncButton.setEnabled(false);
        } else {
            deleteAll.setEnabled(true);
            syncButton.setEnabled(true);
            char_list = getAllCharacters();
            sharedPrefPath = getApplicationContext().getFilesDir() + "/shared_prefs/CharsCreated.xml";
        }
    }

    public void settingsOnClick(View view) {
        switch (view.getId()) {
            case R.id.deleteAll:
                new AlertDialog.Builder(this)
                        .setTitle("Are you Sure?")
                        .setMessage("Are you sure you want to delete all characters?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                deleteAll.setEnabled(false);
                                syncButton.setEnabled(false);

                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case R.id.sync:
                // Alerts user on action then launches the SignIn() method on yes, cancels otherwise
                new AlertDialog.Builder(this)
                        .setTitle("Are you Sure?")
                        .setMessage("Allow sync With Google Drive?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            signIn();
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;



        }
    }

    // After the signIn returns a success code and the DriveClient is intialized
    // this function begins creating the file to save to the Drive
    private void createFolder() {
        Log.i("SharedPrefsPath","the path " + sharedPrefPath);
        try {
            File storeCharsFile = new File(sharedPrefPath);

            final Task<DriveFolder> rootFolderTask = getDriveResourceClient().getRootFolder();
            final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();

            Tasks.whenAll(rootFolderTask, createContentsTask)
                    .continueWithTask(task -> {
                        DriveFolder parent = rootFolderTask.getResult();
                        DriveContents contents = createContentsTask.getResult();
                        OutputStream outputStream = contents.getOutputStream();

                        try (Writer writer = new OutputStreamWriter(outputStream)) {
                            dndCharacters.forEach((n) -> {
                                try {
                                    writer.write("\n=================================\n"+
                                            "Character Name: "+n.getName()+"\n " +
                                            "Strength: "+n.getStrength()+"\n " +
                                            "Dexterity: "+n.getDexterity()+"\n " +
                                            "Constitution: "+n.getConstitution()+"\n "+
                                            "Intelligence: "+n.getIntelligence()+"\n "+
                                            "Wisdom: "+n.getWisdom()+"\n "+
                                            "Charisma: "+n.getCharisma()+"\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            writer.write("=================================");


                        }

                        Date date = new Date();
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle("DnDCharacters_"+ df.format(date) +".xml")
                                .setMimeType("text/xml")
                                .setStarred(true)
                                .build();

                        return getDriveResourceClient().createFile(parent, changeSet, contents);
                    })
                    .addOnSuccessListener(this,
                            driveFile -> {
                                Log.i("Success", "File Created!");
                                Toast.makeText(SettingsActivity.this,"File Downloaded Successfully!",Toast.LENGTH_LONG).show();
                                finish();
                            })
                    .addOnFailureListener(this, e -> {
                        Log.e(TAG, "Unable to create file", e);
                        Toast.makeText(getApplicationContext(),"File Downloaded Was unsuccessful",Toast.LENGTH_LONG).show();
                        finish();
                    });
        } catch (Exception e){
            Log.e("Error Uploading","file failed to upload");
        }
    }

    protected DriveClient getDriveClient() {
        return mDriveClient;
    }

    protected DriveResourceClient getDriveResourceClient() {
        return mDriveResourceClient;
    }

    // uses the data from the signInAccount to create the DriveClient
    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
        onDriveClientReady();
    }

    // Called once the drive Client is ready and launches file download
    protected void onDriveClientReady() {
        createFolder();
    }

    // Performs the SignIn request to get the Google Account so that we
    // can save the file to their drive
    protected void signIn() {
        Log.i("create file","attempted");
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .requestEmail()
                            .requestProfile()
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
        }
    }

    // handles result codes from launch activities
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user.
                    Log.e(TAG, "Sign-in failed.");
                    finish();
                    return;
                }
                Log.i("signed in", "Sign in request code");
                // Called after user is signed in.
                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                } else {
                    Log.e(TAG, "Sign-in failed.");
                    finish();
                }
                break;
        }
    }

    // creates an array list of the character names to use as keys
    public ArrayList<String> getAllCharacters() {
        ArrayList<String> characters = new ArrayList<>();
        Map<String,?> keys = sharedPreferences.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            characters.add(entry.getKey());
        }
        return characters;
    }

    // uses the arraylist from char_list to create a arraylist of
    // DnDCharacters to print stats and names to file later
    public ArrayList<DnDCharacter> getReadableList(){
        ArrayList<DnDCharacter> list = new ArrayList<>();
        for(int i=0; i<char_list.size(); i++) {
            String character = char_list.get(i);
            Gson gson = new Gson();
            String json = sharedPreferences.getString(character, "");
            DnDCharacter storedCharacter = gson.fromJson(json, DnDCharacter.class);
            list.add(storedCharacter);

        }
        return list;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        menu.findItem(R.id.menu_settings).setEnabled(false);
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
                intent = new Intent(this, SettingsActivity.class);
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


