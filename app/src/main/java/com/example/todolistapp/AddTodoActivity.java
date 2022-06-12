package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
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
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.todolistapp.pickers.CustomDate;
import com.example.todolistapp.pickers.DatePickerInterface;
import com.example.todolistapp.pickers.TimePickerInterface;
import com.example.todolistapp.room.TodoDao;
import com.example.todolistapp.room.TodoDatabase;
import com.example.todolistapp.room.TodoEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTodoActivity extends AppCompatActivity implements DatePickerInterface, TimePickerInterface {

    private EditText todoTitle;
    private EditText todoDescription;
    private Button todoCreationDate;
    private TextView todoCreationDateValue;
    private Button todoCreationTime;
    private TextView todoCreationTimeValue;
    private Button todoDeadlineDate;
    private TextView todoDeadlineDateValue;
    private Button todoDeadlineTime;
    private TextView todoDeadlineTimeValue;
    private Button todoAttachment;
    private TextView todoAttachmentValue;
    private Button todoAdd;
    private CheckBox todoNotification;
    private CheckBox todoDone;

    private CustomDate customCreationDate;
    private CustomDate customDeadlineDate;
    private TodoDao todoDao;
    private String attachmentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},1);

        TodoDatabase todoDatabase = TodoDatabase.getInstance(this);
        todoDao = todoDatabase.getTodoDao();
        customCreationDate = new CustomDate();
        customDeadlineDate = new CustomDate();

        todoTitle = findViewById(R.id.addTodoInputTitle);
        todoDescription = findViewById(R.id.addTodoInputDescription);
        todoNotification = findViewById(R.id.addTodoCheckBoxNotifications);
        todoDone = findViewById(R.id.addTodoCheckBoxDone);
        todoCreationDate = findViewById(R.id.addTodoInputCreationDate);
        todoCreationDateValue = findViewById(R.id.addTodoCreationDate);
        todoCreationTime = findViewById(R.id.addTodoInputCreationTime);
        todoCreationTimeValue = findViewById(R.id.addTodoCreationTime);
        todoDeadlineDate = findViewById(R.id.addTodoInputDeadlineDate);
        todoDeadlineDateValue = findViewById(R.id.addTodoDeadlineDate);
        todoDeadlineTime = findViewById(R.id.addTodoInputDeadlineTime);
        todoDeadlineTimeValue = findViewById(R.id.addTodoDeadlineTime);
        todoAttachment = findViewById(R.id.addTodoInputAttachment);
        todoAttachmentValue = findViewById(R.id.addTodoFilePath);
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
        startActivityForResult(chooseFile, 0);
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
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setTitle(todoTitle.getText().toString());
        todoEntity.setDescription(todoDescription.getText().toString());
        todoEntity.setCreationDate(creationDate);
        todoEntity.setDeadlineDate(deadlineDate);
        todoEntity.setNotification(todoNotification.isChecked());
        todoEntity.setDone(todoDone.isChecked());
        todoEntity.setAttachmentPath(attachmentPath);

        todoDao.insertTodos(todoEntity);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != 0 || data == null) {
            return;
        }
        Uri uri = data.getData();
        String sourcePath = uri.getPath();
        File sourceFile = new File(getCustomSourceFilename(sourcePath));

        String formattedFilename = uri.getLastPathSegment();

        String destinationFilename = getCustomDestinationFilename(formattedFilename);
        String destinationFolder = getExternalFilesDir(null) + "/TodoListApp/";
        createDirIfNotExists(destinationFolder);
        File destinationFile = new File(destinationFolder + destinationFilename);
        try {
            copyFile(sourceFile, destinationFile);
            attachmentPath = destinationFolder + destinationFilename;
            todoAttachmentValue.setText(destinationFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCurrentDate(int typeOfDate, int year, int month, int day) {
        if(typeOfDate == R.id.addTodoInputCreationDate){
            this.customCreationDate.setYear(year);
            this.customCreationDate.setMonth(month);
            this.customCreationDate.setDay(day);
            todoCreationDateValue.setText(String.format(Locale.US, "%d-%d-%d", year, month, day));
        }
        else if(typeOfDate == R.id.addTodoInputDeadlineDate){
            this.customDeadlineDate.setYear(year);
            this.customDeadlineDate.setMonth(month);
            this.customDeadlineDate.setDay(day);
            todoDeadlineDateValue.setText(String.format(Locale.US, "%d-%d-%d", year, month, day));
        }

    }

    @Override
    public void setCurrentTime(int typeOfTime, int hour, int minute) {
        if(typeOfTime == R.id.addTodoInputCreationTime){
            this.customCreationDate.setHour(hour);
            this.customCreationDate.setMinute(minute);
            todoCreationTimeValue.setText(String.format(Locale.US, "%d:%d", hour, minute));
        }
        else if(typeOfTime == R.id.addTodoInputDeadlineTime){
            this.customDeadlineDate.setHour(hour);
            this.customDeadlineDate.setMinute(minute);
            todoDeadlineTimeValue.setText(String.format(Locale.US, "%d:%d", hour, minute));
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

    private void copyFile(File source, File destination) throws IOException {

        try (FileChannel in = new FileInputStream(source).getChannel();
             FileChannel out = new FileOutputStream(destination).getChannel())
        {
            in.transferTo(0, in.size(), out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCustomDestinationFilename(String filePath){
        int index = filePath.lastIndexOf("/");
        if(index == -1){
            return filePath;
        }
        else{
            return filePath.substring(index + 1);
        }
    }

    private String getCustomSourceFilename(String filePath){
        String[] split = filePath.split(":", 2);
        int index = split[0].lastIndexOf("/");
        return "/storage/" + split[0].substring(index + 1) + "/" + split[1];
    }

    private void createDirIfNotExists(String destination) {

        File folder = new File(destination);

        if (!folder.isDirectory()) {

            if(folder.mkdirs()){
                System.out.println("Directory created");
            }
            else{
                System.out.println("Directory not created");
            }
        }
    }

}