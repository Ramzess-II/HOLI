package com.example.holi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Activity_person extends AppCompatActivity {
    private LinearLayout click_oper, click_fio;
    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView; // это нижнее меню

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        click_oper = findViewById(R.id.linearLayout_parrr);
        click_fio = findViewById(R.id.linearLayout_fio);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Налаштування оператора");                   // изменили надпись на Акшн баре
        actionBar.setDisplayHomeAsUpEnabled(true);          // и устанавливаем у нее стандартный метод показать кнопку назад
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
        click_oper.setOnClickListener(view -> {
            Intent i = new Intent(Activity_person.this, Operator_set_activity.class);  // открываем новую активити
            startActivity(i);
        });
        click_fio.setOnClickListener(view -> {
            Intent i = new Intent(Activity_person.this, Person_seting_activity.class);  // открываем новую активити
            startActivity(i);
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(Activity_person.this, Seting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_database:
                        Intent oi = new Intent(Activity_person.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent oii = new Intent(Activity_person.this, MainActivity.class);
                        startActivity(oii);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {         //берем управление над кнопкой назад. (на клавиатуру не влияет)
        Intent i = new Intent(Activity_person.this, MainActivity.class);
        startActivity(i);
        finishAffinity();
        return;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            Intent i = new Intent(Activity_person.this, MainActivity.class);
            startActivity(i);
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }
}