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
import com.example.todolistapp.room.TodoDao;
import com.example.todolistapp.room.TodoDatabase;
import com.example.todolistapp.room.TodoEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ToDoViewClickListener {

    RecyclerView.Adapter<ToDoListAdapter.ToDoViewHolder> adapter;
    ArrayList<TodoEntity> toDoTaskList;
    TodoDao todoDao;
    Toolbar todoToolbar;
    private boolean hideTodos = false;

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

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            toDoTaskList.clear();
            toDoTaskList.addAll(todoDao.getAllTodosBySearchTerm(query));
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
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionHideTodos:
                if(hideTodos){
                    hideTodos = false;
                    toDoTaskList.clear();
                    toDoTaskList.addAll(todoDao.getAllTodos());
                }
                else{
                    hideTodos = true;
                    toDoTaskList.clear();
                    toDoTaskList.addAll(todoDao.getAllTodosNotDone());
                }
                adapter.notifyDataSetChanged();
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

    public void previewFile(String filePath){
        File file = new File(filePath);

        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".file_provider", file);
        String mime = getContentResolver().getType(uri);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}