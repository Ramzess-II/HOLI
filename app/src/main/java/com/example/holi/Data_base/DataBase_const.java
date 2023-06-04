package com.example.holi.Data_base;

public class DataBase_const {
    public static final String TABLE_NAME = "my_tabless";         // создали таблицу в базе
    public static final String _ID = "_id_id";                  // _id элемента
    public static final String MASSA_OUTS = "massa_m";          // две колоны
    public static final String FIO = "fio_fi";
    public static final String PAR1 = "par1";
    public static final String PAR2 = "par2";
    public static final String PAR3 = "par3";
    public static final String PARTY = "party";
    public static final String ERROR_MASSA = "error_massa";
    public static final String CARGO = "cargo";
    public static final String NUMBER = "number";               // две колоны
    public static final String DATA = "date";                   // и это колона
    public static final String DB_NAME = "my_db1.db";           // создали базу данных
    public static final int DB_VERSION = 1;                     // это версия БД

    public static final String TABLE_STRUCTURE = "CREATE TABLE IF NOT EXISTS " +       // это шаблон по тому как создавать таблицу в Базе данных если она пустая
            TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + MASSA_OUTS + " TEXT," + FIO + " TEXT,"  + PAR1 + " TEXT," + PAR2 + " TEXT," + PAR3 + " TEXT," +
            PARTY + " TEXT," + ERROR_MASSA + " TEXT," + CARGO + " TEXT," + NUMBER + " TEXT,"  + DATA + " TEXT)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;    // это шаблон для удаления таблицы с заданым именем если она существует

}
