package com.example.irmarcelbag.tembeza;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mDriver, mCustomer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       mDriver = findViewById(R.id.driver);
        mCustomer = findViewById(R.id.customer);
       // mDriver = findViewById(R.id.driver);
       // mCustomer = findViewById(R.id.custom);
    //Activity of Driver
        mDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this, DriverLoginActivity.class);
                //Intent intent = new Intent(getApplicationContext(MainActivity.this)DriverLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        // Activity of Customer
        mCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this, CustomerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}
