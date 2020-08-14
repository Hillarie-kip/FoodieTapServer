package com.techkip.foodietapserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.techkip.foodietapserver.Interface.ItemClickListener;
import com.techkip.foodietapserver.R;
import com.techkip.foodietapserver.common.Common;


/**
 * Created by hillarie on 29/05/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,
View.OnLongClickListener,
        View.OnCreateContextMenuListener  {
    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);
        txtOrderId = itemView.findViewById(R.id.txt_orderId) ;
        txtOrderStatus = itemView.findViewById(R.id.txt_orderStatus) ;
        txtOrderPhone = itemView.findViewById(R.id.txt_orderPhone) ;
        txtOrderAddress = itemView.findViewById(R.id.txt_orderAddress) ;

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
        itemView.setOnClickListener(this);

    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select Action");
        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
    }


    @Override
    public boolean onLongClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),true);
        return true;
    }
}
