package com.example.flint.Matches;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.flint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Matches extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;

    private String currentUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView=(RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(Matches.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), Matches.this);
        mRecyclerView.setAdapter(mMatchesAdapter);

        getUserMatchId();



    }


    private void getUserMatchId() {

        DatabaseReference matchDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID).child("connections").child("matches");
        matchDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot match : snapshot.getChildren()) {
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

            private void FetchMatchInformation(String key) {
                DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
                mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String userID = snapshot.getKey();
                            String name= "";
                            String profileImageUrl = "";

                            if(snapshot.child("name").getValue() !=null){
                                name = snapshot.child("name").getValue().toString();
                            }
                            if(snapshot.child("profileImageUrl").getValue() !=null){
                                profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                            }

                            MatchesObject obj = new MatchesObject(userID,name,profileImageUrl);
                            resultsMatches.add(obj);
                            mMatchesAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }


    private ArrayList<MatchesObject> resultsMatches = new ArrayList<MatchesObject>();
    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }


}