package com.example.flint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.flint.Cards.ArrayAdapter;
import com.example.flint.Cards.Cards;
import com.example.flint.Matches.Matches;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Cards cards_data[];
    private ArrayAdapter arrayAdapter;
    private int i;

    private FirebaseAuth firebaseAuth;

    private String currentUID;

    private DatabaseReference usersDatabase;

    ListView listView;
    List<Cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = firebaseAuth.getCurrentUser().getUid();

        checkUserGender();

        rowItems = new ArrayList<Cards>();

        arrayAdapter = new ArrayAdapter(this, R.layout.item, rowItems);
        arrayAdapter.notifyDataSetChanged();

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);


        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

                Cards obj = (Cards) dataObject;
                String userID = obj.getUserID();
                usersDatabase.child(userID).child("connections").child("How_About_NO").child(currentUID).setValue(true);
                Toast.makeText(MainActivity.this, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {

                Cards obj = (Cards) dataObject;
                String userID = obj.getUserID();
                usersDatabase.child(userID).child("connections").child("Yes_Please").child(currentUID).setValue(true);
                isConnectionMatch(userID);
                Toast.makeText(MainActivity.this, "Right!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
//                // Ask for more data here

//                al.add("XML ".concat(String.valueOf(i)));
//                arrayAdapter.notifyDataSetChanged();
//                Log.d("LIST", "notified");
//                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isConnectionMatch(final String userID) {

        DatabaseReference currentUserConnectionsDatabase = usersDatabase.child(currentUID).child("connections").child("Yes_Please").child(userID);
        currentUserConnectionsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "New Connection!", Toast.LENGTH_SHORT).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDatabase.child(snapshot.getKey()).child("connections").child("matches").child(currentUID).setValue(true);
                    usersDatabase.child(snapshot.getKey()).child("connections").child("matches").child(currentUID).child("ChatId").setValue(key);

                    usersDatabase.child(currentUID).child("connections").child("matches").child(snapshot.getKey()).setValue(true);
                    usersDatabase.child(currentUID).child("connections").child("matches").child(snapshot.getKey()).child("ChatId").setValue(key);



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void logoutUser(View view)
    {

        firebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this, Login_Registration.class);
        startActivity(intent);
        finish();
        return;

    }



    private String userGender;
    private String oppositeUserGender;

    public void checkUserGender() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mUserDatabase = usersDatabase.child(user.getUid());

        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    if(snapshot.child("sex").getValue() != null) {

                        userGender = snapshot.child("sex").getValue().toString();

                        switch (userGender) {
                            case "Male":
                                oppositeUserGender = "Female";
                                break;
                            case "Female":
                                oppositeUserGender = "Male";
                                break;
                        }

                        getOppositeGenderUsers();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getOppositeGenderUsers()
    {

        usersDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("sex").getValue() != null){

                    if (snapshot.exists() && !snapshot.child("connections").child("How_About_NO").hasChild(currentUID) && !snapshot.child("connections").child("Yes_Please").hasChild(currentUID)
                            && !snapshot.child("sex").getValue().toString().equals(userGender)) {

                        String profileImageUrl = "default";

                        if (!snapshot.child("profileImageUrl").getValue().equals("default")) {
                            profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                        }

                        Cards Item = new Cards(snapshot.getKey(), snapshot.child("name").getValue().toString(), profileImageUrl);
                        rowItems.add(Item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void goToSettings (View view){
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
        return;
    }

    public void goToMatches (View view){
        Intent intent = new Intent(MainActivity.this, Matches.class);
        startActivity(intent);
        return;
    }

}








