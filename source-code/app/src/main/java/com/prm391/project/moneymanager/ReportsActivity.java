package com.prm391.project.moneymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;

public class ReportsActivity extends AppCompatActivity {
    DatePicker datePicker;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Calendar c1 = Calendar.getInstance();
        final int month = c1.get(Calendar.MONTH);
        final int year = c1.get(Calendar.YEAR);

        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("ExpenseTracker", MODE_PRIVATE, null);

        sqLiteDatabase.execSQL("create table if not exists category (c_id integer primary key autoincrement, c_name text)");
        sqLiteDatabase.execSQL("create table if not exists expenses " +
                "(e_id integer primary key autoincrement, budget_id integer,e_category_id integer," +
                " e_amount integer, e_mark integer, foreign key(budget_id) references budget(b_id) on delete cascade," +
                " foreign key(e_category_id) references category(c_id) on delete cascade);");

        Cursor c = sqLiteDatabase.rawQuery("select e_category_id,e_amount,e_mark from expenses " +
                "where budget_id = (select b_id from budget where b_month=" + month + " and b_year=" + year + ");", null);
        if (c.moveToFirst() || c.getCount() > 0) {
            int rows = c.getCount();
            int cols = c.getColumnCount();
            ScrollView scrollView = new ScrollView(context);
            scrollView.setVerticalScrollBarEnabled(true);
            scrollView.setBackgroundColor(Color.WHITE);
            scrollView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,
                    ScrollView.LayoutParams.MATCH_PARENT));
            TableLayout table_layout = new TableLayout(context);

            TableRow title_row = new TableRow(context);

            String[] titles = new String[]{"Category", "Amount", "Mark"};
            for(int i = 0; i < titles.length; i++){
                TextView title = new TextView(context);
                title.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                title.setGravity(Gravity.CENTER);
                title.setTextSize(20);
                title.setTypeface(null, Typeface.BOLD);
                title.setPadding(50, 10, 10, 10);
                title.setText(titles[i]);
                title_row.addView(title);
            }

            table_layout.addView(title_row);

            // outer for loop
            for (int i = 0; i < rows; i++) {

                TableRow row = new TableRow(context);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                // inner for loop
                for (int j = 0; j < cols; j++) {

                    TextView tv = new TextView(context);
                    tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(18);
                    tv.setPadding(50, 10, 10, 10);
                    if (j == 2)
                        if (c.getInt(2) != 0)
                            tv.setText("Paid!");
                        else
                            tv.setText("Pay Later!");
                    else if (j == 0) {
                        int y = c.getInt(j);
                        Cursor t = sqLiteDatabase.rawQuery("select c_name from category where c_id=" + y + ";", null);
                        t.moveToFirst();
                        tv.setText(t.getString(0));
                    } else
                        tv.setText(c.getString(j));
                    row.addView(tv);

                }

                c.moveToNext();

                table_layout.addView(row);

            }
            scrollView.addView(table_layout);
            RelativeLayout rt = (RelativeLayout) findViewById(R.id.rx);
            rt.addView(scrollView);
        } else {
            TextView tv = new TextView(context);
            tv.setText("No results!");
            tv.setTextColor(Color.RED);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            tv.setPadding(50, 50, 10, 10);
            RelativeLayout rt = (RelativeLayout) findViewById(R.id.rx);
            rt.addView(tv);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reports, menu);
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
