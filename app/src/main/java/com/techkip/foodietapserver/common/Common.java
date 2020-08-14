package com.techkip.foodietapserver.common;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.techkip.foodietapserver.Remote.APIService;
import com.techkip.foodietapserver.Remote.FCMRetrofitClient;
import com.techkip.foodietapserver.Remote.IGeoCoordinates;
import com.techkip.foodietapserver.Remote.RetrofitClient;
import com.techkip.foodietapserver.model.Request;
import com.techkip.foodietapserver.model.User;

/**
 * Created by hillarie on 28/05/2018.
 */

public class Common {

    public static User currentUser;
    public static Request currentRequest;
    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static final String baseUrl = "https://maps.googleapis.com";
    public static final String FCMUrl = "https://fcm.googleapis.com";

    public static String convertToStatus(String code) {
        if (code.equals("0"))
            return "Placed";
        else if (code.equals("1"))
            return "Order on Its Way";
        else
            return "Shipped";

    }

    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    public static APIService getFCMClient(){
        return FCMRetrofitClient.getClient(FCMUrl).create(APIService.class);
    }


    public static Bitmap scaleBitmap(Bitmap bitmap,int newWidth,int newHeight)
    {
        Bitmap scaleBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_4444);

        float scaleX = newWidth /(float)bitmap.getWidth();
        float  scaleY = newHeight/(float)bitmap.getWidth();
         float pivotX=0,pivotY = 0;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaleBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaleBitmap;

    }

    public  static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager !=null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if (infos !=null){
                for (int i =0;i<infos.length;i++){
                    if (infos[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
