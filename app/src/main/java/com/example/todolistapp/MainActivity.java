package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.todolistapp.adapter.ToDoListAdapter;
import com.example.todolistapp.adapter.ToDoViewClickListener;
import com.example.todolistapp.notifications.ScheduleNotification;
import com.example.todolistapp.room.TodoDao;
import com.example.todolistapp.room.TodoDatabase;
import com.example.todolistapp.room.TodoEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ToDoViewClickListener {

    private RecyclerView.Adapter<ToDoListAdapter.ToDoViewHolder> adapter;
    private ArrayList<TodoEntity> toDoTaskList;
    private TodoDao todoDao;
    private Toolbar todoToolbar;
    private boolean hideTodos = true;
    private String category;
    private ScheduleNotification scheduleNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent settings_intent = getIntent();
        category = settings_intent.getStringExtra("settings_category");
        ScheduleNotification.createNotificationsChannel(this);
        scheduleNotification = ScheduleNotification.getInstance();

        todoToolbar = findViewById(R.id.todoToolbar);
        setSupportActionBar(todoToolbar);

        TodoDatabase todoDatabase = TodoDatabase.getInstance(this);
        todoDao = todoDatabase.getTodoDao();

        List<TodoEntity> todoEntities = todoDao.getAllTodos();
        toDoTaskList = new ArrayList<>(todoEntities);
        if(category != null && !category.isEmpty()) {
            toDoTaskList = filterTodos(toDoTaskList, category);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ToDoListAdapter(toDoTaskList, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addToDoButton = findViewById(R.id.floatingAddTodoButton);
        addToDoButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddTodoActivity.class);
            startActivity(intent);
        });

        FloatingActionButton settingsButton = findViewById(R.id.floatingSettingsButton);
        settingsButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            intent.putExtra("settings_category", category);
            startActivity(intent);
        });

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            toDoTaskList.clear();
            toDoTaskList.addAll(todoDao.getAllTodosBySearchTerm(query));
            toDoTaskList = filterTodos(toDoTaskList, category);
            adapter.notifyDataSetChanged();
        }


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
                scheduleNotification.cancelNotification(this, (int) toDoTaskList.get(position).getId());
                todoDao.deleteTodos(toDoTaskList.get(position));
                updateTodoUI();
                break;
            case R.id.cardViewEdit:
                Intent intent = new Intent(getApplicationContext(), EditTodoActivity.class);
                intent.putExtra("todo_item", toDoTaskList.get(position));
                startActivity(intent);
                break;
            case R.id.cardViewAttachment:
                previewFile(toDoTaskList.get(position).getAttachmentPath());
                break;
            case R.id.cardViewDone:
                toDoTaskList.get(position).setDone(!toDoTaskList.get(position).isDone());
                todoDao.updateTodos(toDoTaskList.get(position));
                updateTodoUI();
                break;
            case R.id.cardViewNotify:
                if(toDoTaskList.get(position).isNotification()) {
                    scheduleNotification.cancelNotification(this, (int) toDoTaskList.get(position).getId());
                } else {
                    scheduleNotification.createNotification(
                            this,
                            (int) toDoTaskList.get(position).getId(),
                            toDoTaskList.get(position).getTitle(),
                            toDoTaskList.get(position).getDescription(),
                            toDoTaskList.get(position).getDeadlineDate());
                }
                toDoTaskList.get(position).setNotification(!toDoTaskList.get(position).isNotification());
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
                if(!hideTodos){
                    toDoTaskList.clear();
                    toDoTaskList.addAll(todoDao.getAllTodos());
                }
                else{
                    toDoTaskList.clear();
                    toDoTaskList.addAll(todoDao.getAllTodosNotDone());
                }
                hideTodos = !hideTodos;
                toDoTaskList = filterTodos(toDoTaskList, category);
                adapter.notifyDataSetChanged();
                return true;

            case R.id.actionSortByDeadlineTime:
                toDoTaskList.sort(Comparator.comparing(TodoEntity::getCreationDate));
                toDoTaskList = filterTodos(toDoTaskList, category);
                adapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void updateTodoUI(){
        List<TodoEntity> todoEntities = todoDao.getAllTodos();
        toDoTaskList.clear();
        toDoTaskList.addAll(todoEntities);
        toDoTaskList = filterTodos(toDoTaskList, category);
        adapter.notifyDataSetChanged();
    }

    private void previewFile(String filePath){
        File file = new File(filePath);

        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".file_provider", file);
        String mime = getContentResolver().getType(uri);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private ArrayList<TodoEntity> filterTodos(ArrayList<TodoEntity> todoEntities, String category){
        if(category == null || category.isEmpty()){
            return todoEntities;
        }
        return todoEntities.stream()
                .filter(todoEntity -> todoEntity.getCategory().equals(category))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}