package com.example.peakproductive.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.peakproductive.EditTaskDetailsActivity;
import com.example.peakproductive.R;
import com.example.peakproductive.adaptors.TaskModelAdaptor;
import com.example.peakproductive.models.TaskModel;
import com.example.peakproductive.repo.MainRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class TaskFragment extends Fragment implements TaskModelAdaptor.CheckboxListener {

    private ArrayList<TaskModel> taskList;
    private FloatingActionButton addButton;
    private RecyclerView taskRecyclerView;
    private TaskModelAdaptor adapter;
    private MainRepository mainRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task, container, false);
        addButton = view.findViewById(R.id.add_task_button);
        taskRecyclerView = view.findViewById(R.id.task_list);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        addButton.setOnClickListener(add);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallBack);
        itemTouchHelper.attachToRecyclerView(taskRecyclerView);
        mainRepository = new MainRepository(getContext());
        taskList = new ArrayList<>();
        adapter = new TaskModelAdaptor(getActivity(), taskList, this);
        taskRecyclerView.setAdapter(adapter);
        getAllTasks();
        return view;
    }

    private void getAllTasks() {
        mainRepository.getAllTask().observe(getActivity(), taskModels -> {
            taskList = (ArrayList<TaskModel>) taskModels;
            adapter = new TaskModelAdaptor(getActivity(), taskList, this);
            taskRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });
    }

    View.OnClickListener add = v -> {
        Intent intent = new Intent(getActivity(), EditTaskDetailsActivity.class);
        intent.putExtra("type", "add");
        startActivity(intent);
    };
    ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();
            switch (direction) {

                case ItemTouchHelper.LEFT:

                    int id = taskList.get(pos).getId();


                    AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());

                    builder.setTitle("Delete Card");
                    builder.setMessage("Are you sure you want to delete this Card ?");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mainRepository.deleteTask(taskList.get(pos));
                                    adapter.notifyItemChanged(pos);

                                }
                            });
                    builder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.notifyItemChanged(pos);
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);

                    dialog.show();


                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.TRANSPARENT);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.TRANSPARENT);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                    break;


                case ItemTouchHelper.RIGHT:
                    //start edit activity for task detail
                    Intent intent = new Intent(getActivity(), EditTaskDetailsActivity.class);
                    intent.putExtra("type", "edit");
                    intent.putExtra("model", taskList.get(pos));
                    startActivity(intent);

                    break;

            }
        }
        ///setting the background color and icon for swipe gestures in the recycler view

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            Drawable icon;
            ColorDrawable background;

            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;

            if (dX > 0) {
                icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_edit);
                background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(), R.color.green_dark));
            } else {
                icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_delete);
                background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(), R.color.red_dark));

            }
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX > 0) { //swipping to the right

                int iconLeft = itemView.getLeft() + iconMargin;
                int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());

            } else if (dX < 0) {//swipping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX - backgroundCornerOffset), itemView.getTop(), itemView.getRight(), itemView.getBottom());


            } else {
                background.setBounds(0, 0, 0, 0);
            }
            background.draw(c);
            icon.draw(c);


        }


    };

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(int position) {
        TaskModel model = taskList.get(position);
        int changedState = model.isCompleted() ? 0 : 1;
        model.setCompleted(changedState == 1 ? true : false);
        mainRepository.updateTask(model);
        adapter.notifyItemChanged(position);
    }
}