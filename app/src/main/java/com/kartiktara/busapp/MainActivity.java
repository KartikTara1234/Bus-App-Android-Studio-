package com.kartiktara.busapp;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private NumberPicker busno;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private CheckBox checkBox;
    private Button save;
    private EditText name, total, present, absent, staff, reading;
    private int BUSNO;

    public ProgressDialog dialog;

    String ServerURL = "https://busrecord.000webhostapp.com/BusRecord/get_data.php";
    String TempDate, TempName, TempTotal, TempPresent, TempAbsent, TempStaff, TempReading, TempBusno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateView = (TextView) findViewById(R.id.dateView);
        checkBox = findViewById(R.id.checkbox);
        save = findViewById(R.id.save);


        name = findViewById(R.id.name);
        total = findViewById(R.id.total);
        present = findViewById(R.id.present);
        absent = findViewById(R.id.absent);
        staff = findViewById(R.id.staff);
        reading = findViewById(R.id.reading);
        busno = findViewById(R.id.Busno);
        dialog = new ProgressDialog(MainActivity.this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetData();
                if(TempName.isEmpty() || TempReading.isEmpty() || TempStaff.isEmpty() || TempTotal.isEmpty() || TempAbsent.isEmpty() || TempPresent.isEmpty() || TempBusno.isEmpty() || TempDate.isEmpty()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Information")
                        .setMessage("Please Fill all entries")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setCancelable(true).show();
                }
                else{
                    GetData();
                    InsertData(TempDate, TempName, TempTotal, TempPresent, TempAbsent, TempStaff, TempReading, TempBusno);
                    Intent intent = new Intent(getApplicationContext(), Submitted.class);
                    startActivity(intent);
                }
            }
        });

        ///////////////////////////
        save.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.disablebtn));
        save.setEnabled(false);
        ///////////////////////////

        ///////////////////////////
        busno.setMaxValue(59);
        busno.setMinValue(0);

        busno.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                BUSNO = newVal;
            }
        });

        ///////////////////////////

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked())
                {
                    save.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorPrimary));
                    save.setEnabled(true);
                }
                else
                {

                    save.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.disablebtn));
                    save.setEnabled(false);
                }
            }
        });
    }

    public void InsertData(final String Date ,final String name, final String Total, final String Present, final String Absent, final String Staff, final String Reading, final String TempBusno){


        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String DateHolder = Date;
                String NameHolder = name;
                String TotalHolder = Total;
                String PresentHolder = Present;
                String AbsentHolder = Absent;
                String StaffHolder = Staff;
                String ReadingHolder = Reading;
                String BusnoHolder = TempBusno;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("date", DateHolder));
                nameValuePairs.add(new BasicNameValuePair("name", NameHolder));
                nameValuePairs.add(new BasicNameValuePair("total", TotalHolder));
                nameValuePairs.add(new BasicNameValuePair("present", PresentHolder));
                nameValuePairs.add(new BasicNameValuePair("absent", AbsentHolder));
                nameValuePairs.add(new BasicNameValuePair("staff", StaffHolder));
                nameValuePairs.add(new BasicNameValuePair("reading", ReadingHolder));
                nameValuePairs.add(new BasicNameValuePair("busno", BusnoHolder));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(ServerURL);

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    String result = httpResponse.toString();

                    HttpEntity httpEntity = httpResponse.getEntity();


                } catch (ClientProtocolException e) {
                    Toast.makeText(getApplicationContext(), "Error 1 caused", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error 2 caused", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                return "Data Inserted Successfully";
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(Date, name, Total, Present, Absent, Staff, Reading, TempBusno);
    }

    public void GetData(){

        TempDate =  dateView.getText().toString();
        TempName = name.getText().toString();
        TempTotal = total.getText().toString();
        TempPresent = present.getText().toString();
        TempAbsent = absent.getText().toString();
        TempStaff = staff.getText().toString();
        TempReading = reading.getText().toString();
        TempBusno = String.valueOf(BUSNO);
    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }


}