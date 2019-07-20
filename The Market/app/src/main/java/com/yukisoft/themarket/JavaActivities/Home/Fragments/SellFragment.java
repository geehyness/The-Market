package com.yukisoft.themarket.JavaActivities.Home.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukisoft.themarket.JavaActivities.Home.HomeActivity;
import com.yukisoft.themarket.JavaRepositories.CollectionName;
import com.yukisoft.themarket.JavaRepositories.ItemCategory;
import com.yukisoft.themarket.JavaRepositories.ItemCondition;
import com.yukisoft.themarket.JavaRepositories.ItemModel;
import com.yukisoft.themarket.JavaRepositories.UserModel;
import com.yukisoft.themarket.MainActivity;
import com.yukisoft.themarket.R;

public class SellFragment extends Fragment {
    private Spinner catSelect;
    private Spinner conSelect;
    private EditText txtDetails;

    private UserModel currentUser;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sell, container, false);

        currentUser = (new Gson()).fromJson(getActivity().getIntent().getStringExtra(MainActivity.USER_MODEL), UserModel.class);
        if (currentUser == null){
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        }

        if (!currentUser.isRegistered()){
            Toast.makeText(getContext(), "You have to be registered to sell!", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileFragment()).commit();
        }

        txtDetails = view.findViewById(R.id.txtSellDetails);
        txtDetails.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (txtDetails.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        catSelect = view.findViewById(R.id.spHomeCat);
        conSelect = view.findViewById(R.id.spSellCondition);
        createSpinner();

        Button sell = view.findViewById(R.id.btnSell);
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellItem(view);
            }
        });

        return view;
    }

    private void sellItem(View v){
        EditText txtName = v.findViewById(R.id.txtSellName);
        EditText txtPrice = v.findViewById(R.id.txtSellPrice);

        String name = txtName.getText().toString();
        String price = txtPrice.getText().toString();
        String details = txtDetails.getText().toString();
        String condition = conSelect.getSelectedItem().toString();
        String category = catSelect.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(getContext(), "Item Name cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(price)){
            Toast.makeText(getContext(), "Item Price cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(details)){
            Toast.makeText(getContext(), "Provide details for the item!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (category.toLowerCase().contains("pick")){
            Toast.makeText(getContext(), "Please specify the item category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (condition.toLowerCase().contains("condition")){
            Toast.makeText(getContext(), "Please specify the item condition", Toast.LENGTH_SHORT).show();
            return;
        }


        ItemModel currItem = new ItemModel(name, Double.valueOf(price), details, ItemCondition.valueOf(condition),
                currentUser.getId(), "", ItemCategory.valueOf(category));

        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        ff.collection(CollectionName.ITEMS).add(currItem).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String userJSON = (new Gson()).toJson(currentUser);
                Intent i = new Intent(getContext(), HomeActivity.class);
                i.putExtra(MainActivity.USER_MODEL, userJSON);
                startActivity(i);
                getActivity().finish();
            }
        });
    }

    private void createSpinner(){
        //Category Spinner
        String[] catList = {
                "Pick an item category",
                ItemCategory.BOOKS.toString(),
                ItemCategory.GADJETS.toString(),
                ItemCategory.CLOTHING.toString(),
                ItemCategory.MISC.toString()
        };
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, catList);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSelect.setAdapter(catAdapter);

        //Condition Spinner
        String[] conList = {
                "Condition of the Item",
                ItemCondition.NEW.toString(),
                ItemCondition.USED_NEW.toString().replace("_", " "),
                ItemCondition.USED_MINOR_DINGS.toString().replace("_", " "),
                ItemCondition.USED_DAMAGED.toString().replace("_", " ")
        };
        ArrayAdapter<String> conAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, conList);
        conAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conSelect.setAdapter(conAdapter);
    }
}
