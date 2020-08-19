package com.hit.project.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.hit.project.R;
import com.hit.project.adapter.PictureInfoAdapter;
import com.hit.project.adapter.PicturePostAdapter;
import com.hit.project.model.AdoptionItemData;
import com.hit.project.model.DogData;
import com.hit.project.model.UserData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class CardViewDogInfo extends AppCompatActivity {
    private PictureInfoAdapter pictureInfoAdapter;
    private TextView nameTV, titleTV, typeTV, colorTV, sizeTV, ageTV, districtTV, cityTV, freeTextTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view_dog_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nameTV = findViewById(R.id.name_view);
        titleTV = findViewById(R.id.title_view);
        typeTV = findViewById(R.id.dog_type_view);
        colorTV = findViewById(R.id.dog_color_view);
        sizeTV = findViewById(R.id.dog_size_view);
        ageTV = findViewById(R.id.dog_age_view);
        districtTV = findViewById(R.id.dog_district_view);
        cityTV = findViewById(R.id.dog_city_view);
        freeTextTV = findViewById(R.id.free_text_view);

        ArrayList<Uri> uriList = getIntent().getParcelableArrayListExtra("images");
        DogData dogData = getIntent().getParcelableExtra("dogData");
        String name = getIntent().getStringExtra("name");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String title = getIntent().getStringExtra("title");
        String city = getIntent().getStringExtra("city");
        String district = getIntent().getStringExtra("district");
        String freeText = getIntent().getStringExtra("freeText");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        ImageView dogIv = findViewById(R.id.dog_image);
        //dogIv.setMaxHeight(350);

        Glide.with(this)
                .load(uriList.get(0))
                .override(width, height)
                .apply(new RequestOptions())
                .into(dogIv);

        // WhatsApp event
        ImageView whatsAppIv = findViewById(R.id.whataspp_image);
        whatsAppIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:"+phoneNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.setPackage("com.whatsapp");
                startActivity(intent);
            }
        });

        // Message event
        ImageView messageIv = findViewById(R.id.message_image);
        messageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hi = getResources().getString(R.string.hi);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
                intent.putExtra("sms_body", hi+name);
                startActivity(intent);
            }
        });

        // Call event
        ImageView callIv = findViewById(R.id.call_image);
        callIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phoneNumber;

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phoneNumber));
                startActivity(intent);
            }
        });

        //Show label when uncollapsing
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
                    collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));//careful there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });


        titleTV.setText(title);
        typeTV.setText(dogData.getBreed());
        colorTV.setText(dogData.getColor());
        switch (dogData.getSize()){
            case SMALL:
                sizeTV.setText(getResources().getString(R.string.small));
                break;
            case NORMAL:
                sizeTV.setText(getResources().getString(R.string.normal));
                break;
            case BIG:
                sizeTV.setText(getResources().getString(R.string.big));
                break;
        }
        int yearsAge = dogData.getYearsAge();
        int monthsAge = dogData.getMonthsAge();
        ageTV.setText(yearsAge + " " +getResources().getString(R.string.years_info)+ " "
                + getResources().getString(R.string.and)+ " " +monthsAge + " " + getResources().getString(R.string.months_info));
        districtTV.setText(district);
        cityTV.setText(city);
        freeTextTV.setText(freeText);
        nameTV.setText(name);

        RecyclerView recyclerView = findViewById(R.id.dog_info_recycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        pictureInfoAdapter = new PictureInfoAdapter(uriList, this);
        recyclerView.setAdapter(pictureInfoAdapter);
    }

}
