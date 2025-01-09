package com.example.prima_pagina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Pag_Rapoarte extends AppCompatActivity {

    public Button buton_s, buton_l, buton_a;
    //DE CREAT PAGINILE PENTRU RAPOARTE!!!!!!!!!!!!!!!!!!1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pag_rapoarte);

        buton_s = (Button) findViewById(R.id.rap_saptamanal);
        buton_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pag_Rapoarte.this, Raport_Saptamanal.class);
                startActivity(intent);
            }
        });

        buton_l = (Button) findViewById(R.id.rap_lunar);
        buton_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pag_Rapoarte.this, Raport_Lunar.class);
                startActivity(intent);
            }
        });

        buton_a = (Button) findViewById(R.id.rap_anual);
        buton_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pag_Rapoarte.this, Raport_Anual.class);
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