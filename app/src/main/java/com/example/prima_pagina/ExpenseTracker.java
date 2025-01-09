package com.example.prima_pagina;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpenseTracker {

    private SQLiteDatabase db;

    public ExpenseTracker(Context context) {
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);
        db = dbHelper.getReadableDatabase();
    }

    public List<Cheltuieli> getExpensesByMonth(int month, int year) {
        List<Cheltuieli> monthlyExpenses = new ArrayList<>();

        // Define selection criteria based on current month and year
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Lunile sunt indexate de la 0

        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        calendar.add(Calendar.MONTH, 1); // Move to next month
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to first day
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Go to last day of previous month
        String endDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

        String selection = "data BETWEEN ? AND ?";
        String[] selectionArgs = new String[]{startDate, endDate};

        Cursor cursor = null;

        try {
            // Execute query within the try block
            cursor = db.query("Cheltuieli", null, selection, selectionArgs, null, null, null);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                double suma = cursor.getDouble(1);
                String categorie = cursor.getString(2);
                String dataString = cursor.getString(3);
                Cheltuieli cheltuiala = new Cheltuieli(id, suma, categorie, dataString);

                monthlyExpenses.add(cheltuiala);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Inregistrare eroare pentru depanare
            return monthlyExpenses; // Returnare lista goala daca apare o exceptie
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return monthlyExpenses;
    }

}
