package com.example.mike.dnd_mobile_app;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class CharacterList extends AppCompatActivity {
    ArrayList<String> char_list = new ArrayList<>();
    TextView noCharacter;
    RecyclerView recyclerView;
    CharacterAdapter adapter;
    public static final String userPREFERENCES = "CharsCreated" ;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);
        sharedpreferences = getSharedPreferences(userPREFERENCES, MODE_PRIVATE);

        recyclerView = findViewById(R.id.myRecyclerView);
        noCharacter = findViewById(R.id.empty_character_view);

        char_list = getAllCharacters();
        Collections.sort(char_list);
        adapter = new CharacterAdapter(this, char_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        toggleEmptyCharacters();
    }

    @Override
    public void onResume(){
        super.onResume();
        viewList();
    }

    public ArrayList<String> getAllCharacters() {
        ArrayList<String> characters = new ArrayList<>();
        Map<String,?> keys = sharedpreferences.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            characters.add(entry.getKey());
        }
        return characters;
    }

    public void toggleEmptyCharacters() {
        if (adapter.getItemCount() > 0) {
            noCharacter.setVisibility(TextView.GONE);
        } else {
            recyclerView.setVisibility(RecyclerView.GONE);
        }
    }

    public void viewList() {
        setContentView(R.layout.activity_character_list);

        recyclerView = findViewById(R.id.myRecyclerView);
        noCharacter = findViewById(R.id.empty_character_view);
        char_list.clear();
        char_list = getAllCharacters();
        Collections.sort(char_list);
        adapter = new CharacterAdapter(this, char_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        toggleEmptyCharacters();
    }

}
