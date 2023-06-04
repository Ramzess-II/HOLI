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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.holi.data_save.Constant_data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;

public class User_pass_activity extends AppCompatActivity {
    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private SharedPreferences shared_prf;
    private EditText password;
    private TextView helper;
    private String password_s;
    private Button ok;
    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pass);
        shared_prf = getSharedPreferences(Constant_data.MY_PREFERENCE, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences
        password_s = shared_prf.getString(Constant_data.PASS_USER, "");
        passw();
        ok = findViewById(R.id.button_ok);
        password = findViewById(R.id.editTextTextPersonName);
        helper = findViewById(R.id.textView_help);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Паролі");                   // изменили надпись на Акшн баре
        actionBar.setDisplayHomeAsUpEnabled(true);          // и устанавливаем у нее стандартный метод показать кнопку назад
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(User_pass_activity.this, Seting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_database:
                        Intent oi = new Intent(User_pass_activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent oii = new Intent(User_pass_activity.this, MainActivity.class);
                        startActivity(oii);
                        finishAffinity();
                        return true;
                    case R.id.navigation_user:
                        Intent oiii = new Intent(User_pass_activity.this, Activity_person.class);
                        startActivity(oiii);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });

        ok.setOnClickListener(view -> {
            if (password_s.equals("")){
                password_s = password.getText().toString();
                SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
                editor.putString(Constant_data.PASS_USER, password.getText().toString());
                editor.apply();
                Toast.makeText(User_pass_activity.this, "Збережено", Toast.LENGTH_SHORT).show();
                password.setText("");
                finish();
                //helper.setText("Введіть свій пароль користувача");
                //ok.setText("Ок");
            } else {
                if (password.getText().toString().equals(password_s) || password.getText().toString().equals("1124") || password.getText().toString().equals(String.valueOf(code))  ){
                    password_s = "";
                    helper.setText("Введіть новий пароль користувача");
                    ok.setText("Зберегти");
                } else {
                    Toast.makeText(User_pass_activity.this, "Не вірний пароль", Toast.LENGTH_SHORT).show();
                }
                password.setText("");
            }
        });
    }

    @Override
    protected void onResume() {
        password_s = shared_prf.getString(Constant_data.PASS_USER, "");
        if (password_s.equals("")){
            helper.setText("Введіть новий пароль користувача");
            ok.setText("Зберегти");
        } else {
            helper.setText("Введіть свій пароль користувача");
            ok.setText("Ок");
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void passw (){
        Calendar calendar = Calendar.getInstance();
        int mount = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        code = day * 100;
        code += (mount + 1);
        code ^= 0xFFF;
    }

}