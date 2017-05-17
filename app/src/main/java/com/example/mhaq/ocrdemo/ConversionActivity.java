package com.example.mhaq.ocrdemo;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.vision.text.Text;

import java.util.Locale;

public class ConversionActivity extends AppCompatActivity {

    private Button takePhotoButton;
    private Button choosePhotoButton;
    private ImageView imageView;
    private TextView detectedTextView;
    private TextView detectedMesgView;
    private Button speakButton;
    private Button saveButton;
    private Button shareButton;
    private TextToSpeech textToSpeech;
    private boolean paused;
    private GoogleApiClient mGoogleApiClient;
    private String recipientUsername;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        //get references to views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
        choosePhotoButton = (Button)findViewById(R.id.choosePhotoButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        detectedMesgView = (TextView) findViewById(R.id.detectedMesg);
        detectedTextView = (TextView) findViewById(R.id.detectedTextView);
        speakButton = (Button) findViewById(R.id.speakButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        shareButton = (Button) findViewById(R.id.shareButton);

        Intent intent = getIntent();
        Uri imageUri = intent.getParcelableExtra(Constants.EXTRA_URI);
        imageView.setImageURI(imageUri);
        detectedTextView.setText(TextDetector.detectText(this, imageView));

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

        //this object is used to read the text aloud
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }

            }
        });

        //on click, this button will make a toast that says 'Reading' and will tell the TextToSpeech object to start
        // speaking whatever text has been displayed
        paused = true;
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getDrawable()==null) {
                    createAlert(Constants.NULL_IMAGE_MESSAGE);
                }
                else {
                    String detectedText = detectedTextView.getText().toString();
                    if (detectedText.isEmpty()) {
                        createAlert(Constants.NOTHING_DETECTED_MESSAGE);
                    }
                    //play or pause the speech
                    else{
                        paused = !paused;
                        if (paused){
                            speakButton.setText("Speak");
                            textToSpeech.stop();
                        }
                        else {
                            speakButton.setText("Stop");
                            Toast.makeText(getApplicationContext(), Constants.READING, Toast.LENGTH_LONG).show();
                            speakText(detectedText);
                        }
                    }
                }
            }
        });

        // allows user to save the text they have just converted to their library
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String detectedText = detectedTextView.getText().toString();
                //if nothing was detected, say so
                if (detectedText.isEmpty()) {
                    createAlert(Constants.NOTHING_DETECTED_MESSAGE);
                }
                else if(Database.currentUserId==null) {
                    createAlert(Constants.MUST_LOGIN_MESSAGE);
                }
                else {
                    //push text to database as DocumentX, then save that to library
                    Database.addDocumentToDatabase(detectedText);
                    Toast.makeText(getApplicationContext(), Constants.SAVED_MESSAGE, Toast.LENGTH_LONG).show();
                }
            }
        });

        // allows user to save the text they have just converted to their library
        shareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String detectedText = detectedTextView.getText().toString();
                //if nothing was detected, say so
                if (detectedText.isEmpty()) {
                    createAlert(Constants.NOTHING_DETECTED_MESSAGE);
                }
                else if(Database.currentUserId==null) {
                    createAlert(Constants.MUST_LOGIN_MESSAGE);
                }
                else if(!Database.documentHasBeenSaved(detectedText)){
                    createAlert(Constants.MUST_SAVE_MESSAGE);
                }
                else
                    createRecipientField();
            }
        });

    }

    private void speakText(final String detectedText){
        //this object is used to read the text aloud
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    //noinspection deprecation
                    textToSpeech.speak(detectedText, TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });
    }

    //allow Google Api client to connect so that user can log out from this activity
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

    //set up the main menu
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

    //configure options for main menu
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

    //once a photo has been selected, display it and convert to text
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.PICK_IMAGE) {
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);
            detectedTextView.setText(TextDetector.detectText(this, imageView));
            speakButton.setText("Speak");
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

    public void createRecipientField(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter a username");
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recipientUsername = input.getText().toString();
                if (!Database.usernames.containsKey(recipientUsername)) {
                    createAlert(Constants.USER_NOT_FOUND);
                }
                else {
                    Database.shareDocument(detectedTextView.getText().toString(), recipientUsername);
                    Toast.makeText(getApplicationContext(), Constants.SHARED_MESSAGE + recipientUsername, Toast
                            .LENGTH_LONG)
                            .show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //if the user leaves the app it should pause the speech
    public void onPause(){
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

}
