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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.yukisoft.themarket.JavaActivities.Items.ItemViewActivity;
import com.yukisoft.themarket.JavaRepositories.CollectionName;
import com.yukisoft.themarket.JavaRepositories.ItemAdapter;
import com.yukisoft.themarket.JavaRepositories.ItemCategory;
import com.yukisoft.themarket.JavaRepositories.ItemModel;
import com.yukisoft.themarket.JavaRepositories.UserModel;
import com.yukisoft.themarket.MainActivity;
import com.yukisoft.themarket.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    ArrayList<ItemModel> ItemList = new ArrayList<>();
    ArrayList<ItemModel> displayList = new ArrayList<>();

    private RecyclerView itemRecyclerView;
    public ItemAdapter itemAdapter;
    private RecyclerView.LayoutManager itemLayoutManager;

    private CollectionReference itemsRef = FirebaseFirestore.getInstance().collection(CollectionName.ITEMS);
    public static final String ITEM_MODEL = "ItemModel";
    private static UserModel currentUser;
    private Spinner catSelect;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        Intent i = getActivity().getIntent();
        String userJSON = i.getStringExtra(MainActivity.USER_MODEL);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        buildRecyclerView(v);

        itemsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null){
                    for(DocumentSnapshot msg : queryDocumentSnapshots){
                        ItemModel tempMsg = msg.toObject(ItemModel.class);
                        tempMsg.setId(msg.getId());

                        boolean exists = false;

                        for (ItemModel m : ItemList)
                            if(m.getId().equals(tempMsg.getId()))
                                exists = true;

                        if(!exists) {
                            displayList.add(tempMsg);
                            ItemList.add(tempMsg);
                        }
                    }
                }
                //Collections.sort(displayList, new MTComparator());
                //Collections.sort(MTList, new MTComparator());
                initCatPicker();
                itemAdapter.notifyDataSetChanged();
            }
        });

        catSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getCategoryList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("category picker", "Nothing selected");
            }
        });

        return v;
    }

    private void getCategoryList(){
        displayList.clear();
        String category = catSelect.getSelectedItem().toString();

        if (category.equals(ItemCategory.ALL.toString())){
            displayList = ItemList;
        } else {
            for (int a = 0; a < ItemList.size(); a++){
                if (ItemList.get(a).getCategory().equals(category)){
                    displayList.add(ItemList.get(a));
                    Log.d("category picker", String.valueOf(a));
                }
            }
        }

        Log.d("category picker", category);
        itemAdapter.notifyDataSetChanged();
    }

    private void initCatPicker(){
        String[] spinnerList = {
                ItemCategory.ALL.toString(),
                ItemCategory.BOOKS.toString(),
                ItemCategory.GADJETS.toString(),
                ItemCategory.CLOTHING.toString(),
                ItemCategory.MISC.toString()
        };

        ArrayList<String> categories = new ArrayList<>();

        for (ItemModel m : ItemList) {
            boolean exists = false;

            for (String c : categories){
                Log.d("Start Fail", m.getName() + m.getCategory());
                try{
                    if (m.getCategory().equals(c)){
                        exists = true;
                    }
                } catch (Exception e) {
                    Log.d("Start Fail", e.getMessage());
                }
            }

            if (!exists) {
                categories.add(String.valueOf(m.getCategory()));
            }
        }

        if (!categories.isEmpty()){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            catSelect.setAdapter(adapter);
        }
    }

    private void buildRecyclerView(View v){
        catSelect = v.findViewById(R.id.spHomeCat);

        itemRecyclerView = v.findViewById(R.id.homeItemRecycler);
        itemRecyclerView.setHasFixedSize(true);
        itemLayoutManager = new LinearLayoutManager(getContext());
        itemAdapter = new ItemAdapter(ItemList);

        itemRecyclerView.setLayoutManager(itemLayoutManager);
        itemRecyclerView.setAdapter(itemAdapter);

        itemAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ItemModel itemModel = ItemList.get(position);
                String itemJSON = (new Gson()).toJson(itemModel);
                String userJSON = (new Gson()).toJson(currentUser);

                Log.d("Item", "Item - " + itemJSON);

                Intent i = new Intent(getContext(), ItemViewActivity.class);
                i.putExtra(ITEM_MODEL, itemJSON);
                i.putExtra(MainActivity.USER_MODEL, userJSON);
                startActivity(i);
            }
        });
    }

}