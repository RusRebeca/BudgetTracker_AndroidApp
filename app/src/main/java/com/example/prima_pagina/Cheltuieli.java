package com.example.prima_pagina;

public class Cheltuieli {
    private Integer id;
    private double suma;
    private String categorie;
    private String data;

    // definire constructori
    public Cheltuieli(double suma, String categorie, String data) {
        this.suma = suma;
        this.categorie = categorie;
        this.data = data;
    }

    public Cheltuieli(int id, double suma, String categorie, String dataString) {
        this.id = id;
        this.suma = suma;
        this.categorie = categorie;
        this.data = dataString;
        //this.data = new Date(dataString);
    }

    // Metode Getters si setters pentru fiecare atribut

    // Getters
    public double getSuma() {
        return suma;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getData() {
        return data;
    }

    // Setters
    public void setSuma(double suma) {
        this.suma = suma;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setData(String data) {
        this.data = data;
    }
}
