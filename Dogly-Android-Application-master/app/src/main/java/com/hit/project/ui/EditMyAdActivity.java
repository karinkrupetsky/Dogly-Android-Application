package com.hit.project.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.hit.project.R;
import com.hit.project.adapter.PictureInfoAdapter;
import com.hit.project.model.AdoptionItemData;
import com.hit.project.model.DogData;
import com.hit.project.service.UploadPostService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditMyAdActivity extends AppCompatActivity {

    private static final int NOTIF_ID = 4;

    private PictureInfoAdapter pictureInfoAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;

    private EditText titleET, phoneNumberET, freeTextET, otherCityET;
    private Spinner colorS, typeS, yearsAgeS, monthsAgeS, districtS, cityS;
    private RadioGroup sizeRG;
    private RadioButton radioButton;
    private ArrayList<Uri> uriList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_ad);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();

        titleET = findViewById(R.id.title_Edit);
        phoneNumberET = findViewById(R.id.contact_phone_Edit);
        freeTextET = findViewById(R.id.free_text_Edit);
        otherCityET = findViewById(R.id.other_city_Edit);
        colorS = findViewById(R.id.dog_color_spinner_Edit);
        typeS = findViewById(R.id.dog_type_spinner_Edit);
        yearsAgeS = findViewById(R.id.dog_age_years_spinner_Edit);
        monthsAgeS = findViewById(R.id.dog_age_months_spinner_Edit);
        sizeRG = (RadioGroup) findViewById(R.id.dog_size_radio_group_Edit);
        districtS = findViewById(R.id.dog_district_spinner_Edit);
        cityS = findViewById(R.id.dog_city_spinner_Edit);

        DogData dogData = getIntent().getParcelableExtra("dogData");

        String title = getIntent().getStringExtra("title");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String freeText = getIntent().getStringExtra("freeText");
        String district = getIntent().getStringExtra("district");
        String city = getIntent().getStringExtra("city");

        titleET.setText(title);
        phoneNumberET.setText(phoneNumber);
        freeTextET.setText(freeText);

        //Get current size
        switch(dogData.getSize()){
            case SMALL:
                radioButton = findViewById(R.id.small_rb_Edit);
                radioButton.setChecked(true);
                break;
            case NORMAL:
                radioButton = findViewById(R.id.normal_rb_Edit);
                radioButton.setChecked(true);
                break;
            case BIG:
                radioButton = findViewById(R.id.big_rb_Edit);
                radioButton.setChecked(true);
                break;
        }

        //Get current type
        ArrayAdapter<String> typeArrayAdapter = new ArrayAdapter<>(EditMyAdActivity.this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.dog_type_list)); //selected item will look like a spinner set from XML
        typeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeS.setAdapter(typeArrayAdapter);
        typeS.setSelection(typeArrayAdapter.getPosition(dogData.getType()));
        typeArrayAdapter.notifyDataSetChanged();

        //Get current color
        ArrayAdapter<String> colorArrayAdapter = new ArrayAdapter<>(EditMyAdActivity.this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.dog_color_list)); //selected item will look like a spinner set from XML
        colorArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorS.setAdapter(colorArrayAdapter);
        colorS.setSelection(colorArrayAdapter.getPosition(dogData.getColor()));
        colorArrayAdapter.notifyDataSetChanged();

        //Get current yearsAge
        ArrayAdapter<String> yearsAgeArrayAdapter = new ArrayAdapter<>(EditMyAdActivity.this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.dog_age_years_list)); //selected item will look like a spinner set from XML
        yearsAgeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearsAgeS.setAdapter(yearsAgeArrayAdapter);
        yearsAgeS.setSelection(yearsAgeArrayAdapter.getPosition(String.valueOf(dogData.getYearsAge())));
        yearsAgeArrayAdapter.notifyDataSetChanged();

        //Get current monthsAge
        ArrayAdapter<String> monthsAgeArrayAdapter = new ArrayAdapter<>(EditMyAdActivity.this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.dog_age_months_list)); //selected item will look like a spinner set from XML
        monthsAgeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsAgeS.setAdapter(monthsAgeArrayAdapter);
        monthsAgeS.setSelection(monthsAgeArrayAdapter.getPosition(String.valueOf(dogData.getMonthsAge())));
        monthsAgeArrayAdapter.notifyDataSetChanged();

        //Get current district
        ArrayAdapter<String> districtArrayAdapter = new ArrayAdapter<>(EditMyAdActivity.this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.dog_district_entries_list)); //selected item will look like a spinner set from XML
        districtArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtS.setAdapter(districtArrayAdapter);
        districtS.setSelection(districtArrayAdapter.getPosition(district), false);
        districtArrayAdapter.notifyDataSetChanged();


        //Get current list city and city
        ArrayAdapter<String> cityArrayAdapter;
        Boolean otherCityFlag = true;
        if(district.equals(getResources().getString(R.string.northern_district))) {
            String[] north = getResources().getStringArray(R.array.dog_northern_district_list);
            cityArrayAdapter = new ArrayAdapter<>(EditMyAdActivity.this,
                    android.R.layout.simple_spinner_item, north);
            cityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cityS.setAdapter(cityArrayAdapter);

            List<String> northList = Arrays.asList(north);
            for (String cityChosen : northList) {
                if(city.equals(cityChosen)){
                    otherCityFlag=false;
                    break;
                }
            }
            if(otherCityFlag){
                cityS.setSelection(cityArrayAdapter.getPosition(getResources().getString(R.string.other)));
                otherCityET.setText(city);
            }
            else{
                cityS.setSelection(cityArrayAdapter.getPosition(city));
            }
            cityArrayAdapter.notifyDataSetChanged();
        }
        else if(district.equals(getResources().getString(R.string.central_district))) {
            String[] center = getResources().getStringArray(R.array.dog_central_district_list);
            cityArrayAdapter = new ArrayAdapter<>(EditMyAdActivity.this,
                    android.R.layout.simple_spinner_item, center);
            cityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cityS.setAdapter(cityArrayAdapter);

            List<String> centerList = Arrays.asList(center);
            for (String cityChosen : centerList) {
                if(city.equals(cityChosen)){
                    otherCityFlag=false;
                    break;
                }
            }
            if(otherCityFlag){
                cityS.setSelection(cityArrayAdapter.getPosition(getResources().getString(R.string.other)));
                otherCityET.setText(city);
            }
            else{
                cityS.setSelection(cityArrayAdapter.getPosition(city));
            }
            cityArrayAdapter.notifyDataSetChanged();
        }
        else if(district.equals(getResources().getString(R.string.jerusalem_district))) {
            String[] jerusalem = getResources().getStringArray(R.array.dog_jerusalem_district_list);
            cityArrayAdapter = new ArrayAdapter<>(EditMyAdActivity.this,
                    android.R.layout.simple_spinner_item, jerusalem);
            cityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cityS.setAdapter(cityArrayAdapter);

            List<String> jerusalemList = Arrays.asList(jerusalem);
            for (String cityChosen : jerusalemList) {
                if(city.equals(cityChosen)){
                    otherCityFlag=false;
                    break;
                }
            }
            if(otherCityFlag){
                cityS.setSelection(cityArrayAdapter.getPosition(getResources().getString(R.string.other)));
                otherCityET.setText(city);
            }
            else{
                cityS.setSelection(cityArrayAdapter.getPosition(city));
            }
            cityArrayAdapter.notifyDataSetChanged();
        }
        else if(district.equals(getResources().getString(R.string.southern_district))) {
            String[] south = getResources().getStringArray(R.array.dog_southern_district_list);
            cityArrayAdapter = new ArrayAdapter<>(EditMyAdActivity.this,
                    android.R.layout.simple_spinner_item, south);
            cityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cityS.setAdapter(cityArrayAdapter);

            List<String> southList =  Arrays.asList(south);
            for (String cityChosen : southList) {
                if(city.equals(cityChosen)){
                    otherCityFlag=false;
                }
            }
            if(otherCityFlag){
                cityS.setSelection(cityArrayAdapter.getPosition(getResources().getString(R.string.other)));
                otherCityET.setText(city);
            }
            else{
                cityS.setSelection(cityArrayAdapter.getPosition(city));
            }
            cityArrayAdapter.notifyDataSetChanged();
        }


        //Choose district
        districtS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String selectedItem = parent.getItemAtPosition(position).toString();

                switch (position){
                    case 1:
                        //northern
                        cityS.setEnabled(true);
                        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<>(EditMyAdActivity.this,
                                android.R.layout.simple_spinner_item,
                                getResources().getStringArray(R.array.dog_northern_district_list)); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter1.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        cityS.setAdapter(spinnerArrayAdapter1);
                        break;
                    case 2:
                        //central
                        cityS.setEnabled(true);
                        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<>(EditMyAdActivity.this,
                                android.R.layout.simple_spinner_item,
                                getResources().getStringArray(R.array.dog_jerusalem_district_list)); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        cityS.setAdapter(spinnerArrayAdapter2);
                        break;
                    case 3:
                        //jerusalem
                        cityS.setEnabled(true);
                        ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<>(EditMyAdActivity.this,
                                android.R.layout.simple_spinner_item,
                                getResources().getStringArray(R.array.dog_central_district_list)); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter3.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        cityS.setAdapter(spinnerArrayAdapter3);
                        break;
                    case 4:
                        //southern
                        cityS.setEnabled(true);
                        ArrayAdapter<String> spinnerArrayAdapter4 = new ArrayAdapter<>(EditMyAdActivity.this,
                                android.R.layout.simple_spinner_item,
                                getResources().getStringArray(R.array.dog_southern_district_list)); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter4.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        cityS.setAdapter(spinnerArrayAdapter4);
                        break;
                    default:
                        //no choose
                        ArrayAdapter<String> spinnerArrayAdapter5 = new ArrayAdapter<>(EditMyAdActivity.this,
                                android.R.layout.simple_spinner_item,
                                getResources().getStringArray(R.array.dog_default_district_list)); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter5.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        cityS.setAdapter(spinnerArrayAdapter5);
                        cityS.setEnabled(false);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cityS.setEnabled(false);
            }
        });


        //Check if other City
        cityS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals(getResources().getString(R.string.other))){
                    otherCityET.setVisibility(View.VISIBLE);
                }
                else{
                    otherCityET.setText("");
                    otherCityET.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RecyclerView recyclerView = findViewById(R.id.picture_recycler_Edit);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //Get Current images
        uriList = getIntent().getParcelableArrayListExtra("images");

        pictureInfoAdapter = new PictureInfoAdapter(uriList, this);

        recyclerView.setAdapter(pictureInfoAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = auth.getCurrentUser();
    }

    public void onClick(View view) {
        // if already posting..
        if(UploadPostService.isRunning()) {
            showMsg(getString(R.string.already_posting));
            return;
        }

        updatePost();
    }

    private void updatePost() {
        String title = titleET.getText().toString();
        String dogType = typeS.getSelectedItem().toString();
        String dogColor = colorS.getSelectedItem().toString();
        String dogYearsAge = yearsAgeS.getSelectedItem().toString();
        String dogMonthsAge = monthsAgeS.getSelectedItem().toString();
        String district = districtS.getSelectedItem().toString();
        String city = cityS.getSelectedItem().toString();
        String docId = getIntent().getStringExtra("docId");
        ArrayList<String> imagesURL = getIntent().getStringArrayListExtra("imagesURL");
        if(city.equals(getResources().getString(R.string.other))) {
            city = otherCityET.getText().toString();
        }

        String phoneNumber = phoneNumberET.getText().toString();
        String freeText = freeTextET.getText().toString();

        DogData.DogSize dogSize = null;
        int size = sizeRG.getCheckedRadioButtonId();
        switch (size){
            case R.id.small_rb_Edit:
                dogSize = DogData.DogSize.SMALL;
                break;
            case R.id.normal_rb_Edit:
                dogSize = DogData.DogSize.NORMAL;
                break;
            case R.id.big_rb_Edit:
                dogSize = DogData.DogSize.BIG;
                break;
        }

        if(currentUser == null) {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        //Validating all fields
        if(title.equals("") || phoneNumber.equals("") || typeS.getSelectedItemPosition()==0
                || colorS.getSelectedItemPosition()==0 || yearsAgeS.getSelectedItemPosition()==0
                || monthsAgeS.getSelectedItemPosition()==0 || districtS.getSelectedItemPosition()==0
                || cityS.getSelectedItemPosition()==0 || freeText.equals("") || city.equals("")) {
            showMsg(getResources().getString(R.string.validate_detalis_message));
            return;
        }

        int dogYears = Integer.parseInt(dogYearsAge);
        int dogMonths = Integer.parseInt(dogMonthsAge);
        DogData dogData = new DogData(dogYears, dogMonths, dogType, dogColor, dogSize);


        final AdoptionItemData data = new AdoptionItemData(currentUser.getUid(), currentUser.getDisplayName(), phoneNumber, title, freeText, city, district, imagesURL, dogData);

        updateAd(data, docId);

        Toast.makeText(this, getResources().getString(R.string.updating), Toast.LENGTH_SHORT).show();

        MyAdsFragment.adToUpdate = data;
        MyAdsFragment.updateAdFlag = true;

        finish();
    }

    private void updateAd(AdoptionItemData adoptionItemData, String docId) {
        FirebaseFirestore.getInstance().collection("ads").document(docId)
                .set(adoptionItemData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void showMessageAndFinish(String msg) {
        try {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    private void showMsg(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.note)
                .setMessage(msg)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setIcon(R.mipmap.icon)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }

}