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
import android.widget.EditText;
import android.widget.Toast;

import com.example.holi.data_save.Constant_data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Net_Activity extends AppCompatActivity {
    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private SharedPreferences shared_prf;
    private EditText edit_ip, edit_port;
    String ip, port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        shared_prf = getSharedPreferences(Constant_data.MY_PREFERENCE, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences
        ip = shared_prf.getString(Constant_data.IP_SET, "192.168.4.1");
        port = shared_prf.getString(Constant_data.PORT_SET, "1234");
        edit_ip = findViewById(R.id.editText_Ip);
        edit_port = findViewById(R.id.editText_port);
        edit_ip.setText(ip);
        edit_port.setText(port);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Мережа");                   // изменили надпись на Акшн баре
        actionBar.setDisplayHomeAsUpEnabled(true);          // и устанавливаем у нее стандартный метод показать кнопку назад
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(Net_Activity.this, Seting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_database:
                        Intent oi = new Intent(Net_Activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent oii = new Intent(Net_Activity.this, MainActivity.class);
                        startActivity(oii);
                        finishAffinity();
                        return true;
                    case R.id.navigation_user:
                        Intent oiii = new Intent(Net_Activity.this, Activity_person.class);
                        startActivity(oiii);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {            // создать меню (при включении)
        getMenuInflater().inflate(R.menu.save_menu, menu);   // добавляем меню в наше активити
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            finish();
        }
        if (item.getItemId() == R.id.id_save) {
            if (!edit_ip.getText().toString().isEmpty() && !edit_port.getText().toString().isEmpty()) {
                SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
                editor.putString(Constant_data.IP_SET, edit_ip.getText().toString());
                editor.putString(Constant_data.PORT_SET, edit_port.getText().toString());
                editor.apply();
                Toast.makeText(Net_Activity.this, "Збережено", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Net_Activity.this, "Введіть усі данні", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onBackPressed() {         //берем управление над кнопкой назад. (на клавиатуру не влияет)
        finish();
        return;
    }*/

}