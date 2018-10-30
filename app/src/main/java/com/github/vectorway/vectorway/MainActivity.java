package com.github.vectorway.vectorway;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<News> newsList = new ArrayList<>();
    public NewsAdapted newsAdapted;
    private DatabaseReference myRef;
    private int STORAGE_PERMISSION_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myRef = FirebaseDatabase.getInstance().getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<News>> t =
                        new GenericTypeIndicator<ArrayList<News>>(){};
                newsList = dataSnapshot.child("events").getValue(t);

                Log.d("MyActivityMain", "Value is: " + newsList.toString());


                newsAdapted.UpdateList(newsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FAILED CONECTION", "Failed to read value.");
            }
        });

        fillData();
        newsAdapted = new NewsAdapted(this,newsList);

        ListView lvMain = findViewById(R.id.lvMain);
        lvMain.setAdapter(newsAdapted);

        //addAutoStartup();
        RequestBootPermission();
    }

    private void addAutoStartup() {

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }
    }

    /**
     * Это было самое больное место разработки. Дело в том, что китайские телефоны (xiaomi, huawei etc.)
     * не предоставляют так просто RECIEVE_BOOT_PERMISSION из-за встроенного защитника, также он не позволяет
     * вывести окно запроса прав с пользовотелем. Данная функция будет работать только с телефонами из списка.
     */
    private void RequestBootPermission() {
        String manufacturer = "xiaomi";

        if(manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            String chinaPhonePermission = ReadDataFromPhone("China_Phone_Permission");
            if(chinaPhonePermission == "error" || chinaPhonePermission == "BOOT PERMISSION 0"){
                new AlertDialog.Builder(this).
                        setTitle("Требуются права доступа").
                        setMessage("Счастливые обладатели xiaomi должны включить права autostart для " +
                                "данного приложения. Эти права нужны для своевременной доставки пуш уведомлений. " +
                                "Нажмите 'oк' чтобы перейти в настройки безопасности xiaomi и передвиньте ползунок " +
                                "у нашего приложения.").
                        setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                WriteDataOnPhone("BOOT PERMISSION 1","China_Phone_Permission");
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            /*Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity.VectorWay"));
                            startActivity(intent);*/
                            }
                        }).
                        setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                WriteDataOnPhone("BOOT PERMISSION 0","China_Phone_Permission");
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseDatabase.getInstance().goOffline();
    }

    private void fillData() {
        //ToDo
    }

    /**
     *
     * @param output строка, которую надо вывести
     * @param filename Имя файла, в который надо вывести
     */
    int WriteDataOnPhone(String output, String filename){
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(filename, MODE_PRIVATE)));
            bw.write(output);
            Log.d("PRINT LOG", output);
            bw.close();
        } catch (IOException e) {
            Log.d("FILE PRINT ERROR","can't create/print file");
            return 1;
        }
        return  0;
    }

    /**
     *
     * @param filename откуда читаем данные
     */
    String ReadDataFromPhone(String filename){
        String output = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(filename)));
            output = br.readLine();
            Log.d("READ LOG", output);
            br.close();
        } catch (IOException e) {
            Log.d("FILE READ ERROR", "Can't open/read file");
            return "error";
        }
        return  output;
    }
}
