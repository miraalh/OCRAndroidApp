package com.example.mhaq.ocrdemo;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mhaq on 5/2/17.
 */

public class Database {

    final static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final static DatabaseReference usersReference = firebaseDatabase.getReference("users");
    final static DatabaseReference usernamesReference = firebaseDatabase.getReference("usernames");
    static int numberOfDocuments;
    static String currentUserId;
    static String currentUsername;
    static ArrayList<String> documentList;
    static HashMap<String,String> usernames;

    /**
     * Checks with the email id if the user is new, if yes then adds user to the database, if no then just sets
     * current user to the email address
     * @param id the current user's unique Google account id
     * @param username the current user's unique Google email address without the @gmail.com extension
     */
    public static void addUserToDatabase(String id, String username){
        usernames = new HashMap<>();
        updateUsernames();
        currentUserId=id;
        currentUsername=username;
        usernamesReference.child(username).setValue(id);
        usersReference.child(id).child("library").child("Document0").child("owner").setValue(username);
        usersReference.child(id).child("library").child("Document0").child("text").setValue(Constants.DUMMY_TEXT);
        usersReference.child(id).child("username").setValue(username);
    }

    /**
     * Updates the usernames array list with the most current list of usernames from Firebase
     */
    public static void updateUsernames()  {
        usernames = new HashMap<>();
        usernamesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    usernames.put(data.getKey(), (String) data.getValue());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Adds a new document to the current user's tree in database and updates the document list accordingly
     * @param documentText the text for the new document
     */
    public static void addDocumentToDatabase(String documentText) {
        String documentName = "Document"+ numberOfDocuments;
        DatabaseReference libraryReference = firebaseDatabase.getReference("users/"+currentUserId+"/library");
        libraryReference.child(documentName).child("text").setValue(documentText);
        libraryReference.child(documentName).child("owner").setValue(currentUsername);
        libraryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //update number of documents
                numberOfDocuments = (int) dataSnapshot.getChildrenCount();

                //update document array list
                documentList = new ArrayList<String>();
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    documentList.add(data.getKey()+ ": \n\n" + (String)data.child("text").getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Updates the document list array list with the current user's documents in database
     */
    public static void updateDocumentList() {
        DatabaseReference libraryReference = firebaseDatabase.getReference("users/"+currentUserId+"/library");

        libraryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //update document array list
                documentList = new ArrayList<String>();
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    documentList.add(data.getKey()+ ": \n\n" + (String)data.child("text").getValue());
                }
                numberOfDocuments =(int)dataSnapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Checks if the current user's database has any documents that contain the detected text.
     * @param detectedText the text we are checking the database for
     * @return true if it does, false if it doesn't
     */
    public static boolean documentHasBeenSaved(String detectedText) {
        for (int i = 0; i < documentList.size(); i++){
            if (documentList.get(i).contains(detectedText)){
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a new document with the given text to the recipient user's database but set the owner as the current
     * user's email
     * @param documentText the text for the shared document
     * @param recipientUsername the unique Google account id of the user that the current user would like to share their
     *                    document with
     */
    public static void shareDocument(String documentText, String recipientUsername) {
        String recipientId = usernames.get(recipientUsername);
        String documentName = "Document"+ numberOfDocuments + " shared by " + currentUsername;
        DatabaseReference libraryReference = firebaseDatabase.getReference("users/"+recipientId+"/library");
        libraryReference.child(documentName).child("text").setValue(documentText);
        libraryReference.child(documentName).child("owner").setValue(currentUsername);
        updateDocumentList();
    }


}