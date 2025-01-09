package com.example.prima_pagina;

public class Limite {
    private Integer id;
    private double limita, diferenta;
    private String categorie;

    // definire constructor
    public Limite(String categorie, Double limita, Double diferenta)
    {
        this.categorie = categorie;
        this.limita = limita;
        this.diferenta = diferenta;
    }

    // definire metode get
    public String getCategorie() {
        return categorie;
    }
    public Double getLimita() { return limita; }
    public Double getDiferenta() { return diferenta; }
}
