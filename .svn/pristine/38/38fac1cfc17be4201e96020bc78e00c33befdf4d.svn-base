package com.example.mhaq.ocrdemo;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity {

    private Button takePhotoButton;
    private Button choosePhotoButton;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //get references to views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
        choosePhotoButton = (Button)findViewById(R.id.choosePhotoButton);
        takePhotoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                takePhotoWithCamera();
            }
        });
        choosePhotoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                chooseFromGallery();
            }
        });
    }
    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem loginItem = menu.findItem(R.id.login);
        if(Database.currentUserId==null){
            loginItem.setTitle("Login");
        }
        else
            loginItem.setTitle("Logout " + Database.currentUsername);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                this.startActivity(homeIntent);
                return true;
            case R.id.library:
                if(Database.currentUserId==null){
                    createAlert(Constants.MUST_LOGIN_MESSAGE);
                }
                else {
                    Intent libraryIntent = new Intent(this, LibraryActivity.class);
                    this.startActivity(libraryIntent);
                }
                return true;
            case R.id.settings:
                if(Database.currentUserId==null){
                    createAlert(Constants.MUST_LOGIN_MESSAGE);
                }
                else {
                    Intent settingsIntent = new Intent(this, SettingsActivity.class);
                    this.startActivity(settingsIntent);
                }
                return true;
            case R.id.login:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...
                                Intent i=new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                            }
                        });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void takePhotoWithCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (camera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(camera, Constants.PICK_IMAGE);
        }
    }

    private void chooseFromGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, Constants.PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.PICK_IMAGE) {
            Uri imageUri = data.getData();
            Intent intent = new Intent(this, ConversionActivity.class);
            intent.putExtra(Constants.EXTRA_URI, imageUri);
            startActivity(intent);
        }
    }

    public void createAlert(String alertMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(alertMessage)
                .setCancelable(false)
                .setPositiveButton(Constants.OK_MESSAGE, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //go back to app screen
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}