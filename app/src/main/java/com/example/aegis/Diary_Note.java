package com.example.aegis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;

public class Diary_Note extends AppCompatActivity {
    private Toolbar toolbar;
    int noteID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary__note);

        toolbar=findViewById(R.id.grocery_toolbar);
        setSupportActionBar(toolbar);
        String date= DateFormat.getDateInstance().format(new Date());
        getSupportActionBar().setTitle(date);
        EditText editText=(EditText)findViewById(R.id.editText);
        Intent intent=getIntent();
        noteID=intent.getIntExtra("noteId",-1);
        if(noteID!=-1){
            editText.setText(Diary.diary.get(noteID));
        }
        else{
            Diary.diary.add("");
            noteID=Diary.diary.size()-1;
            Diary.arrayAdapter.notifyDataSetChanged();
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Diary.diary.set(noteID,String.valueOf(s));
                Diary.arrayAdapter.notifyDataSetChanged();
                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.example.aegis", Context.MODE_PRIVATE);
                HashSet<String> set=new HashSet<String>(Diary.diary);
                sharedPreferences.edit().putStringSet("diary",set).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_share_diary,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.diary_share):
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
                share.putExtra(Intent.EXTRA_TEXT, Diary.diary.get(noteID));

                startActivity(Intent.createChooser(share, "Share"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}