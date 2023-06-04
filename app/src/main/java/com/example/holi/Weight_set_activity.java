package com.example.holi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.holi.data_save.Constant_data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Weight_set_activity extends AppCompatActivity {
    private CheckBox on_of;
    private EditText min_mass, upper_mass, lower_mass, zero_tolerance;
    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private SharedPreferences shared_prf;   // для сохранения
    private Boolean off_on, save, change = false;
    private Menu mMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_set);
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
        actionBar = getSupportActionBar();
        actionBar.setTitle("Зважування");                   // изменили надпись на Акшн баре
        actionBar.setDisplayHomeAsUpEnabled(true);          // и устанавливаем у нее стандартный метод показать кнопку назад
        min_mass = findViewById(R.id.editText_min_mass);
        lower_mass = findViewById(R.id.editText_lower);
        upper_mass = findViewById(R.id.editText_upper);
        on_of = findViewById(R.id.checkBox_on_off);
        zero_tolerance = findViewById(R.id.editText_zeroTolerance);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(Weight_set_activity.this, Seting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_database:
                        Intent oi = new Intent(Weight_set_activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent oii = new Intent(Weight_set_activity.this, MainActivity.class);
                        startActivity(oii);
                        finishAffinity();
                        return true;
                    case R.id.navigation_user:
                        Intent oiii = new Intent(Weight_set_activity.this, Activity_person.class);
                        startActivity(oiii);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
        on_of.setOnClickListener(view -> {
          if (on_of.isChecked()){
              SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
              editor.putBoolean(Constant_data.TRACKING, true);
              editor.apply();
          }  else {
              SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
              editor.putBoolean(Constant_data.TRACKING, false);
              editor.apply();
          }
        });
    }

    @Override
    protected void onResume() {
        save = false;
        readSharedPrf();
        change_set(false);
        super.onResume();
    }

    private void readSharedPrf() {
        shared_prf = getSharedPreferences(Constant_data.MY_PREFERENCE, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences  Context обязательно
        min_mass.setText(shared_prf.getString(Constant_data.MIN_MASS, "3.00"));
        upper_mass.setText(shared_prf.getString(Constant_data.UPPER_MASS, "10.250"));
        lower_mass.setText(shared_prf.getString(Constant_data.LOWER_MASS, "9.850"));
        zero_tolerance.setText(shared_prf.getString(Constant_data.ZERO_TOLERANCE, "0.05"));
        off_on = shared_prf.getBoolean(Constant_data.TRACKING, false);
        if (off_on){
            on_of.setChecked(true);
        }else {
            on_of.setChecked(false);
        }
    }

    private void saveSharedPrf(boolean on, String min_mas, String upper, String lower, String zero) {
        String check;
        float check2;
        SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
        editor.putBoolean(Constant_data.TRACKING, on);
        save = true;
        check = min_mas;
        try {
            check2 = Float.parseFloat(check);
        }
        catch (NumberFormatException e){
            save = false;
            return;
        }
        check = upper;
        try {
            check2 = Float.parseFloat(check);
        }
        catch (NumberFormatException e){
            save = false;
            return;
        }
        check = lower;
        try {
            check2 = Float.parseFloat(check);
        }
        catch (NumberFormatException e){
            save = false;
            return;
        }
        check = zero;
        try {
            check2 = Float.parseFloat(check);
        }
        catch (NumberFormatException e){
            save = false;
            return;
        }
        editor.putString(Constant_data.MIN_MASS, min_mas);
        editor.putString(Constant_data.LOWER_MASS, lower);
        editor.putString(Constant_data.UPPER_MASS, upper);
        editor.putString(Constant_data.ZERO_TOLERANCE, zero);
        editor.apply();
    }

    private void change_set(boolean i){
            min_mass.setEnabled(i);
            zero_tolerance.setEnabled(i);
            lower_mass.setEnabled(i);
            upper_mass.setEnabled(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {            // создать меню (при включении)
        getMenuInflater().inflate(R.menu.change_menu, menu);   // добавляем меню в наше активити
        mMenuItem = menu;                                      // переменная чтоб можно было получить доступ к картинке в меню
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            finish();
        }
        if (item.getItemId() == R.id.id_rename) {
            change = !change;
            change_set(change);
            if (change) {
                mMenuItem.getItem(0).setIcon(R.drawable.ic_baseline_create_red_24);    // через переменную изменяем картинку. индекс это позиция картинки
            } else {
                mMenuItem.getItem(0).setIcon(R.drawable.ic_baseline_create_24);
            }
        }
        if (item.getItemId() == R.id.id_saved) {
            saveSharedPrf(on_of.isChecked(), min_mass.getText().toString(), upper_mass.getText().toString(), lower_mass.getText().toString(), zero_tolerance.getText().toString());
            if (save) {
                Toast.makeText(Weight_set_activity.this, "Збережено", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(Weight_set_activity.this, "Перевірте правильність вводу", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }


}