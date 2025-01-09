package com.example.prima_pagina.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prima_pagina.Pag_Cheltuieli;
import com.example.prima_pagina.Pag_Rapoarte;
import com.example.prima_pagina.Pagina_setari;
import com.example.prima_pagina.R;

public class MainActivity extends AppCompatActivity {


    public Button button_s, button_c, button_r;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        button_s = (Button) findViewById(R.id.pag_setari);
        button_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Pagina_setari.class);
                startActivity(intent);
            }
        });

        sharedPreferences = getSharedPreferences("isFirstLaunch", MODE_PRIVATE);
        boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            // Lansată prima dată, redirecționare la LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply();
        } else {
            button_c = (Button) findViewById(R.id.cheltuieli);
            button_c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this, Pag_Cheltuieli.class);
                    startActivity(intent);
                }
            });

            button_r = (Button) findViewById(R.id.rapoarte);
            button_r.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, Pag_Rapoarte.class);
                    startActivity(intent);
                }
            });
        }

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
    }
}