package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;

public class Menu_ZMY extends AppCompatActivity {

    private Button received;
    private Button sent;
    private Button favoutite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__z_m_y);
        setTitle("                                    Home");

        received=findViewById(R.id.btn_received);
        sent=findViewById(R.id.btn_sent);
        favoutite=findViewById(R.id.btn_favourite);

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        return true;
                    case R.id.capsule:
                        startActivity(new Intent(getApplicationContext(),TimeCapsuleNavigatePage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.contact:
                        startActivity(new Intent(getApplicationContext(),ContactActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.timeline:
                        startActivity(new Intent(getApplicationContext(),TimeLineActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoReceivedCapsulepage();
            }
        });

        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSentCapsulepage();
            }
        });

        favoutite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFavouriteCapsulepage();
            }
        });
    }

    private void gotoReceivedCapsulepage() {
        Intent intent=new Intent(getApplicationContext(),TimeCapsuleNavigatePage.class);
        startActivity(intent);
    }

    private void gotoSentCapsulepage() {
        Intent intent=new Intent(getApplicationContext(),TimeCapsuleNavigatePage_Sent.class);
        startActivity(intent);
    }

    private void gotoFavouriteCapsulepage() {
        Intent intent=new Intent(getApplicationContext(),TimeCapsuleNavigatePage_favourite.class);
        startActivity(intent);
    }
}