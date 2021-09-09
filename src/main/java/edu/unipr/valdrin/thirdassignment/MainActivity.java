package edu.unipr.valdrin.thirdassignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {

    SQLiteDatabase db;
    public static final String TABLE_NAME = "CONTACT";
    EditText editText,editText1,editText2,editText3,editText4,editText5,editText6;
    private static final int GPS_REQ_CODE = 385;
    LocationManager manager;
    Location userLocation;
    TextView textView,textView2;
    public double latitude, longitude;
    MyTTS myTTS;
    private final int VOICE_REC_CODE = 654;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==VOICE_REC_CODE && resultCode==RESULT_OK){
            ArrayList<String> recognized = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //showMessage("Recognized",recognized.toString());
            if (recognized.contains("red")){
                getWindow().getDecorView().setBackgroundColor(Color.RED);
                myTTS.speak("I just changed the screen color to red!");
            }
            if (recognized.contains("green"))
                getWindow().getDecorView().setBackgroundColor(Color.GREEN);
            if (recognized.contains("yellow"))
                getWindow().getDecorView().setBackgroundColor(Color.YELLOW);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editTextTextPersonName);
        editText1 = findViewById(R.id.editTextTextPersonName1);
        editText2 = findViewById(R.id.editTextTextPersonName2);
        editText3 = findViewById(R.id.editTextTextPersonName3);
        editText4 = findViewById(R.id.editTextTextPersonName4);
        editText5 = findViewById(R.id.editTextTextPersonName5);
        editText6 = findViewById(R.id.editTextTextPersonName6);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        db = openOrCreateDatabase(TABLE_NAME,MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS CONTACT(CONTACT_ID TEXT PRIMARY KEY,FULL_NAME TEXT ,ADDRESS TEXT,E_MAIL TEXT,PHONE_NUMBER TEXT" +
                ",LOCATIONLATITUDE TEXT,LOCATIONLONGITUDE TEXT)");
        //myTTS = new MyTTS(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GPS_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thank you!", Toast.LENGTH_LONG).show();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else {
                showMessage("User info", "I really need the GPS permission in order to send a special code...");
            }
        }
    }

    /*public void maps(View view){

        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
        //intent.putExtra("userLocation", new double[]{userLocation.getLatitude(),userLocation.getLongitude()});
        startActivity(intent);
    }*/

    public void gps(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_REQ_CODE);
        }else {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

    }

    public void stopgps(View view){
        manager.removeUpdates(this);
        textView.setText("");
        textView2.setText("");
    }

    public void locateContact(View view){
        editText = findViewById(R.id.editTextTextPersonName);
        String contactID = editText.getText().toString();
        Cursor c = db.rawQuery("SELECT LOCATIONLATITUDE,LOCATIONLONGITUDE FROM CONTACT WHERE CONTACT_ID='"+contactID+"'",null);
        if (c.getCount()==0){
            Toast.makeText(getApplicationContext(),"No Contacts found!",Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
        intent.putExtra("userLocation",new double[]{latitude,longitude});
        startActivity(intent);

        latitude = Double. parseDouble(c.getString(0));
        longitude = Double. parseDouble(c.getString(1));
        Toast.makeText(getApplicationContext(),"Contact Found!" ,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //showMessage("Info","User is located successfully");
        latitude = location.getLatitude();
        textView.setText(String.valueOf(location.getLatitude()));
        longitude = location.getLongitude();
        textView2.setText(String.valueOf(location.getLongitude()));

        userLocation = location;
        //manager.removeUpdates(this);
        /*String locationLatitude = editText5.getText().toString(); //NEW
        String locationLongitude = editText5.getText().toString();
*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
    public void hear(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say something!");
        startActivityForResult(intent,VOICE_REC_CODE);
    }

    public void speak(View view){

        myTTS.speak(editText1.getText().toString());
    }

    public void createC(View view){
        String contactID = editText.getText().toString();
        String fullName = editText1.getText().toString();
        String address = editText2.getText().toString();
        String eMail = editText3.getText().toString();
        String phoneNumber = editText4.getText().toString();
        String locationLatitude = editText5.getText().toString(); //NEW
        String locationLongitude = editText6.getText().toString();
        db.execSQL("INSERT INTO CONTACT VALUES(?,?,?,?,?,?,?)",new Object[]{contactID,fullName,address,eMail,phoneNumber,locationLatitude,locationLongitude});
        //showMessage("Information","Contact created successfully!");
        Toast.makeText(getApplicationContext(),"Contact Created Successfully!" ,Toast.LENGTH_LONG).show();
        editText.setText("");
        editText1.setText("");
        editText2.setText("");
        editText3.setText("");
        editText4.setText("");
        editText5.setText("");
        editText6.setText("");
    }

    public void updateC(View view){
        editText = findViewById(R.id.editTextTextPersonName);
        String id = editText.getText().toString();
        String fullName = editText1.getText().toString();
        String address = editText2.getText().toString();
        String E_MAIL = editText3.getText().toString();
        String phoneNumber = editText4.getText().toString();
        String locationLatitude = editText5.getText().toString(); //NEW
        String locationLongitude = editText6.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put("FULL_NAME",fullName);
        cv.put("ADDRESS",address);
        cv.put("E_MAIL",E_MAIL);
        cv.put("PHONE_NUMBER",phoneNumber);
        cv.put("LOCATIONLATITUDE",locationLatitude);
        cv.put("LOCATIONLONGITUDE",locationLongitude);
        db.update(TABLE_NAME,cv,"CONTACT_ID=?",new String[]{id});
        editText.setText("");
        editText1.setText("");
        editText2.setText("");
        editText3.setText("");
        editText4.setText("");
        editText5.setText("");
        editText6.setText("");
        Toast.makeText(getApplicationContext(),"Contact Updated Successfully!" ,Toast.LENGTH_LONG).show();
    }

    public void searchC(View view){
        editText1 = findViewById(R.id.editTextTextPersonName1);
        String fullName = editText1.getText().toString();
        Cursor c = db.rawQuery("SELECT * FROM CONTACT WHERE FULL_NAME='"+fullName+"'",null);
        if (c.getCount()==0){
            Toast.makeText(getApplicationContext(),"No Contacts found!",Toast.LENGTH_LONG).show();
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()){
            buffer.append("Contact ID: "+c.getString(0)).append("\n");
            buffer.append("Full Name: "+c.getString(1)).append("\n");
            buffer.append("Address: "+c.getString(2)).append("\n");
            buffer.append("E-Mail: "+c.getString(3)).append("\n");
            buffer.append("Phone Number: "+c.getString(4)).append("\n");
            buffer.append("Location Latitude: "+c.getString(5)).append("\n");
            buffer.append("Location Longitude: "+c.getString(6)).append("\n");
            buffer.append("-------------------------------------------------------------------\n");
        }
        showMessage("Contacts",buffer.toString());
        Toast.makeText(getApplicationContext(),"Contact Found!" ,Toast.LENGTH_LONG).show();
        editText1.setText("");
    }

    public void deleteC(View view){
        editText = findViewById(R.id.editTextTextPersonName);
        String id = editText.getText().toString();
        db.execSQL("DELETE FROM CONTACT WHERE CONTACT_ID = '"+id+"'");

        Toast.makeText(getApplicationContext(),"Contact Deleted Succesfully!" ,Toast.LENGTH_LONG).show();
        editText.setText("");
    }

    public void viewC(View view){
        Cursor cursor = db.rawQuery("SELECT * FROM CONTACT",null);
        StringBuilder builder = new StringBuilder();
        while (cursor.moveToNext()){
            builder.append("Contact ID: ").append(cursor.getString(0)).append("\n");
            builder.append("Full Name: ").append(cursor.getString(1)).append("\n");
            builder.append("Address: ").append(cursor.getString(2)).append("\n");
            builder.append("E-Mail: ").append(cursor.getString(3)).append("\n");
            builder.append("Phone Number: ").append(cursor.getString(4)).append("\n");
            builder.append("Location Latitude: ").append(cursor.getString(5)).append("\n");
            builder.append("Location Longitude: ").append(cursor.getString(6)).append("\n");
            builder.append("-------------------------------------------------------------------\n");

        }
        showMessage("All Contacts",builder.toString());
    }

    private void showMessage(String title,String message){
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .show();
    }
}
