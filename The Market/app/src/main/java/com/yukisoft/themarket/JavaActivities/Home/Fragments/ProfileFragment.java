package com.yukisoft.themarket.JavaActivities.Home.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.yukisoft.themarket.JavaActivities.Items.ItemViewActivity;
import com.yukisoft.themarket.JavaActivities.UserManagement.LoginActivity;
import com.yukisoft.themarket.JavaActivities.UserManagement.RegisterActivity;
import com.yukisoft.themarket.JavaActivities.UserManagement.SettingsActivity;
import com.yukisoft.themarket.JavaRepositories.CollectionName;
import com.yukisoft.themarket.JavaRepositories.ItemAdapter;
import com.yukisoft.themarket.JavaRepositories.ItemModel;
import com.yukisoft.themarket.JavaRepositories.UserModel;
import com.yukisoft.themarket.MainActivity;
import com.yukisoft.themarket.R;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private UserModel currentUser;

    private RecyclerView itemRecyclerView;
    public ItemAdapter itemAdapter;
    private RecyclerView.LayoutManager itemLayoutManager;

    private ArrayList<ItemModel> ItemList = new ArrayList<>();
    private CollectionReference itemsRef = FirebaseFirestore.getInstance().collection(CollectionName.ITEMS);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ConstraintLayout profile = v.findViewById(R.id.userProfile);
        ConstraintLayout account = v.findViewById(R.id.userAccount);

        Intent i = getActivity().getIntent();
        currentUser = (new Gson()).fromJson(i.getStringExtra(MainActivity.USER_MODEL), UserModel.class);

        if (!currentUser.isRegistered()){
            profile.setVisibility(View.INVISIBLE);
            account.setVisibility(View.VISIBLE);
            unregisteredControls(v);
        } else {
            profile.setVisibility(View.VISIBLE);
            account.setVisibility(View.INVISIBLE);
            initProfile(v);
        }

        return v;
    }

    private void initProfile(View v){
        TextView name = v.findViewById(R.id.lblPName);
        TextView email = v.findViewById(R.id.lblEmail);
        TextView phone = v.findViewById(R.id.lblPhone);
        TextView whatsapp = v.findViewById(R.id.lblWhatsapp);

        name.setText(currentUser.getName());
        email.setText(currentUser.getEmail());
        phone.setText("Phone number: ");
        whatsapp.setText("Whatsapp number: ");
        try {
            phone.append(currentUser.getPhoneNum());
            whatsapp.append(currentUser.getWhatsappNum());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initRecycler(v);
        getItems();

        ImageView settings = v.findViewById(R.id.btnSettings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingsActivity.class)
                    .putExtra(MainActivity.USER_MODEL, (new Gson()).toJson(currentUser)));
            }
        });
    }

    private void unregisteredControls(View v){
        CardView login = v.findViewById(R.id.btnPLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), LoginActivity.class)
                        .putExtra(MainActivity.USER_MODEL, (new Gson()).toJson(currentUser)));
                getActivity().finish();
            }
        });

        CardView register = v.findViewById(R.id.btnPRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RegisterActivity.class)
                        .putExtra(MainActivity.USER_MODEL, (new Gson()).toJson(currentUser)));
                getActivity().finish();
            }
        });
    }

    /**
     *
     * EXTRA
     *
     */
    private void initRecycler(View v){
        itemRecyclerView = v.findViewById(R.id.profileItemRecycler);
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
                i.putExtra(HomeFragment.ITEM_MODEL, itemJSON);
                i.putExtra(MainActivity.USER_MODEL, userJSON);
                startActivity(i);
            }
        });
    }

    private void getItems(){
        itemsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null){
                    for(DocumentSnapshot item : queryDocumentSnapshots){
                        ItemModel tempItem = item.toObject(ItemModel.class);
                        tempItem.setId(item.getId());

                        boolean exists = false;

                        for (ItemModel m : ItemList)
                            if(m.getId().equals(tempItem.getId()))
                                exists = true;

                        if(!exists && tempItem.getUserId().equals(currentUser.getId())) {
                            ItemList.add(tempItem);
                        }
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }
        });
    }
}
