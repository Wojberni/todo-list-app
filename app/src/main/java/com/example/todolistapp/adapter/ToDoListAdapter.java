package com.example.todolistapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;
import com.example.todolistapp.room.TodoEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ToDoViewHolder> {

    private final List<TodoEntity> toDoTaskList;
    private static ToDoViewClickListener toDoViewClickListener;

    public ToDoListAdapter(List<TodoEntity> toDoTaskList, ToDoViewClickListener toDoViewClickListener) {
        this.toDoTaskList = toDoTaskList;
        ToDoListAdapter.toDoViewClickListener = toDoViewClickListener;
    }

    @NonNull
    @Override
    public ToDoListAdapter.ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_cardview, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoListAdapter.ToDoViewHolder holder, int position) {
        holder.title.setText(toDoTaskList.get(position).getTitle());
        holder.description.setText(toDoTaskList.get(position).getDescription());
        Date date = toDoTaskList.get(position).getCreationDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);
        holder.creationDate.setText(simpleDateFormat.format(date));
        date = toDoTaskList.get(position).getDeadlineDate();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);
        holder.deadlineDate.setText(simpleDateFormat.format(date));
        holder.done.setChecked(toDoTaskList.get(position).isDone());
        holder.notification.setChecked(toDoTaskList.get(position).isNotification());
        holder.attachment.setChecked(toDoTaskList.get(position).getAttachmentPath() != null);
    }

    @Override
    public int getItemCount() {
        return toDoTaskList.size();
    }

    public static class ToDoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private TextView description;
        private TextView creationDate;
        private TextView deadlineDate;
        private CheckBox done;
        private CheckBox notification;
        private CheckBox attachment;
        private Button edit;
        private Button delete;

        public ToDoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cardViewTitle);
            description = itemView.findViewById(R.id.cardViewDescription);
            creationDate = itemView.findViewById(R.id.cardViewCreationDate);
            deadlineDate = itemView.findViewById(R.id.cardViewDeadlineDate);
            done = itemView.findViewById(R.id.cardViewDone);
            notification = itemView.findViewById(R.id.cardViewNotify);
            attachment = itemView.findViewById(R.id.cardViewAttachment);
            edit = itemView.findViewById(R.id.cardViewEdit);
            delete = itemView.findViewById(R.id.cardViewDelete);
            notification.setOnClickListener(this);
            edit.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            toDoViewClickListener.onClick(view, getBindingAdapterPosition());
        }
    }

}
