package gmedia.net.id.psplocationfinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.RuntimePermissionsActivity;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.psplocationfinder.Adapter.ListPelangganAdapter;
import gmedia.net.id.psplocationfinder.Utils.ServerURL;

public class MainActivity extends RuntimePermissionsActivity {

    private static final int REQUEST_PERMISSIONS = 20;

    private static boolean doubleBackToExitPressedOnce;
    private boolean exitState = false;
    private int timerClose = 2000;

    private EditText edtSearch;
    private ImageButton btnSearch;
    private ListView lvPelanggan;
    private ProgressBar pbLoading;
    private View footerList;
    private int startIndex = 0, count = 10;
    private String keyword = "";
    private List<CustomItem> listPelanggan;
    private boolean isLoading = false;
    private final String TAG = "MainAc";
    private ItemValidation iv = new ItemValidation();
    private boolean firstLoad = true;
    private ListPelangganAdapter adapter;
    private SessionManager session;
    private String kodeArea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            MainActivity.super.requestAppPermissions(new
                            String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_SETTINGS}, R.string
                            .runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }

        //Check close statement
        doubleBackToExitPressedOnce = false;
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getBoolean("exit", false)){
                exitState = true;
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        }

        initUI();
    }

    private void initUI() {

        edtSearch = (EditText) findViewById(R.id.edt_search);
        btnSearch = (ImageButton) findViewById(R.id.btn_serach);
        lvPelanggan = (ListView) findViewById(R.id.lv_pelanggan);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        session = new SessionManager(MainActivity.this);
        kodeArea = session.getUserInfo(SessionManager.TAG_KODE_AREA);
        isLoading = false;
        getDataPelanggan();

        lvPelanggan.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                if(absListView.getLastVisiblePosition() == i2-1 && lvPelanggan.getCount() > (count-1) && !isLoading ){
                    isLoading = true;
                    lvPelanggan.addFooterView(footerList);
                    startIndex += count;
                    getMoreData();
                    Log.i(TAG, "onScroll: last");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:

                loguoutUser();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loguoutUser(){

        Intent intent = new Intent(MainActivity.this, LoginScreen.class);
        session.logoutUser(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        edtSearch.setText("");
        getDataPelanggan();
    }

    private void getDataPelanggan() {

        listPelanggan = new ArrayList<>();
        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("kodearea", kodeArea);
            jBody.put("keyword", keyword);
            jBody.put("startindex", String.valueOf(startIndex));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(MainActivity.this, jBody, "POST", ServerURL.getLocation, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray items = response.getJSONArray("response");
                        for(int i  = 0; i < items.length(); i++){

                            JSONObject jo = items.getJSONObject(i);
                            listPelanggan.add(new CustomItem(jo.getString("kdcus"), jo.getString("nama"), jo.getString("alamat"), jo.getString("notelp"), jo.getString("nohp"), jo.getString("latitude"), jo.getString("longitude")));
                        }
                    }

                    final List<CustomItem> tableList = new ArrayList<>(listPelanggan);
                    getAutocompleteEvent(tableList);
                    getTableList(tableList);
                    pbLoading.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    getAutocompleteEvent(null);
                    getTableList(null);
                    pbLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String result) {

                getAutocompleteEvent(null);
                getTableList(null);
                pbLoading.setVisibility(View.GONE);
            }
        });
    }

    private void getMoreData() {

        final List<CustomItem> moreList = new ArrayList<>();
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("kodeArea", kodeArea);
            jBody.put("keyword", keyword);
            jBody.put("startindex", String.valueOf(startIndex));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(MainActivity.this, jBody, "POST", ServerURL.getLocation, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray items = response.getJSONArray("response");
                        for(int i  = 0; i < items.length(); i++){

                            JSONObject jo = items.getJSONObject(i);
                            moreList.add(new CustomItem(jo.getString("kdcus"), jo.getString("nama"), jo.getString("alamat"), jo.getString("notelp"), jo.getString("nohp"), jo.getString("latitude"), jo.getString("longitude")));
                        }
                    }
                    isLoading = false;
                    lvPelanggan.removeFooterView(footerList);
                    if(adapter != null) adapter.addMoreData(moreList);
                } catch (JSONException e) {
                    e.printStackTrace();
                    isLoading = false;
                    lvPelanggan.removeFooterView(footerList);
                }
            }

            @Override
            public void onError(String result) {
                isLoading = false;
                lvPelanggan.removeFooterView(footerList);
            }
        });
    }

    private void getAutocompleteEvent(final List<CustomItem> tableList) {

        if(firstLoad){
            firstLoad = false;

            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if(edtSearch.getText().length() == 0){

                        keyword = "";
                        startIndex = 0;
                        getDataPelanggan();
                    }
                }
            });
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                keyword = edtSearch.getText().toString();
                startIndex = 0;
                getDataPelanggan();

                iv.hideSoftKey(MainActivity.this);
            }
        });
    }

    private void getTableList(List<CustomItem> tableList) {

        lvPelanggan.setAdapter(null);

        if(tableList != null && tableList.size() > 0){

            adapter = new ListPelangganAdapter(MainActivity.this, tableList);
            lvPelanggan.setAdapter(adapter);

            lvPelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomItem selectedItem = (CustomItem) adapterView.getItemAtPosition(i);

                    Intent intent = new Intent(MainActivity.this, FormMapsActivity.class);
                    intent.putExtra("kdcus", selectedItem.getItem1());
                    intent.putExtra("nama", selectedItem.getItem2());
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {


    }

    @Override
    public void onBackPressed() {

        // Origin backstage
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra("exit", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //System.exit(0);
        }

        if(!exitState && !doubleBackToExitPressedOnce){
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.app_exit), Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, timerClose);
    }
}
