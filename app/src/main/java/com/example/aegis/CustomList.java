package com.example.aegis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class CustomList extends AppCompatActivity{
    private Toolbar toolbar;
    Intent intent;
    Button add,newbtn;
    public String btnname;
    public int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        LinearLayout layout=(LinearLayout) findViewById(R.id.rootlayout);

        toolbar=findViewById(R.id.sh_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Custom List");
    }

    private void btn_showDialog() {
        final AlertDialog.Builder alert=new AlertDialog.Builder(CustomList.this);
        View mView=getLayoutInflater().inflate(R.layout.custom_list_input,null);
        final EditText txt_input=(EditText)mView.findViewById(R.id.list_name);
        Button ok=(Button)mView.findViewById(R.id.add_list);
        Button cancel=(Button)mView.findViewById(R.id.cancel_list);
        alert.setView(mView);
        final AlertDialog alertDialog=alert.create();
//        alertDialog.setCanceledOnTouchOutside(false);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnname=txt_input.getText().toString();
                addButton(btnname);
                Toast.makeText(CustomList.this,  "New List created", Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void addButton(String x) {
        Resources r = this.getResources();
        int top_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,32,r.getDisplayMetrics());
        LinearLayout layout=(LinearLayout) findViewById(R.id.rootlayout);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final Button newBtn=new Button(this);
        lp.setMargins(0,top_px,0,0);
        newBtn.setLayoutParams(lp);
        newBtn.setText(x);
        newBtn.setTextColor(Color.BLACK);
        Drawable d1=getResources().getDrawable(R.drawable.customlistbutton);
        newBtn.setBackground(d1);
        layout.addView(newBtn);
        registerForContextMenu(newBtn);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (i){
                    case 1:
                        intent = new Intent(CustomList.this,Activity1.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(CustomList.this, Activity2.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(CustomList.this,Activity3.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(CustomList.this,Activity4.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(CustomList.this,Activity5.class);
                        startActivity(intent);
                        break;
                    default:
                        intent = new Intent(CustomList.this,Activity5.class);
                        startActivity(intent);
                        break;
                }
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
        switch (item.getItemId()){
            case(R.id.diary_add):
                btn_showDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}