package com.example.expresstrackingfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();
    public static final String EXTRA_MESSAGE =
            "com.example.expresstrackingfinal.extra.MESSAGE";
    public static final int TEXT_REQUEST = 1;
    JSONArray listData = new JSONArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFilters();
    }
    public void CreateNew(View view) {
        Intent intent = new Intent(this, MainEdit.class);
        intent.putExtra(EXTRA_MESSAGE, "");
        startActivityForResult(intent,TEXT_REQUEST);
    }
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {

                String tpJson=data.getStringExtra(MainEdit.EXTRA_REPLY);
                try {

                    JSONObject one = new JSONObject(tpJson);
                    listData.put(one);
                    reload();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if (resultCode==2){
                //edit
                String tpJson=data.getStringExtra(MainEdit.EXTRA_REPLY);
                try {
                    JSONObject editone = new JSONObject(tpJson);

                    for (int i = 0; i < listData.length(); i++) {
                        JSONObject one = listData.getJSONObject(i);
                        String id = one.getString("id");
                        String editId=editone.getString("id");
                        if (id.equals(editId)){


                            listData.put(i,editone);
                            reload();
                            break;

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void reload(){
        LinearLayout displayer=findViewById(R.id.displayer);
        displayer.removeAllViews();
        float total=0;

        try {

            for (int i = 0; i < listData.length(); i++) {
                JSONObject one = listData.getJSONObject(i);


                String date = one.getString("date");
                String cost = one.getString("cost");
                String name = one.getString("name");
                String category = one.getString("category");
                String reason = one.getString("reason");
                String note = one.getString("note");
                String id = one.getString("id");
                createItem(date,cost,name,category,id);
                total=total+Float.parseFloat(cost);

            }
            TextView totalForAll = findViewById(R.id.total);
            totalForAll.setText("Total: $"+String.valueOf(total));
            TextView average = findViewById(R.id.average);
            DecimalFormat df = new DecimalFormat(".00");
            average.setText("Average: $"+df.format(total/listData.length()) );
            initFilterCategory();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void createItem(String date,String cost,String name,String category,String id){
        int heightInDp = 80;
        float scale = getResources().getDisplayMetrics().density;

        int heightInPx = (int) (heightInDp * scale + 0.5f);
        LinearLayout newLinearLayout = new LinearLayout(this);
        newLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, heightInPx));
        newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        newLinearLayout.setBackgroundColor(Color.parseColor("#9CCC60"));
        newLinearLayout.setPadding(20,0,0,0);

        TextView dateTV = new TextView(this);
        dateTV.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,2));
        dateTV.setGravity(Gravity.CENTER_VERTICAL);
        dateTV.setTextColor(Color.parseColor("#FFFFFF"));
        dateTV.setTextSize(14);
        dateTV.setText(date);

        TextView nameTV = new TextView(this);
        nameTV.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,2));
        nameTV.setGravity(Gravity.CENTER_VERTICAL);
        nameTV.setTextColor(Color.parseColor("#FFFFFF"));
        nameTV.setTextSize(14);
        nameTV.setText(name);

        TextView categoryTV = new TextView(this);
        categoryTV.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,2));
        categoryTV.setGravity(Gravity.CENTER_VERTICAL);
        categoryTV.setTextColor(Color.parseColor("#FFFFFF"));
        categoryTV.setTextSize(14);
        categoryTV.setText(category);

        TextView costTV = new TextView(this);
        costTV.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,2));
        costTV.setGravity(Gravity.CENTER_VERTICAL);
        costTV.setTextColor(Color.parseColor("#FFFFFF"));
        costTV.setTextSize(14);
        costTV.setText("$"+cost);




        newLinearLayout.addView(dateTV);
        newLinearLayout.addView(nameTV);
        newLinearLayout.addView(categoryTV);
        newLinearLayout.addView(costTV);
        newLinearLayout.setId(Integer.parseInt(id));

        newLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                int buttonId = view.getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setItems(new CharSequence[]{"Edit", "Remove"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {

                            Intent intent = new Intent(MainActivity.this, MainEdit.class);
                            intent.putExtra(EXTRA_MESSAGE, getOne(buttonId));
                            startActivityForResult(intent,TEXT_REQUEST);
                        } else if (which == 1) {

                            showConfirmationDialog(buttonId);

                        }
                    }
                });
                builder.create().show();

                return true;
            }
        });


        LinearLayout displayer=findViewById(R.id.displayer);
        displayer.addView(newLinearLayout);
        displayer.scrollTo(0, 0);
    }
    private String getOne(int buttonId){
        try {
            //
            for (int i = 0; i < listData.length(); i++) {
                JSONObject one = listData.getJSONObject(i);

                String id = one.getString("id");
                if (Integer.parseInt(id)== buttonId){

                    return one.toString();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    private void showConfirmationDialog(int buttonId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to remove this track?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    //
                    for (int i = 0; i < listData.length(); i++) {
                        JSONObject one = listData.getJSONObject(i);

                        String id = one.getString("id");
                        if (Integer.parseInt(id)== buttonId){
                            listData.remove(i);
                            break;
                        }
                    }
                    reload();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    public void initFilters(){
        initFilterYear();
        initFilterMonth();
        initFilterDay();
        initFilterCategory();
    }

    private void initFilterYear(){
        Spinner filter = findViewById(R.id.filteryear);
        List<String> list = new ArrayList<>();
        list.add("all year");
        for (int i=2000;i<=2023;i++){
            list.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(adapter);
    }
    private void initFilterMonth(){
        Spinner filter = findViewById(R.id.filtermonth);
        List<String> list = new ArrayList<>();
        list.add("all months");
        for (int i=1;i<=12;i++){
            list.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(adapter);
    }
    private void initFilterDay(){
        Spinner filter = findViewById(R.id.filterday);
        List<String> list = new ArrayList<>();
        list.add("all days");
        for (int i=1;i<=31;i++){
            list.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(adapter);
    }
    private void initFilterCategory(){
        Spinner filter = findViewById(R.id.filtercategory);
        List<String> list = new ArrayList<>();
        list.add("all categories");
        for (int i = 0; i < listData.length(); i++) {
            try {
            JSONObject one = listData.getJSONObject(i);
            String category = one.getString("category");
            if (!list.contains(category)){
                list.add(category);
            }}
            catch  (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(adapter);

    }

    public void Search(View view) {
        Spinner filteryear = findViewById(R.id.filteryear);
        String _filteryear=filteryear.getSelectedItem().toString();
        Spinner filtermonth = findViewById(R.id.filtermonth);
        String _filtermonth=filtermonth.getSelectedItem().toString();
        Spinner filterday= findViewById(R.id.filterday);
        String _filterday=filterday.getSelectedItem().toString();
        Spinner filtercategory= findViewById(R.id.filtercategory);
        String _filtercategory=filtercategory.getSelectedItem().toString();

        try {

            for (int i = 0; i < listData.length(); i++) {
                JSONObject one = listData.getJSONObject(i);
                int id = Integer.parseInt(one.getString("id"));
                LinearLayout newLinearLayout =findViewById(id);
                newLinearLayout.setVisibility(view.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (_filteryear=="all year"
                && _filtermonth=="all months"
                && _filterday=="all days"
                && _filtercategory=="all categories"
        ){

        }else{
            try {
                for (int i = 0; i < listData.length(); i++) {
                    JSONObject one = listData.getJSONObject(i);
                    int id = Integer.parseInt(one.getString("id"));
                    LinearLayout newLinearLayout =findViewById(id);
                    newLinearLayout.setVisibility(view.GONE);
                }

                float total=0;
                int amount=0;

                for (int i = 0; i < listData.length(); i++) {
                    JSONObject one = listData.getJSONObject(i);
                    String date[] = one.getString("date").split("/");
                    String year=date[0];
                    String month=date[1];
                    String day=date[2];
                    String category = one.getString("category");
                    String cost = one.getString("cost");
                    if (_filteryear.equals(year) || _filtermonth.equals(month) || _filterday.equals(day) || _filtercategory.equals(category)){
                        int id = Integer.parseInt(one.getString("id"));
                        LinearLayout newLinearLayout =findViewById(id);
                        newLinearLayout.setVisibility(view.VISIBLE);
                        total=total+Float.parseFloat(cost);
                        amount++;
                    }
                }
                TextView totalForAll = findViewById(R.id.total);
                totalForAll.setText("Total: $"+String.valueOf(total));
                TextView average = findViewById(R.id.average);
                if (amount!=0){
                    DecimalFormat df = new DecimalFormat(".00");
                    average.setText("Average: $"+df.format(total/amount) );

                }else{
                    average.setText("Average: $0" );
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}