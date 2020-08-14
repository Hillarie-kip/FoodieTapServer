package com.techkip.foodietapserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.techkip.foodietapserver.ViewHolder.OrderDetailAdapter;
import com.techkip.foodietapserver.common.Common;

public class OrderDetail extends AppCompatActivity {

    TextView order_id,order_phone,order_address,order_total,order_comment;

    String order_id_value ="";
    RecyclerView lstOrders;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order_id=findViewById(R.id.txt_orderId);
        order_phone=findViewById(R.id.txt_orderPhone);
        order_address=findViewById(R.id.txt_orderAddress);
        order_total=findViewById(R.id.txt_orderTotal);
        order_comment=findViewById(R.id.txt_orderComment);

        lstOrders=findViewById(R.id.lst_orders);
        lstOrders.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        lstOrders.setLayoutManager(layoutManager);

        if (getIntent()!=null)
            order_id_value=getIntent().getStringExtra("OrderId");

        //set value
        order_id.setText(order_id_value);
        order_phone.setText(Common.currentUser.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_address.setText(Common.currentRequest.getAddress());
        order_comment.setText(Common.currentRequest.getComment());

        OrderDetailAdapter adapter= new OrderDetailAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        lstOrders.setAdapter(adapter);
    }
}
