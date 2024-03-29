package com.example.androidproject.Task;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidproject.LoginAndRegister.LoginActivity;
import com.example.androidproject.R;
import com.example.androidproject.model.Course;
import com.example.androidproject.model.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
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
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar calendar;
    Course course;
    private SharedPreferences prefs;
    ConstraintLayout constraintLayout;
    TimePickerDialog timePickerDialog;
    private MaterialTimePicker timePicker;

    int hour = 0, minutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra("course");
        setupViews();
        txtCourseName.setText(course.getCourseID() + "-Add Task");
        createNotificationChannel();

        btnClock.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Select Alarm Time")
                        .build();
                timePicker.show(getSupportFragmentManager(), "androidknowledge");
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (timePicker.getHour() > 12) {
                            textInputEditTextTime.setText(
                                    String.format("%02d", (timePicker.getHour() - 12)) + ":" + String.format("%02d", timePicker.getMinute()) + "PM"
                            );
                        } else {
                            textInputEditTextTime.setText(timePicker.getHour() + ":" + timePicker.getMinute() + "AM");
                        }
                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                        calendar.set(Calendar.MINUTE, timePicker.getMinute());
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                    }
                });


//                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        String amPm;
//                        if (selectedHour >= 12) {
//                            amPm = "PM";
//                            selectedHour -= 12;
//                        } else {
//                            amPm = "AM";
//                        }
//                        // Handle midnight (12:00 AM) and noon (12:00 PM)
//                        if (selectedHour == 0) {
//                            selectedHour = 12;
//                        }
//                        hour = selectedHour;
//                        minutes = selectedMinute;
//
//                    }
//                };
//                textInputEditTextTime.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hour, minutes, amPm));
//                 timePickerDialog = new TimePickerDialog(AddTask.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK, onTimeSetListener, hour, minutes, false);
//                timePickerDialog.setTitle("Select Time");
//                timePickerDialog.show();
            }
        });

        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar1 = Calendar.getInstance();
                int year = calendar1.get(Calendar.YEAR);
                int month = calendar1.get(Calendar.MONTH);
                int day = calendar1.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddTask.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Month is zero-based, so we add 1 to it
                                monthOfYear += 1;
                                // Display selected date in the TextInputEditText

                                textInputEditTextDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", monthOfYear, dayOfMonth, year));



                                System.out.println("year: "+year+monthOfYear+dayOfMonth);
                                calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear -1); // Adjusting month index for Calendar
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                String formattedDate = format.format(calendar.getTime());
                                System.out.println("calender time :"+formattedDate);
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

        setupSharedPrefs();
        ColorMode();

    }

    public void setupViews() {
        txtCourseName = findViewById(R.id.txtCourseName);
        textInputEditTextTitle = findViewById(R.id.textInputEditTextTitle);
        textInputEditTextDescription = findViewById(R.id.textInputEditTextDescription);
        textInputEditTextDate = findViewById(R.id.textInputEditTextDate);
        textInputEditTextTime = findViewById(R.id.textInputEditTextTime);
        btnClock = findViewById(R.id.btnClock);
        btnCalender = findViewById(R.id.btnCalender);
        //btnAddTask = findViewById(R.id.btnUpdateTask);
        txtWarningTitle = findViewById(R.id.txtWarningTitle);
        txtWarningDescription = findViewById(R.id.txtWarningDescription);
        txtWarningTime = findViewById(R.id.txtWarningTime);
        txtWarningDate = findViewById(R.id.txtWarningDate);
        constraintLayout = findViewById(R.id.constraintLayout);

    }

    public void AddNewTask(View view) {


        String title = textInputEditTextTitle.getText().toString();
        String description = textInputEditTextDescription.getText().toString();
        String time = textInputEditTextTime.getText().toString();

        String oldDate = textInputEditTextDate.getText().toString();

        if (title.isEmpty()) {
            txtWarningTitle.setVisibility(View.VISIBLE);
        }
        if (description.isEmpty()) {
            txtWarningDescription.setVisibility(View.VISIBLE);
        }
        if (time.isEmpty()) {
            txtWarningTime.setVisibility(View.VISIBLE);
        }
        if (oldDate.isEmpty()) {
            txtWarningDate.setVisibility(View.VISIBLE);
        } else {


            String[] date = oldDate.split("/");
            String[] finalTime = time.split(" ");
            String newDate = date[2] + "-" + date[0] + "-" + date[1];//2024-01-16
            String url = "http://10.0.2.2:5000/addTask/" + course.getCourseID() + "/" + title + "/"
                    + description + "/" + finalTime[0] + "/" + newDate + "/" + LoginActivity.id;

            RequestQueue queue = Volley.newRequestQueue(AddTask.this);
            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("CourseID", course.getCourseID());
                jsonParams.put("taskTitle", title);
                jsonParams.put("taskDescription", description);
                jsonParams.put("taskTime", time);
                jsonParams.put("taskDate", newDate);
                jsonParams.put("studentID", LoginActivity.id);
                //jsonParams.put("taskID", countTasks);
                //   insertIntoCourseTaskTable(course.getCourseID(),course.);

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

                            Toast.makeText(AddTask.this, str, Toast.LENGTH_SHORT).show();
                            lastTask();

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
            finish();
        }

    }


    private void setupSharedPrefs() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }


    private void ColorMode() {
        boolean dark_mode = prefs.getBoolean("DARK MODE", false);
        if (dark_mode) {
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.blackModeColor));
            txtCourseName.setTextColor(getResources().getColor(R.color.white));
            textInputEditTextTitle.setTextColor(getResources().getColor(R.color.white));
            textInputEditTextTitle.setHintTextColor(getResources().getColor(R.color.white));

            textInputEditTextDescription.setTextColor(getResources().getColor(R.color.white));
            textInputEditTextDescription.setHintTextColor(getResources().getColor(R.color.white));


            textInputEditTextDate.setTextColor(getResources().getColor(R.color.white));
            textInputEditTextDate.setHintTextColor(getResources().getColor(R.color.white));

            textInputEditTextTime.setTextColor(getResources().getColor(R.color.white));
            textInputEditTextTime.setHintTextColor(getResources().getColor(R.color.white));

            btnCalender.setImageResource(R.drawable.light_calender);
            btnClock.setImageResource(R.drawable.light_clock);

        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "akchannel";
            String desc = "Channel for Alarm Manager";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("androidknowledge", name, imp);
            channel.setDescription(desc);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void lastTask(){

        String url = "http://10.0.2.2:5000/getLastTask";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @SuppressLint("ScheduleExactAlarm")
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        String taskID = obj.getString("taskID");

                        System.out.println("taskID in do notification"+ taskID);



                        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(AddTask.this, AlarmReceiver.class);
                        intent.putExtra("taskID",taskID);
                        pendingIntent = PendingIntent.getBroadcast(AddTask.this, Integer.parseInt(taskID), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        System.out.println("time in milliS "+calendar.getTimeInMillis());
                        // Toast.makeText(AddTask.this, "Alarm Set", Toast.LENGTH_SHORT).show();

                    } catch (JSONException exception) {
                        Log.d("Error", exception.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddTask.this, error.toString(),
                        Toast.LENGTH_SHORT).show();
                Log.d("Error_json", error.toString());
            }
        });
        queue.add(request);
    }
}