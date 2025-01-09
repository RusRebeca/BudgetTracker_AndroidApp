package com.example.prima_pagina;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Pag_Cheltuieli extends AppCompatActivity {

    Button buton;
    Spinner sp_categorii, sp_repetare;
    ArrayAdapter<CharSequence> adapter1, adapter2;
    Switch switch_rep;
    private TextView view_data;
    private Button data_buton;
    private EditText sumaEditText;
    private DatePicker datePicker;
    Date date;
    ExpenseDbHelper dbHelper;
    // Pentru a obține datele din baza de date
    SQLiteDatabase db;
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 100; // pt permisiune notificari
     LocalDate today = LocalDate.now(); // Obtinere data de astazi
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pag_cheltuieli);

        passwordEditText = findViewById(R.id.editParola);
        // Ascund initial butonul de editare a parolei
        passwordEditText.setVisibility(View.GONE);

        view_data = findViewById(R.id.Data_view);
        data_buton = findViewById(R.id.alege_data);

        view_data.setText(" ");

        datePicker = new DatePicker(this);

        data_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlegeData();
            }
        });

        sp_categorii = (Spinner) findViewById(R.id.spinner_categorii);
        adapter1 = ArrayAdapter.createFromResource(this, R.array.categorii, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_categorii.setAdapter(adapter1);

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

        switch_rep = findViewById(R.id.switch1);

        sp_repetare = (Spinner) findViewById(R.id.spinner_repetare);
        adapter2 = ArrayAdapter.createFromResource(this, R.array.repetare, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_repetare.setAdapter(adapter2);

        // Controleaza vizibilitatea spinner-ului pt "Repetare" pe baza starii on/off a switch-ului

        sp_repetare.setVisibility(View.GONE); // Initial seteaza vizibilitatea la GONE

        switch_rep.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp_repetare.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        sp_repetare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 ((TextView) parent.getChildAt(0)).setTextSize(30); //pt a seta dimensiunea textului din spinner
                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " Selectat", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        buton = (Button) findViewById(R.id.salvare_cheltuiala);
        // buton.setEnabled(false);

        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Salvare_cheltuiala();

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void AlegeData() {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                if(month < 9)
                {
                    if(day < 10)
                        view_data.setText(year + "-0" + (month+1) + "-0" + day);
                    else
                        view_data.setText(year + "-0" + (month+1) + "-" + day);
                }
                else
                {
                    if(day < 10)
                        view_data.setText(year + "-" + (month+1) + "-0" + day);
                    else
                        view_data.setText(year + "-" + (month+1) + "-" + day);
                }
                //view_data.setText(String.valueOf(day) + "." + String.valueOf(month+1) + "." + String.valueOf(year));
            }
        }, today.getYear(), today.getMonthValue()-1, 1); // lunile sunt indexate de la 0

        dialog.show();
    }

    public void Salvare_cheltuiala() {
        // Gaseste butonul de tip EditText dupa ID
        sumaEditText = findViewById(R.id.editTextNumberDecimal);

        // preiau parola introdusa de utilizator
        String enteredPassword = passwordEditText.getText().toString();

        // codez parola introdusa
        String hashedEnteredPassword = hashPassword(enteredPassword); // apelez metoda implementata de codare  parolei

        // preiau parola codata stocata
        SharedPreferences storedPassword = getSharedPreferences("user_login", MODE_PRIVATE);
        String storedHashedPassword = storedPassword.getString("hashed_password", "");

        // Verific daca butonul de tip EditText nu contine nimic sau contine caractere nenumerice
        // Folosesc metoda trim() pentru a elimina orice spații albe de la începutul sau de la sfârșitul intrării
        String sumaString = sumaEditText.getText().toString().trim();
        if (sumaString.isEmpty() || !sumaString.matches("[0-9.]+")) {
            Toast.makeText(Pag_Cheltuieli.this, "Va rog sa introduceti o suma valida (doar numere si zecimale)", Toast.LENGTH_SHORT).show();
            return;  // iesire din metoda daca intrarea e invalida
        }

        // Converteste suma valida la tipul double
        double suma = Double.parseDouble(sumaString);

        String dateString = view_data.getText().toString().trim();

        // Verificare daca s-a introdus o data calendaristica
        if (dateString.isEmpty()) {
            Toast.makeText(Pag_Cheltuieli.this, "Va rugam sa selectati o data", Toast.LENGTH_SHORT).show();
            return; // Iesire din metoda daca nu s-a selectat o data
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD",  Locale.getDefault());
        date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            // Afisare detalii exceptie
            Log.e("Pag_Cheltuieli", "Eroare la convertirea datei: " + e.getMessage());
            // Tratarea exceptiei de convertire a datei
            Toast.makeText(Pag_Cheltuieli.this, "Format invalid pentru data. Va rog folositi formatul: DD.MM.YYYY", Toast.LENGTH_SHORT).show();
        }

        // Afisez butonul cu textul de editare a parolei daca se introduce o suma valida si se alege o data
        passwordEditText.setVisibility(View.VISIBLE);

        // Enable password edit text for input
        passwordEditText.setEnabled(true);

        if (passwordEditText.getText().toString().isEmpty()) {
            Toast.makeText(Pag_Cheltuieli.this, "Va rugam introduceti parola", Toast.LENGTH_SHORT).show();
            return;
        }

        // Concentrare pe textul de editare a parolei pentru introducere imediata
        passwordEditText.requestFocus();

        // Verific daca parola introdusa nu e corecta
        if (!hashedEnteredPassword.equals(storedHashedPassword)) {
            // Parola introdusa nu coincide cu cea stocata
            Toast.makeText(Pag_Cheltuieli.this, "Parola gresita! Reintroduceti parola.", Toast.LENGTH_SHORT).show();
            // Stergere parola gresita introdusa
            passwordEditText.setText("");
            // Concentrare pe textul de introducere a parolei
            passwordEditText.requestFocus();
            return;
        }
        
        // Preluare informatii si salvare cheltuiala introdusa daca datele si parola au fost introduse corect

        // Preia categoria cheltuielii
        String categorie = sp_categorii.getSelectedItem().toString();

        // Preia tipul de repetitie cu litere mici
        String repeatType = sp_repetare.getSelectedItem().toString().trim().toLowerCase();

        if (switch_rep.isChecked()) {
            Calendar calendar = Calendar.getInstance();
            if (date != null) {
                calendar.set(Calendar.YEAR, date.getYear()+1900);
                calendar.set(Calendar.MONTH, date.getMonth()+1); // lunile sunt indexate de la 0
                calendar.set(Calendar.DAY_OF_MONTH, date.getDay());
            }

            // setez ultima zi din anul curent
            Calendar dataFinala = Calendar.getInstance();
            dataFinala.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            dataFinala.set(Calendar.MONTH, Calendar.DECEMBER);
            dataFinala.set(Calendar.DAY_OF_MONTH, 31);

            if (repeatType.equals("zilnic") || repeatType.equals("saptamanal") || repeatType.equals("lunar")) {

                // Bucla pt a gestiona cheltuielile repetate
                while ((calendar.get(Calendar.YEAR) == dataFinala.get(Calendar.YEAR) &&
                                (calendar.get(Calendar.MONTH) < dataFinala.get(Calendar.MONTH) ||
                                        (calendar.get(Calendar.MONTH) == dataFinala.get(Calendar.MONTH) &&
                                                calendar.get(Calendar.DAY_OF_MONTH) < dataFinala.get(Calendar.DAY_OF_MONTH)))))
                {
                    // Adaug cheltuiala repetata in baza de date
                    Cheltuieli recurringExpense = new Cheltuieli(suma, categorie, dateFormat.format(calendar.getTime()));
                    ExpenseDbHelper dbHelper = new ExpenseDbHelper(Pag_Cheltuieli.this);
                    dbHelper.addExpense(recurringExpense);
                    //updateDifference(recurringExpense.getCategorie()); // apelez functia care recalculeaza diferenta pt a trimite notificari
                    recurringExpense.setData(dateFormat.format(calendar.getTime()));

                    // Incrementare data pentru următoarea repetare
                    if (repeatType.equals("lunar")) {
                        calendar.add(Calendar.MONTH, 1); // Lunar
                    } else if (repeatType.equals("saptamanal")) {
                        calendar.add(Calendar.WEEK_OF_YEAR, 1); // Săptămânal
                    } else if (repeatType.equals("zilnic")) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1); // Zilnic
                    }
                }

                Toast.makeText(Pag_Cheltuieli.this, "Cheltuiala a fost adaugata cu succes (repetata)", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Gestionare cheltuieli unice

            // Creare obiect de Cheltuieli
            Cheltuieli cheltuiala = new Cheltuieli(suma, categorie, dateString);
            // Adaugare cheltuiala la baza de date folosind ExpenseManager
            ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);
            Boolean verificadaugare = dbHelper.addExpense(cheltuiala);
            if (verificadaugare == true)
            {
                Toast.makeText(Pag_Cheltuieli.this, "Cheltuiala a fost adaugata cu succes.", Toast.LENGTH_SHORT).show();
                updateDifference(categorie); // apelez functia care recalculeaza diferenta
            }
            else
                Toast.makeText(Pag_Cheltuieli.this, "Cheltuiala nu a fost adaugata.", Toast.LENGTH_SHORT).show();

        }
        // Trecere la pagina urmatoare (doar daca se introduce o suma valida si se alege o data) daca parola introdusa e corecta
        Intent intent = new Intent(Pag_Cheltuieli.this, Salvare_cheltuieli.class);
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
    // Implementare metoda care calculeaza diferenta intre limita introdusa si suma cheltuita pt o categorie
    public void updateDifference(String category) {
        // Foloseste contextul preluat pentru a face legatura cu baza de date
        dbHelper = new ExpenseDbHelper(this);
        db = dbHelper.getReadableDatabase();
        // Calculeaza suma cheltuita in luna curenta pentru categoria specificata
        float totalSpent = dbHelper.calculateTotalSpentForCategoryPerMonth(category);

        // Obtine limita pentru categoria specificata
        float limita = dbHelper.getLimitForCategory(category);

        // Calculeaza diferenta
        float difference = limita - totalSpent;

        // Verific daca diferenta calculata e mai mica decat cea introdusa de utilizator sau daca suma cheltuita e mai mare decat limita
        if (difference <= dbHelper.getDifferenceForCategory(category) || totalSpent > limita) {
            NotificationHelper notificationHelper = new NotificationHelper(this, this);
            notificationHelper.sendLimitNotification(category, difference);
        }
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
            } else {
                // Permisiunea nu a fost acordatam afisez mesaj
                Toast.makeText(this, "Permisiunea de notificare a fost refuzată", Toast.LENGTH_SHORT).show();
            }
        }
    }
}