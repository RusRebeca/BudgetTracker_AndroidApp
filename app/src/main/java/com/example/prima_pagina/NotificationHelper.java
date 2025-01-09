package com.example.prima_pagina;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

// Definire clasa pt a gestiona trimiterea de notificari
public class NotificationHelper {

    private Context context;
    private Pag_Cheltuieli activity; // Pt a lua referinta la pagina Pag_Cheltuieli

    // definire constructor cu parametri
    public NotificationHelper(Context context, Pag_Cheltuieli activity) {
        this.context = context;
        this.activity = activity; // Iau referinta la pagina Pag_Cheltuieli
    }
    public void sendLimitNotification(String category, float difference) {
        String message = "Va apropiati de limita setata pentru categoria " + category + ". Diferenta: " + difference;

        // Verific daca permisiunea de notificare este acordata
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            // Creeez un NotificationCompat.Builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                    .setSmallIcon(R.drawable.warning_limita) // Your notification icon
                    .setContentTitle("Aplicatie de gestionare a bugetului")
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // Creez un obiect de tip NotificationManagerCompat
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Creez un canal de notificare
            createNotificationChannel(notificationManager);

            // Trimit notificarea
            notificationManager.notify(1, builder.build()); // You can use a unique ID for each notification
        } else {
            //Gestionez cazul in care permisiunea de notificare nu a fost acordata
            Toast.makeText(context, "Aplicatia necesita permisiunea de notificare", Toast.LENGTH_SHORT).show();
            // Cer permisiune de notificare folosind ActivityCompat.requestPermissions
            if (activity != null) { // Assuming you have a reference to the activity
                activity.requestNotificationPermission();
            } else {
                Log.w("NotificationHelper", "Nicio referinta de activitate pentru a solicita permisiunea de notificare");
            }
        }
    }

    private void createNotificationChannel(NotificationManagerCompat notificationManager) {
        // Creez un obiect de tip NotificationChannel
            CharSequence name = "Notificari privind managementul bugetului";
            String description = "Notificari pentru gestionarea bugetului";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }
