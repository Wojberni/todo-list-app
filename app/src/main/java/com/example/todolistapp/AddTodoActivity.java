package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.todolistapp.model.ToDoTask;
import com.example.todolistapp.pickers.CustomDate;
import com.example.todolistapp.pickers.DatePickerInterface;
import com.example.todolistapp.pickers.TimePickerInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTodoActivity extends AppCompatActivity implements DatePickerInterface, TimePickerInterface {

    private EditText todoTitle;
    private EditText todoDescription;
    private Button todoCreationDate;
    private Button todoCreationTime;
    private Button todoDeadlineDate;
    private Button todoDeadlineTime;
    private Button todoAttachment;
    private Button todoAdd;
    private CheckBox todoNotification;
    private CheckBox todoDone;

    private CustomDate customCreationDate;
    private CustomDate customDeadlineDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo);

        todoTitle = findViewById(R.id.addTodoInputTitle);
        todoDescription = findViewById(R.id.addTodoInputDescription);
        todoNotification = findViewById(R.id.addTodoCheckBoxNotifications);
        todoDone = findViewById(R.id.addTodoCheckBoxDone);
        todoCreationDate = findViewById(R.id.addTodoInputCreationDate);
        todoCreationTime = findViewById(R.id.addTodoInputCreationTime);
        todoDeadlineDate = findViewById(R.id.addTodoInputDeadlineDate);
        todoDeadlineTime = findViewById(R.id.addTodoInputDeadlineTime);
        todoAttachment = findViewById(R.id.addTodoInputAttachment);
        todoAdd = findViewById(R.id.addTodoInputButton);
        todoCreationDate.setOnClickListener(this::showDatePickerDialog);
        todoCreationTime.setOnClickListener(this::showTimePickerDialog);
        todoDeadlineDate.setOnClickListener(this::showDatePickerDialog);
        todoDeadlineTime.setOnClickListener(this::showTimePickerDialog);
        todoAttachment.setOnClickListener(this::addAttachment);
        todoAdd.setOnClickListener(this::addTodo);
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment(this, view.getId());
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment(this, view.getId());
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void addAttachment(View view) {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
//        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
//        Uri uri = data.getData();
//        String src = uri.getPath();
    }

    public void addTodo(View view) {
        String creationDateText = String.format(Locale.US, "%d-%d-%d %d:%d:%d", customCreationDate.getYear(),
                customCreationDate.getMonth(), customCreationDate.getDay(),
                customCreationDate.getHour(), customCreationDate.getMinute(), 0);
        String deadlineDateText = String.format(Locale.US, "%d-%d-%d %d:%d:%d",
                customDeadlineDate.getYear(), customDeadlineDate.getMonth(),
                customDeadlineDate.getDay(), customDeadlineDate.getHour(),
                customDeadlineDate.getMinute(), 0);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date creationDate = null;
        Date deadlineDate = null;
        try{
            creationDate = format.parse(creationDateText);
            deadlineDate = format.parse(deadlineDateText);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ToDoTask toDoTask = new ToDoTask(todoTitle.getText().toString(),
                todoDescription.getText().toString(), creationDate, deadlineDate,
                todoDone.isChecked(), todoNotification.isChecked(), false);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setCurrentDate(int typeOfDate, int year, int month, int day) {
        if(typeOfDate == R.id.addTodoInputCreationDate){
            this.customCreationDate.setYear(year);
            this.customCreationDate.setMonth(month);
            this.customCreationDate.setDay(day);
        }
        else if(typeOfDate == R.id.addTodoInputDeadlineDate){
            this.customDeadlineDate.setYear(year);
            this.customDeadlineDate.setMonth(month);
            this.customDeadlineDate.setDay(day);
        }
    }

    @Override
    public void setCurrentTime(int typeOfTime, int hour, int minute) {
        if(typeOfTime == R.id.addTodoInputCreationTime){
            this.customCreationDate.setHour(hour);
            this.customCreationDate.setMinute(minute);
        }
        else if(typeOfTime == R.id.addTodoInputDeadlineTime){
            this.customDeadlineDate.setHour(hour);
            this.customDeadlineDate.setMinute(minute);

        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        DatePickerInterface datePickerInterface;
        int typeOfDate;

        DatePickerFragment(DatePickerInterface datePickerInterface, int typeOfDate) {
            this.datePickerInterface = datePickerInterface;
            this.typeOfDate = typeOfDate;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            datePickerInterface.setCurrentDate(typeOfDate, year, month, day);
        }

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        TimePickerInterface timePickerInterface;
        int typeOfTime;

        TimePickerFragment(TimePickerInterface timePickerInterface, int typeOfTime) {
            this.timePickerInterface = timePickerInterface;
            this.typeOfTime = typeOfTime;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            timePickerInterface.setCurrentTime(typeOfTime, hour, minute);
        }
    }

}