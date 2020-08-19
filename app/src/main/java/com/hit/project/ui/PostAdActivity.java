package com.hit.project.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hit.project.R;
import com.hit.project.adapter.PicturePostAdapter;
import com.hit.project.model.DogData;
import com.hit.project.service.UploadPostService;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;



public class PostAdActivity extends AppCompatActivity
{
    private final int CAMERA_REQUEST = 1;
    private final int SELECT_IMAGE = 2;
    private final int WRITE_PERMISSION_REQUEST = 3;
    private final int MAX_PICTURES = 5;
    private PicturePostAdapter picturePostAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private TextView countPictureTV;
    private EditText titleET, freeTextET, otherCityET, phoneNumberET;
    private Spinner colorS, typeS, yearsAgeS, monthsAgeS, districtS, cityS;
    private RadioGroup sizeRG;
    private int numOfPictures = 0;
    private File file;
    private Uri imageUri = null;
    private ArrayList<Uri> uriList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();

        countPictureTV = findViewById(R.id.count_picture);
        titleET = findViewById(R.id.title);
        phoneNumberET = findViewById(R.id.contact_phone);
        freeTextET = findViewById(R.id.free_text);
        otherCityET = findViewById(R.id.other_city);
        colorS = findViewById(R.id.dog_color_spinner);
        typeS = findViewById(R.id.dog_type_spinner);
        yearsAgeS = findViewById(R.id.dog_age_years_spinner);
        monthsAgeS = findViewById(R.id.dog_age_months_spinner);
        sizeRG = (RadioGroup) findViewById(R.id.dog_size_radio_group);
        districtS = findViewById(R.id.dog_district_spinner);
        cityS = findViewById(R.id.dog_city_spinner);

        cityS.setEnabled(false);

        //Take picture button
        Button takePictureB = findViewById(R.id.take_picture);
        takePictureB.setOnClickListener(v -> {

            //Request permissions
            if(Build.VERSION.SDK_INT>=23) {
                int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(hasWritePermission!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
                }
                else{
                    //has permission
                    takePicture();
                }
            }
            else {
                //has permission
                takePicture();
            }
        });

        //Upload picture button
        Button uploadPictureB = findViewById(R.id.upload_picture);
        uploadPictureB.setOnClickListener(v -> {
            uploadPicture();
        });


        //Choose area
        districtS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String selectedItem = parent.getItemAtPosition(position).toString();
                switch (position){
                    case 1:
                        //northern
                        cityS.setEnabled(true);
                        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<>(PostAdActivity.this,
                                android.R.layout.simple_spinner_item,
                                getResources().getStringArray(R.array.dog_northern_district_list)); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter1.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        cityS.setAdapter(spinnerArrayAdapter1);
                        break;
                    case 2:
                        //central
                        cityS.setEnabled(true);
                        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<>(PostAdActivity.this,
                                android.R.layout.simple_spinner_item,
                                getResources().getStringArray(R.array.dog_jerusalem_district_list)); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        cityS.setAdapter(spinnerArrayAdapter2);
                        break;
                    case 3:
                        //jerusalem
                        cityS.setEnabled(true);
                        ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<>(PostAdActivity.this,
                                android.R.layout.simple_spinner_item,
                                getResources().getStringArray(R.array.dog_central_district_list)); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter3.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        cityS.setAdapter(spinnerArrayAdapter3);
                        break;
                    case 4:
                        //southern
                        cityS.setEnabled(true);
                        ArrayAdapter<String> spinnerArrayAdapter4 = new ArrayAdapter<>(PostAdActivity.this,
                                android.R.layout.simple_spinner_item,
                                getResources().getStringArray(R.array.dog_southern_district_list)); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter4.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        cityS.setAdapter(spinnerArrayAdapter4);
                        break;
                    default:
                        //no choose
                        ArrayAdapter<String> spinnerArrayAdapter5 = new ArrayAdapter<>(PostAdActivity.this,
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

        RecyclerView recyclerView = findViewById(R.id.dog_post_recycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        uriList = new ArrayList<>();
        picturePostAdapter = new PicturePostAdapter(uriList, this);

        picturePostAdapter.setListener(new PicturePostAdapter.MyPictureListener() {
            @Override
            public void onRemoveClicked(int position, View view) {
                new AlertDialog.Builder(PostAdActivity.this)
                        .setTitle(R.string.delete_title)
                        .setMessage(R.string.delete_message)
                        .setIcon(R.drawable.ic_remove)
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                            deletePictureFromView(position);
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .create()
                        .show();
            }

            @Override
            public void onPictureLongClicked(int position, View view) {

            }
        });

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.START|ItemTouchHelper.END|ItemTouchHelper.UP|ItemTouchHelper.DOWN,
                ItemTouchHelper.UP|ItemTouchHelper.DOWN) {


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(uriList, fromPosition, toPosition);
                picturePostAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                picturePostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                picturePostAdapter.notifyItemRangeChanged(viewHolder.getAdapterPosition(), picturePostAdapter.getItemCount());
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(picturePostAdapter);
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

        if(numOfPictures == 0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.note)
                    .setMessage(R.string.no_photos_alert)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setIcon(R.mipmap.icon)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        uploadPost();
                    })
                    .setNegativeButton(R.string.no, null)
                    .create()
                    .show();
        } else {
            uploadPost();
        }
    }

    private void uploadPost() {
        String title = titleET.getText().toString();
        String dogType = typeS.getSelectedItem().toString();
        String dogColor = colorS.getSelectedItem().toString();
        String dogYearsAge = yearsAgeS.getSelectedItem().toString();
        String dogMonthsAge = monthsAgeS.getSelectedItem().toString();
        String district = districtS.getSelectedItem().toString();
        String city = cityS.getSelectedItem().toString();
        if(city.equals(getResources().getString(R.string.other))) {
            city = otherCityET.getText().toString();
        }

        String phoneNumber = phoneNumberET.getText().toString();
        String freeText = freeTextET.getText().toString();

        DogData.DogSize dogSize = null;
        int size = sizeRG.getCheckedRadioButtonId();
        switch (size){
            case R.id.rbSmall:
                dogSize = DogData.DogSize.SMALL;
                break;
            case R.id.rbNormal:
                dogSize = DogData.DogSize.NORMAL;
                break;
            case R.id.rbBig:
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

        Intent intent = new Intent(this, UploadPostService.class)
                .putParcelableArrayListExtra("images", uriList)
                .putExtra("userId", currentUser.getUid())
                .putExtra("userName", currentUser.getDisplayName())
                .putExtra("phoneNumber", phoneNumber)
                .putExtra("title", title)
                .putExtra("city", city)
                .putExtra("district", district)
                .putExtra("freeText", freeText)
                .putExtra("dogData", dogData);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent);
        else
            startService(intent);

        finish();
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

    //Take picture from camera
    private void takePicture(){
        if(numOfPictures>=MAX_PICTURES){
            Toast.makeText(PostAdActivity.this, getResources().getString(R.string.max_pictures_message), Toast.LENGTH_SHORT).show();
            return;
        }
        String pictureName = String.valueOf(System.currentTimeMillis());
        file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), pictureName + ".jpg");

        imageUri = FileProvider.getUriForFile(PostAdActivity.this,"com.hit.project.provider", file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    //Upload picture from gallery
    private void uploadPicture() {
        if(numOfPictures>=MAX_PICTURES){
            Toast.makeText(PostAdActivity.this, getResources().getString(R.string.max_pictures_message), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }


    //Remove picture from recyclerView
    private void deletePictureFromView(int position){
        uriList.remove(position);
        numOfPictures--;
        countPictureTV.setText(numOfPictures+"/"+MAX_PICTURES);
        picturePostAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                addPictureFromCamera();
            }
            else {
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                file = null;
            }
        }
        else if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    addPictureFromGallery(data);
                }
            }
        }
    }

    private void addPictureFromGallery(Intent data) {
        imageUri = data.getData();
        numOfPictures++;
        uriList.add(imageUri);
        countPictureTV.setText(numOfPictures +"/"+MAX_PICTURES);
        picturePostAdapter.notifyDataSetChanged();
    }

    private void addPictureFromCamera() {
        numOfPictures++;
        uriList.add(imageUri);
        countPictureTV.setText(numOfPictures+"/"+MAX_PICTURES);
        picturePostAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==WRITE_PERMISSION_REQUEST){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, getResources().getString(R.string.picture_error), Toast.LENGTH_SHORT).show();
            }
            else{
                //Has permissions
                takePicture();
            }
        }
    }
}