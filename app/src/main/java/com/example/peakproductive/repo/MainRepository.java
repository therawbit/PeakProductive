package com.example.peakproductive.repo;

import android.content.Context;
import android.util.Log;

import com.example.peakproductive.dao.TaskDao;
import com.example.peakproductive.db.LocalDatabase;
import com.example.peakproductive.models.TaskModel;

import java.util.List;

public class MainRepository {

    LocalDatabase localDatabase;
    Context context;
    TaskDao taskDao;

    public MainRepository(Context context){
        this.context = context;
        localDatabase = LocalDatabase.getInstance(context);

    }
    public List<TaskModel> getAllTask(){
        taskDao = localDatabase.getTaskDao();
        return taskDao.getAllTasks();
    }
    public void addTask(TaskModel task){
        taskDao = localDatabase.getTaskDao();
        taskDao.insert(task);
        Log.d("TAG", "addTask: added"+getAllTask());
    }

}