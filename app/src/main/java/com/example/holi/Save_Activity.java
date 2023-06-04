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
import android.widget.TextView;
import android.widget.Toast;

import com.example.holi.Data_base.AppExecuter;
import com.example.holi.Data_base.MyDbManager;
import com.example.holi.data_save.Constant_data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Save_Activity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private ActionBar actionBar;
    private SharedPreferences shared_prf;
    private MyDbManager myDbManager;
    private TextView massa_out,data_tim;
    private String massa_common,formattedDate;
    EditText number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        myDbManager = new MyDbManager(this);
        init();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_home:
                        Intent i = new Intent(Save_Activity.this, MainActivity.class);  // открываем новую активити
                        startActivity(i);
                        return true;
                    /*case R.id.navigation_data_base:
                        Intent oi = new Intent(Seting_Activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        return true;
                    case R.id.navigation_home:
                        Intent ooi = new Intent(Seting_Activity.this, MainActivity.class);
                        startActivity(ooi);
                        return true;*/
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        myDbManager.openDb();   // открыть базу данных
    }


    private void init(){
        actionBar = getSupportActionBar();
        actionBar.setTitle("Налаштування");                   // изменили надпись на Акшн баре
        actionBar.setDisplayHomeAsUpEnabled(true);          // и устанавливаем у нее стандартный метод показать кнопку назад
        massa_out = findViewById(R.id.text_massa);
        shared_prf = getSharedPreferences(Constant_data.MY_PREFERENCE, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences
        Intent i = getIntent();
       // massa_common = i.getStringExtra(Constant_data.COMMON_MASSA);
        massa_out.setText(massa_common);
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
        number = findViewById(R.id.editTextNumber);
        data_tim = findViewById(R.id.data_tim);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(calendar.getTime());
        data_tim.setText(formattedDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {            // создать меню (при включении)
        getMenuInflater().inflate(R.menu.save_menu, menu);   // добавляем меню в наше активити
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            Intent i = new Intent(Save_Activity.this, MainActivity.class);  // открываем новую активити
            startActivity(i);

        }
        if (item.getItemId() == R.id.id_save){
            // создаем календарь, чтоб дата автоматом сохранялась

            if (!number.getText().toString().isEmpty()){
                Toast.makeText(this, "Збережено", Toast.LENGTH_SHORT).show();
                AppExecuter.getInstance().getSubIO().execute(new Runnable() {
                    @Override
                    public void run() {    // запускаем запись на второстепенном потоке
                        myDbManager.insertToDb(number.getText().toString(), massa_common, "non",  "non", "non",
                                formattedDate, " ", " ","","");             // записываем в базу
                       // finish();
                    }
                });
            } else Toast.makeText(this, "Введіть номер", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDbManager.CloseDb();    // закрыть базу данных
    }

    @Override
    public void onBackPressed() {         //берем управление над кнопкой назад. (на клавиатуру не влияет)
        Intent i = new Intent(Save_Activity.this, MainActivity.class);  // открываем новую активити
        startActivity(i);                                                // закрыть текущую активити
        return;
    }

}