package com.hit.project.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hit.project.R;
import com.hit.project.adapter.MyAdsRecyclerAdapter;
import com.hit.project.model.AdoptionItemData;
import com.hit.project.model.Consumer;
import com.hit.project.service.UploadPostService;
import com.hit.project.viewmodel.IMainViewModel;
import com.hit.project.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;


public class MyAdsFragment extends Fragment
{
    private IMainViewModel viewModel;
    private FirebaseAuth auth;
    private MyAdsRecyclerAdapter adapter = new MyAdsRecyclerAdapter();
    public static Boolean updateAdFlag = false;
    public static AdoptionItemData adToUpdate;
    private static int currentPosition;

    public MyAdsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_ads, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(()-> {
            Consumer<List<AdoptionItemData>> consumerList = new Consumer<List<AdoptionItemData>>() {
                @Override
                public void apply(List<AdoptionItemData> result) {
                    if(updateAdFlag){
                        result.remove(currentPosition);
                        result.add(0, adToUpdate);
                        //result.set(currentPosition, adToUpdate);
                        updateAdFlag = false;
                    }
                    adapter.setData(result);
                    adapter.notifyDataSetChanged();
                }
            };
            viewModel.searchMyAdsAndRun(consumerList, auth.getCurrentUser().getUid());
        }, 200);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.myAdsRecycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.setRecyclerListener(new MyAdsRecyclerAdapter.MyAdsRecyclerListener() {
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
                        .putExtra("phoneNumber", clickedAd.getPostedUserPhoneNumber())
                        .putExtra("title", clickedAd.getTitle())
                        .putExtra("city", clickedAd.getCity())
                        .putExtra("district", clickedAd.getDistrict())
                        .putExtra("freeText", clickedAd.getText())
                        .putExtra("dogData", clickedAd.getDogData());
                startActivity(intent);
            }

            @Override
            public void onEditClick(int position, View clickedView, AdoptionItemData clickedAd) {

                // Convert List<String> to ArrayList<Uri>
                ArrayList<Uri> uriList = new ArrayList<>();
                if (clickedAd.getImagesURL()==null) {
                    uriList.add(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.mipmap.dog_profile));
                }
                else {
                    for (String uri : clickedAd.getImagesURL())
                        uriList.add(Uri.parse(uri));
                }

                // Convert List<String> to ArrayList<String>
                ArrayList<String> urls = new ArrayList<>();
                if(clickedAd.getImagesURL()==null)
                    urls = null;
                else {
                    for (String str : clickedAd.getImagesURL())
                        urls.add(str);
                }

                currentPosition = position;
                Intent intent = new Intent(getContext(), EditMyAdActivity.class)
                        .putParcelableArrayListExtra("images", uriList)
                        .putStringArrayListExtra("imagesURL", urls)
                        .putExtra("phoneNumber", clickedAd.getPostedUserPhoneNumber())
                        .putExtra("title", clickedAd.getTitle())
                        .putExtra("city", clickedAd.getCity())
                        .putExtra("district", clickedAd.getDistrict())
                        .putExtra("freeText", clickedAd.getText())
                        .putExtra("docId", clickedAd.getId())
                        .putExtra("dogData", clickedAd.getDogData());
                startActivity(intent);

            }

            @Override
            public void onDeleteClick(int position, View clickedView, AdoptionItemData clickedAd) {

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.delete_title)
                        .setMessage(R.string.delete_ad)
                        .setIcon(R.drawable.ic_remove)
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                            viewModel.deleteAd(clickedAd);
                            adapter.getData().remove(position);
                            adapter.notifyItemRemoved(position);
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .create()
                        .show();

            }
        });



    }

}