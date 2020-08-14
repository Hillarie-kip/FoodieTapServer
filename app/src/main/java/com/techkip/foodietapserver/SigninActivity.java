package com.techkip.foodietapserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.techkip.foodietapserver.common.Common;
import com.techkip.foodietapserver.model.User;


import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class SigninActivity extends AppCompatActivity {

    MaterialEditText etPhone, etPassword,etSecureCode;
    Button btnSigIn;
    com.rey.material.widget.CheckBox cbkRem;
    TextView TxtForgotPass;

    FirebaseDatabase db;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

         db = FirebaseDatabase.getInstance();
         databaseReference = db.getReference("Users");

        etPassword = findViewById(R.id.et_password);
        etPhone = findViewById(R.id.et_phone);
        cbkRem = findViewById(R.id.cbk_remember);
        TxtForgotPass = findViewById(R.id.tv_forgotPass);
        Paper.init(this);

        btnSigIn = findViewById(R.id.btn_signIn);

        btnSigIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    if (cbkRem.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY,etPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY,etPassword.getText().toString());
                    }
                    signInUser(etPhone.getText().toString(), etPassword.getText().toString());
                }
                else
                {
                    Toast.makeText(SigninActivity.this, "check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            private void signInUser(String phone, final String password) {
                final android.app.AlertDialog pd = new SpotsDialog(SigninActivity.this);
                pd.show();
                pd.setMessage("Tryin to Login");
                pd.setCancelable(false);

                final  String localPhone= phone;
                final  String Password= password;
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(localPhone).exists()) {

                            pd.dismiss();
                            User user = dataSnapshot.child(localPhone).getValue(User.class);
                            user.setPhone(localPhone);

                            if (Boolean.parseBoolean(user.getIsStaff())){



                            if (user.getPassword().equals(Password)) {

                                Intent main = new Intent(SigninActivity.this,Home.class);
                                Common.currentUser=user;
                                startActivity(main);
                                finish();
                                Toast.makeText(SigninActivity.this, "successful", Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(SigninActivity.this, "login with staff unable to sign in", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            pd.dismiss();
                            Toast.makeText(SigninActivity.this, "user not found", Toast.LENGTH_SHORT).show();
                        }
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        TxtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotPwdDialog();
            }
        });


    }

    private void ForgotPwdDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Forgot Password?");
        alertDialog.setMessage("enter yor phone number and keyword");
        alertDialog.setIcon(R.mipmap.ic_cart);

        LayoutInflater inflater = LayoutInflater.from(this);
        View addmenu_Layout = inflater.inflate(R.layout.layout_forgot_pass, null);

        etPhone = addmenu_Layout.findViewById(R.id.et_phone);
        etSecureCode = addmenu_Layout.findViewById(R.id.et_secureCode);


        alertDialog.setView(addmenu_Layout);


        alertDialog.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //validation
                if (TextUtils.isEmpty(etPhone.getText().toString())) {
                    Toast.makeText(SigninActivity.this, "Have your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child(etPhone.getText().toString()).exists()) {

                            Toast.makeText(SigninActivity.this, "Phone Number doesn't Exist", Toast.LENGTH_SHORT).show();

                        } else {
                            User user = dataSnapshot.child(etPhone.getText().toString()).getValue(User.class);
                            if (user.getSecureCode().equals(etSecureCode.getText().toString()))
                                Toast.makeText(SigninActivity.this, "your password is : " + user.getPassword(), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(SigninActivity.this, "wrong keyword", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


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


}
