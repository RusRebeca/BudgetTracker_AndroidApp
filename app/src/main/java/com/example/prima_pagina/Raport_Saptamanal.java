package com.example.prima_pagina;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prima_pagina.ui.login.MainActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class Raport_Saptamanal extends AppCompatActivity {

    Button buton;
    ExpenseDbHelper dbHelper;
    // Pentru a obține datele din baza de date
    SQLiteDatabase db;
    ArrayList cheltuieliSaptamanale;
    List<String> categorii_legenda; // Pt legenda
    String[] categories = {"Alimente", "Haine", "Cumparaturi pentru casa", "Masa in oras", "Facturi", "Combustibil pentru masina", "Cadouri", "Activitati recreative", "Sport", "Vacante", "Altele"};
    int[] categoryPositions = new int[categories.length];
    ArrayList<LegendEntry> legendEntries = new ArrayList<>(); //pentru legenda diagrama
    TextView text_totalcheltuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_raport_saptamanal);

        text_totalcheltuit = findViewById(R.id.text_cheltuieli_saptamanale);

        buton = (Button) findViewById(R.id.revenire);
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Raport_Saptamanal.this, MainActivity.class);
                startActivity(intent);
            }
        });

        categorii_legenda = new ArrayList<>();

        // Creare diagrama
        BarChart barChart = findViewById(R.id.bar_chart);

        Legend legend = barChart.getLegend(); // preiau obiectul de tip legenda

        barChart.getAxisRight().setDrawLabels(false);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(1000f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        // apelare metoda de setare a datelor pentr diagrama
        getData();

        // afisare date pt legenda
        legend.setExtra(ColorTemplate.COLORFUL_COLORS, categorii_legenda.toArray(new String[0])); // setez legenda
        legend.setEnabled(true);

        BarDataSet barDataSet = new BarDataSet(cheltuieliSaptamanale, "Raport saptamanal");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        // Setez alinierea orizontală a legendei la DREAPTA
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);

        // Personalizare diagrama
        barChart.getDescription().setEnabled(true);
        // Creare obiect de tip Description
        Description desc = new Description();
        desc.setText("Cheltuielile din aceasta saptamana pe categorii");

        // Setare descriere pentru diagrama(chart)
        barChart.setDescription(desc);
        // culoare bar data
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // culoare text
        barDataSet.setValueTextColor(Color.BLACK);

        // setare marime text
        barDataSet.setValueTextSize(16f);

        // Setare legenda
        //barChart.getLegend().setEnabled(true);
        //barChart.getLegend().setCustom(legendEntries);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getData()
    {
        // Foloseste contextul preluat pentru a face legatura cu baza de date
        dbHelper = new ExpenseDbHelper(this);
        db = dbHelper.getReadableDatabase();

        for (int i = 0; i < categories.length; i++) {
            categoryPositions[i] = i; // Asocierea indexului la pozitia in lista
        }

        float totalCheltuit = 0f; // pentru a stoca totalul cheeltuit in ultimele 7 zile, fara a tine cont dee categorii

        String query = "SELECT categorie, SUM(suma) As suma_saptamanala " +
                "FROM Cheltuieli " +
                "WHERE data BETWEEN date('now', '-6 days') AND date('now') " +
                "GROUP BY categorie;";

        Cursor cursor = db.rawQuery(query, null);

        cheltuieliSaptamanale= new ArrayList();
        int  categoryIndex = 0; // Index de urmarire pentru intrarile din diagrama
        int categoryIndexPredefinit = -1;

        while (cursor.moveToNext()) {
            String categorie = cursor.getString(0);
            float sumaSaptamanala = cursor.getFloat(1); // Use retrieved sum from cursor

            // Adaug datele la diagrama
            cheltuieliSaptamanale.add(new BarEntry(categoryIndex, sumaSaptamanala));
            categoryIndex++;
            categorii_legenda.add(categorie); //Pt legenda

            // Acumulez suma cheltuita pe categorii pentru total
            totalCheltuit += sumaSaptamanala;

            // Verific daca categoria a fost gasita
//            if (categoryIndexPredefinit == -1) {
//
//                // Afisez mesaj de informare daca categoria nu este gasita
//                Toast.makeText(this, " Categoria '" + categorie + "' nu a fost găsită", Toast.LENGTH_SHORT).show();
//            }
        }
        cursor.close();

        // Afisare total cheltuiei din ultimele 7 zile
        text_totalcheltuit.setText(String.format(" %.2f RON", totalCheltuit));
    }

}