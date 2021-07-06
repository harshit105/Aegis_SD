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

public class Todo extends AppCompatActivity {
    private Toolbar toolbar;
    static ArrayList<String> todo= new ArrayList<String>();
    static ArrayAdapter<String> arrayAdapter1;
    String date= DateFormat.getDateInstance().format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        toolbar=findViewById(R.id.grocery_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("To-Do List");

        ListView list=(ListView)findViewById(R.id.ListView);
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.example.aegis", Context.MODE_PRIVATE);
        HashSet<String> set=(HashSet<String>)sharedPreferences.getStringSet("todo",null);
        if(set==null){
            todo.add(date);
        }
        else{
            todo=new ArrayList<String>(set);
        }

        arrayAdapter1=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,todo);
        list.setAdapter(arrayAdapter1);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),todo_note.class);
                intent.putExtra("noteId1",position);
                startActivity(intent);
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(Todo.this).setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you Sure?").setMessage("Do you want to delete this diary entry?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                todo.remove(position);
                                arrayAdapter1.notifyDataSetChanged();
                                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.example.aegis", Context.MODE_PRIVATE);
                                HashSet<String> set=new HashSet<String>(Todo.todo);
                                sharedPreferences.edit().putStringSet("todo",set).apply();
                            }
                        }).setNegativeButton("No",null).show();
                return true;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_add,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.todo_add):
                startActivity(new Intent(getApplicationContext(), todo_note.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}