package com.techkip.foodietapserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.esotericsoftware.kryo.NotNull;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.techkip.foodietapserver.Interface.ItemClickListener;
import com.techkip.foodietapserver.Remote.APIService;
import com.techkip.foodietapserver.ViewHolder.OrderViewHolder;
import com.techkip.foodietapserver.common.Common;
import com.techkip.foodietapserver.model.MyResponse;
import com.techkip.foodietapserver.model.Notification;
import com.techkip.foodietapserver.model.Request;
import com.techkip.foodietapserver.model.Sender;
import com.techkip.foodietapserver.model.Token;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference requests;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MaterialSpinner SpOrderStatus;

    APIService mService;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        mService = Common.getFCMClient();
        //load menu
        recyclerView = findViewById(R.id.lst_orders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // Toast.makeText(this, ""+Common.currentUser.getPhone(), Toast.LENGTH_LONG).show();


        loadOrders();


    }

    private void loadOrders() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class, R.layout.model_order, OrderViewHolder.class, requests)

        {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());

                if (viewHolder.txtOrderStatus.equals("0")) {
                    viewHolder.txtOrderStatus.setTextColor(getResources().getColor(R.color.white));
                }


                viewHolder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if (!isLongClick) {
                            Intent trackOrder = new Intent(OrderStatus.this, OrderDetail.class);
                            Common.currentRequest = model;
                            startActivity(trackOrder);
                        } else {
                            Intent detailOrder = new Intent(OrderStatus.this, OrderDetail.class);
                            Common.currentRequest = model;
                            detailOrder.putExtra("OrderId", adapter.getRef(position).getKey());
                            startActivity(detailOrder);
                        }


                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            showDeleteDialog(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }


    private void showDeleteDialog(String key) {
        requests.child(key).removeValue();
    }

    private void showUpdateDialog(final String key, final Request item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Update the Order");
        alertDialog.setMessage("Order status");
        alertDialog.setIcon(R.mipmap.ic_cart);

        LayoutInflater inflater = LayoutInflater.from(this);
        View addmenu_Layout = inflater.inflate(R.layout.update_order_layout, null);

        SpOrderStatus = addmenu_Layout.findViewById(R.id.sp_status);
        SpOrderStatus.setItems("Placed", "Order on Its Way", "Shipped");

        alertDialog.setView(addmenu_Layout);

        final String localKey = key;
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                item.setStatus(String.valueOf(SpOrderStatus.getSelectedIndex()));
                requests.child(localKey).setValue(item);
                Toast.makeText(OrderStatus.this, "Your Order Has been Updated", Toast.LENGTH_SHORT).show();
                sendOrderStatus(localKey, item);
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


    private void sendOrderStatus(final String localKey, Request item) {
        final DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                            Token token = postSnapShot.getValue(Token.class);

                            //make payload
                            Notification notification = new Notification("FoodieTap", "Your Order #" + localKey + "was updated");
                            Sender content = new Sender(token.getToken(), notification);
                            mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {


                                        if (response.body().success == 1) {
                                            Toast.makeText(OrderStatus.this, "Your Order Has been Updated", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(OrderStatus.this, "Failed", Toast.LENGTH_SHORT).show();

                                        }

                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("Order Update Error", t.getMessage());
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    }

