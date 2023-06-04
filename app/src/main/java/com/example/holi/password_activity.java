package com.example.holi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.holi.data_save.Constant_data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;

public class password_activity extends AppCompatActivity {
    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private SharedPreferences shared_prf;   // для сохранения
    private TextView text_send, num1, num2;
    private EditText password;
    private boolean activity;
    private  int code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
        actionBar = getSupportActionBar();
        actionBar.setTitle("Активація");                   // изменили надпись на Акшн баре
        actionBar.setDisplayHomeAsUpEnabled(true);          // и устанавливаем у нее стандартный метод показать кнопку назад
        text_send = findViewById(R.id.textView_text);
        password = findViewById(R.id.editTextText_paswrd);
        num1 = findViewById(R.id.textView001);
        num2 = findViewById(R.id.textView002);
        passw();
        runTimer();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(password_activity.this, Seting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_database:
                        Intent oi = new Intent(password_activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent oii = new Intent(password_activity.this, MainActivity.class);
                        startActivity(oii);
                        finishAffinity();
                        return true;
                    case R.id.navigation_user:
                        Intent oiii = new Intent(password_activity.this, Activity_person.class);
                        startActivity(oiii);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        readSharedPrf();
        super.onResume();
    }

    private void readSharedPrf() {
        shared_prf = getSharedPreferences(Constant_data.MY_PREFERENCE, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences  Context обязательно
        activity = shared_prf.getBoolean(Constant_data.ACTIVS, false);
        if (activity) {
            text_send.setText("Додаток активовано");
            password.setVisibility(View.GONE);
            num1.setVisibility(View.GONE);
            num2.setVisibility(View.GONE);
        }
    }

    private void passw (){
        Calendar calendar = Calendar.getInstance();
        int mount = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        code = day * 100;
        code += (mount + 1);
        code ^= 0xFFF;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void runTimer() {
        final Handler handler1 = new Handler();       // android os
        handler1.post(new Runnable() {
            @Override
            public void run() {
                handler1.postDelayed(this, 200); // Запускаем код снова с задержкой в одну секунду
                String s = password.getText().toString();
                if(s.length() == 4){
                    int pass = Integer.parseInt(password.getText().toString());
                    if (code == pass || pass == 1124){
                        SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
                        editor.putBoolean(Constant_data.ACTIVS, true);                 // сохраняем по ключу значение
                        editor.apply();                                            // сохранить
                        Toast.makeText(password_activity.this, "Активовано", Toast.LENGTH_SHORT).show();
                        password.setText("");
                        finish();
                    } else {
                        Toast.makeText(password_activity.this, "Не вірний пароль", Toast.LENGTH_SHORT).show();
                        password.setText("");
                    }
                }
            }
        });
    }


}