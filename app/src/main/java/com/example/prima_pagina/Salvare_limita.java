package com.example.prima_pagina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prima_pagina.ui.login.MainActivity;

public class Salvare_limita extends AppCompatActivity {

    Button buton;
    TextView mesaj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_salvare_limita);

        mesaj = findViewById(R.id.mesaj);

        // Obțin referinta la obiectul de tip Intent care a inceput aceasta activitate
        Intent intent = getIntent();

        // Verific dacă obiectul de tip Intent contine in plus „Categoria”.
        if (intent.hasExtra("Categoria")) {
            // Preiau valoarea categoriei din obiectul de tip Intent
            String categorie = intent.getStringExtra("Categoria");

            // Folosesc categoria preluata pt a seta un mesaj
            mesaj.setText("Limita pentru categoria: " + categorie + " a fost setata cu succes.");
        } else {
            // Gestionez cazul in care lipseste extra-ul „Categoria”: afisez un mesaj de eroare
            Toast.makeText(this, "Categoria nu a fost primita", Toast.LENGTH_SHORT).show();
        }

        buton = (Button) findViewById(R.id.revenire);
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Salvare_limita.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}