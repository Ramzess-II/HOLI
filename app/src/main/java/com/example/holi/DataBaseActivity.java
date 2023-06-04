package com.example.holi;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.holi.Adapter.BDAdapter;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DataBaseActivity extends AppCompatActivity implements OnDataResive {
    private boolean csv_ok = false;      // проверка разрешения доступа к памяти для вывода csv
    private int pozition = 1;        // это короче для фильтра выбор по чем фильтровать
    private ListView listView;      // это сам класс который хранит инфу
    private BDAdapter bdAdapter;
    private MyDbManager myDbManager;
    private ActionBar actionBar;
    private TextView poz1, poz2;
    private List<ListItm> data = new ArrayList<>();  // а это массив классов которые хранят инфу
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private SharedPreferences shared_prf;   // для сохранения
    private String par1_s, par2_s, par3_s, password_s;
    private boolean activiti_ok = false;
    // это короче куда будет сохраняться csv
    //private String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/ARM_VIS.csv"); // Here csv file name is MyCsvFile.csv

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);
        readSharedPrf();
        myDbManager = new MyDbManager(DataBaseActivity.this);         // создаем класс для работы с БД
        listView = findViewById(R.id.list_data_bs);             // подключаем listView на который будем выводить список
        listView.setDivider(null);                          // эти две строки удаляют разделительные линии между элементами листвью
        listView.setDividerHeight(0);
        actionBar = getSupportActionBar();
        actionBar.setTitle("База даних");                   // изменили надпись на Акшн баре
        actionBar.setDisplayHomeAsUpEnabled(true);          // и устанавливаем у нее стандартный метод показать кнопку назад
        poz1 = findViewById(R.id.poz1);
        poz2 = findViewById(R.id.poz2);
        poz1.setBackgroundResource(R.color.grey);            // на нулевом элементе выбрали заливку
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве

        if (isStoragePermissionGranted()) {       //проверить есть ли разрешение на использование внутреней памяти
            csv_ok = true;
        }
        poz1.setOnClickListener(v -> {
            set_poz_background(1);
            pozition = 1;
        });
        poz2.setOnClickListener(v -> {
            set_poz_background(2);
            pozition = 2;
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(DataBaseActivity.this, Seting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent ooi = new Intent(DataBaseActivity.this, MainActivity.class);
                        startActivity(ooi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_user:
                        Intent oii = new Intent(DataBaseActivity.this, Activity_person.class);
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
        Intent i = new Intent(DataBaseActivity.this, MainActivity.class);
        startActivity(i);
        finishAffinity();
        return;
    }

    private void set_poz_background(int poz) {              // это короче позволяет выбрать только один фильтр
        poz1.setBackgroundResource(R.color.white);
        poz2.setBackgroundResource(R.color.white);
        switch (poz) {                                       // и устанавливаем  только тот который прише
            case 1:
                poz1.setBackgroundResource(R.color.grey);
                break;
            case 2:
                poz2.setBackgroundResource(R.color.grey);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myDbManager.openDb();             // открыть базу и считать элементы
        readFromDb("");  // если передаем пустоту то ищем все
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            Intent i = new Intent(DataBaseActivity.this, MainActivity.class);  // открываем новую активити
            startActivity(i);                                                                                                 // закрыть текущую активити
        }
        if (item.getItemId() == R.id.id_delete) {                      // если удалить
            deliteDialog();                                            // вызываем алерт диалог
        }
        if (item.getItemId() == R.id.id_csv) {                         // если создать csv
            if (activiti_ok) {
                if (csv_ok) {                                              // проверяем разрешение
                    Toast.makeText(DataBaseActivity.this, "Збережено у папку download", Toast.LENGTH_SHORT).show(); // пишем сразу
                    AppExecuter.getInstance().getSubIO().execute(new Runnable() {  // создаем второстепенный поток
                        @Override
                        public void run() {
                               /* File file  = new File(csv);   // сначала создаем объект библиотеки File у которого путь такой же как и для создания
                                boolean delete1 = delete.delete();  // а потом удаляем его если он есть
                            if (file .exists()) {                   // https://ru.stackoverflow.com/questions/741591/%D0%A3%D0%B4%D0%B0%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BF%D0%B0%D0%BF%D0%BE%D0%BA-%D0%B8%D0%BB%D0%B8-%D1%84%D0%B0%D0%B9%D0%BB%D0%BE%D0%B2-%D0%B8%D0%B7-android
                               file .delete();
                            }*/
                            // создаем календарь, чтоб дата автоматом сохранялась
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH-mm");
                            String formattedDate = df.format(calendar.getTime());
                            try {
                                OutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/Base " + formattedDate+ ".csv");
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

                                /*  это чтоб открыть файл в экселе
                                File F = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                        "Base " + formattedDate+ ".csv");
                                Uri U = Uri.fromFile(F);
                                Intent intent = new Intent(Intent.ACTION_VIEW, U);
                                intent.setDataAndType(U, "application/csv");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent); */

                                Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sendIntent.setType("application/csv");
                                File F = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                        "Base " + formattedDate+ ".csv");
                                Uri U = FileProvider.getUriForFile(DataBaseActivity.this,  "com.example.homefolder.example.provider" , F);
                                sendIntent.putExtra(Intent.EXTRA_STREAM, U);
                                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);  //https://ali-dev.medium.com/open-a-file-in-another-app-with-android-fileprovider-for-android-7-42c9abb198c1
                               // StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();  // https://stackoverflow.com/questions/48117511/exposed-beyond-app-through-clipdata-item-geturi
                               // StrictMode.setVmPolicy(builder.build()); // костыль
                                startActivity(Intent.createChooser(sendIntent, "Відправити файл"));

                            } catch (IOException e) {
                                 e.printStackTrace();
                                // Toast.makeText(DataBaseActivity.this, "Исключение", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(DataBaseActivity.this, "Немає доступу до пам'яті", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DataBaseActivity.this, "Не можливо у тестовій версії", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
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
    public boolean onCreateOptionsMenu(Menu menu) {                   // привязали меню сверху
        getMenuInflater().inflate(R.menu.search_menu, menu);          // надуваем меню
        MenuItem item = menu.findItem(R.id.id_search);              // ищем наш значок поиска
        SearchView sv = (SearchView) item.getActionView();           // создаем поисковик, и превращаем в SearchView
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {  // слушатель ввода
            @Override
            public boolean onQueryTextSubmit(String s) {  // запускается полсе нажатия на клавиатуре поиска
                readFromDb(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {  // запускается каждый раз как мы добавляем букву
                readFromDb(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void readFromDb(final String text) {  // считать из Базы
        AppExecuter.getInstance().getSubIO().execute(new Runnable() {  // на второстепенном потоке
            @Override
            public void run() { // тут ответа не будет он появиться на основном потоке после считывания orResive()
                myDbManager.ReadDb(text, DataBaseActivity.this, pozition);    // запускаем выполнение на второстепенном потоке
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


    private void deliteDialog() {   // функция вызова диалогового окна
        // https://mkyong.com/android/android-prompt-user-input-dialog-example/
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
                                        deleteFromDB();         // удалить всю базу
                                        readFromDb("");  // считали заново и обновили лист
                                    } else {
                                        Toast.makeText(DataBaseActivity.this, "Не вірний пароль", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();// create alert dialog
            alertDialog.show();// show it d
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // функция вызова диалогового окна
            builder.setMessage("Видалити?");   // основная строка
            builder.setTitle("Видалення БД");    // заголовок
            builder.setPositiveButton("Так", new DialogInterface.OnClickListener() { // если нажали да
                @Override
                public void onClick(DialogInterface dialog, int which) {       // если нажали да
                    deleteFromDB();         // удалить всю базу
                    readFromDb("");  // считали заново и обновили лист
                }
            });
            builder.setNegativeButton("Ні", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }

    private void readSharedPrf() {
        shared_prf = getSharedPreferences(Constant_data.MY_PREFERENCE, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences  Context обязательно
        par1_s = shared_prf.getString(Constant_data.PAR1_1, "");
        par2_s = shared_prf.getString(Constant_data.PAR2_1, "");
        par3_s = shared_prf.getString(Constant_data.PAR3_1, "");
        password_s = shared_prf.getString(Constant_data.PASS_USER, "");
        activiti_ok = shared_prf.getBoolean(Constant_data.ACTIVS, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDbManager.CloseDb();  // закрыть базу при выходе
    }

    @Override
    public void orResive(List<ListItm> list) {
        AppExecuter.getInstance().getMainIO().execute(new Runnable() { // а это уже запускается основной поток
            @Override
            public void run() {   // обновляем адаптер новыми данными
                bdAdapter = new BDAdapter(DataBaseActivity.this, R.layout.exampl_list_view, list);
                listView.setAdapter(bdAdapter);  // присваиваем их лист вью
                data = list;    // а так же записываем в промежуточный клас, с него мы пишем в csv
            }
        });
    }
}