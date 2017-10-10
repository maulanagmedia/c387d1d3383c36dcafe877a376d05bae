package gmedia.net.id.psplocationfinder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gmedia.net.id.psplocationfinder.Adapter.PhotosAdapter;
import gmedia.net.id.psplocationfinder.Utils.ServerURL;

public class FormMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    private String TAG = "TAGMAP";
    private String kdCus = "";
    private String nmCus = "";
    private ItemValidation iv = new ItemValidation();

    // Location
    private double latitude, longitude;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private Location location;
    private final int REQUEST_PERMISSION_COARSE_LOCATION=2;
    private final int REQUEST_PERMISSION_FINE_LOCATION=3;
    public boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    private TextView tvNama, tvAddress, tvPoint;
    private Button btnSimpan;
    private String cityName = "", stateName = "", countryName = "";
    private FloatingActionButton fabUpload;

    //Upload Handler
    private static int RESULT_OK = -1;
    private static int PICK_IMAGE_REQUEST = 1212;
    private ImageUtils iu = new ImageUtils();
    private Bitmap bitmap;
    private List<Bitmap> photoList;
    private PhotosAdapter adapter;
    private RecyclerView rvPhotos;
    private LinearLayout llReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        initUI();
    }

    private void initUI() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fabUpload = (FloatingActionButton) findViewById(R.id.fab_upload);
        tvNama = (TextView) findViewById(R.id.tv_nama);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvPoint = (TextView) findViewById(R.id.tv_point);
        btnSimpan = (Button) findViewById(R.id.btn_simpan);
        rvPhotos = (RecyclerView) findViewById(R.id.rv_image);
        llReset = (LinearLayout) findViewById(R.id.ll_reset);

        photoList = new ArrayList<>();
        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        adapter = new PhotosAdapter(FormMapsActivity.this, photoList);
        rvPhotos.setLayoutManager(layoutManager);
        rvPhotos.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            kdCus = bundle.getString("kdcus");
            nmCus = bundle.getString("nama");
            tvNama.setText(nmCus);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            String latitudeString = bundle.getString("latitude");
            setCriteria();
            if(latitudeString.length()>0){
                String longitudeString = bundle.getString("longitude");
                latitude = iv.parseNullDouble(latitudeString);
                longitude = iv.parseNullDouble(longitudeString);
                location = new Location("set");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                onMapReady(mMap);

            }else{
                getLocation();
            }

            llReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getLocation();
                }
            });

            getImagesData();
        }

        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadChooserDialog();
            }
        });
    }

    private void getImagesData() {

        ApiVolley request = new ApiVolley(FormMapsActivity.this, new JSONObject(), "GET", ServerURL.getImages+kdCus, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");

                        for (int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            String image = jo.getString("image");
                            new AsyncGettingBitmapFromUrl().execute(image);

                        }
                    }

                } catch (JSONException e) {
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    public static  Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("downloadImage"+ e1.toString());
        }
        return bitmap;
    }

    public static  InputStream getHttpConnection(String urlString)  throws IOException {

        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("downloadImage" + ex.toString());
        }
        return stream;
    }

    private class AsyncGettingBitmapFromUrl extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... params) {

            System.out.println("doInBackground");

            Bitmap bitmap1 = null;

            bitmap1 = downloadImage(params[0]);
            photoList.add(bitmap1);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
            return bitmap1;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            System.out.println("bitmap" + bitmap);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            /*File file = new File(String.valueOf(filePath));
            long length = file.length();
            length = length/1024; //in KB*/

            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(
                        filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 70, stream);
            byte[] byteArray = stream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            bitmap = scaleDown(bitmap, 380, true);

            try {
                stream.close();
                stream = null;
            } catch (IOException e) {

                e.printStackTrace();
            }

            if(bitmap != null){

                photoList.add(bitmap);
                adapter.notifyDataSetChanged();
            }

        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoFromCameraURI));
                bitmap = scaleDown(bitmap, 380, true);

                if(bitmap != null){

                    photoList.add(bitmap);
                    adapter.notifyDataSetChanged();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if(googleMap != null){
            mMap = googleMap;

            // Add a marker
            if(location != null){
                mMap.clear();
                LatLng current = new LatLng(latitude, longitude);
                markerOptions = new MarkerOptions().position(current).draggable(true).title("Lokasi");
                mMap.addMarker(markerOptions);
                updateKeterangan(current);
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(current, 17f);
                mMap.moveCamera(cu);
                mMap.animateCamera(cu);
                mMap.getUiSettings().setZoomControlsEnabled(true);
            }

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    LatLng position = marker.getPosition();
                    updateKeterangan(position);
                    Log.d(TAG, "onMarkerDragEnd: " + position.latitude +" "+ position.longitude);
                }
            });

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    markerOptions = new MarkerOptions().position(latLng).draggable(true).title("Lokasi");
                    mMap.clear();
                    mMap.addMarker(markerOptions);
                    updateKeterangan(latLng);
                    Log.d(TAG, "onMarkerDragEnd: " + latLng.latitude +" "+ latLng.longitude);
                }
            });
        }
    }

    private void updateKeterangan(LatLng position){

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        latitude = position.latitude;
        longitude = position.longitude;

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addresses != null && addresses.size() > 0){
            cityName = addresses.get(0).getAddressLine(0);
            stateName = addresses.get(0).getAddressLine(1);
            countryName = addresses.get(0).getAddressLine(2);

            tvAddress.setText(cityName+", "+stateName + ", " +countryName);
        }

        tvPoint.setText(iv.doubleToStringFull(latitude)+" ; "+iv.doubleToStringFull(longitude));

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog = new AlertDialog.Builder(FormMapsActivity.this)
                        .setTitle("Konfirmasi")
                        .setMessage("Simpan posisi "+nmCus)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                simpanPosisi();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();

            }
        });
    }

    private ProgressDialog progressDialog;

    private void simpanPosisi() {

        progressDialog = new ProgressDialog(FormMapsActivity.this, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        JSONObject jData = new JSONObject();

        try {
            jData.put("kdcus", kdCus);
            jData.put("latitude", iv.doubleToStringFull(latitude));
            jData.put("longitude", iv.doubleToStringFull(longitude));
            jData.put("city", cityName);
            jData.put("state", stateName);
            jData.put("country", countryName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kdcus", kdCus);
            jBody.put("data", jData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(FormMapsActivity.this, jBody, "POST", ServerURL.saveLocation, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        if(photoList.size()> 0){

                            insertImagesToDB();
                        }else{
                            progressDialog.dismiss();
                            String message = response.getJSONObject("response").getString("message");
                            Toast.makeText(FormMapsActivity.this, message, Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(FormMapsActivity.this, "Terjadi kesalahan, mohon ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                progressDialog.dismiss();
                Toast.makeText(FormMapsActivity.this, "Terjadi kesalahan, mohon ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void insertImagesToDB() {

        JSONArray jData = new JSONArray();

        for (Bitmap item : photoList){

            JSONObject jo = new JSONObject();
            try {
                jo.put("image", ImageUtils.convert(item));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jData.put(jo);
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kdcus", kdCus);
            jBody.put("data", jData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(FormMapsActivity.this, jBody, "POST", ServerURL.saveImages, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        progressDialog.dismiss();
                        String message = response.getJSONObject("response").getString("message");
                        Toast.makeText(FormMapsActivity.this, message, Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(FormMapsActivity.this, "Terjadi kesalahan, mohon ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                progressDialog.dismiss();
                Toast.makeText(FormMapsActivity.this, "Terjadi kesalahan, mohon ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setCriteria() {
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        provider = locationManager.getBestProvider(criteria, true);
    }

    public Location getLocation() {
        try {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
                Toast.makeText(FormMapsActivity.this, "Cannot identify the location.\nPlease turn on GPS or turn on your data.",
                        Toast.LENGTH_LONG).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    location = null;

                    // Granted the permission first
                    if (ActivityCompat.checkSelfPermission(FormMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormMapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(FormMapsActivity.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale(FormMapsActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        }
                        return null;
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    location=null;
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                onLocationChanged(location);
                            }
                        }
                    }
                }else{
                    //Toast.makeText(context, "Turn on your GPS for better accuracy", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(location != null){
            onLocationChanged(location);
        }
        return location;
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FormMapsActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(FormMapsActivity.this,
                new String[]{permissionName}, permissionRequestCode);
    }

    //region Selected Order Menu
    private void loadChooserDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(FormMapsActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_chooser, null);
        builder.setView(view);

        final LinearLayout llBrowse= (LinearLayout) view.findViewById(R.id.ll_browse);
        final LinearLayout llCamera = (LinearLayout) view.findViewById(R.id.ll_camera);

        final AlertDialog alert = builder.create();

        llBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showFileChooser();
                alert.dismiss();
            }
        });

        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCamera();
                alert.dismiss();
            }
        });

        alert.show();
    }

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private String photoFromCameraURI;

    private void openCamera(){

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoFromCameraURI = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(FormMapsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        onMapReady(mMap);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

        onMapReady(mMap);
    }

    @Override
    public void onProviderEnabled(String s) {

        onMapReady(mMap);
    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
