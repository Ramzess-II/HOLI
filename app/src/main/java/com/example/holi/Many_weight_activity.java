package com.example.holi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.holi.Adapter.ListItm;
import com.example.holi.Data_base.AppExecuter;
import com.example.holi.Data_base.MyDbManager;
import com.example.holi.Data_base.OnDataResive;
import com.example.holi.WiFi.Parser_data;
import com.example.holi.WiFi.WiFi_Manager;
import com.example.holi.data_save.Constant_data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Many_weight_activity extends AppCompatActivity implements OnDataResive {
    private EditText num_party, par1, par2, par3, cargo;
    private Button start, stop;
    private TextClock clock;
    private LinearLayout one, two, three;
    private TextView stab, zero, over, massa, fio, kg_lb, brutto_netto, name_par1, name_par2, name_par3, tablo, num_weight, total_weight;
    private ImageView img_wifi_ok, img_wifi_no, img_stab, img_no_stab, img_disk;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private WiFi_Manager wifiManager;
    private boolean activiti_ok = false, blink_flag, overload;
    private int count_send, blink, num_operator;
    private Parser_data parsingData;
    private String _ip, _port, times, name_par1_s, name_par2_s, name_par3_s, fio_s, string_int;
    private SharedPreferences shared_prf;   // для сохранения
    private MyDbManager myDbManager;
    private List<ListItm> data = new ArrayList<>();  // а это массив классов которые хранят инфу
    //----------------------- отличия----------------------------------//
    private LinearLayout display;
    private TextView num_weight2, min, max, ngz;
    private View v_ngz;
    private boolean starts, tracing_weight, zero_ok, stability, sound, err_mass;
    private float float_zero_tolerance, float_mass, min_mass, lower_weight, upper_weight, total_sum;
    private MediaPlayer mediaPlayer, mediaPlayer1, mediaPlayer2;
    private int count_stab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_many_weight);
        myDbManager = new MyDbManager(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.pravilnyiy);
        mediaPlayer1 = MediaPlayer.create(this, R.raw.not);
        mediaPlayer2 = MediaPlayer.create(this, R.raw.line_open);
        init();
        runTimer();                                                   // запускаем таймер для обратных отсчетов
        runTimer1();
        small_display();
        parsingData = new Parser_data(this);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(Many_weight_activity.this, Seting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_database:
                        Intent oi = new Intent(Many_weight_activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent oii = new Intent(Many_weight_activity.this, MainActivity.class);
                        startActivity(oii);
                        finishAffinity();
                        return true;
                    case R.id.navigation_user:
                        Intent oiii = new Intent(Many_weight_activity.this, Activity_person.class);
                        startActivity(oiii);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
        start.setOnClickListener(view -> {
            if (count_send > 0) {
                if (num_party.getText().toString().isEmpty()) {
                    Toast.makeText(Many_weight_activity.this, "Введіть партію!", Toast.LENGTH_SHORT).show();
                } else {
                    stop.setVisibility(View.VISIBLE);
                    start.setVisibility(View.GONE);
                    starts = true;
                    zero_ok = false;
                    stability = false;
                    display.setVisibility(View.VISIBLE);
                    total_weight.setText("0" + parsingData.type_massa);
                }
            } else {
                Toast.makeText(Many_weight_activity.this, "Не має зв'язку", Toast.LENGTH_SHORT).show();
            }
        });
        stop.setOnClickListener(view -> {
            stop.setVisibility(View.GONE);
            start.setVisibility(View.VISIBLE);
            starts = false;
            display.setVisibility(View.GONE);
            zero_ok = false;
            stability = false;
            total_sum = 0;
            //String form = String.format("%10.0f", total_sum);
            //total_weight.setText(form + parsingData.type_massa);
            total_weight.setText("0" + parsingData.type_massa);
            finish();
        });
    }

    private void read_last() {                     //  считываем номер накладной последний и прибавляем 1
        if (!(data.size() == 0)) {                 // если база данных не пустая
            string_int = data.get(0).getNumber();  // получаем нулевой номер, так как он самый последний (сортировка такая)
            try {
                long i = Long.parseLong(string_int);  // преобразуем в лонг
                i++;                                   // добавим 1
                string_int = Long.toString(i);         // и снова в стринг
                num_weight.setText(string_int);        // установим новое значение
                num_weight2.setText(string_int);
            } catch (NumberFormatException ignored) {
            }
        } else {
            num_weight.setText("1");
            num_weight2.setText("1");
        }
    }

    private void init() {
        start = findViewById(R.id.button_start);
        stop = findViewById(R.id.button_stop);
        num_party = findViewById(R.id.editText_num_party);
        stab = findViewById(R.id.textView_stab_1);
        zero = findViewById(R.id.textView_zero_1);
        over = findViewById(R.id.textView_over_1);
        massa = findViewById(R.id.textView_mass_1);
        fio = findViewById(R.id.textView_fio_1);
        num_weight = findViewById(R.id.textView_nums_weight);
        par1 = findViewById(R.id.editText_parr1_1);
        par2 = findViewById(R.id.editText_parr2_1);
        par3 = findViewById(R.id.editText_parr3_1);
        name_par1 = findViewById(R.id.num_read1_1);
        name_par2 = findViewById(R.id.num_read2_1);
        name_par3 = findViewById(R.id.num_read3_1);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        img_wifi_ok = findViewById(R.id.imageView_wifi_ok);
        img_wifi_no = findViewById(R.id.imageView_wifi_no);
        kg_lb = findViewById(R.id.textView_units_1);
        clock = findViewById(R.id.textView_time_1);
        brutto_netto = findViewById(R.id.textView_netto_1);
        one = findViewById(R.id.linearLayout_one_1);
        two = findViewById(R.id.linearLayout_two_1);
        three = findViewById(R.id.linearLayout_thre_1);
        tablo = findViewById(R.id.textView_tablo);
        display = findViewById(R.id.Lay_display);
        num_weight2 = findViewById(R.id.textView_number2);
        cargo = findViewById(R.id.editText_num_weight_1);
        total_weight = findViewById(R.id.textView_total_weight);
        min = findViewById(R.id.textView_min);
        max = findViewById(R.id.textView_max);
        v_ngz = findViewById(R.id.view_ngz1);
        ngz = findViewById(R.id.textView_ngz1);
        img_stab = findViewById(R.id.image_stab_con3);
        img_no_stab = findViewById(R.id.image_no_stab_con3);
        img_disk = findViewById(R.id.image_stab_dis3);
    }

    @Override
    protected void onResume() {
        super.onResume();         // функция которая запускается после onCreate
        myDbManager.openDb();   // открыть базу данных
        readFromDb("");    // если передаем пустоту то ищем все
        readSharedPrf();         // считаем из памяти данные
        activiti_ok = shared_prf.getBoolean(Constant_data.ACTIVS, false);  // считываем значени флага блокировки
        starts = false;
        display.setVisibility(View.GONE);
        if (Wifi_on_off()) {       // проверяем если вай фай включен то пробуем подключиться к сокету
            startSocet();
        }
        total_sum = 0;
        stop.setVisibility(View.GONE);
    }

    private void readFromDb(final String text) {  // считать из Базы
        AppExecuter.getInstance().getSubIO().execute(new Runnable() {  // на второстепенном потоке
            @Override
            public void run() { // тут ответа не будет он появиться на основном потоке после считывания orResive()
                myDbManager.ReadDb(text, Many_weight_activity.this, 1);    // запускаем выполнение на второстепенном потоке
            }
        });
    }

    private void readSharedPrf() {
        shared_prf = getSharedPreferences(Constant_data.MY_PREFERENCE, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences  Context обязательно
        _ip = shared_prf.getString(Constant_data.IP_SET, "192.168.4.1");      // считываем из памяти данные сокета
        _port = shared_prf.getString(Constant_data.PORT_SET, "1234");
        num_operator = shared_prf.getInt(Constant_data.NUM_OPERATOR, 1);    // считали номер оператора
        tracing_weight = shared_prf.getBoolean(Constant_data.TRACKING, false);
        min_mass = Float.parseFloat(shared_prf.getString(Constant_data.MIN_MASS, "3.00"));
        lower_weight = Float.parseFloat(shared_prf.getString(Constant_data.LOWER_MASS, "9.850"));
        upper_weight = Float.parseFloat(shared_prf.getString(Constant_data.UPPER_MASS, "10.250"));
        min.setText(shared_prf.getString(Constant_data.LOWER_MASS, "9.850"));
        max.setText(shared_prf.getString(Constant_data.UPPER_MASS, "10.250"));
        float_zero_tolerance = Float.parseFloat(shared_prf.getString(Constant_data.ZERO_TOLERANCE, "0.05"));
        switch (num_operator) {
            case 1:
                fio_s = shared_prf.getString(Constant_data.FIO_1, " ");    // потом его ФИО по номеру
                break;
            case 2:
                fio_s = shared_prf.getString(Constant_data.FIO_2, " ");
                break;
            case 3:
                fio_s = shared_prf.getString(Constant_data.FIO_3, " ");
                break;
            case 4:
                fio_s = shared_prf.getString(Constant_data.FIO_4, " ");
                break;
            case 5:
                fio_s = shared_prf.getString(Constant_data.FIO_5, " ");
                break;
        }
        fio.setText(fio_s);                                                    // сразу установили его
        name_par1_s = shared_prf.getString(Constant_data.PAR1_1, "");      // считали параметры из установок
        name_par2_s = shared_prf.getString(Constant_data.PAR2_1, "");
        name_par3_s = shared_prf.getString(Constant_data.PAR3_1, "");
        if (!name_par1_s.isEmpty()) {                                          // если параметр пуст то скрыть нафиг, весь линефр лайоут, чтоб другие подвинулись вверх
            name_par1.setText(name_par1_s);
        } else {
            one.setVisibility(View.GONE);
        }
        if (!name_par2_s.isEmpty()) {
            name_par2.setText(name_par2_s);
        } else {
            two.setVisibility(View.GONE);
        }
        if (!name_par3_s.isEmpty()) {
            name_par3.setText(name_par3_s);
        } else {
            three.setVisibility(View.GONE);
        }
    }

    private void blink_wifi() {      // это моргать если нет подключения
        if (blink < 10) blink++;
        if (blink == 5) {
            blink = 0;
            blink_flag = !blink_flag;
        }
        if (blink_flag) {
            img_wifi_no.setVisibility(View.GONE);
        } else {
            img_wifi_no.setVisibility(View.VISIBLE);
        }
    }

    private void startSocet() {          // запускаем сокет
        wifiManager = new WiFi_Manager(Many_weight_activity.this, _ip, _port);    // создаем класс и передаем туда параметры
        wifiManager.start();             // запускаем второстепенный поток
    }

    private void img_connect(boolean flag) {       // это короче чтоб показать картинки что у нас есть конект
        if (flag) {
            img_wifi_ok.setVisibility(View.VISIBLE);
            img_wifi_no.setVisibility(View.GONE);
            img_disk.setVisibility(View.GONE);
        } else {
            img_wifi_ok.setVisibility(View.GONE);
            img_wifi_no.setVisibility(View.VISIBLE);
            massa.setText("------ ");
            massa.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            brutto_netto.setText("");
            kg_lb.setText("");
            stab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            zero.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            over.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            stab.setBackgroundResource(R.drawable.round_grey);
            zero.setBackgroundResource(R.drawable.round_grey);
            over.setBackgroundResource(R.drawable.round_grey);
            img_stab.setVisibility(View.GONE);
            img_no_stab.setVisibility(View.GONE);
            img_disk.setVisibility(View.VISIBLE);
        }
    }

    void small_display() {     // проверяем размер экрана
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int screenWidth = point.x;
        int screenHeight = point.y;
        if (screenHeight < 1000) {  // если по длине меньше 1000 то прячем картинку
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    private void NPV(boolean  flag){
        if(flag){
            v_ngz.setVisibility(View.VISIBLE);
            ngz.setVisibility(View.VISIBLE);
        } else {
            v_ngz.setVisibility(View.GONE);
            ngz.setVisibility(View.GONE);
        }
    }

    private boolean Wifi_on_off() {     // спрашиваем у системы, а вай фай включен?
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);   // кидаем запрос
        if (!wifi.isWifiEnabled()) {      // если ложь то пишем что вай фай не включен
            Toast.makeText(Many_weight_activity.this, "Wifi не ввімкнено", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;      // ну или возвращаем правду
        }
    }

    private void runTimer1() {
        final Handler handler1 = new Handler();       // android os
        handler1.post(new Runnable() {
            @Override
            public void run() {
                handler1.postDelayed(this, 2000); // Запускаем код снова с задержкой в одну секунду
                if (starts) {
                    if (zero_ok) {
                        tablo.setText("--Очікування ваги--");
                        if (!sound) {
                            mediaPlayer.start();
                            sound = true;
                        }
                        tablo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                    } else {
                        tablo.setText("--Очікування нуля--");
                        sound = false;
                        tablo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    }
                }

                if (zero_ok && starts && !overload) {
                    if (stability) {
                        stability = false;
                        count_stab++;
                        if (count_stab == 2) {            // нужно 10 раз словить стабилизацию
                            count_stab = 0;
                            zero_ok = false;
                            times = clock.getText().toString();                       // получаем время с текст клок
                            if (err_mass) {
                                myDbManager.insertToDb(num_weight.getText().toString(), massa.getText().toString() + " " + parsingData.type_massa, fio_s, par1.getText().toString(),
                                        par2.getText().toString(), par3.getText().toString(), times, num_party.getText().toString(), "X", cargo.getText().toString());             // записываем в базу
                                tablo.setText("Не коректна вага");
                                tablo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                                mediaPlayer1.start();
                            } else {
                                myDbManager.insertToDb(num_weight.getText().toString(), massa.getText().toString() + " " + parsingData.type_massa, fio_s, par1.getText().toString(),
                                        par2.getText().toString(), par3.getText().toString(), times, num_party.getText().toString(), "", cargo.getText().toString());             // записываем в базу
                                tablo.setText("Зважування завершено");
                                tablo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                                mediaPlayer2.start();
                            }
                            total_sum += Float.parseFloat(massa.getText().toString());

                       /* DecimalFormat format = new DecimalFormat("0.####"); // Choose the number of decimal places to work with in case they are different than zero and zero value will be removed
                        format.setRoundingMode(RoundingMode.DOWN); // Choose your Rounding Mode
                        total_weight.setText(format.format(total_sum) + parsingData.type_massa);
                        //https://stackoverflow.com/questions/703396/how-to-nicely-format-floating-numbers-to-string-without-unnecessary-decimal-0s*/

                            String form = " ";
                            switch (parsingData.point) {
                                case 0:
                                    form = String.format("%.0f", total_sum);
                                    break;
                                case 1:
                                    form = String.format("%.1f", total_sum);
                                    break;
                                case 2:
                                    form = String.format("%.2f", total_sum);
                                    break;
                                case 3:
                                    form = String.format("%.3f", total_sum);
                                    break;
                                case 4:
                                    form = String.format("%.4f", total_sum);
                                    break;
                            }
                            total_weight.setText(form + parsingData.type_massa);
                            readFromDb("");  // считали заново и обновили лист
                        }
                    }
                }
            }
        });
    }

    private int lengthOfFloatAfterComma(float f) {
        return String.valueOf(f).split("\\.")[1].length();
    }

    private void runTimer() {                       // функция таймера который запускается через 1 секунду
        final Handler handler = new Handler();       // android os
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 200); // Запускаем код снова с задержкой в одну секунду,
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
                        img_connect(true);
                    } else {
                        img_connect(false);
                        blink_wifi();
                        if (starts){
                            finish();
                        }
                    }
                    if (parsingData.parsing_ok) {
                        parsingData.parsing_ok = false;
                        massa.setText(String.format("%.8s", parsingData.out_symbol));
                        kg_lb.setText(parsingData.type_massa);
                        brutto_netto.setText(parsingData.brutto_netto);

                        if (tracing_weight) {
                            if (parsingData.out_data <= upper_weight && parsingData.out_data >= lower_weight) {
                                err_mass = false;
                            } else {
                                err_mass = true;
                            }
                        }
                        if (parsingData.stab_no_stab == 1) {
                            if (tracing_weight) {
                                if (parsingData.out_data >= min_mass) {
                                    stability = true;
                                }
                            } else {
                                stability = true;
                            }
                        }
                        if (parsingData.out_data <= (0 + float_zero_tolerance) && parsingData.out_data >= (0 - float_zero_tolerance)) {
                            stability = false;
                            if (parsingData.stab_no_stab == 1) {
                                zero_ok = true;
                            }
                        }

                        if (parsingData.stab_no_stab == 1) {
                            massa.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            stab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            stab.setBackgroundResource(R.drawable.round);
                            img_stab.setVisibility(View.VISIBLE);
                            img_no_stab.setVisibility(View.GONE);
                        }
                        if (parsingData.stab_no_stab == 2) {
                            massa.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            stab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            stab.setBackgroundResource(R.drawable.round_red);
                            img_stab.setVisibility(View.GONE);
                            img_no_stab.setVisibility(View.VISIBLE);
                        }
                        if (parsingData.overload) {
                            over.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            over.setBackgroundResource(R.drawable.red_rim);
                            overload = true;
                        } else {
                            over.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            over.setBackgroundResource(R.drawable.green_rim);
                            overload = false;
                        }
                        NPV(overload);
                        if (parsingData.out_data <= (0 + float_zero_tolerance) && parsingData.out_data >= (0 - float_zero_tolerance)) {
                            zero.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            zero.setBackgroundResource(R.drawable.round);
                        } else {
                            zero.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            zero.setBackgroundResource(R.drawable.round_grey);
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
        myDbManager.CloseDb();    // закрыть базу данных
        if (wifiManager != null) wifiManager.socketClose();
        super.onDestroy();              // когда выходим из активити убить сокет
    }

    final Handler h = new Handler();     // экземпляр класса для возможности добраться к основному потоку

    @Override
    public void orResive(List<ListItm> list) {
        data = list;                            // мы его скачаем себе
        h.post(new Runnable() {          // это позволяет выводить информацию в главном потоке
            @Override
            public void run() {
                num_weight.setText("");  // обновим поле ввода
                read_last();            // и потом покажем его на экране, тут нужен еще один поток
            }
        });
    }
}