package com.example.holi.Adapter;

import java.io.Serializable;

public class ListItm implements Serializable {  // Serializable это для того чтоб можно было передать весь класс целиком через интент

    private int id = 0;
    private String massa_common;      // данные для упрощения работы с БД
    private String fio_fio;
    private String par_1;
    private String par_2;
    private String par_3;
    private String party;
    private String number;
    private String date;
    private String cargo;

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getError_mas() {
        return error_mas;
    }

    public void setError_mas(String error_mas) {
        this.error_mas = error_mas;
    }

    private String error_mas;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMassa_common() {
        return massa_common;
    }

    public void setMassa_common(String massa_common) {
        this.massa_common = massa_common;
    }

    public String getFio_fio() {
        return fio_fio;
    }

    public void setFio_fio(String fio_fio) {
        this.fio_fio = fio_fio;
    }

    public String getPar_1() {
        return par_1;
    }

    public void setPar_1(String par_1) {
        this.par_1 = par_1;
    }

    public String getPar_2() {
        return par_2;
    }

    public void setPar_2(String par_2) {
        this.par_2 = par_2;
    }

    public String getPar_3() {
        return par_3;
    }

    public void setPar_3(String par_3) {
        this.par_3 = par_3;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}