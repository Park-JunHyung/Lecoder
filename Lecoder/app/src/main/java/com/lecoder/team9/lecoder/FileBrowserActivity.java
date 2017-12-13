package com.lecoder.team9.lecoder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileBrowserActivity extends AppCompatActivity {

    int totalSelected = 0;
    EditText editView;
    TextView textView;
    MyListAdapterMy listadapter;
    ArrayList<MyListItem> listAllItems = new ArrayList<MyListItem>();
    ArrayList<MyListItem> listDispItems = new ArrayList<MyListItem>();
    ListView listView;
    Toolbar toolbar;
    Intent intent;
    private String root = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String CurPath = Environment.getExternalStorageDirectory().getPath()+"/Lecoder/";
    private ArrayList<String> itemFiles = new ArrayList<String>();
    private ArrayList<String> pathFiles = new ArrayList<String>();
    private final long FINISH_INTERVAL_TIME = 2000;
    private long   backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        toolbar = (Toolbar) findViewById(R.id.fileBrowserToobar);
        toolbar.setTitle("FileBrowser");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        textView = (TextView) findViewById(R.id.textView_debug);
        setupList();
        setupAdapter();
        setupFilter();
        intent=getIntent();
     /*   listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }

        });
        */

    }

    private void setupList() {
        listView = (ListView) findViewById(R.id.list1);
    }

    public class MyListItem {
        int selectedNumber;
        boolean checked;
        // for display
        String name;
        // for using ; path+name
        String path;
    }

    public class MyListAdapterMy extends MyArrayAdapter<MyListItem> {

        public MyListAdapterMy(Context context) {
            super(context, R.layout.item_file_browser);
            totalSelected = 0;
            setSource(listDispItems);
        }

        @Override
        public void bindView(View view, MyListItem item) {
            TextView name = (TextView) view.findViewById(R.id.name_saved);
            name.setText(item.name);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retView = super.getView(position, convertView, parent);
            final int pos = position;
            final View parView = retView;
            TextView name = (TextView) retView.findViewById(R.id.name_saved);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyListItem item = listDispItems.get(pos);
                    itemClick(item.name, item.path);
                }
            });

            return retView;
        }

        public void filter(String searchText) {
            listDispItems.clear();
            totalSelected = 0;
            for (int i = 0; i < listAllItems.size(); i++) {
                MyListItem item = listAllItems.get(i);
                item.checked = false;
                item.selectedNumber = 0;
            }
            if (searchText.length() == 0) {
                listDispItems.addAll(listAllItems);
            } else {
                for (MyListItem item : listAllItems) {
                    if (item.name.contains(searchText)) {
                        listDispItems.add(item);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }


    public class AdapterAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog mDlg;
        Context mContext;

        public AdapterAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDlg = new ProgressDialog(mContext);
            mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDlg.setMessage("loading");
            mDlg.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            // listAllItems MyListItem
            listAllItems.clear();
            listDispItems.clear();

            getDirInfo(CurPath);

            for (int i = 0; i < itemFiles.size(); i++) {
                MyListItem item = new MyListItem();
                item.checked = false;
                item.name = itemFiles.get(i);
                item.path = pathFiles.get(i);
                listAllItems.add(item);
            }

            if (listAllItems != null) {
                Collections.sort(listAllItems, nameComparator);
            }
            listDispItems.addAll(listAllItems);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mDlg.dismiss();
            listadapter = new MyListAdapterMy(mContext);
            listView.setAdapter(listadapter);

            String searchText = editView.getText().toString();
            if (listadapter != null) listadapter.filter(searchText);
            int index=CurPath.indexOf("Lecoder");
            String newPath=CurPath.substring(index);
            textView.setText("Location: " + newPath);
        }

        private final Comparator<MyListItem> nameComparator
                = new Comparator<MyListItem>() {
            public final int
            compare(MyListItem a, MyListItem b) {
                return collator.compare(a.name, b.name);
            }

            private final Collator collator = Collator.getInstance();
        };
    }

    private void setupAdapter() {
        AdapterAsyncTask adaterAsyncTask = new AdapterAsyncTask(FileBrowserActivity.this);
        adaterAsyncTask.execute();
    }

    private void setupFilter() {
        editView = (EditText) findViewById(R.id.savedfilter);
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = editView.getText().toString();
                if (listadapter != null) listadapter.filter(searchText);
            }
        });
    }

    private int getSelectedItemCount() {
        int checkcnt = 0;
        for (int i = 0; i < listDispItems.size(); i++) {
            MyListItem item = listDispItems.get(i);
            if (item.checked) checkcnt++;
        }
        return checkcnt;
    }

    private List<String> getSelectedItems() {
        List<String> ret = new ArrayList<String>();
        int count = 0;
        for (int i = 0; i < listDispItems.size(); i++) {
            MyListItem item = listDispItems.get(i);
            if (item.checked) {
                if (count < item.selectedNumber) {
                    count = item.selectedNumber;
                }
            }
        }
        for (int j = 1; j <= count; j++) {
            for (int i = 0; i < listDispItems.size(); i++) {
                MyListItem item = listDispItems.get(i);
                if (item.checked && item.selectedNumber == j) {
                    ret.add(item.name);
                }
            }
        }
        return ret;
    }

    private String getSelectedItem() {
        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < listDispItems.size(); i++) {
            MyListItem item = listDispItems.get(i);
            if (item.checked) {
                return item.name;
            }
        }
        return "";
    }

        //textView.setText(sb.toString());
        @Override
        public void onBackPressed() {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;
            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
            {
                super.onBackPressed();
            }
            else
            {
                backPressedTime = tempTime;
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(FileBrowserActivity.this);
                alert_confirm.setMessage("뒤로 가시겠습니까?").setCancelable(false).setPositiveButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              return;
                            }
                        }).setNegativeButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FileBrowserActivity.super.onBackPressed();
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();


            }



        }

    private void getDirInfo(String dirPath) {
        if (!dirPath.endsWith("/")) dirPath = dirPath + "/";
        File f = new File(dirPath);
        File[] files = f.listFiles();
        if (files == null) return;

        itemFiles.clear();
        pathFiles.clear();

        if (!root.endsWith("/")) root = root + "/";
        if (!root.equals(dirPath)) {
            if (!dirPath.endsWith("/Lecoder/")){
                itemFiles.add("...뒤로");
                pathFiles.add(f.getParent());
            }
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            pathFiles.add(file.getPath());

            if (file.isDirectory())
                itemFiles.add(file.getName());
            else
                itemFiles.add(file.getName());
        }
    }

    private void itemClick(String name, String path) {
        File file = new File(path);
        boolean isFastLecture=path.contains("Lecoder/빠른녹음");
        if (file.isDirectory()) {
            if (file.canRead()) {
                CurPath = path;
                setupAdapter();
            } else {
                if(file.getName().endsWith(".mp4")) {
                    playLecture(isFastLecture,path);
                }
            }
        } else {
            if(file.getName().endsWith(".mp4")) {
                playLecture(isFastLecture,path);
            }
        }
    }

    private void playLecture(boolean isFastLecture,String path) {

        int index=path.indexOf("Lecoder");
        String newPath=path.substring(index);
        String[] newArray=newPath.split("/");
        Intent intent=new Intent(getApplicationContext(),PlayActivity.class);
        String playClass=(isFastLecture?"":newArray[2]);
        String playName=(isFastLecture?newArray[2]:newArray[3]);
        String playType=newArray[1];
        String playDate=playName.substring(0,4)+"/"+playName.substring(4,6)+"/"+playName.substring(6,8);
        intent.putExtra("playClass",playClass);
        intent.putExtra("playType",playType);
        intent.putExtra("playDate",playDate);
        intent.putExtra("playName",playName);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.file_menu, menu);
        return true;
    }

}


