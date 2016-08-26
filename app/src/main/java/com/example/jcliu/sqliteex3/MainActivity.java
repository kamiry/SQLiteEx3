package com.example.jcliu.sqliteex3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    static final String DB_NAME = "HotlineDB";
    static final String TB_NAME = "hostlist";
    ListView lv;
    EditText etName, etPhone, etEmail;
    Button btInsert, btUpdate, btDelete;
    SimpleCursorAdapter adapter;
    SQLiteDatabase db;
    Cursor cur;
    static final String[] FROM =  {"name", "phone", "email"};
    static final int MAX = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        lv = (ListView)findViewById(R.id.listView);
        etName = (EditText)findViewById(R.id.editText);
        etPhone = (EditText)findViewById(R.id.editText2);
        etEmail = (EditText)findViewById(R.id.editText3);
        btInsert = (Button)findViewById(R.id.button);
        btUpdate = (Button)findViewById(R.id.button2);
        btDelete = (Button)findViewById(R.id.button3);

        //
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        String createTable = "CREATE TABLE IF NOT EXISTS " + TB_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT,  " +
                "name VARCHAR(32), " + "phone VARCHAR(16), " + "email VARCHAR(64))";
        db.execSQL(createTable);
        Log.d("Hotlist", "create DB sucessful");
        // insert data if empty
        cur = db.rawQuery("SELECT * FROM " + TB_NAME, null);
        if (cur.getCount() == 0){
            addData("暨南大學", "049-2910960", "www@ncnu.edu.tw");
            addData("暨南大學資工系", "049-2910960", "www@csie.ncnu.edu.tw");
            Log.d("Hotlist", "insert data to empty table");
        }
        else
            Log.d("Hotlist", "table is not empty");
        // adapter
        adapter = new SimpleCursorAdapter(this,
                R.layout.item, cur,
                FROM, new int[] {R.id.name, R.id.phone, R.id.email}, 0);
        lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        requery();
    }

    public void call(View v){
        String uri = "tel:" + cur.getString(cur.getColumnIndex(FROM[1]));
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(it);
    }

    public void mail(View v){
        String uri = "mailto:" + cur.getString(cur.getColumnIndex(FROM[2]));
        Intent it = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        startActivity(it);
    }

    public void delete(View v){
        db.delete(TB_NAME, "_id="+cur.getInt(0), null);
        requery();
    }

    private void update(String name, String phone, String email, int id){
        ContentValues cv = new ContentValues(3);
        cv.put(FROM[0], name);
        cv.put(FROM[1], phone);
        cv.put(FROM[2], email);

        db.update(TB_NAME, cv, "_id="+id, null);
        Log.d("Hotlist", "update table");
    }

    public void InsertOrUpdate(View v){
        String nameStr = etName.getText().toString().trim();
        String phoneStr = etPhone.getText().toString().trim();
        String emailStr = etEmail.getText().toString().trim();

        if(nameStr.length() == 0 || phoneStr.length() == 0 || emailStr.length() == 0)
            return;
        if(v.getId() == R.id.button2)
            update(nameStr, phoneStr, emailStr, cur.getInt(0));
        else {
            addData(nameStr, phoneStr, emailStr);
            Log.d("Hotlist", "insert data");
        }

        requery();
    }

    private void requery(){
        cur = db.rawQuery("SELECT * FROM " + TB_NAME, null);
        adapter.changeCursor(cur);
        if (cur.getCount() == MAX)
            btInsert.setEnabled(false);
        else
            btInsert.setEnabled(true);
        btUpdate.setEnabled(false);
        btDelete.setEnabled(false);
    }

    private void addData(String name, String phone, String email){
        ContentValues cv = new ContentValues(3);
        cv.put(FROM[0], name);
        cv.put(FROM[1], phone);
        cv.put(FROM[2], email);

        db.insert(TB_NAME, null, cv);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        cur.moveToPosition(position);
        etName.setText(cur.getString(cur.getColumnIndex(FROM[0])));
        etPhone.setText(cur.getString(cur.getColumnIndex(FROM[1])));
        etEmail.setText(cur.getString(cur.getColumnIndex(FROM[2])));
        btUpdate.setEnabled(true);
        btDelete.setEnabled(true);
    }
}
