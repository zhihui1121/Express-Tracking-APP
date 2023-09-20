package com.example.expresstrackingfinal;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainEdit extends AppCompatActivity {
    public List<String> categories = new ArrayList<>();
    public static final String EXTRA_REPLY =
            "com.example.expresstrackingfinal.extra.REPLY";

    TextView date;
    EditText name;
    EditText cost;
    Spinner category;
    EditText reason;
    EditText note;
    DatePicker datePicker;

    TextView dateDisplay;

    String id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_edit);
        initSpinnerForDialog();
        dateDisplay=findViewById(R.id.dateDisplay);
        datePicker = findViewById(R.id.datePicker);
        date=findViewById(R.id.dateDisplay);
        name=findViewById(R.id.name);
        cost=findViewById(R.id.cost);
        category=findViewById(R.id.category);
        reason=findViewById(R.id.reason);
        note=findViewById(R.id.note);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        if (message.length()!=0){
            //edit
            try {
                JSONObject editone = new JSONObject(message);

                String[] datearray = editone.getString("date").split("/");
                int year = Integer.parseInt(datearray[0]);
                int month =  Integer.parseInt(datearray[1]);
                int day =  Integer.parseInt(datearray[2]);
                dateDisplay.setText(year+"/"+month+"/"+day);


                datePicker.init(year,month,day,  new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        FrameLayout datePanel =findViewById(R.id.datePanel);
                        String date = year + "/" + (month + 1) + "/" + day;
                        dateDisplay.setText(date);
                        datePanel.setVisibility(view.GONE);
                        enableAll();
                    }
                });

                name.setText(editone.getString("name"));
                cost.setText(editone.getString("cost"));
                int position = categories.indexOf(editone.getString("category"));
                category.setSelection(position);

                reason.setText(editone.getString("reason"));
                note.setText(editone.getString("note"));
                id=editone.getString("id");

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }else{
            //create
            id="";
            initDate();
        }



    }
    public void Cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
    public void Add(View view) {
        showInputDialog();
    }




    private void initDate(){





        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateDisplay.setText(year+"/"+(month+1)+"/"+day);



        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                FrameLayout datePanel =findViewById(R.id.datePanel);
                String date = year + "/" + (month + 1) + "/" + day;
                dateDisplay.setText(date);
                datePanel.setVisibility(view.GONE);
                enableAll();
            }
        });


    }

    private void initSpinnerForDialog() {
        Spinner Category = findViewById(R.id.category);


        categories.add("Food");
        categories.add("Rent");
        categories.add("Travel");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Category.setAdapter(adapter);

    }


    private void showInputDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new Category");

        EditText inputText = new EditText(this);
        builder.setView(inputText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input = inputText.getText().toString();
                categories.add(input);
                Toast.makeText(MainEdit.this, "New Category: " + input, Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void showDate(View view) {
        FrameLayout datePanel =findViewById(R.id.datePanel);

        datePanel.setVisibility(view.VISIBLE);
        disableAll();
    }


    public void Save(View view) {





        JSONObject json = new JSONObject();


        try {
            json.put("date", date.getText());
            json.put("cost", cost.getText());
            json.put("name", name.getText());
            json.put("category", category.getSelectedItem());
            json.put("reason", reason.getText());
            json.put("note", note.getText());
            if (id != "") {
                json.put("id",id);
            }else{
                json.put("id", generateRandom());
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


        String reply = json.toString();
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, reply);
        if (id != "") {
            //edit
            setResult(2, replyIntent);
        }else{
            setResult(RESULT_OK, replyIntent);
        }

        finish();

    }
    private void disableAll(){
        TextView date=findViewById(R.id.dateDisplay);
        EditText name=findViewById(R.id.name);
        Spinner category=findViewById(R.id.category);
        EditText reason=findViewById(R.id.reason);
        EditText note=findViewById(R.id.note);

        name.setEnabled(false);
        category.setEnabled(false);
        reason.setEnabled(false);
        note.setEnabled(false);
    }

    private void enableAll(){
        TextView date=findViewById(R.id.dateDisplay);
        EditText name=findViewById(R.id.name);
        Spinner category=findViewById(R.id.category);
        EditText reason=findViewById(R.id.reason);
        EditText note=findViewById(R.id.note);

        name.setEnabled(true);
        category.setEnabled(true);
        reason.setEnabled(true);
        note.setEnabled(true);
    }
    private String generateRandom() {
        Random random = new Random();
        int randomNumber = random.nextInt(90000000) + 10000000;
        return String.valueOf(randomNumber);
    }


}