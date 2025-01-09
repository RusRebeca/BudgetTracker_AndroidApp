package com.example.prima_pagina;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ExpenseManager {

    private SQLiteDatabase db;

    public ExpenseManager(Context context) {
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // adaugare cheltuiala in baza de date
    public void addExpense(Cheltuieli cheltuiala) {
        ContentValues values = new ContentValues();
        values.put("suma", cheltuiala.getSuma());
        values.put("categorie", cheltuiala.getCategorie());
        values.put("data", cheltuiala.getData().toString()); // Data e converita la tipul String
        // Adauga cheltuiala in baza de date si se verifica valoarea returnata
        long insertedRowId = db.insert("cheltuieli", null, values);

        if (insertedRowId == -1) {
            // Tratare esuare introducere date in baza de date
            Log.e("ExpenseManager", "Eroare la inserarea cheltuielii in baza de date.");
        } else {
            // Cheltuiala a fost adaugata cu succes in baza de date
            Log.i("ExpenseManager", "Cheltuiala a fost adaugata la baza de date cu ID: " + insertedRowId);
        }
    }

    // preluare cheltuieli dupa categorie din baza de date
    public List<Cheltuieli> getExpensesByCategory(String category) {
        List<Cheltuieli> categoryExpenses = new ArrayList<>();
        String selection = "categorie = ?"; // Add selection parameters if needed
        String[] selectionArgs = new String[]{category}; // Replace with actual category
        Cursor cursor = db.query("Cheltuieli", null, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            double suma = cursor.getDouble(1);
            String categorie = cursor.getString(2);
            String dataString = cursor.getString(3); // Convert back to Date if needed
            Cheltuieli cheltuiala = new Cheltuieli(id, suma, categorie, dataString);

            categoryExpenses.add(cheltuiala); //adaug cheltuiala curenta in lista
        }
        cursor.close();
        return categoryExpenses;
    }
    protected void onDestroy() {
        super.notify();
        if (db != null) {
            db.close();
        }
    }

}

