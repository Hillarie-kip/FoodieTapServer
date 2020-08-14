package com.techkip.foodietapserver.ViewHolder;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techkip.foodietapserver.Interface.ItemClickListener;
import com.techkip.foodietapserver.R;
import com.techkip.foodietapserver.model.Order;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder  {
    public TextView txtName, txtQuantity, txtPrice, txtDiscount;


    private ItemClickListener itemClickListener;

    public MyViewHolder(View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txt_productName);
        txtQuantity = itemView.findViewById(R.id.txt_productQuantity);
        txtPrice = itemView.findViewById(R.id.txt_productPrice);
        txtDiscount = itemView.findViewById(R.id.txt_productDiscount);


    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder>{
List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_layout,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
Order order =myOrders.get(position);
        holder.txtName.setText(String.format("Name : %s",order.getProductName()));
        holder.txtQuantity.setText(String.format("Quantity : %s",order.getQuantity()));
        holder.txtPrice.setText(String.format("Price : %s",order.getPrice()));
        holder.txtDiscount.setText(String.format("Discount : %s",order.getDiscount()));

    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
