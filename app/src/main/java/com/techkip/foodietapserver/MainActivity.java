package com.techkip.foodietapserver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techkip.foodietapserver.common.Common;
import com.techkip.foodietapserver.model.User;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    FButton btnSignUp, btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignUp = findViewById(R.id.btn_signUp);
        btnSignIn = findViewById(R.id.btn_signIn);

        //check remember
        Paper.init(this);
        String user = Paper.book().read(Common.USER_KEY);
        String pass = Paper.book().read(Common.PWD_KEY);

        if (user != null && pass != null) {
            if (!user.isEmpty() && !pass.isEmpty())
                login(user, pass);
        }
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign = new Intent(MainActivity.this, SigninActivity.class);
                startActivity(sign);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUp = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(signUp);
            }
        });
    }

    private void login(String phone, String pass) {
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference users = db.getReference("Users");
        final android.app.AlertDialog pd = new SpotsDialog(MainActivity.this);
        pd.show();
        pd.setMessage("Tryin to Login");
        pd.setCancelable(false);

        final String localPhone = phone;
        final String Password = pass;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists()) {

                    pd.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);

                    if (Boolean.parseBoolean(user.getIsStaff())) {


                        if (user.getPassword().equals(Password)) {

                            Intent main = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(main);
                            finish();
                            Toast.makeText(MainActivity.this, "successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "login with staff unable to sign in", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "user not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
