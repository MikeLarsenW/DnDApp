package com.example.mike.dnd_mobile_app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static com.example.mike.dnd_mobile_app.CharacterList.userPREFERENCES;

public class SettingsActivity extends AppCompatActivity {

    Switch cameraSwitch;
    Button deleteAll;
    ImageButton syncButton;
    SharedPreferences sharedPreferences;
    CharacterAdapter adapter;
    ArrayList<String> char_list = new ArrayList<>();
    private String TAG = "error";
    private DriveResourceClient mDriveResourceClient;
    private DriveClient mDriveClient;
    GoogleSignInAccount account;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1;
    int RC_REQUEST_PERMISSION_SUCCESS_CONTINUE_FILE_CREATION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences(userPREFERENCES, MODE_PRIVATE);
        char_list = getAllCharacters();
        Collections.sort(char_list);
        adapter = new CharacterAdapter(this, char_list);
        syncButton = (ImageButton) findViewById(R.id.sync);
        deleteAll = (Button) findViewById(R.id.deleteAll);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);


        if (adapter.getItemCount() == 0) {
            deleteAll.setEnabled(false);
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

                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case R.id.sync:
                new AlertDialog.Builder(this)
                        .setTitle("Are you Sure?")
                        .setMessage("Allow sync With Google Drive?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {

                            if (account != null) {
                                initializeDriveClient(account);
                                createFolder();
                            } else {
                                signIn();

                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;


        }
    }

    private void createFolder() {
        getDriveResourceClient()
                .getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("DND Characters")
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(true)
                            .build();
                    return getDriveResourceClient().createFolder(parentFolder, changeSet);
                });
    }

    protected DriveResourceClient getDriveResourceClient() {
        return mDriveResourceClient;
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        requestPermissions();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && resultCode != RC_SIGN_IN) {
            if (RC_REQUEST_PERMISSION_SUCCESS_CONTINUE_FILE_CREATION == requestCode) {
                createFolder();
            }
        }
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            initializeDriveClient(account);
            createFolder();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    public ArrayList<String> getAllCharacters() {
        ArrayList<String> characters = new ArrayList<>();
        Map<String, ?> keys = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            characters.add(entry.getKey());
        }
        return characters;
    }

    private void requestPermissions() {
        if (!GoogleSignIn.hasPermissions(
                GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
                Drive.SCOPE_APPFOLDER)) {
            GoogleSignIn.requestPermissions(
                    SettingsActivity.this,
                    RC_REQUEST_PERMISSION_SUCCESS_CONTINUE_FILE_CREATION,
                    GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
                    Drive.SCOPE_APPFOLDER);
        }
    }
}

