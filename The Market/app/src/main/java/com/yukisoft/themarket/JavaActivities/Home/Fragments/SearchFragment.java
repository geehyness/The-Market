package com.yukisoft.themarket.JavaActivities.Home.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

public class SearchFragment extends Fragment {
    ArrayList<ItemModel> ItemList = new ArrayList<>();
    ArrayList<ItemModel> catList = new ArrayList<>();
    ArrayList<ItemModel> displayList = new ArrayList<>();

    private RecyclerView mtRecyclerView;
    public ItemAdapter itemAdapter;
    private RecyclerView.LayoutManager mtLayoutManager;

    private CollectionReference messages = FirebaseFirestore.getInstance().collection(CollectionName.ITEMS);
    public static final String ITEM_MODEL = "ItemModel";
    private static UserModel currentUser;
    private Spinner catSelect;
    EditText txtSearch;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        buildRecyclerView(v);

        txtSearch = v.findViewById(R.id.txtMarketSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void afterTextChanged(Editable editable) {
                txtSearch.removeTextChangedListener(this);
                search();
                txtSearch.setSelection(editable.length()); //moves the pointer to end
                txtSearch.addTextChangedListener(this);
            }
        });

        return v;
    }

    private void getItems(){
        messages.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
    }

    private void search(){
        String input = txtSearch.getText().toString();
        displayList.clear();

        if (input.isEmpty()){
            getCategoryList();
        } else {
            getCategoryList();

            for (ItemModel m : catList){
                if (m.getName().toLowerCase().contains(input.toLowerCase()) ||
                        m.getDetails().toLowerCase().contains(input.toLowerCase())){
                    displayList.add(m);
                    Log.d("search", String.valueOf(m.getName()));
                }
            }

            itemAdapter.notifyDataSetChanged();
        }
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
                if (m.getCategory().equals(c)){
                    exists = true;
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

    private void getCategoryList(){
        displayList.clear();
        String category = catSelect.getSelectedItem().toString();

        if (category.equals(ItemCategory.ALL.toString())){
            displayList = catList;
        } else {
            for (int a = 0; a < catList.size(); a++){
                if (ItemList.get(a).getCategory().equals(category)){
                    catList.add(ItemList.get(a));
                    Log.d("category picker", String.valueOf(a));
                }
            }
        }

        Log.d("category picker", category);
        itemAdapter.notifyDataSetChanged();
    }

    private void buildRecyclerView(View v){
        catSelect = v.findViewById(R.id.spSearchCat);

        mtRecyclerView = v.findViewById(R.id.searchItemRecycler);
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
