package com.example.aegis;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.TimeZone;

import Model.Data;
import Model.Schedule_data;

import static java.security.AccessController.getContext;

public class Schedule extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private FloatingActionButton add;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private FirebaseRecyclerOptions<Schedule_data> options;
    private FirebaseRecyclerAdapter<Schedule_data,MyViewHolder> adapter;
    private TextView itemcount;
    private String task,date,time,alertTime,description;
    private String postkey;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private PriorityQueue<Long> timequeue=new PriorityQueue<Long>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        itemcount=findViewById(R.id.Task_count);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uID=mUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Schedule List").child(uID);
        mDatabase.keepSynced(true);

        recyclerView=findViewById(R.id.recycler_schedule);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count=0;
                for(DataSnapshot snap:snapshot.getChildren()){
                    Schedule_data schedule_data=snap.getValue(Schedule_data.class);
                    count+=1;
                    String ct=String.valueOf(count);
                    itemcount.setText(ct);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add=(FloatingActionButton)findViewById(R.id.add_schedule);
        add.setOnClickListener(this);

        Query query=mDatabase.orderByKey();
        options=new FirebaseRecyclerOptions.Builder<Schedule_data>().setQuery(query,Schedule_data.class).build();
        adapter=new FirebaseRecyclerAdapter<Schedule_data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i, @NonNull Schedule_data schedule_data) {
                viewHolder.setDate(schedule_data.getDate());
                viewHolder.setTime(schedule_data.getTime());
                viewHolder.setTask(schedule_data.getTask());
                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postkey=getRef(i).getKey();
                        task=schedule_data.getTask();
                        date=schedule_data.getDate();
                        time=schedule_data.getTime();
                        alertTime=schedule_data.getAlertTime();
                        description=schedule_data.getDescription();
                        updateData();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater Inflater=LayoutInflater.from(parent.getContext());
                View view=Inflater.inflate(R.layout.schedule_item_data,parent,false);
                MyViewHolder holder=new MyViewHolder(view);
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
        AlertDialog.Builder mydialog = new AlertDialog.Builder(Schedule.this);
        LayoutInflater Inflater = LayoutInflater.from(Schedule.this);
        View myview = Inflater.inflate(R.layout.schedule_input, null);

        AlertDialog dialog = mydialog.create();
        dialog.setView(myview);

        EditText Task = myview.findViewById(R.id.Task);

        EditText Date = myview.findViewById(R.id.Date);
        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar= Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog=new DatePickerDialog(
                        mydialog.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        mDateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String date = dayOfMonth+"/"+month+"/"+year;
                Date.setText(date);
            }
        };



        EditText Time = myview.findViewById(R.id.Time);
        Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                int hours=calendar.get(Calendar.HOUR_OF_DAY);
                int mins=calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog=new TimePickerDialog(mydialog.getContext(), R.style.Widget_MaterialComponents_Light_ActionBar_Solid, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar c=Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        c.set(Calendar.MINUTE,minute);
                        c.setTimeZone(TimeZone.getDefault());
                        SimpleDateFormat format=new SimpleDateFormat("k:mm");
                        String time=format.format(c.getTime());
                        Time.setText(time);

                    }
                },hours,mins,true);
                timePickerDialog.show();


            }
        });

        EditText Description = myview.findViewById(R.id.Description);

        Button save = myview.findViewById(R.id.Schedule_save);
        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String mTask = Task.getText().toString().trim();

                String mDate = Date.getText().toString().trim();

                String mTime = Time.getText().toString().trim();

                String mDescription = Description.getText().toString().trim();


                if (TextUtils.isEmpty(mTask)) {
                    Task.setError("Task Required");
                    return;
                }
                if (TextUtils.isEmpty(mDate)) {
                    Date.setError("Date Required");
                    return;
                }
                if (TextUtils.isEmpty(mTime)) {
                    Time.setError("Time Required");
                    return;
                }
                if (TextUtils.isEmpty(mDescription)) {
                    Description.setError("Description Required");
                    return;
                }

                String myDate=mDate+" "+mTime;
                LocalDateTime localDateTime = LocalDateTime.parse(myDate,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm") );
                long millis = localDateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli();

                String id = mDatabase.push().getKey();
                Schedule_data schedule_data = new Schedule_data(mDate,mTime,"10mins",mTask,mDescription,id);
                mDatabase.child(id).setValue(schedule_data);
                setAlarm(millis-(10*60*1000), mTask,mDescription);
                Toast.makeText(Schedule.this, "Task added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    @Override
    protected void onStart(){
        super.onStart();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View myView;
        public MyViewHolder(View itemView){
            super(itemView);
            myView=itemView;
        }
        public void setDate(String date){
            TextView myDate=myView.findViewById(R.id.schedule_item_data_date);
            myDate.setText(date);
        }
        public void setTime(String time){
            TextView myTime=myView.findViewById(R.id.schedule_item_data_time);
            myTime.setText(time);
        }
        public void setTask(String task){
            TextView myTask=myView.findViewById(R.id.schedule_item_data_task);
            myTask.setText(task);
        }
    }


    public void updateData(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(Schedule.this);
        LayoutInflater Inflater=LayoutInflater.from(Schedule.this);
        View upview=Inflater.inflate(R.layout.update_schedule_input,null);
        AlertDialog dialog=mydialog.create();
        dialog.setView(upview);
        EditText updateTask=upview.findViewById(R.id.Edit_task);
        EditText updateDate=upview.findViewById(R.id.Edit_date);
        updateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar= Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog=new DatePickerDialog(
                        mydialog.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        mDateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String date = dayOfMonth+"/"+month+"/"+year;
                updateDate.setText(date);
            }
        };

        EditText updateTime=upview.findViewById(R.id.Edit_time);
        updateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                int hours=calendar.get(Calendar.HOUR_OF_DAY);
                int mins=calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog=new TimePickerDialog(mydialog.getContext(), R.style.Widget_MaterialComponents_Light_ActionBar_Solid, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar c=Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        c.set(Calendar.MINUTE,minute);
                        c.setTimeZone(TimeZone.getDefault());
                        SimpleDateFormat format=new SimpleDateFormat("h:mm a");
                        String time=format.format(c.getTime());
                        updateTime.setText(time);

                    }
                },hours,mins,false);
                timePickerDialog.show();

            }
        });

        EditText updateDescription=upview.findViewById(R.id.Edit_description);
        updateTask.setText(task);
        updateTask.setSelection(task.length());
        updateDate.setText(date);
        updateDate.setSelection(date.length());
        updateTime.setText(time);
        updateTime.setSelection(time.length());
        updateDescription.setText(description);
        updateDescription.setSelection(description.length());
        Button update=upview.findViewById(R.id.schedule_update);
        Button delete=upview.findViewById(R.id.schedule_delete);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String upTask=updateTask.getText().toString().trim();
                String upDate=updateDate.getText().toString().trim();
                String upTime=updateTime.getText().toString().trim();
                String upDescription=updateDescription.getText().toString().trim();

                if(TextUtils.isEmpty(upTask)){
                    updateTask.setError("Task Required");
                    return;
                }
                if(TextUtils.isEmpty(upDate)){
                    updateDate.setError("Date Required");
                    return;
                }
                if(TextUtils.isEmpty(upTime)){
                    updateTime.setError("Time Required");
                    return;
                }
                if(TextUtils.isEmpty(upDescription)){
                    updateDescription.setError("Description Required");
                    return;
                }
                String id=postkey;
                String date= DateFormat.getDateInstance().format(new Date());
                Schedule_data schedule_data=new Schedule_data(upDate,upTime,"10mins",upTask,upDescription,id);
                mDatabase.child(id).setValue(schedule_data);
                Toast.makeText(Schedule.this,"Task updated",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=postkey;
                mDatabase.child(id).removeValue();
                Toast.makeText(Schedule.this,"Task deleted",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void setAlarm(long timeinMillis,String title,String Description){
        AlarmManager am=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        final int id = (int) System.currentTimeMillis();
        Intent intent=new Intent(this,MyAlarm.class);
        intent.putExtra("title", title);
        intent.putExtra("Description", Description);
        intent.putExtra("id",id);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP,timeinMillis,pendingIntent);
    }
}