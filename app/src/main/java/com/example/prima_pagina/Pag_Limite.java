package com.example.prima_pagina;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Pag_Limite extends AppCompatActivity {

    EditText limita, Edit_diferenta, parola;
    TextView  informatii;
    Spinner sp_categorii;
    String categorie;
    double diferenta;
    ArrayAdapter<CharSequence> adapter;
    Button buton;
    private Button notificationButton;
    private boolean hasNotificationPermission = false;
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 100; // pt permisiune notificari
    ExpenseDbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pag_limite);

        informatii = findViewById(R.id.textViewInfo);
        // Ascund initial butonul de informatii despre diferenta
        informatii.setVisibility(View.GONE);

        parola = findViewById(R.id.editParola);
        // Ascund initial butonul de editare a parolei
        parola.setVisibility(View.GONE);

        limita = findViewById(R.id.editLimita);
        Edit_diferenta = findViewById(R.id.editDiferenta);

        sp_categorii = (Spinner) findViewById(R.id.spinner_categorii);
        adapter = ArrayAdapter.createFromResource(this, R.array.categorii, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_categorii.setAdapter(adapter);

        sp_categorii.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(25); //pt a seta dimensiunea textului din spinner
                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " Selectat", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        Edit_diferenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informatii.setVisibility(View.VISIBLE); // activez vizibilitatea pt informatiile despre diferenta
            }
        });

        notificationButton = findViewById(R.id.notificari);

        // Verific starea permisiunii notificarilor
        hasNotificationPermission = checkNotificationPermission();

        // Ascundeți butonul de notificări dacă permisiunea este deja acordată
        if (hasNotificationPermission) {
            notificationButton.setVisibility(View.GONE);
        }

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ascund iar butonul de informatii despre diferenta deoarece nu mai e nevoie de aceste informatii
                informatii.setVisibility(View.GONE);
                requestNotificationPermission();
            }
        });

        buton = (Button) findViewById(R.id.salvare_limita);
        // buton.setEnabled(false);

        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ascund iar butonul de informatii despre diferenta deoarece nu mai e nevoie de aceste informatii
                informatii.setVisibility(View.GONE);
                Salvare_limita(); // apelez metoda de salvare in tabela a datelor despre limita introdusa

            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Definire metoda de salvare limita in tabela Limite
    public void Salvare_limita()
    {
        dbHelper = new ExpenseDbHelper(this);
        // preiau parola codata stocata
        SharedPreferences storedPassword = getSharedPreferences("user_login", MODE_PRIVATE);
        String storedHashedPassword = storedPassword.getString("hashed_password", "");

        // Preia categoria cheltuielii pt limita
         categorie = sp_categorii.getSelectedItem().toString();

        // Verific daca butonul de tip EditText pt limita nu contine nimic sau contine caractere nenumerice
        // Folosesc metoda trim() pentru a elimina orice spații albe de la începutul sau de la sfârșitul intrării
        String limitaString = limita.getText().toString().trim();
        if (limitaString.isEmpty() || !limitaString.matches("[0-9.]+")) {
            Toast.makeText(Pag_Limite.this, "Vă rog să introduceți o sumă validă pentru limită (doar numere și zecimale)", Toast.LENGTH_SHORT).show();
            return;  // iesire din metoda daca intrarea e invalida
        }
        // Converteste limita valida la tipul double
        double limita = Double.parseDouble(limitaString);

        // Verific daca butonul de tip EditText pt diferenta nu contine nimic sau contine caractere nenumerice
        // Folosesc metoda trim() pentru a elimina orice spații albe de la începutul sau de la sfârșitul intrării
        String diferentaString = Edit_diferenta.getText().toString().trim();
        if (diferentaString.isEmpty() || !diferentaString.matches("[0-9.]+")) {
            Toast.makeText(Pag_Limite.this, "Vă rog să introduceți o sumă validă pentru diferență (doar numere și zecimale)", Toast.LENGTH_SHORT).show();
            return;  // iesire din metoda daca intrarea e invalida
        }

        // Converteste diferenta valida la tipul double
        diferenta = Double.parseDouble(diferentaString);

        // Afisez butonul cu textul de editare a parolei daca se introduce o suma valida si se alege o data
        parola.setVisibility(View.VISIBLE);
        // Activez butonul de tip edit pt introducerea parolei
        parola.setEnabled(true);

        // preiau parola introdusa de utilizator
        String enteredPassword = parola.getText().toString();

        // codez parola introdusa
        String hashedEnteredPassword = hashPassword(enteredPassword); // apelez metoda implementata de codare  parolei

        if (parola.getText().toString().isEmpty()) {
            Toast.makeText(Pag_Limite.this, "Va rugam introduceti parola", Toast.LENGTH_SHORT).show();
            return;
        }

        // Concentrare pe textul de editare a parolei pentru introducere imediata
        parola.requestFocus();

        // Verific daca parola introdusa nu e corecta
        if (!hashedEnteredPassword.equals(storedHashedPassword)) {
            // Parola introdusa nu coincide cu cea stocata
            Toast.makeText(Pag_Limite.this, "Parolă greșită! Reintroduceți parola.", Toast.LENGTH_SHORT).show();
            // Stergere parola gresita introdusa
            parola.setText("");
            // Concentrare pe textul de introducere a parolei
            parola.requestFocus();
            return;
        }

        // Salvare limita introdusa daca datele introduse sunt corecte
        Limite limitaObject = new Limite(categorie, limita, diferenta);

        if (dbHelper != null) {
            Boolean verifAdaugare = dbHelper.addLimit(limitaObject); // apelare metoda de introducere a obiectlui limita in tabela Limite
            if (verifAdaugare == true)
            {
                Toast.makeText(Pag_Limite.this, "Limita fost setata cu succes.", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(Pag_Limite.this, "Limita nu a fost setata.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Pag_Limite.this, "Eroare! Limita nu a fost setata.", Toast.LENGTH_SHORT).show();
        }

        // Trecere la pagina urmatoare (doar daca se introduce o limita si o diferenta valida si daca parola introdusa e corecta
        Intent intent = new Intent(Pag_Limite.this, Salvare_limita.class);
        intent.putExtra("Categoria", categorie); // trimit categoria spre pagina urmatoare
        startActivity(intent);

    }

    // Metoda de criptare a parolei
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

    // metoda pt a cere utilizatorului permisiune pt a primi notificari
    public void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { //Verific pt Android 13 sau o versiune ulterioară
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION_PERMISSION);
        } else {
            // Gestionez versiunile mai vechi de Android
            Toast.makeText(this, "Versiunea de Android nu acceptă trimiterea de notificări", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permisiune acordata, afisez mesaj
                Toast.makeText(this, "Permisiunea de notificare a fost acceptată", Toast.LENGTH_SHORT).show();
                notificationButton.setVisibility(View.GONE); // Ascund butonul de solicitare permisiune
                hasNotificationPermission = true; // Actualizez starea permisiunii
            } else {
                // Permisiunea nu a fost acordatam afisez mesaj
                Toast.makeText(this, "Permisiunea de notificare a fost refuzată", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // definire metoda care verifica daca a fost acordata permisiunea de notificari
    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true; // Presupun că permisiunea este acordată pentru versiunile mai vechi de Android 13
        }
    }

}