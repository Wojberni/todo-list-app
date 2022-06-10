package com.example.todolistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.todolistapp.adapter.ToDoListAdapter;
import com.example.todolistapp.adapter.ToDoViewClickListener;
import com.example.todolistapp.model.ToDoTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ToDoViewClickListener {

    RecyclerView.Adapter<ToDoListAdapter.ToDoViewHolder> adapter;
    ArrayList<ToDoTask> toDoTaskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toDoTaskList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ToDoListAdapter(toDoTaskList, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.floatingAddTodoButton);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddTodoActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()){
            case R.id.cardViewDelete:
                toDoTaskList.remove(position);
                break;
            case R.id.cardViewSave:
                break;
            case R.id.cardViewNotify:
                toDoTaskList.get(position).setNotification(!toDoTaskList.get(position).isNotification());
                break;
            case R.id.cardViewDone:
                toDoTaskList.get(position).setDone(!toDoTaskList.get(position).isDone());
                break;
            default:
                break;
        }
        adapter.notifyDataSetChanged();
    }
}