package com.example.holi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.holi.WiFi.Parser_data;
import com.example.holi.WiFi.WiFi_Manager;
import com.example.holi.data_save.Constant_data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ImageButton save_one, save_many;
    private ImageView img, img_wifi_ok, img_wifi_no, img_stab, img_no_stab, img_disk;
    private TextView display,connect,brutto_netto,stab,zero,over,kg_lb, active, new_brutto,zero1,stab1, npv;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private String _ip, _port;              // для передачи в сокет параметров
    private SharedPreferences shared_prf;   // для сохранения
    private SwipeRefreshLayout mSwipeRefreshLayout;     // потянул вниз - обновил
    private WiFi_Manager wifiManager;
    private boolean activiti_ok = false,blink_flag;
    private int count_send = 4,blink;
    private Parser_data parsingData;
    private float float_zero_tolerance;
    private LinearLayout look;
    private View v_npv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //light тема
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);//ночная тема
        init();
        small_display();
        runTimer();                                                   // запускаем таймер для обратных отсчетов
        save_one.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, One_sawe_activity.class);  // открываем новую активити
            startActivity(i);
        });
        save_many.setOnClickListener(view -> {
            if (!activiti_ok){
                Toast.makeText(MainActivity.this, "Доступно після активації", Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(MainActivity.this, Many_weight_activity.class);  // открываем новую активити
                startActivity(i);
            }
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(MainActivity.this, Seting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                   case R.id.navigation_database:
                        Intent oi = new Intent(MainActivity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_user:
                        Intent oii = new Intent(MainActivity.this, Activity_person.class);
                        startActivity(oii);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        mSwipeRefreshLayout.setRefreshing(true);
        super.onResume();         // функция которая запускается после onCreate
        readSharedPrf();          // считаем из памяти данные
        activiti_ok = shared_prf.getBoolean(Constant_data.ACTIVS, false);  // считываем значени флага блокировки
            if (Wifi_on_off()) {       // проверяем если вай фай включен то пробуем подключиться к сокету
                startSocet();
            }
    }

    private void readSharedPrf() {
        shared_prf = getSharedPreferences(Constant_data.MY_PREFERENCE, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences  Context обязательно
        _ip = shared_prf.getString(Constant_data.IP_SET, "192.168.4.1");      // считываем из памяти данные сокета
        _port = shared_prf.getString(Constant_data.PORT_SET, "1234");
        activiti_ok = shared_prf.getBoolean(Constant_data.ACTIVS, false);
        float_zero_tolerance = Float.parseFloat(shared_prf.getString(Constant_data.ZERO_TOLERANCE, "0.05"));
        if (!activiti_ok){
            active.setVisibility(View.VISIBLE);
        }
    }

    private void startSocet() {          // запускаем сокет
        wifiManager = new WiFi_Manager(MainActivity.this, _ip, _port);    // создаем класс и передаем туда параметры
        wifiManager.start();             // запускаем второстепенный поток
        mSwipeRefreshLayout.setRefreshing(false);   // включаем крутилку
    }


    private boolean Wifi_on_off() {     // спрашиваем у системы, а вай фай включен?
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);   // кидаем запрос
        if (!wifi.isWifiEnabled()) {      // если ложь то пишем что вай фай не включен
            Toast.makeText(MainActivity.this, "Wifi не ввімкнено", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;      // ну или возвращаем правду
        }
    }

    void small_display() {     // проверяем размер экрана
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int screenWidth = point.x;
        int screenHeight = point.y;
        if (screenHeight < 1000) {  // если по длине меньше 1000 то прячем картинку
            img.setVisibility(View.GONE);
        }
    }

    private void init() {
        save_one = findViewById(R.id.imageButton_save);
        save_many = findViewById(R.id.imageButton_many_save);
        img = findViewById(R.id.imageView_logo);
        display = findViewById(R.id.textView_display);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout1);  // это потянул - обновил
        mSwipeRefreshLayout.setOnRefreshListener(this);  // и для него активити выбрали
        mSwipeRefreshLayout.setColorSchemeResources(R.color.black);
        img_wifi_ok = findViewById(R.id.wifi_ok);
        img_wifi_no = findViewById(R.id.wifi_no);
        connect = findViewById(R.id.Text_wifi);
        brutto_netto = findViewById(R.id.textView_brutto);
        kg_lb = findViewById(R.id.text_units);
        stab = findViewById(R.id.textViewstab);
        zero = findViewById(R.id.textViewzero);
        over = findViewById(R.id.textViewower);
        parsingData = new Parser_data(this);
        active = findViewById(R.id.textView_active);
        new_brutto = findViewById(R.id.textView_new_brutto);
        look = findViewById(R.id.linearLayout_look);
        zero1 = findViewById(R.id.textView_zero1);
        stab1 = findViewById(R.id.textView_stab1);
        npv = findViewById(R.id.textView_ngz2);
        v_npv = findViewById(R.id.view_ngz2);
        img_stab = findViewById(R.id.image_stab_con1);
        img_no_stab = findViewById(R.id.image_no_stab_con1);
        img_disk = findViewById(R.id.image_stab_dis1);
    }

    private void NPV(boolean  flag){
        if(flag){
            v_npv.setVisibility(View.VISIBLE);
            npv.setVisibility(View.VISIBLE);
        } else {
            v_npv.setVisibility(View.GONE);
            npv.setVisibility(View.GONE);
        }
    }

    private void img_connect(boolean flag){
        if (flag){
            img_wifi_ok.setVisibility(View.VISIBLE);
            img_wifi_no.setVisibility(View.GONE);
            connect.setText("Connect");
        } else {
            img_wifi_ok.setVisibility(View.GONE);
            img_wifi_no.setVisibility(View.VISIBLE);
            connect.setText("Disconnect");
            display.setText("------ ");
            display.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            brutto_netto.setText("");
            kg_lb.setText("");
            stab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            zero.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            over.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            stab.setBackgroundResource(R.drawable.grey_rim);
            zero.setBackgroundResource(R.drawable.grey_rim);
            over.setBackgroundResource(R.drawable.grey_rim);
            zero1.setBackgroundResource(R.drawable.round_grey);
            zero1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            img_stab.setVisibility(View.GONE);
            img_no_stab.setVisibility(View.GONE);
            img_disk.setVisibility(View.VISIBLE);
        }
    }



    private void blink_wifi(){
        if(blink<10)blink ++;
        if(blink == 5) {
            blink = 0;
            blink_flag = !blink_flag;
        }
        if(blink_flag){
            img_wifi_no.setVisibility(View.GONE);
        } else {
            img_wifi_no.setVisibility(View.VISIBLE);
        }
    }

    private void runTimer() {                       // функция таймера который запускается через 1 секунду
        final Handler handler = new Handler();       // android os
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 200); // Запускаем код снова с задержкой в одну секунду
                if (wifiManager != null) {
                    if (wifiManager.probros().inpt_data) {       // через пустую функцию probros добираемся к флагу данных
                        count_send = 20;                          // взводим таймер чтоб данные сразу не пропали
                        wifiManager.probros().inpt_data = false; // и сбрасываем флаг, чтоб при следующей посылке увидеть новые данные
                        parsingData.Parsing_inpt(wifiManager.probros().data);// отправляем данные на парсинг
                        for (int i = 0; i < 19; i++) {                       // очищаем буфер чтоб не парсить тоже самое
                            wifiManager.probros().data[i] = 0x00;
                        }
                        wifiManager.probros().inpt_str = ""; // а так же очищаем ту строку по которой оприделяем данные (костыль)
                    }
                    // если счетчик больше нудя то показываем что есть соеденение
                    if (count_send > 0) {
                        count_send--;
                        img_connect (true);
                    } else {
                        img_connect (false);
                        blink_wifi();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    if(parsingData.parsing_ok){
                        parsingData.parsing_ok = false;
                        display.setText(String.format("%.8s", parsingData.out_symbol));
                        kg_lb.setText(parsingData.type_massa);
                        brutto_netto.setText(parsingData.brutto_netto);
                        new_brutto.setText(parsingData.brutto_netto);
                        if(parsingData.stab_no_stab == 1){
                            display.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            stab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            stab.setBackgroundResource(R.drawable.green_rim);
                            stab1.setBackgroundResource(R.drawable.round);
                            stab1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            img_stab.setVisibility(View.VISIBLE);
                            img_no_stab.setVisibility(View.GONE);
                        }
                        if(parsingData.stab_no_stab == 2){
                            display.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            stab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            stab.setBackgroundResource(R.drawable.red_rim);
                            stab1.setBackgroundResource(R.drawable.round_red);
                            stab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            img_stab.setVisibility(View.GONE);
                            img_no_stab.setVisibility(View.VISIBLE);
                        }
                        if(parsingData.overload){
                            over.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            over.setBackgroundResource(R.drawable.red_rim);
                        } else {
                            over.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            over.setBackgroundResource(R.drawable.green_rim);
                        }
                        NPV(parsingData.overload);
                        if(parsingData.out_data <= (0 + float_zero_tolerance) && parsingData.out_data >= (0 - float_zero_tolerance)){
                            zero.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            zero.setBackgroundResource(R.drawable.green_rim);
                            zero1.setBackgroundResource(R.drawable.round);
                            zero1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                        }
                        else {
                            zero.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            zero.setBackgroundResource(R.drawable.grey_rim);
                            zero1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            zero1.setBackgroundResource(R.drawable.round_grey);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wifiManager != null)
            wifiManager.socketClose();     // когда переходим в паузу убить сокет
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();              // когда выходим из активити убить сокет
        if (wifiManager != null) wifiManager.socketClose();
    }

    @Override
    public void onRefresh() {//если сразу делать setRefreshing(false); то єта функция тупо не вызывается
        mSwipeRefreshLayout.setRefreshing(true);
        if (Wifi_on_off()) {          // если есть вай фай то если тянем вниз
            if (wifiManager != null) wifiManager.socketClose();  //закрываем старый сокет
            if (!wifiManager.connect_ok && !wifiManager.more_connect) {  // если нет подключения и подключение к сокету не запущено
                startSocet();  // пробуем подключиться
            }
        }
    }


}