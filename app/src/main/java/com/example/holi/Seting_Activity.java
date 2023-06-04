package com.example.holi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Seting_Activity extends AppCompatActivity {
    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private LinearLayout net, protocol, weight, info, password, pass_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Налаштування");                   // изменили надпись на Акшн баре
        actionBar.setDisplayHomeAsUpEnabled(true);          // и устанавливаем у нее стандартный метод показать кнопку назад
        net = findViewById(R.id.linearLayout1);
        protocol = findViewById(R.id.linearLayout2);
        password = findViewById(R.id.linearLayout3);
        weight = findViewById(R.id.linearLayout13);
        info = findViewById(R.id.linearLayout14);
        pass_user = findViewById(R.id.linearLayoutPass_user);
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_home:
                        Intent i = new Intent(Seting_Activity.this, MainActivity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_database:
                        Intent oi = new Intent(Seting_Activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_user:
                        Intent oii = new Intent(Seting_Activity.this, Activity_person.class);
                        startActivity(oii);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
        net.setOnTouchListener((view, motionEvent) -> {
            net.setBackgroundResource(R.color.grey);
            Intent i = new Intent(Seting_Activity.this, Net_Activity.class);
            startActivity(i);
            return false;
        });
        protocol.setOnTouchListener((view, motionEvent) -> {
            protocol.setBackgroundResource(R.color.grey);
            Intent i = new Intent(Seting_Activity.this, Protocol_activity.class);
            startActivity(i);
            return false;
        });
        password.setOnTouchListener((view, motionEvent) -> {
            password.setBackgroundResource(R.color.grey);
            Intent i = new Intent(Seting_Activity.this, password_activity.class);
            startActivity(i);
            return false;
        });
        weight.setOnTouchListener((view, motionEvent) -> {
            weight.setBackgroundResource(R.color.grey);
            Intent i = new Intent(Seting_Activity.this, Weight_set_activity.class);
            startActivity(i);
            return false;
        });
        info.setOnTouchListener((view, motionEvent) -> {
            info.setBackgroundResource(R.color.grey);
            Intent i = new Intent(Seting_Activity.this, Info_activity.class);
            startActivity(i);
            return false;
        });
        pass_user.setOnTouchListener((view, motionEvent) -> {
            pass_user.setBackgroundResource(R.color.grey);
            Intent i = new Intent(Seting_Activity.this, User_pass_activity.class);
            startActivity(i);
            return false;
        });
    }

    @Override
    public void onBackPressed() {         //берем управление над кнопкой назад. (на клавиатуру не влияет)
        Intent i = new Intent(Seting_Activity.this, MainActivity.class);
        startActivity(i);
        finishAffinity();
        return;
    }

    @Override
    protected void onResume() {
        net.setBackgroundResource(R.color.white);
        protocol.setBackgroundResource(R.color.white);
        password.setBackgroundResource(R.color.white);
        weight.setBackgroundResource(R.color.white);
        info.setBackgroundResource(R.color.white);
        pass_user.setBackgroundResource(R.color.white);
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            Intent i = new Intent(Seting_Activity.this, MainActivity.class);  // открываем новую активити
            startActivity(i);                                                // закрыть текущую активити
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }

}