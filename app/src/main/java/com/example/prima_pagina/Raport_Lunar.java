package com.example.prima_pagina;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Raport_Lunar extends AppCompatActivity {

    Button buton;
    ExpenseDbHelper dbHelper;
    // Pentru a obține datele din baza de date
    SQLiteDatabase db;
    List<String> categorii_legenda; // Pt legenda
    //List<String> categorii_legenda = Arrays.asList("Alim", "Haine", "Casa", "Masa", "Facturi", "Combust", "Cadouri", "Recreare", "Sport", "Vacante");
    TextView venitLunarTextView; // Pentru a afisa venitul lunar
    TextView cheltuieliLunareTextView; // Pentru a afisa cheltuielile lunare

    // Lista pentru a stoca obiectele de tip BarEntry
    ArrayList<BarEntry> cheltuieliLunare;
    String[] categories = {"Alimente", "Haine", "Cumparaturi pentru casa", "Masa in oras", "Facturi", "Combustibil pentru masina", "Cadouri", "Activitati recreative", "Sport", "Vacante", "Altele"};
    int[] categoryPositions = new int[categories.length];
    //Calendar calendar = Calendar.getInstance();

    //ExpenseTracker expenseTracker = new ExpenseTracker(this);

    // Folosesc Hash map pentru a stoca categoria si suma totala cheltuita
    HashMap<String, Double> categoryTotals = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_raport_lunar);

        venitLunarTextView = findViewById(R.id.text_venit_lunar);
        cheltuieliLunareTextView = findViewById(R.id.text_cheltuieli_lunare);

        // Preluare venit de la SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        float venitLunar = sharedPreferences.getFloat("venit", 0.0f); // Default la 0 daca nu e setat

        // Afsare venit lunar
        venitLunarTextView.setText(String.format(" %.2f RON", venitLunar));

        categorii_legenda = new ArrayList<>();

        // Creare diagrama
        BarChart barChart = findViewById(R.id.bar_chart);
        Legend legend = barChart.getLegend(); // preiau obiectul de tip legenda

        barChart.getAxisRight().setDrawLabels(false);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(venitLunar/2);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        // apelare metoda de setare a datelor pentr diagrama
        getData();

        // afisare date pt legenda
        legend.setExtra(ColorTemplate.COLORFUL_COLORS, categorii_legenda.toArray(new String[0])); // setez legenda
        legend.setEnabled(true);

        BarDataSet barDataSet = new BarDataSet(cheltuieliLunare, "Raport lunar");

       // barDataSet.setLabel(getString(R.string.legend_categories)); // Set legend title (optional)
        BarData barData = new BarData(barDataSet);
        barChart.setDrawValueAboveBar(true); // Afișare valori deasupra barelor
        barChart.setData(barData);

        // setare marime text
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(true);

        // Setez alinierea orizontală a legendei la DREAPTA
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);

        //barChart.invalidate();

        // afisare legenda
//        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(categorii_legenda));
//        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//        barChart.getXAxis().setGranularity(1f);
//        barChart.getXAxis().setGranularityEnabled(true);

        // barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(categorii));
        barChart.animateY(500); // Animare afisare diagrama(chart)

        // Personalizare diagrama

        // Creare obiect de tip Description
        Description desc = new Description();
        desc.setText("Cheltuieli lunare pe categorii");

        // Setare descriere pentru diagrama(chart)
        barChart.setDescription(desc);

        // culoare bar data
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // culoare text
        barDataSet.setValueTextColor(Color.BLACK);

        // setare marime text
        //barDataSet.setValueTextSize(10f);

        buton = (Button) findViewById(R.id.revenire);
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Raport_Lunar.this, MainActivity.class);
                startActivity(intent);
            }
        });

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

        // sterg datele pt a nu avea duplicate
       // categorii_legenda.clear();

        String query = "SELECT categorie, SUM(suma) AS suma_lunara " + // Use SUM for category totals
                "FROM Cheltuieli " +
                "WHERE strftime('%Y-%m', data) = strftime('%Y-%m', CURRENT_TIMESTAMP) " +
                "GROUP BY categorie;";

        Cursor cursor = db.rawQuery(query, null);

        cheltuieliLunare = new ArrayList<>();
        int categoryIndex = 0;
        double totalExpenses = 0.0;
        int categoryIndexPredefinit = -1;

        while (cursor.moveToNext()) {
            String categorie = cursor.getString(0);
            float sumaLunara = cursor.getFloat(1); // Use retrieved sum

            cheltuieliLunare.add(new BarEntry(categoryIndex, sumaLunara));
            categoryIndex++;
            categorii_legenda.add(categorie);

            totalExpenses += sumaLunara;

        // Verific daca categoria nu a fost gasita
//            if (categoryIndexPredefinit == -1) {
//                // Afisez mesaj de eroare daca categoria nu este gasita
//                Toast.makeText(this, "Categoria '" + categorie + "' nu a fost găsită", Toast.LENGTH_SHORT).show();
//            }
        }
        cursor.close();

        // Afisare cheltuieli totale
        cheltuieliLunareTextView.setText(String.format(" %.2f RON", totalExpenses));
    }
}