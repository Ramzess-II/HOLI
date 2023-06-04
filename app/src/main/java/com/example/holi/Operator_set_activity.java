package com.example.holi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.holi.Adapter.ListItm;
import com.example.holi.Data_base.AppExecuter;
import com.example.holi.Data_base.MyDbManager;
import com.example.holi.Data_base.OnDataResive;
import com.example.holi.data_save.Constant_data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Operator_set_activity extends AppCompatActivity implements OnDataResive {
    private EditText par1, par2, par3;
    private boolean csv_ok = false;      // проверка разрешения доступа к памяти для вывода csv
    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private SharedPreferences shared_prf;   // для сохранения
    private String par1_s, par2_s, par3_s;
    private MyDbManager myDbManager;
    private Menu mMenuItem;
    private boolean change;
    private boolean activiti_ok = false;
    private String password_s;
    private List<ListItm> data = new ArrayList<>();  // а это массив классов которые хранят инфу
    // это короче куда будет сохраняться csv
    //private String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/VIS_ARM_data.csv"); // Here csv file name is MyCsvFile.csv


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_set);
        myDbManager = new MyDbManager(this);         // создаем класс для работы с БД
        init();
        readSharedPrf();
        init_text();
        change_setting(false);
        if (isStoragePermissionGranted()) {       //проверить есть ли разрешение на использование внутреней памяти
            csv_ok = true;
        }

        if (!password_s.equals("")) {
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.promts, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(promptsView);
            final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);// set dialog message
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) { // get user input and set it to result
                                    if (userInput.getText().toString().equals(password_s)) {

                                    } else {
                                        Toast.makeText(Operator_set_activity.this, "Не вірний пароль", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();// create alert dialog
            alertDialog.show();// show it
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_home:
                        Intent i = new Intent(Operator_set_activity.this, MainActivity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_database:
                        Intent oi = new Intent(Operator_set_activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_setting:
                        Intent ii = new Intent(Operator_set_activity.this, Seting_Activity.class);  // открываем новую активити
                        startActivity(ii);
                        finishAffinity();
                        return true;
                    case R.id.navigation_user:
                        Intent oiii = new Intent(Operator_set_activity.this, Activity_person.class);
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
        super.onResume();
        myDbManager.openDb();             // открыть базу и считать элементы
        readFromDb("");  // если передаем пустоту то ищем все
    }

    private void readFromDb(final String text) {  // считать из Базы
        AppExecuter.getInstance().getSubIO().execute(new Runnable() {  // на второстепенном потоке
            @Override
            public void run() { // тут ответа не будет он появиться на основном потоке после считывания orResive()
                myDbManager.ReadDb(text, Operator_set_activity.this, 1);    // запускаем выполнение на второстепенном потоке
            }
        });
    }

    private void deleteFromDB() {      // удаление всей базы так как параметры не передаем
        AppExecuter.getInstance().getSubIO().execute(new Runnable() {
            @Override
            public void run() {
                myDbManager.delete();    // запускаем выполнение на второстепенном потоке
            }
        });
    }

    private void init() {
        par1 = findViewById(R.id.editTextText_par1);
        par2 = findViewById(R.id.editTextText_par2);
        par3 = findViewById(R.id.editTextText_par3);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Параметри збереження");                   // изменили надпись на Акшн баре
        actionBar.setDisplayHomeAsUpEnabled(true);          // и устанавливаем у нее стандартный метод показать кнопку назад
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
    }

    private void change_setting(boolean i) {
        if (i) {
            par1.setEnabled(true);
            par2.setEnabled(true);
            par3.setEnabled(true);
        } else {
            par1.setEnabled(false);
            par2.setEnabled(false);
            par3.setEnabled(false);
        }
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
            change_setting(change);
            if (change) {
                mMenuItem.getItem(0).setIcon(R.drawable.ic_baseline_create_red_24);    // через переменную изменяем картинку. индекс это позиция картинки
            } else {
                mMenuItem.getItem(0).setIcon(R.drawable.ic_baseline_create_24);
            }
        }
        if (item.getItemId() == R.id.id_saved) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // функция вызова диалогового окна
            builder.setMessage("Продовжити?");   // основная строка
            builder.setTitle("Після зміни данних, потрібно видалити БД");    // заголовок
            builder.setPositiveButton("Так", new DialogInterface.OnClickListener() { // если нажали да
                @Override
                public void onClick(DialogInterface dialog, int which) {       // если нажали да
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Operator_set_activity.this); // функция вызова диалогового окна
                    builder1.setTitle("Бажаєте зберегти минулу базу в exel?");    // заголовок
                    builder1.setPositiveButton("Так", new DialogInterface.OnClickListener() { // если нажали да
                        @Override
                        public void onClick(DialogInterface dialog, int which) {       // если нажали да
                            save_exel();
                            deleteFromDB();
                            save_SharedPrf();
                            Toast.makeText(Operator_set_activity.this, "Збережено", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    builder1.setNegativeButton("Ні", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteFromDB();
                            save_SharedPrf();
                            Toast.makeText(Operator_set_activity.this, "Збережено", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    builder1.show();
                }
            });
            builder.setNegativeButton("Ні", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void save_exel() {
        if (activiti_ok) {
            if (csv_ok) {                                              // проверяем разрешение
                Toast.makeText(Operator_set_activity.this, "Збережено у папку download", Toast.LENGTH_SHORT).show(); // пишем сразу
                AppExecuter.getInstance().getSubIO().execute(new Runnable() {  // создаем второстепенный поток
                    @Override
                    public void run() {
                       /* File delete = new File(csv);   // сначала создаем объект библиотеки File у которого путь такой же как и для создания
                        boolean delete1 = delete.delete();  // а потом удаляем его если он есть*/
                        // создаем календарь, чтоб дата автоматом сохранялась
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH-mm");
                        String formattedDate = df.format(calendar.getTime());
                        try {
                            OutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/Base " + formattedDate + ".csv");
                            fos.write(239);     // https://askdev.ru/q/ustanovka-utf-8-v-java-i-csv-fayle-dublikat-105239/
                            fos.write(187);     // явно указываем какая у нас кодировка
                            fos.write(191);
                            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                            // https://zetcode.com/java/opencsv/
                            //CSVWriter writer = new CSVWriter(osw);  // стандартный конструктор или расширенный
                            CSVWriter writer = new CSVWriter(osw, ';', CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                    CSVWriter.DEFAULT_LINE_END);
                            List<String[]> datas = new ArrayList<String[]>();  // промежуточный лист который хранит строки для записи в csv
                            datas.add(new String[]{"Номер зважування", "Дата", "Час", "Вантаж", "Вага", "П.І.Б.", "Партія", par1_s, par2_s, par3_s, "Вага не в межах зважувань"}); // первая строка
                            for (int i = 0; i < data.size(); i++) {  // а остальные считываем из памяти пока есть элементы
                                char[] buf = {};
                                StringBuffer out = new StringBuffer();  // тут мы дату и время разделяем на части
                                String inpt = data.get(i).getDate();
                                buf = inpt.toCharArray();
                                for (int q = 0; q < 10; q++) {
                                    out.append(buf[q]);
                                }
                                String date = out.toString();
                                StringBuffer out1 = new StringBuffer();
                                for (int ii = 11; ii < 19; ii++) {
                                    out1.append(buf[ii]);
                                }
                                String time = out1.toString();
                                datas.add(new String[]{data.get(i).getNumber(), date, time, data.get(i).getCargo(), data.get(i).getMassa_common(), data.get(i).getFio_fio(), data.get(i).getParty(), data.get(i).getPar_1(),
                                        data.get(i).getPar_2(), data.get(i).getPar_3(), data.get(i).getError_mas()}); // а берем из из списка классов
                            }
                            writer.writeAll(datas); // записали все в csv
                            writer.close();         // закрыли
                        } catch (IOException e) {
                          //  e.printStackTrace();
                        }
                    }
                });
            } else {
                Toast.makeText(Operator_set_activity.this, "Немає доступу до пам'яті", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Operator_set_activity.this, "Не можливо у тестовій версії", Toast.LENGTH_SHORT).show();
        }
    }

    private void init_text() {
        par1.setText(par1_s);
        par2.setText(par2_s);
        par3.setText(par3_s);
    }

    private void readSharedPrf() {
        shared_prf = getSharedPreferences(Constant_data.MY_PREFERENCE, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences  Context обязательно
        par1_s = shared_prf.getString(Constant_data.PAR1_1, "");
        par2_s = shared_prf.getString(Constant_data.PAR2_1, "");
        par3_s = shared_prf.getString(Constant_data.PAR3_1, "");
        password_s = shared_prf.getString(Constant_data.PASS_USER, "");
        activiti_ok = shared_prf.getBoolean(Constant_data.ACTIVS, false);
    }

    private void save_SharedPrf() {
        SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
        editor.putString(Constant_data.PAR1_1, par1.getText().toString());
        editor.putString(Constant_data.PAR2_1, par2.getText().toString());
        editor.putString(Constant_data.PAR3_1, par3.getText().toString());
        editor.apply();
    }

    @SuppressLint("NewApi")
    public boolean isStoragePermissionGranted() {    // функция для отправки запроса в систему
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;  // если есть разрешение на использование то вернуть правду
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;  // если нету разрешения то запустить запрос и пердать код 1
        }
    }

    @Override
    // функция которая получает от системы разрешение или отказ о предоставлении доступа к памяти
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {               // если вернулся код который запрашивали мы
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {         // проверяем ответило согласие или нет
                csv_ok = true;                                                  // подтверждаем
            }    // делать елз не нужно так как csv_ok у нас изначально ложь
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);   // если это не наш запрос то просто ничего не делаем
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDbManager.CloseDb();  // закрыть базу при выходе
    }

    @Override
    public void orResive(List<ListItm> list) {
        data = list;    // а так же записываем в промежуточный клас, с него мы пишем в csv
    }
}