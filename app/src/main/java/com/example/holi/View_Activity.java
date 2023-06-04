package com.example.holi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.holi.Data_base.AppExecuter;
import com.example.holi.Data_base.MyDbManager;
import com.example.holi.data_save.Constant_data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class View_Activity extends AppCompatActivity {
    private int id_id;
    private TextView number, data, massa, fio, party, par1, par2, par3, par1_via, par2_via, par3_via, error_via, cargo;
    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private String par1_s, par2_s, par3_s, party_s, error_s;
    private TableRow tabl_1,tabl_2,tabl_3, tabl_4, tabl_5;
    private SharedPreferences shared_prf;   // для сохранения
    private MyDbManager myDbManager;
    private String password_s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        myDbManager = new MyDbManager(View_Activity.this);         // создаем класс для работы с БД
        shared_prf = getSharedPreferences(Constant_data.MY_PREFERENCE, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences  Context обязательно
        Intent i = getIntent();
        actionBar = getSupportActionBar();                 // соеденяем акшн бар и переменную
        actionBar.setDisplayHomeAsUpEnabled(true);         // и устанавливаем у нее стандартный метод показать кнопку назад
        actionBar.setTitle("№ " + i.getStringExtra(Constant_data.NUMBER_EXTRA));    // изменили надпись на Акшн баре
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
        number = findViewById(R.id.textView_number);
        data = findViewById(R.id.textViewdata_time);
        massa = findViewById(R.id.textView_massa);
        fio = findViewById(R.id.textViewax_fio);
        party = findViewById(R.id.textViewax_party);
        par1_via = findViewById(R.id.textViewax_par1_via);
        par2_via = findViewById(R.id.textViewax_par2_via);
        par3_via = findViewById(R.id.textViewax_par3_via);
        error_via = findViewById(R.id.textViewax_error_via);
        par1 = findViewById(R.id.textViewax_par1);
        par2 = findViewById(R.id.textViewax_par2);
        par3 = findViewById(R.id.textViewax_par3);
        tabl_1 = findViewById(R.id.Tabl_1);
        tabl_2 = findViewById(R.id.Tabl_2);
        tabl_3 = findViewById(R.id.Tabl_3);
        tabl_4 = findViewById(R.id.Tabl_4);
        tabl_5 = findViewById(R.id.Tabl_03);
        cargo = findViewById(R.id.textView_cargos);
        number.setText(i.getStringExtra(Constant_data.NUMBER_EXTRA));
        data.setText(i.getStringExtra(Constant_data.DATA_EXTRA));
        massa.setText(i.getStringExtra(Constant_data.MASSA_EXTRA));
        fio.setText(i.getStringExtra(Constant_data.FIO_EXTRA));
        party_s = i.getStringExtra(Constant_data.PARTY_EXTRA);
        par1_s = i.getStringExtra(Constant_data.PAR1_EXTRA);
        par2_s = i.getStringExtra(Constant_data.PAR2_EXTRA);
        par3_s = i.getStringExtra(Constant_data.PAR3_EXTRA);
        cargo.setText(i.getStringExtra(Constant_data.CARGO_EXTRA));
        id_id = i.getIntExtra(Constant_data.ID_EXTRA, 0);
        error_s = i.getStringExtra(Constant_data.ERROR_EXTRA);
        par1.setText(shared_prf.getString(Constant_data.PAR1_1, " "));
        par2.setText(shared_prf.getString(Constant_data.PAR2_1, " "));
        par3.setText(shared_prf.getString(Constant_data.PAR3_1, " "));
        password_s = shared_prf.getString(Constant_data.PASS_USER, "");
        if (error_s.isEmpty()){
            tabl_5.setVisibility(View.GONE);
        } else {
            error_via.setText(error_s);
        }
        if (party_s.isEmpty()){
            tabl_4.setVisibility(View.GONE);
        } else {
            party.setText(i.getStringExtra(Constant_data.PARTY_EXTRA));
        }
        if (par1_s.isEmpty()){
            tabl_1.setVisibility(View.GONE);
        } else {
            par1_via.setText(par1_s);
        }
        if (par2_s.isEmpty()){
            tabl_2.setVisibility(View.GONE);
        } else {
            par2_via.setText(par2_s);
        }
        if (par3_s.isEmpty()){
            tabl_3.setVisibility(View.GONE);
        } else {
            par3_via.setText(par3_s);
        }
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(View_Activity.this, Seting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_database:
                        Intent oi = new Intent(View_Activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent oii = new Intent(View_Activity.this, MainActivity.class);
                        startActivity(oii);
                        finishAffinity();
                        return true;
                    case R.id.navigation_user:
                        Intent oiii = new Intent(View_Activity.this, Activity_person.class);
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
    }

    private void deleteFromDB_colum() {      // удаление всей базы так как параметры не передаем
        AppExecuter.getInstance().getSubIO().execute(new Runnable() {
            @Override
            public void run() {
                myDbManager.delete_colum(id_id);    // запускаем выполнение на второстепенном потоке
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {            // создать меню (при включении)
        getMenuInflater().inflate(R.menu.delite_menu, menu);   // добавляем меню в наше активити
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            finish();                                                  // закрыть текущую активити
        }
        if(item.getItemId() == R.id.id_delite){
            if (!password_s.equals("")) {
                // https://mkyong.com/android/android-prompt-user-input-dialog-example/
                LayoutInflater li = LayoutInflater.from(this);
                View promptsView = li.inflate(R.layout.promts, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);// set dialog message
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) { // get user input and set it to result
                                        if (userInput.getText().toString().equals(password_s)) {
                                            deleteFromDB_colum();
                                            finish();
                                        } else {
                                            Toast.makeText(View_Activity.this, "Не вірний пароль", Toast.LENGTH_SHORT).show();
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
                alertDialog.show();// show it
            } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Дійсно видалити?");   // основная строка
           // builder.setTitle("Видалити");    // заголовок
            builder.setPositiveButton("Так", new DialogInterface.OnClickListener() { // если нажали да
                @Override
                public void onClick(DialogInterface dialog, int which) {       // если нажали да
                    deleteFromDB_colum();
                    finish();
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDbManager.CloseDb();  // закрыть базу при выходе
    }

}