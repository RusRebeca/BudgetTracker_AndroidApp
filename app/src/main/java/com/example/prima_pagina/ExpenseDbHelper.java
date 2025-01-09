package com.example.prima_pagina;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExpenseDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Cheltuieli.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "cheltuieli";

    ExpenseDbHelper dbHelper;

    public ExpenseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "suma REAL NOT NULL," +
                "categorie TEXT NOT NULL," +
                "data DATE NOT NULL" +
                ")");

        createLimiteTable(db); //apelare functie care creeaza tabela Limite
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // adaugare cheltuiala in baza de date
    public Boolean addExpense(Cheltuieli cheltuiala) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("suma", cheltuiala.getSuma());
        values.put("categorie", cheltuiala.getCategorie());
        values.put("data", cheltuiala.getData()); // Data e converita la tipul String
        // Adauga cheltuiala in baza de date si se verifica valoarea returnata
        long insertedRowId = db.insert(TABLE_NAME, null, values);

        if (insertedRowId == -1) {
            // Tratare esuare introducere date in baza de date
            return false;
        } else {
            // Cheltuiala a fost adaugata cu succes in baza de date
           return true;
        }
    }

    public void createLimiteTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Limite (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "categorie TEXT NOT NULL," +
                "limita REAL NOT NULL," +
                "diferenta REAL NOT NULL" +
                ")");
    }

    public Boolean addLimit(Limite limita) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("categorie", limita.getCategorie());
        values.put("limita", limita.getLimita());
        values.put("diferenta", limita.getDiferenta());

        // Adauga limita in baza de date si se verifica valoarea returnata
        long insertedRowId = db.insert("Limite", null, values);

        if (insertedRowId == -1) {
            // Tratare esuare introducere date in baza de date
            return false;
        } else {
            // Limita a fost adaugata cu succes in baza de date
            return true;
        }
    }

    // Funcție pentru calcularea sumei totale cheltuite pentru o categorie
    public float calculateTotalSpentForCategory(String category) {
        float totalSpent = 0f;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor categoryCursor = db.rawQuery("SELECT suma FROM Cheltuieli WHERE categorie = ?", new String[]{category});
        while (categoryCursor.moveToNext()) {
            totalSpent += categoryCursor.getFloat(0); // Adaugă suma la total
        }
        categoryCursor.close(); // Închide cursorul
        return totalSpent;
    }
    // Funcție pentru calcularea sumei totale cheltuite pentru o categorie intr-o luna
    public float calculateTotalSpentForCategoryPerMonth(String category) {
        float totalSpent = 0f;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor categoryCursor = db.rawQuery("SELECT suma FROM Cheltuieli WHERE categorie = ? AND strftime('%Y-%m', data) = strftime('%Y-%m', CURRENT_TIMESTAMP) ", new String[]{category});
        while (categoryCursor.moveToNext()) {
            totalSpent += categoryCursor.getFloat(0); // Adaugă suma la total
        }
        categoryCursor.close(); // Închide cursorul
        return totalSpent;
    }

    // Implementare metoda pt a prelua limita introdusa de utilizator din tabela Limite pt o categorie specificata ca parametru
    public float getLimitForCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        float limit = 100000f; // definesc o valoare foarte mare daca nu se gaseste limita pt o categorie in tabela
        Cursor cursor = db.rawQuery("SELECT limita FROM Limite WHERE categorie = ?", new String[]{category});

        if (cursor.moveToFirst()) {
            // Obțin valoarea din coloana „limită” din tabela
            limit = cursor.getFloat(0);
        }

        cursor.close();
        return limit;
    }

    // Implementare metoda pt a prelua diferenta din tabela Limite pt o categorie specificata ca parametru
    public float getDifferenceForCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        float diferenta = 0f;
        Cursor cursor = db.rawQuery("SELECT diferenta FROM Limite WHERE categorie = ?", new String[]{category});

        if (cursor.moveToFirst()) {
            // Obțin valoarea din coloana „limită” din tabela
            diferenta = cursor.getFloat(0);
        }

        cursor.close();
        return diferenta;
    }

    @Override
    public void close() {
        super.close();
    }

}

