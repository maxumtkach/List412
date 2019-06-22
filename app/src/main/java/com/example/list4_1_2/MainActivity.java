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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    private String[] from;
    private final String ATTRIBUTE_TITLE_TEXT = "title";
    private final String ATTRIBUTE_SUBTITLE_TEXT = "subtitle";
    int[] to = {R.id.textView_text_1, R.id.textView_text_2};
    private static final int CM_DELETE_ID = 1;
    SimpleAdapter listContentAdapter;
    ArrayList<Map<String, Object>> data;
    SharedPreferences sharedPreferences;
    private final String LARGE_TEXT = "large text";
    TextView textView;
    String[] values;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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

        values = prepareContent();
        from = new String[]{ATTRIBUTE_TITLE_TEXT, ATTRIBUTE_SUBTITLE_TEXT};
        //массив данных
        addData(values);

        sharedPreferences = getSharedPreferences(LARGE_TEXT, Context.MODE_PRIVATE);

        shareEdit();

        listContentAdapter = createAdapter();
        listView.setAdapter(listContentAdapter);
        registerForContextMenu(listView);
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
                textView.setText(String.valueOf(i));
            }
        }, 3000);
    }

    private void shareEdit() {
        if (sharedPreferences.contains(getString(R.string.large_text))) {
            textView.setText(sharedPreferences.getString(LARGE_TEXT, ""));
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LARGE_TEXT, values.toString());
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
        menu.add(0, CM_DELETE_ID, 0, "Удалить запись");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем инфу о пункте списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // удаляем Map из коллекции, используя позицию пункта в списке
            data.remove(acmi.position);
            // уведомляем, что данные изменились
            listContentAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
