package com.lecoder.team9.lecoder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;

import java.io.File;
import java.text.Collator;
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
        setSupportActionBar(toolbar);
        textView = (TextView) findViewById(R.id.textView_debug);
        setupList();
        setupAdapter();
        setupFilter();
        setupPermission();


     /*   listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }

        });
        */

    }

    private void setupPermission() {
        //check for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //ask for permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        setupAdapter();
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
            CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox_saved);
            cb.setChecked(item.checked);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retView = super.getView(position, convertView, parent);
            final int pos = position;
            final View parView = retView;
            CheckBox cb = (CheckBox) retView.findViewById(R.id.checkBox_saved);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyListItem item = listDispItems.get(pos);
                    item.checked = !item.checked;
                    if (item.checked) totalSelected++;
                    item.selectedNumber = totalSelected;
                    Toast.makeText(FileBrowserActivity.this, "1: Click " + pos + "th " + item.checked + " " + totalSelected, Toast.LENGTH_SHORT).show();
                    printDebug();
                }
            });
            TextView name = (TextView) retView.findViewById(R.id.name_saved);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyListItem item = listDispItems.get(pos);
                    //item.checked = !item.checked;
                    //if( item.checked ) totalSelected++;
                    //item.selectedNumber=totalSelected;
                    //Toast.makeText(MainActivity.this,"2: Click "+pos+ "th " + item.checked + " "+totalSelected,Toast.LENGTH_SHORT).show();
                    //printDebug();
                    //bindView(parView, item);
                    itemClick(item.name, item.path);
                }
            });

            return retView;
        }

        public void fillter(String searchText) {
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
            if (listadapter != null) listadapter.fillter(searchText);

            textView.setText("Location: " + CurPath);
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
                if (listadapter != null) listadapter.fillter(searchText);
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

    private void printDebug() {
        StringBuilder sb = new StringBuilder();
        sb.append("Count:" + getSelectedItemCount() + "\n");
        sb.append("getSelectedItem:" + getSelectedItem() + "\n");
        sb.append("getSelectedItems:");
        List<String> data = getSelectedItems();
        for (int i = 0; i < data.size(); i++) {
            String item = data.get(i);
            sb.append(item + ",");
        }
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
                alert_confirm.setMessage("프로그램을 종료 하시겠습니까?").setCancelable(false).setPositiveButton("취소",
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
            itemFiles.add(".");
            pathFiles.add(f.getParent());
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
        if (file.isDirectory()) {
            if (file.canRead()) {
                CurPath = path;
                setupAdapter();
            } else {
                if(file.getName().endsWith(".mp3") || file.getName().endsWith(".mp4") || file.getName().endsWith(".m4a") || file.getName().endsWith(".3gp") || file.getName().endsWith(".wav") || file.getName().endsWith(".ogg"))
                {
                    Intent music = new Intent();
                    music.setAction(Intent.ACTION_VIEW);
                    music.setDataAndType(
                        FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file)
                        , "audio/*");
                    music.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(music);
                } else if(file.getName().endsWith(".txt")){
                    Intent text = new Intent();
                    text.setAction(Intent.ACTION_VIEW);
                    text.setDataAndType(
                            FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file)
                            , "text/*");
                    text.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(text);
                } else if(file.getName().endsWith(".jpg") || file.getName().endsWith(".gif") || file.getName().endsWith(".png") || file.getName().endsWith(".bmp"))
                {
                    Intent image = new Intent();
                    image.setAction(Intent.ACTION_VIEW);
                    image.setDataAndType(
                            FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file)
                            , "image/*");
                    image.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(image);
                } else if (file.getName().endsWith(".pdf") || file.getName().endsWith(".ppt")){
                    Intent pdfandppt = new Intent();
                    pdfandppt.setAction(Intent.ACTION_VIEW);
                    pdfandppt.setDataAndType(
                            FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file)
                            , "application/*");
                    pdfandppt.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(pdfandppt);
                } else {
                    Toast.makeText(getApplicationContext(), "열 수 없는 파일 형식입니다.", Toast.LENGTH_LONG).show();
                }

            }
        } else {
            if(file.getName().endsWith(".mp3") || file.getName().endsWith(".mp4") || file.getName().endsWith(".m4a") || file.getName().endsWith(".3gp") || file.getName().endsWith(".wav") || file.getName().endsWith(".ogg"))
            {
                Intent music = new Intent();
                music.setAction(Intent.ACTION_VIEW);
                music.setDataAndType(
                        FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file)
                        , "audio/*");
                music.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(music);
            } else if(file.getName().endsWith(".txt")){
                Intent text = new Intent();
                text.setAction(Intent.ACTION_VIEW);
                text.setDataAndType(
                        FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file)
                        , "text/*");
                text.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(text);
            } else if(file.getName().endsWith(".jpg") || file.getName().endsWith(".gif") || file.getName().endsWith(".png") || file.getName().endsWith(".bmp"))
            {
                Intent image = new Intent();
                image.setAction(Intent.ACTION_VIEW);
                image.setDataAndType(
                        FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file)
                        , "image/*");
                image.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(image);
            } else if (file.getName().endsWith(".pdf") || file.getName().endsWith(".ppt")){
                Intent pdfandppt = new Intent();
                pdfandppt.setAction(Intent.ACTION_VIEW);
                pdfandppt.setDataAndType(
                        FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file)
                        , "application/*");
                pdfandppt.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(pdfandppt);
            } else {
                Toast.makeText(getApplicationContext(), "열 수 없는 파일 형식입니다.", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.file_menu, menu);
        return true;
    }

}


