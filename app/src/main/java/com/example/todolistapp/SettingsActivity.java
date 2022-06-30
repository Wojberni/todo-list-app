package com.example.todolistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.todolistapp.notifications.ScheduleNotification;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText filterCategory;
    private Button saveSettingsButton;
    private Spinner notificationTimeSpinner;
    private Integer notificationTime;
    private String category;
    private ScheduleNotification scheduleNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Intent settings_intent = getIntent();
        category = settings_intent.getStringExtra("settings_category");
        scheduleNotification = ScheduleNotification.getInstance();
        notificationTime = scheduleNotification.getNotificationOffset();

        filterCategory = findViewById(R.id.settingsFilterInput);
        saveSettingsButton = findViewById(R.id.settingsSaveButton);
        notificationTimeSpinner = findViewById(R.id.settingsNotificationTimeSpinner);
        notificationTimeSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_category_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        notificationTimeSpinner.setAdapter(adapter);

        if(category != null && !category.isEmpty()) {
            filterCategory.setText(category);
        }

        notificationTimeSpinner.setSelection(adapter.getPosition(notificationTime.toString()));

        saveSettingsButton.setOnClickListener(v -> {
            category = filterCategory.getText().toString();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("settings_category", category);
            scheduleNotification.setNotificationOffset(notificationTime);
            startActivity(intent);
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        notificationTime = Integer.parseInt(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}