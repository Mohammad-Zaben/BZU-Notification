package com.example.androidproject.Task;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidproject.LoginAndRegister.Register;
import com.example.androidproject.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class AddTask extends AppCompatActivity {

    private TextView txtCourseName;
    private TextInputEditText textInputEditTextTitle;
    private TextInputEditText textInputEditTextDescription;
    private TextInputEditText textInputEditTextDate;
    private TextInputEditText textInputEditTextTime;
    private TextView txtWarningTitle;
    private TextView txtWarningDescription;
    private TextView txtWarningDate;
    private TextView txtWarningTime;
    private ImageButton btnClock;
    private ImageButton btnCalender;
    private Button btnAddTask;
    private String courseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        setupViews();
        Intent intent = getIntent();
        if (intent != null) {
            courseID = intent.getStringExtra("courseID");
        }
        btnClock.setOnClickListener(new View.OnClickListener() {
            int hour = 0, minutes = 0;

            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String amPm;
                        if (selectedHour >= 12) {
                            amPm = "PM";
                            selectedHour -= 12;
                        } else {
                            amPm = "AM";
                        }
                        // Handle midnight (12:00 AM) and noon (12:00 PM)
                        if (selectedHour == 0) {
                            selectedHour = 12;
                        }
                        hour = selectedHour;
                        minutes = selectedMinute;
                        textInputEditTextTime.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hour, minutes, amPm));
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddTask.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK, onTimeSetListener, hour, minutes, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddTask.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Month is zero-based, so we add 1 to it
                                monthOfYear += 1;
                                // Display selected date in the TextInputEditText
                                textInputEditTextDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", monthOfYear, dayOfMonth, year));
                            }
                        },
                        year, // Initial year
                        month, // Initial month
                        day // Initial day
                );
                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });
    }

    public  void setupViews(){
        txtCourseName = findViewById(R.id.txtCourseName);
        textInputEditTextTitle = findViewById(R.id.textInputEditTextTitle);
        textInputEditTextDescription = findViewById(R.id.textInputEditTextDescription);
        textInputEditTextDate = findViewById(R.id.textInputEditTextDate);
        textInputEditTextTime = findViewById(R.id.textInputEditTextTime);
        btnClock = findViewById(R.id.btnClock);
        btnCalender = findViewById(R.id.btnCalender);
        btnAddTask = findViewById(R.id.btnAddTask);
        txtWarningTitle = findViewById(R.id.txtWarningTitle);
        txtWarningDescription = findViewById(R.id.txtWarningDescription);
        txtWarningTime = findViewById(R.id.txtWarningTime);
        txtWarningDate = findViewById(R.id.txtWarningDate);
    }
    public void AddNewTask(View view) {
            String oldDate = textInputEditTextDate.getText().toString();
            String[] date = oldDate.split("/");
            String newDate = date[2] +"-"+date[0]+"-"+date[1];//2024-01-16
            String url = "http://10.0.2.2:5000/addTask/" + courseID + "/" + textInputEditTextTitle.getText().toString() + "/" + textInputEditTextDescription.getText().toString() + "/" + textInputEditTextTime.getText().toString() + "/" + newDate;
            RequestQueue queue = Volley.newRequestQueue(AddTask.this);

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("courseID", courseID);
            jsonParams.put("taskTitle", textInputEditTextTitle.getText().toString());
            jsonParams.put("taskDescription", textInputEditTextDescription.getText().toString());
            jsonParams.put("taskTime", textInputEditTextTime.getText().toString());
            jsonParams.put("taskDate", newDate);
            System.out.println("try 1");

        } catch (JSONException e) {
            System.out.println("catch 1");
            e.printStackTrace();
        }

        // Create a JsonObjectRequest with POST method
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String str = "";
                        try {
                            System.out.println("try");
                            str = response.getString("result");
                        } catch (JSONException e) {
                            System.out.println("catch 2");
                            System.out.println(e.toString());
                            e.printStackTrace();
                        }

                        Toast.makeText(AddTask.this, str,
                                Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("error");
                        Log.d("VolleyError", error.toString());
                    }
                }
        );
            queue.add(request);
    }
}