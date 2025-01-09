package com.example.prima_pagina;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Raport_Anual extends AppCompatActivity {

    ExpenseDbHelper dbHelper;
    // Pentru a obține datele din baza de date
    SQLiteDatabase db;

    ArrayList cheltuieliAnuale;
    List<String> categorii_legenda; // Pt legenda
    TextView venitAnualTextView; // Pentru a afisa venitul anual (pana in  prezent)
    TextView cheltuieliAnualeTextView; // Pentru a afisa cheltuielile anuale (pana in  prezent)
    LocalDate today = LocalDate.now(); // Obtinere data de astazi
    String[] categories = {"Alimente", "Haine", "Cumparaturi pentru casa", "Masa in oras", "Facturi", "Combustibil pentru masina", "Cadouri", "Activitati recreative", "Sport", "Vacante", "Altele"};
    int[] categoryPositions = new int[categories.length];

    // Map pentru a stoca denumirile de categorii si pozitiile lor
    //Map<String, Integer> categoryPositions = new HashMap<>();

    Button buton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_raport_anual);

        venitAnualTextView = findViewById(R.id.text_venit_anual);
        cheltuieliAnualeTextView = findViewById(R.id.text_cheltuieli_anuale);

        // Preluare venit de la SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        float venitAnual = sharedPreferences.getFloat("venit", 0.0f); // Default la 0 daca nu e setat

        // Afsare venit anual total (pana in prezent)
        venitAnualTextView.setText(String.format("%.2f RON", venitAnual * today.getMonthValue()));

        categorii_legenda = new ArrayList<>();

        // Creare diagrama
        BarChart barChart = findViewById(R.id.bar_chart);
        Legend legend = barChart.getLegend(); // preiau obiectul de tip legenda

        barChart.getAxisRight().setDrawLabels(false);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum((venitAnual * today.getMonthValue())/2);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        // apelare metoda de setare a datelor pentr diagrama
        getData();

        // afisare date pt legenda
        legend.setExtra(ColorTemplate.COLORFUL_COLORS, categorii_legenda.toArray(new String[0])); // setez legenda
        legend.setEnabled(true);

        barChart.setDrawValueAboveBar(true); // Afișare valori deasupra barelor
        BarDataSet barDataSet = new BarDataSet(cheltuieliAnuale, "Raport anual");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        // Personalizare diagrama
        //legend.setCustom(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
       // legend.setCustom(Legend.LegendOrientation.VERTICAL);

        // Setez alinierea orizontală a legendei la DREAPTA
        //legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);

        // Creare obiect de tip Description
        Description desc = new Description();
        desc.setText("Cheltuieli anuale pe categorii");

        // Setare descriere pentru diagrama(chart)
        barChart.setDescription(desc);

        // culoare bar data
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // culoare text
        barDataSet.setValueTextColor(Color.BLACK);

        // setare marime text
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(true);

        buton = (Button) findViewById(R.id.revenire);
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Raport_Anual.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Ajustarea paddingului pentru bara de sistem
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getData()
    {
        dbHelper = new ExpenseDbHelper(this);
        // Obțin datele din baza de date
        db = dbHelper.getReadableDatabase();

        // Pentru a calcula totalul cheltuielilor din acest an (pana in prezent) fara a tine cont de categorii
        double totalExpenses = 0.0;

        for (int i = 0; i < categories.length; i++) {
            categoryPositions[i] = i; // Asocierea indexului la pozitia in lista
        }

        String query = "SELECT categorie, SUM(suma) AS suma_anuala " +
                "FROM Cheltuieli " +
                "WHERE strftime('%Y', data) = strftime('%Y', CURRENT_TIMESTAMP) " +
                "GROUP BY categorie;";

        Cursor cursor = db.rawQuery(query, null);
        // Pregătire date pentru diagrama
         cheltuieliAnuale = new ArrayList<>();

        Double totalAnualExpenses = 0.0;
        int categoryIndex = 0;

        while (cursor.moveToNext()) {
            String category = cursor.getString(0);
            float sumaTotala = cursor.getFloat(1); // Calculez totalul cheltuit pentru categorie

            // Verific daca suma totala este mai mare de 0

            // Caut indexul categoriei in lista predefinita
            int categoryIndexPredefinit = -1;
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(category)) {
                    categoryIndexPredefinit = i;
                    break;
                }
            }
            cheltuieliAnuale.add(new BarEntry(categoryIndex, sumaTotala));
            categoryIndex++;
            categorii_legenda.add(category); //Pt legenda

            totalAnualExpenses += sumaTotala;

            // Verific daca categoria nu a fost gasita
//            if (categoryIndexPredefinit == -1) // Afisez mesaj de eroare daca categoria nu este gasita
//                Toast.makeText(this, " Categoria '" + category + "' nu a fost găsită", Toast.LENGTH_SHORT).show();
        }

        cursor.close(); // inchidere cursor

        // Afisare total cheltuiei anuale (pana in prezent)
        cheltuieliAnualeTextView.setText(String.format(" %.2f RON", totalAnualExpenses));
    }
}
