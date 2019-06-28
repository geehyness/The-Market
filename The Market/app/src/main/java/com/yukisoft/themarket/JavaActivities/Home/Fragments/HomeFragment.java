package com.yukisoft.themarket.JavaActivities.Home.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.yukisoft.themarket.JavaActivities.Home.HomeActivity;
import com.yukisoft.themarket.JavaActivities.Items.ItemViewActivity;
import com.yukisoft.themarket.JavaRepositories.CollectionName;
import com.yukisoft.themarket.JavaRepositories.ItemAdapter;
import com.yukisoft.themarket.JavaRepositories.ItemModel;
import com.yukisoft.themarket.JavaRepositories.UserModel;
import com.yukisoft.themarket.MainActivity;
import com.yukisoft.themarket.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    ArrayList<ItemModel> itemList = new ArrayList<>();

    private RecyclerView mtRecyclerView;
    public ItemAdapter itemAdapter;
    private RecyclerView.LayoutManager mtLayoutManager;

    private CollectionReference messages = FirebaseFirestore.getInstance().collection(CollectionName.ITEMS);
    public static final String ITEM_MODEL = "ItemModel";
    private static UserModel currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        Intent i = getActivity().getIntent();
        currentUser = (new Gson()).fromJson(i.getStringExtra(MainActivity.USER_MODEL), UserModel.class);

        buildRecyclerView(v);

        messages.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Toast.makeText(getContext(), "Error While Loading! \nError - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (queryDocumentSnapshots != null){
                    for(DocumentSnapshot msg : queryDocumentSnapshots){
                        ItemModel tempItem = msg.toObject(ItemModel.class);
                        tempItem.setId(msg.getId());

                        boolean exists = false;

                        for (ItemModel m : itemList)
                            if(m.getId().equals(tempItem.getId()))
                                exists = true;

                        if(!exists) {
                            itemList.add(tempItem);
                        }
                    }
                }
                //Collections.sort(itemList, new MTComparator());
                itemAdapter.notifyDataSetChanged();
            }
        });

        return v;
    }

    private void buildRecyclerView(View v){
        mtRecyclerView = v.findViewById(R.id.itemRecycler);
        mtRecyclerView.setHasFixedSize(true);
        mtLayoutManager = new LinearLayoutManager(getContext());
        itemAdapter = new ItemAdapter(itemList);

        mtRecyclerView.setLayoutManager(mtLayoutManager);
        mtRecyclerView.setAdapter(itemAdapter);

        itemAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ItemModel itemModel = itemList.get(position);
                String itemJSON = (new Gson()).toJson(itemModel);
                String userJSON = (new Gson()).toJson(currentUser);

                Log.d("MTItem", "Item - " + itemJSON);

                Intent i = new Intent(getContext(), ItemViewActivity.class);
                i.putExtra(ITEM_MODEL, itemJSON);
                i.putExtra(MainActivity.USER_MODEL, userJSON);
                startActivity(i);
            }
        });
    }

}