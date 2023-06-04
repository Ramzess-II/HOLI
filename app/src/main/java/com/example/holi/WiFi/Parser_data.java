package com.example.holi.WiFi;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class Parser_data {
    private Context context;  // хз зачем))
    public boolean parsing_ok;
    public boolean parsing_command_ok;
    public float out_data;
    public String out_symbol;
    public int stab_no_stab;
    public int point;
    public String type_massa,brutto_netto;
    public boolean overload = false;



    // данные которые нужно отправить
    public Parser_data(Context context) {
        this.context = context;
    }  // конструктор

    public byte[] Buf_to_string(@NonNull byte[] buf) {       // преобразовываем из буфера в строку данные для отправки
        byte[] out = new byte[buf.length];                  // так как в массиве указіваем не с 0 а просто количество
        for (int i = 0; i < buf.length; i++) {
            out[i] = buf[i];
        }
        return out;                          // вернуть готовый массив
    }

   /* public byte[] send32_bit(byte comand, int pars) {
        byte[] out = new byte[7];                  // размер массива
        out[0] = 0x02;
        out[1] = comand;
        out[2] = (byte) pars;                      // раскладываем инт по своим ячейкам
        pars = pars >> 8;
        out[3] = (byte) pars;
        pars = pars >> 8;
        out[4] = (byte) pars;
        pars = pars >> 8;
        out[5] = (byte) pars;
        out[6] = 0x03;
        return out;                          // вернуть готовый массив
    }*/


    public void Parsing_inpt(byte[] data) {         // парсинг входящих данных
        char[] out = new char[10];
        if (data[16] == 0x0D ) {                        // ну и сам парсинг
            if (data[0] == 0x4F){
                overload = true;
            }
            if (data[0] == 0x53){
                stab_no_stab = 1;
                overload = false;
            }
            if (data[0] == 0x55){
                stab_no_stab = 2;
                overload = false;
            }
            if (data[14] == 0x6B){
                type_massa = "Кг";
            }
            if (data[14] == 0x6C){
                type_massa = "Фунт";
            }
            if (data[3] == 0x47){
                brutto_netto = "Брутто";
            }
            if (data[3] == 0x4E){
                brutto_netto = "Нетто";
            }
            if (data[12] == 0x2E){
                point = 1;
            } else if (data[11] == 0x2E){
                point = 2;
            } else if (data[10] == 0x2E){
                point = 3;
            } else if (data[9] == 0x2E){
                point = 4;
            } else {
                point = 0;
            }
            out[0] = (char) (data[6] & 0xFF);                    // 0xFF позволяет убрать все лишнее из входящего байта
            out[1] = (char) (data[7] & 0xFF);
            out[2] = (char) (data[8] & 0xFF);
            out[3] = (char) (data[9] & 0xFF);
            out[4] = (char) (data[10] & 0xFF);
            out[5] = (char) (data[11] & 0xFF);
            out[6] = (char) (data[12] & 0xFF);
            out[7] = (char) (data[13] & 0xFF);
            try {
                out_symbol = String.valueOf(out);                    // byte оно не понимает, по этому только чар
                out_symbol = out_symbol.replaceAll("\\s+","");  // убираем пробелы перед числом
                out_data = Float.valueOf(out_symbol);
                parsing_ok = true;                                   // поднимаем флаг что данные коректны
            } catch (NumberFormatException e){

            }
        }
    }
}
