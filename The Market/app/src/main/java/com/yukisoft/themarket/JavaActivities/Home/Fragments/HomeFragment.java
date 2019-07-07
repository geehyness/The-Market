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
import android.widget.Toast;

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
    ArrayList<ItemModel> displayList = new ArrayList<>();

    private RecyclerView mtRecyclerView;
    public ItemAdapter itemAdapter;
    private RecyclerView.LayoutManager mtLayoutManager;

    private CollectionReference messages = FirebaseFirestore.getInstance().collection(CollectionName.ITEMS);
    public static final String ITEM_MODEL = "ItemModel";
    private static UserModel currentUser;
    private Spinner catSelect;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        Intent i = getActivity().getIntent();
        currentUser = (new Gson()).fromJson(i.getStringExtra(MainActivity.USER_MODEL), UserModel.class);

        buildRecyclerView(v);
        createSpinner();
        getItems(ItemCategory.ALL.toString());

        catSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getItems(catSelect.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getItems(ItemCategory.ALL.toString());
            }
        });

        return v;
    }

    private void getItems(final String category){
        displayList.clear();
        messages.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null){
                    for(DocumentSnapshot msg : queryDocumentSnapshots){
                        ItemModel tempItem = msg.toObject(ItemModel.class);
                        tempItem.setId(msg.getId());

                        boolean isCategory = true;
                        boolean exists = false;

                        if (!category.equals(ItemCategory.ALL.toString()) && !category.equals(tempItem.getCategory())){
                            isCategory = false;
                        }

                        for (ItemModel m : displayList)
                            if(m.getId().equals(tempItem.getId()))
                                exists = true;

                        if(!exists && isCategory) {
                            displayList.add(tempItem);
                        }
                    }
                }

                itemAdapter.notifyDataSetChanged();
                //Toast.makeText(getContext(), displayList.get(0).getName(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createSpinner(){
        String[] spinnerList = {
                ItemCategory.ALL.toString(),
                ItemCategory.BOOKS.toString(),
                ItemCategory.GADJETS.toString(),
                ItemCategory.CLOTHING.toString(),
                ItemCategory.MISC.toString()
        };

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSelect.setAdapter(adapter);
    }

    private void buildRecyclerView(View v){
        catSelect = v.findViewById(R.id.spSellCat);

        mtRecyclerView = v.findViewById(R.id.itemRecycler);
        mtRecyclerView.setHasFixedSize(true);
        mtLayoutManager = new LinearLayoutManager(getContext());
        itemAdapter = new ItemAdapter(displayList);

        mtRecyclerView.setLayoutManager(mtLayoutManager);
        mtRecyclerView.setAdapter(itemAdapter);

        itemAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ItemModel itemModel = displayList.get(position);
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