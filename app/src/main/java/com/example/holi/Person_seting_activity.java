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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.holi.data_save.Constant_data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Person_seting_activity extends AppCompatActivity {
    private EditText oper1, oper2, oper3, oper4, oper5;
    private TextView param;

    private CheckBox c_oper1, c_oper2, c_oper3, c_oper4, c_oper5;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private SharedPreferences shared_prf;   // для сохранения
    private int num_operator;
    private ActionBar actionBar;
    private String name_op1, name_op2, name_op3, name_op4, name_op5;
    private boolean change = false;
    private Menu mMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_seting);
        init();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(Person_seting_activity.this, Seting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_database:
                        Intent oi = new Intent(Person_seting_activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent oii = new Intent(Person_seting_activity.this, MainActivity.class);
                        startActivity(oii);
                        finishAffinity();
                        return true;
                    case R.id.navigation_user:
                        Intent oiii = new Intent(Person_seting_activity.this, Activity_person.class);
                        startActivity(oiii);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
        c_oper1.setOnClickListener(view -> {
            reset_check();
            c_oper1.setChecked(true);
            num_operator = 1;
            saveSharedPrf();
        });
        c_oper2.setOnClickListener(view -> {
            reset_check();
            c_oper2.setChecked(true);
            num_operator = 2;
            saveSharedPrf();
        });
        c_oper3.setOnClickListener(view -> {
            reset_check();
            c_oper3.setChecked(true);
            num_operator = 3;
            saveSharedPrf();
        });
        c_oper4.setOnClickListener(view -> {
            reset_check();
            c_oper4.setChecked(true);
            num_operator = 4;
            saveSharedPrf();
        });
        c_oper5.setOnClickListener(view -> {
            reset_check();
            c_oper5.setChecked(true);
            num_operator = 5;
            saveSharedPrf();
        });

    }

    @Override
    protected void onResume() {
        readSharedPrf();
        set_fio();
        start_set();
        change_fio(false);
        super.onResume();
    }


    private void saveSharedPrf() {
        SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
        editor.putInt(Constant_data.NUM_OPERATOR, num_operator);
        editor.putString(Constant_data.FIO_1, oper1.getText().toString());
        editor.putString(Constant_data.FIO_2, oper2.getText().toString());
        editor.putString(Constant_data.FIO_3, oper3.getText().toString());
        editor.putString(Constant_data.FIO_4, oper4.getText().toString());
        editor.putString(Constant_data.FIO_5, oper5.getText().toString());
        editor.apply();
    }

    private void readSharedPrf() {
        shared_prf = getSharedPreferences(Constant_data.MY_PREFERENCE, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences  Context обязательно
        num_operator = shared_prf.getInt(Constant_data.NUM_OPERATOR, 1);
        name_op1 = shared_prf.getString(Constant_data.FIO_1, "");
        name_op2 = shared_prf.getString(Constant_data.FIO_2, "");
        name_op3 = shared_prf.getString(Constant_data.FIO_3, "");
        name_op4 = shared_prf.getString(Constant_data.FIO_4, "");
        name_op5 = shared_prf.getString(Constant_data.FIO_5, "");
    }

    private void set_fio() {
        oper1.setText(name_op1);
        oper2.setText(name_op2);
        oper3.setText(name_op3);
        oper4.setText(name_op4);
        oper5.setText(name_op5);
    }

    private void start_set() {
        switch (num_operator) {
            case (1):
                c_oper1.setChecked(true);
                break;
            case (2):
                c_oper2.setChecked(true);
                break;
            case (3):
                c_oper3.setChecked(true);
                break;
            case (4):
                c_oper4.setChecked(true);
                break;
            case (5):
                c_oper5.setChecked(true);
                break;
        }
    }

    private void init() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("Вибір оператора");                   // изменили надпись на Акшн баре
        actionBar.setDisplayHomeAsUpEnabled(true);          // и устанавливаем у нее стандартный метод показать кнопку назад
        oper1 = findViewById(R.id.textView_op1);
        oper2 = findViewById(R.id.textView_op2);
        oper3 = findViewById(R.id.textView_op3);
        oper4 = findViewById(R.id.textView_op4);
        oper5 = findViewById(R.id.textView_op5);
        c_oper1 = findViewById(R.id.checkBox_1);
        c_oper2 = findViewById(R.id.checkBox_2);
        c_oper3 = findViewById(R.id.checkBox_3);
        c_oper4 = findViewById(R.id.checkBox_4);
        c_oper5 = findViewById(R.id.checkBox_5);
        param = findViewById(R.id.textView_save);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
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
        if (item.getItemId() == R.id.id_saved) {
            saveSharedPrf();
            Toast.makeText(Person_seting_activity.this, "Збережено", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (item.getItemId() == R.id.id_rename) {
            change = !change;
            change_fio(change);
            if (change) {
                mMenuItem.getItem(0).setIcon(R.drawable.ic_baseline_create_red_24);    // через переменную изменяем картинку. индекс это позиция картинки
            } else {
                mMenuItem.getItem(0).setIcon(R.drawable.ic_baseline_create_24);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void change_fio(boolean chan) {
        if (chan) {
            oper1.setEnabled(true);
            oper2.setEnabled(true);
            oper3.setEnabled(true);
            oper4.setEnabled(true);
            oper5.setEnabled(true);
        } else {
            oper1.setEnabled(false);
            oper2.setEnabled(false);
            oper3.setEnabled(false);
            oper4.setEnabled(false);
            oper5.setEnabled(false);
        }
    }

    private void reset_check() {
        c_oper1.setChecked(false);
        c_oper2.setChecked(false);
        c_oper3.setChecked(false);
        c_oper4.setChecked(false);
        c_oper5.setChecked(false);
    }
}