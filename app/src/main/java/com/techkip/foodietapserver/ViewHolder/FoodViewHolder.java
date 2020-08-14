package com.techkip.foodietapserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techkip.foodietapserver.Interface.ItemClickListener;
import com.techkip.foodietapserver.R;
import com.techkip.foodietapserver.common.Common;


/**
 * Created by hillarie on 28/05/2018.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnCreateContextMenuListener {
    public TextView txtFoodName;
    public TextView txtFoodPrice;
    public TextView txtFoodDescription;
    public TextView txtFoodOffer;
    public ImageView ivFoodImage;

    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);
        txtFoodName = itemView.findViewById(R.id.txt_foodName) ;
        txtFoodPrice = itemView.findViewById(R.id.txt_foodPrice) ;
        txtFoodOffer = itemView.findViewById(R.id.txt_foodOffer) ;
        txtFoodDescription = itemView.findViewById(R.id.txt_foodDescription) ;
        ivFoodImage = itemView.findViewById(R.id.iv_foodImage) ;
        itemView.setOnCreateContextMenuListener(this);
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


}
