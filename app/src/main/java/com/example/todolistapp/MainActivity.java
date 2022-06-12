package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.todolistapp.adapter.ToDoListAdapter;
import com.example.todolistapp.adapter.ToDoViewClickListener;
import com.example.todolistapp.room.TodoDao;
import com.example.todolistapp.room.TodoDatabase;
import com.example.todolistapp.room.TodoEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ToDoViewClickListener {

    RecyclerView.Adapter<ToDoListAdapter.ToDoViewHolder> adapter;
    ArrayList<TodoEntity> toDoTaskList;
    TodoDao todoDao;
    Toolbar todoToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoToolbar = findViewById(R.id.todoToolbar);
        setSupportActionBar(todoToolbar);

        TodoDatabase todoDatabase = TodoDatabase.getInstance(this);
        todoDao = todoDatabase.getTodoDao();

        List<TodoEntity> todoEntities = todoDao.getAllTodos();
        toDoTaskList = new ArrayList<>(todoEntities);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()){
            case R.id.cardViewDelete:
                todoDao.deleteTodos(toDoTaskList.get(position));
                updateTodoUI();
                break;
            case R.id.cardViewEdit:
                break;
            case R.id.cardViewNotify:
                toDoTaskList.get(position).setNotification(!toDoTaskList.get(position).isNotification());
                todoDao.updateTodos(toDoTaskList.get(position));
                updateTodoUI();
                break;
            case R.id.cardViewDone:
                toDoTaskList.get(position).setDone(!toDoTaskList.get(position).isDone());
                todoDao.updateTodos(toDoTaskList.get(position));
                updateTodoUI();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionHideTodos:
                return true;

            case R.id.actionFilterByCategory:

                return true;

            case R.id.actionSetNotificationTime:
                return true;

            case R.id.actionSortByDeadlineTime:
                toDoTaskList.sort(Comparator.comparing(TodoEntity::getCreationDate));
                adapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    public void updateTodoUI(){
        List<TodoEntity> todoEntities = todoDao.getAllTodos();
        toDoTaskList.clear();
        toDoTaskList.addAll(todoEntities);
        adapter.notifyDataSetChanged();
    }
}