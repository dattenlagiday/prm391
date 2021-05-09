package com.prm391.project.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyChangeActivity extends AppCompatActivity {

    final Context context = this;
    EditText edtAmount1;
    EditText edtAmount2;
    EditText edtCurrency1;
    EditText edtCurrency2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_change);
        edtAmount1 = findViewById(R.id.edtAmountCurrency);
        edtAmount2 = findViewById(R.id.edtAmountCurrency2);
        edtCurrency1 = findViewById(R.id.edtCurrency1);
        edtCurrency2 = findViewById(R.id.edtCurrency2);
        edtAmount2.setEnabled(false);
    }

    public void click(View view) {

        DownloadData downloadData = new DownloadData();

        try {

            String url = "http://data.fixer.io/api/latest?access_key=a0de140e1c69a82900fa75101d426113&base=";
            String chosenBase = edtCurrency1.getText().toString().trim().equals("") ? "EUR" : edtCurrency1.getText().toString();

            downloadData.execute(url + chosenBase.toUpperCase());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DownloadData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONObject jsonObject = new JSONObject(s);
                String base = jsonObject.getString("base");
                System.out.println(base);
                String date = jsonObject.getString("date");
                System.out.println(date);
                String rates = jsonObject.getString("rates");
                System.out.println(rates);

                JSONObject jsonObject1 = new JSONObject(rates);

                String raw_amount = edtAmount1.getText().toString().trim().equals("") ? "1" : edtAmount1.getText().toString();

                double amount = Double.valueOf(raw_amount);

                double standard_amount = amount > 0 ? amount : 1;

                String newCurrency = edtCurrency2.getText().toString().trim().equals("") ? "VND" : edtCurrency2.getText().toString();
                System.out.println("currency: " + newCurrency);
                String newAmount = jsonObject1.getString(newCurrency.toUpperCase());
                System.out.println(newAmount);
                System.out.println("standard: " + standard_amount);
                double result = standard_amount * Double.valueOf(newAmount);

                edtAmount2.setText(String.valueOf(result));

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        protected String doInBackground(String... strings) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try {

                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while (data > 0) {

                    char character = (char) data;
                    result += character;

                    data = inputStreamReader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_budget, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Exit");
            builder.setMessage("Are you sure you want to exit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
