package com.example.prima_pagina;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Pagina_venit_nou extends AppCompatActivity {

    Button button;
    private EditText venit;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pagina_venit_nou);

        passwordEditText = findViewById(R.id.verificParola);

        button = (Button) findViewById(R.id.salvare_venit);
        venit = (EditText) findViewById(R.id.editVenit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Salvare_venit();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Salvare_venit() {
        String venitString = venit.getText().toString().trim();

        // Verific daca butonul de tip EditText nu contine nimic sau contine caractere nenumerice
        if (venitString.isEmpty() || !venitString.matches("[0-9.]+")) {
            Toast.makeText(Pagina_venit_nou.this, "Va rugam sa introduceti un venit valid (doar numere si zecimale)", Toast.LENGTH_SHORT).show();
            return; // Iesire din metoda daca nu se introduce un venit valid
        }

        //preia parola codata introdusa de utilizator
        SharedPreferences storedPassword = getSharedPreferences("user_login", MODE_PRIVATE);
        String storedHashedPassword = storedPassword.getString("hashed_password", "");

        // preiau parola introdusa de utilizator
        String enteredPassword = passwordEditText.getText().toString();

        // codez parola introdusa
        String hashedEnteredPassword = hashPassword(enteredPassword); // apelez metoda implementata de codare  parolei

        if (passwordEditText.getText().toString().isEmpty()) {
            Toast.makeText(Pagina_venit_nou.this, "Va rugam sa introduceti parola", Toast.LENGTH_SHORT).show();
            return;
        }

        // Concentrare pe textul de editare a parolei pentru introducere imediata
        passwordEditText.requestFocus();

        // Verific daca parola introdusa nu e corecta
        if (!hashedEnteredPassword.equals(storedHashedPassword)) {
            Toast.makeText(Pagina_venit_nou.this, "Parola gresita! Reintroduceti parola.", Toast.LENGTH_SHORT).show();
            // Stergere parola gresita introdusa
            passwordEditText.setText("");
            // Readuc focalizarea pe campul de parola pentru reincercare
            passwordEditText.requestFocus();
            return;
        }

        // Converteste venitul valid la tipul double
        double venit = Double.parseDouble(venitString);

        // Salvare venit introdus
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat("venit", (float) venit);
        editor.apply(); // Salvare modificari

        // Trecere la pagina urmatoare (doar daca se introduce un venit valid)
        Intent intent = new Intent(Pagina_venit_nou.this, Venit_salvat.class);
        startActivity(intent);
    }
    private String hashPassword(String password) {
        // Criptare parola
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return android.util.Base64.encodeToString(hash, Base64.NO_WRAP); // Use Base64.NO_WRAP for a more compact string
    }
}
