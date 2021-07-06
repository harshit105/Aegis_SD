package com.example.aegis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import Model.Data;

public class Activity2 extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private FloatingActionButton add;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private FirebaseRecyclerOptions<Data> options;
    private FirebaseRecyclerAdapter<Data, MyViewHolder> adapter;
    private TextView itemcount;
    private String type;
    private String quantity;
    private String postkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);

        toolbar = findViewById(R.id.grocery_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("List");

        itemcount = findViewById(R.id.Grocery_count);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uID = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Activity2").child(uID);
        mDatabase.keepSynced(true);

        recyclerView = findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);
                    count += 1;
                    String ct = String.valueOf(count);
                    itemcount.setText(ct);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add = findViewById(R.id.add);
        add.setOnClickListener(this);

        Query query = mDatabase.orderByKey();
        options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(query, Data.class).build();
        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i, @NonNull Data data) {
                viewHolder.setDate(data.getDate());
                viewHolder.setType(data.getItem());
                viewHolder.setCount(data.getQuantity());
                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postkey = getRef(i).getKey();
                        type = data.getItem();
                        quantity = data.getQuantity();
                        updateData();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater Inflater = LayoutInflater.from(parent.getContext());
                View view = Inflater.inflate(R.layout.item_data, parent, false);
                MyViewHolder holder = new MyViewHolder(view);
                return holder;
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        customdialog();
    }

    private void customdialog() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(Activity2.this);
        LayoutInflater Inflater = LayoutInflater.from(Activity2.this);
        View myview = Inflater.inflate(R.layout.input, null);

        AlertDialog dialog = mydialog.create();
        dialog.setView(myview);

        EditText item = myview.findViewById(R.id.item);
        EditText quantity = myview.findViewById(R.id.quantity);

        Button save = myview.findViewById(R.id.grocery_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mitem = item.getText().toString().trim();
                String mquantity = quantity.getText().toString().trim();
                if (TextUtils.isEmpty(mitem)) {
                    item.setError("Item Name Required");
                    return;
                }
                if (TextUtils.isEmpty(mquantity)) {
                    quantity.setError("Quantity Required");
                    return;
                }
                String id = mDatabase.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(mitem, mquantity, date, id);
                mDatabase.child(id).setValue(data);
                Toast.makeText(Activity2.this, "Item added to your list", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View myView;

        public MyViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setType(String type) {
            TextView mytype = myView.findViewById(R.id.type);
            mytype.setText(type);
        }

        public void setDate(String date) {
            TextView myDate = myView.findViewById(R.id.date);
            myDate.setText(date);
        }

        public void setCount(String count) {
            TextView myCount = myView.findViewById(R.id.count);
            myCount.setText(count);
        }
    }

    public void updateData() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(Activity2.this);
        LayoutInflater Inflater = LayoutInflater.from(Activity2.this);
        View upview = Inflater.inflate(R.layout.update_input, null);
        AlertDialog dialog = mydialog.create();
        dialog.setView(upview);
        EditText update_item = upview.findViewById(R.id.Edit_item);
        EditText update_quantity = upview.findViewById(R.id.Edit_quantity);
        update_item.setText(type);
        update_item.setSelection(type.length());
        update_quantity.setText(quantity);
        update_quantity.setSelection(quantity.length());
        String date = DateFormat.getDateInstance().format(new Date());
        Button update = upview.findViewById(R.id.grocery_update);
        Button delete = upview.findViewById(R.id.grocery_delete);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uptype = update_item.getText().toString().trim();
                String upquantity = update_quantity.getText().toString().trim();
                if (TextUtils.isEmpty(uptype)) {
                    update_item.setError("Item Name Required");
                    return;
                }
                if (TextUtils.isEmpty(upquantity)) {
                    update_quantity.setError("Quantity Required");
                    return;
                }
                String id = postkey;
                String date = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(uptype, upquantity, date, id);
                mDatabase.child(id).setValue(data);
                Toast.makeText(Activity2.this, "Item updated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = postkey;
                mDatabase.child(id).removeValue();
                Toast.makeText(Activity2.this, "Item deleted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}