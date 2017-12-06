package com.lecoder.team9.lecoder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class FileBrowserActivity extends AppCompatActivity {


    ItemAdapter adapter;
    ListView listView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        toolbar = (Toolbar)findViewById(R.id.fileBrowserToolbar);
        toolbar.setTitle("FileBrowser");
        setSupportActionBar(toolbar);

        adapter = new ItemAdapter();
        listView = (ListView) findViewById(R.id.fileListView);
        adapter.addItem(new FileBrowserItem("파일위치", "파일크기", R.drawable.file_folder));
        adapter.addItem(new FileBrowserItem("파일위치", "파일크기", R.drawable.file_folder));
        adapter.addItem(new FileBrowserItem("파일위치", "파일크기", R.drawable.file_folder));
        adapter.addItem(new FileBrowserItem("파일위치", "파일크기", R.drawable.file_folder));
        adapter.addItem(new FileBrowserItem("파일위치", "파일크기", R.drawable.file_folder));
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

     /*   listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }

        });
        */

    }

    class ItemAdapter extends BaseAdapter {
        ArrayList<FileBrowserItem> items = new ArrayList<FileBrowserItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(FileBrowserItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) { //생성되는 뷰에 정보를 넣어주는 부분이다.
            FileBrowserView view = new FileBrowserView(getApplicationContext());

            FileBrowserItem item = items.get(position);
            view.setTitle1(item.getTitle());
            view.setTitle2(item.getDesc());
            view.setImage(item.getIcon());
            return view;
        }

        public void removeItem(int position) {
            items.remove(position);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.file_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.newsetting){
            Toast.makeText(FileBrowserActivity.this, "새 글 버튼 등록", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }




}
