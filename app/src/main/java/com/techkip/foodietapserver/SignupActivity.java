package com.techkip.foodietapserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techkip.foodietapserver.common.Common;
import com.techkip.foodietapserver.model.User;


import dmax.dialog.SpotsDialog;

public class SignupActivity extends AppCompatActivity {
    EditText etPhone,etName, etPassword,etSecureCode;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        final DatabaseReference table_users = db.getReference("Users");
        etPhone = findViewById(R.id.et_phone);
        etName = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);
        etSecureCode = findViewById(R.id.et_secureCode);


        btnSignUp = findViewById(R.id.btn_signUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                final android.app.AlertDialog pd = new SpotsDialog(SignupActivity.this);
                pd.show();
                pd.setMessage("Registering....");
                table_users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(etPhone.getText().toString()).exists()) {

                            pd.dismiss();
                            Toast.makeText(SignupActivity.this, "Phone Number already Exist",Toast.LENGTH_LONG).show();

                        } else {
                            pd.dismiss();
                            User user = new User(etName.getText().toString(),etPhone.getText().toString(),etPassword.getText().toString(),etSecureCode.getText().toString());
                            table_users.child(etPhone.getText().toString()).setValue(user);
                            Toast.makeText(SignupActivity.this, "Successs Registration", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                }
                else {
                    Toast.makeText(SignupActivity.this, "check internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
