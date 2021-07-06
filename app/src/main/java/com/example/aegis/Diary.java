package com.example.aegis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class Diary extends AppCompatActivity {
    private Toolbar toolbar;
    static String str;
    static ArrayList<String> diary= new ArrayList<String>();
    static ArrayAdapter<String> arrayAdapter;
    String date= DateFormat.getDateInstance().format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        toolbar=findViewById(R.id.grocery_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Diary");

        ListView list=(ListView)findViewById(R.id.ListView);
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.example.aegis", Context.MODE_PRIVATE);
        HashSet<String> set=(HashSet<String>)sharedPreferences.getStringSet("diary",null);
        if(set==null){
            diary.add(date);
        }
        else{
            diary=new ArrayList<String>(set);
        }

        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,diary);
        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),Diary_Note.class);
                intent.putExtra("noteId",position);
                startActivity(intent);
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(Diary.this).setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you Sure?").setMessage("Do you want to delete this diary entry?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                diary.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.example.aegis", Context.MODE_PRIVATE);
                                HashSet<String> set=new HashSet<String>(Diary.diary);
                                sharedPreferences.edit().putStringSet("diary",set).apply();
                            }
                        }).setNegativeButton("No",null).show();
                return true;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.diary_add,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.diary_add):
                startActivity(new Intent(getApplicationContext(), Diary_Note.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}