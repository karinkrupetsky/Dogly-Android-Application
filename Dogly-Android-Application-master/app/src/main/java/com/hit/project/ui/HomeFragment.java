package com.hit.project.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hit.project.R;
import com.hit.project.adapter.AdsRecyclerAdapter;
import com.hit.project.model.AdoptionItemData;
import com.hit.project.model.Consumer;
import com.hit.project.service.UploadPostService;
import com.hit.project.viewmodel.IMainViewModel;
import com.hit.project.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment
{
    public static final String TAG = "HOME_FRG";
    private IMainViewModel viewModel;
    private SwipeRefreshLayout swipeContainer;
    private AdsRecyclerAdapter adapter = new AdsRecyclerAdapter();
    private BroadcastReceiver reloadAdsReceiver;
    private boolean cardIsVisible = false;

    private Button searchB, cleanB;
    private Spinner districtS, typeS, yearsAgeS;
    private EditText freeTextET;
    private CardView cardView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        reloadAdsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(viewModel != null)
                    viewModel.loadAds(null);
            }
        };
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_bar);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.mainRecycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        adapter.setRecyclerListener(new AdsRecyclerAdapter.AdsRecyclerListener() {
            @Override
            public void onItemClick(int position, View clickedView, AdoptionItemData clickedAd) {
                ArrayList<Uri> uriList = new ArrayList<>();
                if (clickedAd.getImagesURL()==null) {
                    uriList.add(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.mipmap.dog_profile));
                }
                else {
                    for (String uri : clickedAd.getImagesURL())
                        uriList.add(Uri.parse(uri));
                }

                Intent intent = new Intent(getContext(), CardViewDogInfo.class)
                        .putParcelableArrayListExtra("images", uriList)
                        .putExtra("name", clickedAd.getPostedUserName())
                        .putExtra("email", clickedAd.getPostedUserId())
                        .putExtra("phoneNumber", clickedAd.getPostedUserPhoneNumber())
                        .putExtra("title", clickedAd.getTitle())
                        .putExtra("city", clickedAd.getCity())
                        .putExtra("district", clickedAd.getDistrict())
                        .putExtra("freeText", clickedAd.getText())
                        .putExtra("dogData", clickedAd.getDogData());
                startActivity(intent);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), PostAdActivity.class));
        });

        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            // onRefresh()..
            viewModel.loadAds(() -> swipeContainer.setRefreshing(false));
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        viewModel.getAdsLiveData().observe(getViewLifecycleOwner(), adsList -> {
            adapter.setData(adsList);
            adapter.notifyDataSetChanged();
        });

        viewModel.loadAds(null);

        districtS = getActivity().findViewById(R.id.dog_district_spinner_search);
        typeS = getActivity().findViewById(R.id.dog_type_spinner_search);
        yearsAgeS = getActivity().findViewById(R.id.dog_age_years_spinner_search);
        freeTextET = getActivity().findViewById(R.id.free_text_search);

        cleanB = getActivity().findViewById(R.id.clean_fields_button);
        cleanB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                districtS.setSelection(0);
                typeS.setSelection(0);
                yearsAgeS.setSelection(0);
                freeTextET.setText("");

                viewModel.getAdsLiveData().observe(getViewLifecycleOwner(), adsList -> {
                    adapter.setData(adsList);
                    adapter.notifyDataSetChanged();
                });
            }
        });

        searchB = getActivity().findViewById(R.id.search_ads_button);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String district = districtS.getSelectedItem().toString();
                if(districtS.getSelectedItemPosition()==0)
                    district="%";
                String type = typeS.getSelectedItem().toString();
                if(typeS.getSelectedItemPosition()==0)
                    type="%";

                int maxAge = yearsAgeS.getSelectedItemPosition() == 0 ? 100
                                : Integer.parseInt(yearsAgeS.getSelectedItem().toString());

                String freeText = "%"+freeTextET.getText().toString()+"%";
                if(freeTextET.getText().toString().equals(""))
                    freeText = "%";

                Consumer<List<AdoptionItemData>> consumerList = new Consumer<List<AdoptionItemData>>() {
                    @Override
                    public void apply(List<AdoptionItemData> param) {
                        adapter.setData(param);
                        adapter.notifyDataSetChanged();
                    }
                };
                viewModel.searchAndRun(consumerList, district, type, maxAge, freeText);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.search_bar){
            cardView = getActivity().findViewById(R.id.card_view_search);
            if(!cardIsVisible){
                item.setIcon(R.drawable.ic_opened_details);
                cardIsVisible=true;
                cardView.setVisibility(View.VISIBLE);
            }
            else {
                item.setIcon(R.mipmap.ic_search);
                cardIsVisible=false;
                cardView.setVisibility(View.GONE);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(reloadAdsReceiver,
                    new IntentFilter("com.hit.project.ACTION_RELOAD"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(reloadAdsReceiver);
        }
    }
}