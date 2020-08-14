package com.techkip.foodietapserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.techkip.foodietapserver.Interface.ItemClickListener;
import com.techkip.foodietapserver.ViewHolder.FoodViewHolder;
import com.techkip.foodietapserver.common.Common;
import com.techkip.foodietapserver.model.Category;
import com.techkip.foodietapserver.model.Food;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import info.hoang8f.widget.FButton;

public class FoodListActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;


    RecyclerView recyclerFood;
    RecyclerView.LayoutManager layoutManager;
    RelativeLayout relativeLayout;

    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    //search
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchadapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar searchBar;
    FloatingActionButton Fab;
    MaterialEditText TxtFoodName, TxtFoodDescription, TxtFoodPrice, TxtFoodDiscount;
    FButton btnSelectImage,btnUpload;
    Food newFood;

    Uri saveUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        relativeLayout = findViewById(R.id.rootLayout);
        //Init FIREBASE
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //load menu
        recyclerFood = findViewById(R.id.recycler_food);
        recyclerFood.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerFood.setLayoutManager(layoutManager);
        Fab = findViewById(R.id.fab);
        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFoodDialog();
            }
        });

        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");

            if (!categoryId.isEmpty() && categoryId != null) {

                if (Common.isConnectedToInternet(this))
                    loadListFood(categoryId);
                else {
                    Toast.makeText(this, "check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }


        //search
        searchBar = findViewById(R.id.search_bar);
        searchBar.setHint("search food");
        loadSuggest();
        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(9);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //when text is type itl list suggest
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //wen search is closed
                //restore original adapter
                if (!enabled)
                    recyclerFood.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //wen search finished
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    private void showFoodDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Add a Menu");
        alertDialog.setMessage("add name with image");
        alertDialog.setIcon(R.mipmap.ic_cart);

        LayoutInflater inflater = LayoutInflater.from(this);
        View addmenu_Layout = inflater.inflate(R.layout.add_new_food, null);

        TxtFoodName = addmenu_Layout.findViewById(R.id.et_name);
        TxtFoodDescription = addmenu_Layout.findViewById(R.id.et_desc);
        TxtFoodPrice = addmenu_Layout.findViewById(R.id.et_price);
        TxtFoodDiscount = addmenu_Layout.findViewById(R.id.et_discount);

        btnSelectImage = addmenu_Layout.findViewById(R.id.btn_select);
        btnUpload = addmenu_Layout.findViewById(R.id.btn_upload);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        alertDialog.setView(addmenu_Layout);


        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //validation
                if (TextUtils.isEmpty(TxtFoodName.getText().toString())) {
                    Toast.makeText(FoodListActivity.this, "add food name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newFood != null) {
                    foodList.push().setValue(newFood);
                    Snackbar.make(relativeLayout, "New food : " + newFood.getName() + "added", Snackbar.LENGTH_LONG).show();
                }

            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();


    }

    private void uploadImage() {
        if (saveUri != null) {

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Uploading image..");
            pd.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images").child("Menu/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();

                    Toast.makeText(FoodListActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            newFood = new Food();
                            newFood.setName(TxtFoodName.getText().toString());
                            newFood.setDescription(TxtFoodDescription.getText().toString());
                            newFood.setPrice(TxtFoodPrice.getText().toString());
                            newFood.setDiscount(TxtFoodDiscount.getText().toString());
                            newFood.setMenuId(categoryId);
                            newFood.setImage(uri.toString());


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();

                    Toast.makeText(FoodListActivity.this, "Sorry retry", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    pd.setMessage("uploading..." + progress + "%");
                }
            });


        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void startSearch(CharSequence text) {
        searchadapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.model_food, FoodViewHolder.class, foodList.orderByChild("name").equalTo(text.toString())) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.txtFoodName.setText(model.getName());
                viewHolder.txtFoodPrice.setText(model.getPrice());
                viewHolder.txtFoodOffer.setText(model.getDiscount());
                viewHolder.txtFoodDescription.setText(model.getDescription());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.ivFoodImage);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get
                       /* Toast.makeText(FoodListActivity.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",searchadapter.getRef(position).getKey());//sed food id to new activity
                        startActivity(foodDetail);*/

                    }
                });

            }
        };
        recyclerFood.setAdapter(searchadapter); //set adapter for recycler viw is sesarch results
    }

    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Food item = postSnapshot.getValue(Food.class);
                    suggestList.add(item.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.model_food, FoodViewHolder.class, foodList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.txtFoodName.setText(model.getName());
                viewHolder.txtFoodPrice.setText(model.getPrice());
                viewHolder.txtFoodOffer.setText(model.getDiscount());
                viewHolder.txtFoodDescription.setText(model.getDescription());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.ivFoodImage);

                final Food local = model;
              /*  viewHolder.setItemClickListener(new ItemClickListener(){

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get
                        //  Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());//sed food id to new activity
                        startActivity(foodDetail);

                    }
                });*/
            }
        };
        recyclerFood.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            saveUri = data.getData();
            btnSelectImage.setText("Image Picked");

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else
        if (item.getTitle().equals(Common.DELETE)) {
            showDeleteFoodDialog(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void showDeleteFoodDialog(String key) {
        foodList.child(key).removeValue();
    }
    private void showUpdateFoodDialog(final String key, final Food item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Edit Food");
        alertDialog.setMessage("Fill All Info");
        alertDialog.setIcon(R.mipmap.ic_cart);
        LayoutInflater inflater = LayoutInflater.from(this);
        View addmenu_Layout = inflater.inflate(R.layout.add_new_food, null);

        TxtFoodName = addmenu_Layout.findViewById(R.id.et_name);
        TxtFoodDescription = addmenu_Layout.findViewById(R.id.et_desc);
        TxtFoodPrice = addmenu_Layout.findViewById(R.id.et_price);
        TxtFoodDiscount = addmenu_Layout.findViewById(R.id.et_discount);

        btnSelectImage = addmenu_Layout.findViewById(R.id.btn_select);
        btnUpload = addmenu_Layout.findViewById(R.id.btn_upload);

       TxtFoodName.setText(item.getName());
        TxtFoodDescription.setText(item.getDescription());
        TxtFoodPrice.setText(item.getPrice());
        TxtFoodDiscount.setText(item.getDiscount());

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }
        });

        alertDialog.setView(addmenu_Layout);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //validation
               /* if (TextUtils.isEmpty(TxtFoodName.getText().toString())) {
                    Toast.makeText(FoodListActivity.this, "add food name", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                    item.setName(TxtFoodName.getText().toString());
                    item.setDescription(TxtFoodDescription.getText().toString());
                    item.setPrice(TxtFoodPrice.getText().toString());
                    item.setDiscount(TxtFoodDiscount.getText().toString());
                    foodList.child(key).setValue(item);
                    Snackbar.make(relativeLayout," food : "+item.getName()+" was added",Snackbar.LENGTH_SHORT);



            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();


    }

    private void changeImage(final Food item) {
        if (saveUri != null) {

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Uploading image..");
            pd.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images").child("Menu/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();

                    Toast.makeText(FoodListActivity.this, "Uloaded", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            item.setImage(uri.toString());

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();

                    Toast.makeText(FoodListActivity.this, "Sorry retry", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    pd.setMessage("uploading..." + progress + "%");
                }
            });


        }
    }
}
