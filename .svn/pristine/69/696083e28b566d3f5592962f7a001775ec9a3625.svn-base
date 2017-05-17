package com.example.mhaq.ocrdemo;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {

    private ArrayList<String> documentList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView mListView;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        if (Database.documentList==null){
            documentList = new ArrayList<>();
            documentList.add(Constants.NO_DOCUMENTS);
        }
        else {
            documentList = Database.documentList;
        }

        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.list_view);
        }
        adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, documentList);
        setListAdapter(adapter);

    }
    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.list_view);
        }
        return mListView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
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
                    Intent libraryIntent = new Intent(this, LibraryActivity.class);
                    this.startActivity(libraryIntent);
            case R.id.settings:
                    Intent settingsIntent = new Intent(this, SettingsActivity.class);
                    this.startActivity(settingsIntent);
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
}
