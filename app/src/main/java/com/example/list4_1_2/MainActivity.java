package com.example.list4_1_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private String[] from;
    private final String ATTRIBUTE_TITLE_TEXT = "title";
    private final String ATTRIBUTE_SUBTITLE_TEXT = "subtitle";
    int[] to = {R.id.textView_text_1, R.id.textView_text_2};
    private static final int CM_DELETE_ID = 1;
    SimpleAdapter listContentAdapter;

    private ArrayList<Map<String, Object>> data;
    SharedPreferences sharedPreferences;
    private static final String PREFS_FILE = "Account";//---------------------------

    private static final String LARGE_TEXT = "large text";//--------------------------------
    private final static String MY_KEY = "my key";
    private TextView textView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<Integer> integerArrayList = new ArrayList<>();  //лист для инд. удаления -------------------------------
    private int n;
    AdapterView.AdapterContextMenuInfo acmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
                mSwipeRefreshLayout.setColorSchemeResources
                        (R.color.light_blue, R.color.middle_blue, R.color.deep_blue);
            }
        });

        String[] values = prepareContent();
        from = new String[]{ATTRIBUTE_TITLE_TEXT, ATTRIBUTE_SUBTITLE_TEXT};
        //массив данных
        addData(values);

        sharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);

        shareEdit();

        listContentAdapter = createAdapter();
        listView.setAdapter(listContentAdapter);
        registerForContextMenu(listView);
//---------------------------------------------------------------------------------------------------------------------
        if (savedInstanceState == null) {
            Toast.makeText(this, getString(R.string.text_view_null), Toast.LENGTH_LONG).show();
        } else {
            integerArrayList = savedInstanceState.getIntegerArrayList(MY_KEY);  //восстановление сост. -----------------
            assert integerArrayList != null;
            for (int m = 0; m < integerArrayList.size(); m++) {
                data.remove(integerArrayList.get(m).intValue());
            }
            listContentAdapter.notifyDataSetChanged();
        }
        removePos();
    }

    private void removePos() { // удаляем эл.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int deletePosition = position;

                Toast.makeText(MainActivity.this, (R.string.remove), Toast.LENGTH_LONG).show();
                data.remove(deletePosition);
                integerArrayList.add(deletePosition);     //  добавление инекса удал. элемента  --------------------------------
                listContentAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(MY_KEY, integerArrayList);   //сохранение листа эл. удаления ------------------------------
    }

    public void refreshList() {
        mSwipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Останавливаем обновление:
                mSwipeRefreshLayout.setRefreshing(false);
                shareEdit();

                int min = 0;
                int max = 100;
                Random random = new Random();
                int i = random.nextInt((max - min + 1) + min);
                textView = findViewById(R.id.textView);
                textView.setText(String.valueOf(i) + "\n" + n + "\n" + integerArrayList);
            }
        }, 3000);
    }

    private void shareEdit() {//-----------------------------------------------------------------------------------------------------
        if (sharedPreferences.contains(getString(R.string.large_text))) {
            textView.setText(sharedPreferences.getString(LARGE_TEXT, ""));
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LARGE_TEXT, String.valueOf(R.string.large_text));
            editor.apply();
        }
    }

    private void addData(String[] values) {
        data = new ArrayList<Map<String, Object>>(values.length);
        Map<String, Object> mapText;

        for (int i = 0; i < values.length; i++) {
            mapText = new HashMap<>();

            mapText.put(ATTRIBUTE_TITLE_TEXT, values[i]);
            mapText.put(ATTRIBUTE_SUBTITLE_TEXT, "" + values[i].length());
            data.add(mapText);
        }
    }

    private void initViews() { //инициализация

        listView = findViewById(R.id.listView_text_1);
        textView = findViewById(R.id.textView_text_1);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
    }

    private SimpleAdapter createAdapter() {
        return new SimpleAdapter(this, data, R.layout.item,
                from, to);
    }

    private String[] prepareContent() {
        return getString(R.string.large_text).split("\n\n");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, (R.string.delete_text));
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {

        if (item.getItemId() == CM_DELETE_ID) {
            // получаем инфу о пункте списка
            acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            // удаляем Map из коллекции, используя позицию пункта в списке
            n = acmi.position; // индекс удаляемого элем.  -----------------------------------
            data.remove(acmi.position);

            integerArrayList.add(acmi.position);     //  добавление инекса удал. элемента  --------------------------------

            // уведомляем, что данные изменились
            listContentAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
    }
}
