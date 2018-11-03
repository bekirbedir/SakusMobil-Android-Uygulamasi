package bekirbedir.sakusmobilapp;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener {
    MyFirebaseMessagingService myFirebaseMessagingService;
    private GoogleMap mMap;
    public List<LatLng> polyLineList = new ArrayList<>();
    private PolylineOptions polyLineOptions, blackPolyLineOptions;
    private Polyline blackPolyline, greyPolyline;
    private String customColor = "#E9EF45";
    Marker currentMarker, currentMarker2, currentMarker3, currentMarker4 ,currentMarker5 = null;
    String url, urlKonum ,  lastBusNumber , currentBusNumber= "" ;
    TextView tvTopText;
    Handler handler = new Handler();
    Button btnLoadMore, btnFavorite , btnBusClock;
    DBHelper dbHelper;
    GridView gridView, gridViewFavorite;
    ArrayList<Otobus> favoriteBusList = new ArrayList<Otobus>();

    InterstitialAd mInterstitialAd;
    int reklamSayisi = 0;
    SharedPreferences bilgiler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //------------db----------------------

        dbHelper = new DBHelper(getApplicationContext());
        //------------db----------------------



        //--------------admob------------------
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("admob-key-gelecek");
        bilgiler= getSharedPreferences("bilgiler",MODE_PRIVATE);
        reklamSayisi = bilgiler.getInt("reklam",0);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
                super.onAdLoaded();
            }
        });
        //--------------admob------------------


        //-----------component----------------
        final Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        final Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        gridView = (GridView) findViewById(R.id.gridview);
        gridViewFavorite = (GridView) findViewById(R.id.gridview_fav);
        BoomMenuButton bmb = (BoomMenuButton) findViewById(R.id.bmb);

        tvTopText = (TextView) findViewById(R.id.tvTopText);
        btnLoadMore = (Button) findViewById(R.id.btnLoadMore);
        btnFavorite = (Button) findViewById(R.id.btnFavorite);
        btnBusClock = (Button) findViewById(R.id.btnBusClock);
        //-----------component----------------

        btnBusClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webViewClockActivity = new Intent(getApplicationContext() , WebViewClockActivty.class);
                if( currentBusNumber.equals("")){
                    webViewClockActivity.putExtra("lastBusNumber",lastBusNumber);
                }
                else{
                    webViewClockActivity.putExtra("lastBusNumber",currentBusNumber);
                }

                startActivity(webViewClockActivity);
            }
        });


        //-------favorite bus List-----------
        favoriteBusList = (ArrayList<Otobus>) dbHelper.getFavoriteBusList();

        //-------favorite bus List-----------







        final FavoriteBus favoriteBus = new FavoriteBus(getApplicationContext(),favoriteBusList);
        gridViewFavorite.setAdapter(favoriteBus);
        gridViewFavorite.setOnItemClickListener(this);
        gridViewFavorite.setVisibility(View.INVISIBLE);
        //-------favorite bus-----------
        //----------All Bus-------------------//
        final OtobusAdapter otobusAdapter = new OtobusAdapter(getApplicationContext());
        gridView.setAdapter(otobusAdapter);
        gridView.setOnItemClickListener(this);
        //----------All Bus-------------------//

        //shared
        /*
        SharedPreferences sharedpreferences = getSharedPreferences("lastBus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("key", "value");
        editor.commit();
        */


        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_2);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_2);

        final String [] mapTypes= {  "Normal" , "Uydu"};

        for (int i = 0; i < mapTypes.length; i++) {
            HamButton.Builder builder = new HamButton.Builder()
                   .normalText( mapTypes[i] + " harita görünümü kullan  ")
                    .subNormalText("Değiştirmek için tıklayın")
                    .listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    Toast.makeText(MapsActivity.this, mapTypes [index] + " görünümü.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor yazici = bilgiler.edit();
                    yazici.putInt("mapType",index);
                    yazici.commit();
                    roadDraw(url);
                    switch (index)
                    {
                        case 0:
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                        case 1:
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            break;
                        case 2:
                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            break;
                        default:
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                    }

                }
            });

            if(i==0)
            { builder.normalImageRes(R.drawable.if_redbird_72184); }
            else{ builder.normalImageRes(R.drawable.if_yellowbird_72181); }

            bmb.addBuilder(builder);

        }





        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if( gridView.getVisibility() == View.VISIBLE )
               {
                   gridView.startAnimation(slideDown);
                   gridView.setVisibility(View.INVISIBLE);
               }
               else{
                   gridViewFavorite.startAnimation(slideDown);
                   gridViewFavorite.setVisibility(View.INVISIBLE);
                   gridView.startAnimation(slideUp);
                   gridView.setVisibility(View.VISIBLE);
               }

            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( gridViewFavorite.getVisibility() == View.VISIBLE )
                {
                    gridViewFavorite.startAnimation(slideDown);
                    gridViewFavorite.setVisibility(View.INVISIBLE);
                }
                else{

                    gridView.startAnimation(slideDown);
                    gridView.setVisibility(View.INVISIBLE);
                    gridViewFavorite.startAnimation(slideUp);
                    gridViewFavorite.setVisibility(View.VISIBLE);
                }
            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng startpos = new LatLng( 40.7695533, 30.3595176);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startpos));

        int mapType  = bilgiler.getInt("mapType",1);
        switch (mapType)
        {
            case 0:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }


        lastBusNumber = dbHelper.getLastBus();
        //-----------Location------------------
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

               Boolean gpsQuestion = bilgiler.getBoolean("gpsQuestion",true);
                if(gpsQuestion){ buildAlertMessageNoGps(); }
            }
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER ))
            {
                    mMap.setMyLocationEnabled(true);

            }


        }

        //----------Location-------------------

        if (lastBusNumber != "" || lastBusNumber != null)
        {
            startAppBusRoadDraw(lastBusNumber);
        }


    }
    private void buildAlertMessageNoGps() {
        Boolean gpsQuestion = bilgiler.getBoolean("gpsQuestion",true);

        SharedPreferences.Editor yazici = bilgiler.edit();
        yazici.putBoolean("gpsQuestion",false);
        yazici.commit();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Cihazınızın konum özelliği açık ise ve uygulamaya konum izni verirseniz haritada yerinizi görebilirsiniz.Bu izni vermediğiniz durumda yine normal şekilde çalışmaya devam eder. Konum özelliğini açmak İster misiniz?")
                .setCancelable(false)
                .setPositiveButton("Evet.", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Hayır.", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private void roadDraw(String url)
    {
        Log.d("burasi", "roaddrawww");

        handler.removeCallbacks(runnableCode);
        mMap.clear();
        polyLineList.clear();
        handler.post(runnableCode);
        selectPolylineColor();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            Log.d("response: " ,response.toString());
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("features");
                            Log.d("jsonArray: " ,jsonArray.toString());
                            JSONObject jsonObject2 =  jsonArray.getJSONObject(0);

                            JSONObject jsonObject3 = jsonObject2.getJSONObject("geometry");

                            JSONArray jsonArray2 = jsonObject3.getJSONArray("coordinates");


                       //     Toast.makeText(getApplicationContext(), String.valueOf( jsonArray2.length()), Toast.LENGTH_SHORT).show();
                            for(int i = 0 ; i < jsonArray2.length() ; i++ ) {

                                JSONArray jsonArray4 = jsonArray2.getJSONArray(i);
                                polyLineList.add(new LatLng( jsonArray4.getDouble(1), jsonArray4.getDouble(0)));
                            }

                            ////////////////////////////////////////
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for(LatLng latlng:polyLineList)
                                builder.include(latlng);
                            LatLngBounds bounds = builder.build();


                            polyLineOptions = new PolylineOptions();
                            polyLineOptions.color(Color.GRAY);
                            polyLineOptions.width(5);
                            polyLineOptions.startCap(new SquareCap());
                            polyLineOptions.endCap(new SquareCap());
                            polyLineOptions.jointType(JointType.ROUND);
                            polyLineOptions.addAll(polyLineList);
                            greyPolyline = mMap.addPolyline(polyLineOptions);


                            blackPolyLineOptions = new PolylineOptions();
                            blackPolyLineOptions.color(Color.parseColor(customColor));
                            blackPolyLineOptions.width(7);
                            blackPolyLineOptions.startCap(new SquareCap());
                            blackPolyLineOptions.endCap(new SquareCap());
                            blackPolyLineOptions.jointType(JointType.ROUND);
                            blackPolyline = mMap.addPolyline(blackPolyLineOptions);


                            //Animation
                            ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0,100);
                            polyLineAnimator.setDuration(1000);
                            polyLineAnimator.setInterpolator(new LinearInterpolator());
                            polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    List<LatLng> points = greyPolyline.getPoints();
                                    int percentValue = (int) animation.getAnimatedValue();
                                    int size = points.size();
                                    int newPoints = (int) (size * (percentValue/100.0f));

                                    List<LatLng> p = points.subList(0 , newPoints);
                                    blackPolyline.setPoints(p);
                                }
                            });
                            polyLineAnimator.start();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), -135));
                          //  mMap.animateCamera(CameraUpdateFactory.zoomIn());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int x = 5;
                Log.d("error response: " , error.toString());
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.

        queue.add(stringRequest);





    }

    private void selectPolylineColor() {
        int mapType  = bilgiler.getInt("mapType",3);
        switch (mapType)
        {
            case 0:
                customColor ="#2e1b6d";
                break;
            case 1:
                customColor = "#E9EF45";
                break;
            case 2:
                customColor ="#2e1b6d";
                break;
            default:
                customColor = "#E9EF45";
                break;
        }
    }

    @Override
    public void onPause(){
        handler.removeCallbacks(runnableCode);
        super.onPause();

    }
    @Override
    protected void onStop()
    {
        handler.removeCallbacks(runnableCode);
        super.onStop();

    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            currentBusPosition(urlKonum);
            handler.postDelayed(this, 1200);
        }
    };


    private void currentBusPosition(String urlKonum)
    {

        //-------------------konum getir-----------------------------
        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlKonum,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("features");

                            for(int i = 0 ; i < jsonArray.length() ; i++)
                            {
                                JSONObject jsonObjectBusFinal = jsonArray.getJSONObject(i);
                                jsonFeaturesParse(jsonObjectBusFinal , i+1);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });

        queue.add(stringRequest);
        //------------------konum getir------------------------------
    }


    public void MarkerChangePosition()
    {


    }
    public void jsonFeaturesParse(JSONObject jsonObject , int busNumber)
    {
        if( currentMarker5 != null && busNumber ==5 )
        {
            currentMarker5.remove();
        }
        if( currentMarker4 != null && busNumber ==4 )
        {
            currentMarker4.remove();
        }
        if(currentMarker3 != null && busNumber ==3 )
        {
            currentMarker3.remove();
        }
        if(currentMarker2 != null && busNumber ==2)
        {
            currentMarker2.remove();
        }
        if(currentMarker != null && busNumber ==1)
        {
            currentMarker.remove();
        }
        try
        {
            JSONObject jsonObject3 = jsonObject.getJSONObject("geometry");
            JSONArray jsonArray2 = jsonObject3.getJSONArray("coordinates");
            int degree = jsonObject.getJSONObject("properties").getInt("naci");

            LatLng sydney = new LatLng(jsonArray2.getDouble(1), jsonArray2.getDouble(0));

            MarkerOptions markerOptions = new MarkerOptions()
                    .title("burada")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus0_180))
                    .flat(true).anchor(0.5f,0.5f).rotation(degree)
                    .position(sydney);

            if(busNumber == 1) {
               currentMarker = mMap.addMarker(markerOptions);
                currentMarker.setPosition(sydney);
            }
            if(busNumber == 2) {
                currentMarker2 = mMap.addMarker(markerOptions);
                currentMarker2.setPosition(sydney);
            }
            if(busNumber == 3) {
                currentMarker3 = mMap.addMarker(markerOptions);
                currentMarker3.setPosition(sydney);
            }
            if(busNumber == 4) {
                currentMarker4 = mMap.addMarker(markerOptions);
                currentMarker4.setPosition(sydney);
            }
            if(busNumber == 5) {
                currentMarker5 = mMap.addMarker(markerOptions);
                currentMarker5.setPosition(sydney);
            }
        }
        catch (Exception ex)
        {

        }
    }
    public void LastRoadDraw()
    {

    }
    public void startAppBusRoadDraw(String currentBusNumber){
        urlKonum = "https://sakus.sakarya.bel.tr/Sbb/OtobusKonum/" + currentBusNumber;
        url = "https://sakus.sakarya.bel.tr/Sbb/HatCizimKonum/"+ currentBusNumber;
        tvTopText.setText("Son Seçilen Otobüs : " + String.valueOf(currentBusNumber) );
        roadDraw(url);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        reklamSayisi = bilgiler.getInt("reklam",0);
        reklamSayisi++;
        SharedPreferences.Editor yazici = bilgiler.edit();
        yazici.putInt("reklam",reklamSayisi);
        yazici.commit();
        if(reklamSayisi>6) {
            reklamGetir();
            yazici = bilgiler.edit();
            yazici.putInt("reklam",0);
            yazici.commit();
        }
        currentBusNumber = (String) ( (TextView) view.findViewById(R.id.btnOtobus) ).getText();
        favoriteBusList.add(new Otobus(currentBusNumber));
        dbHelper.insertBus( currentBusNumber );

        urlKonum = "https://sakus.sakarya.bel.tr/Sbb/OtobusKonum/" + currentBusNumber;
        url = "https://sakus.sakarya.bel.tr/Sbb/HatCizimKonum/"+ currentBusNumber;
        tvTopText.setText(busRouteName (String.valueOf(currentBusNumber) ));
        roadDraw(url);
     //   Toast.makeText(this, currentBusNumber, Toast.LENGTH_SHORT).show();

    }
    private void reklamGetir() {
    //   Toast.makeText(this, "5 tıklama oldu burada reklam gelecek daha sonra", Toast.LENGTH_SHORT).show();
       AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("17F49C199F30C48B351536E7526C8E23")
                .build();

        mInterstitialAd.loadAd(adRequest);



    }

    private String busRouteName(String busNumber){
        String result = "";
        Log.d("busNumber" , busNumber);
        switch (busNumber){
            case "1" : result = "1 - SAKARYA PARK - KÜPÇÜLER-K.TERMİNAL" ; break;
            case "2" : result = "2 - GAR MEYDANI - HIZIR İLYAS" ; break;
            case "3" : result = "3 - KENT MEY. - YEŞİLTEPE - YENİ TERMİNAL" ; break;
            case "4" : result = "4 - KAMPÜS – ET BALIK" ; break;
            case "5" : result = "5 - GAR MEY.- DÖRTYOL - BEŞKÖPRÜ - KAMPÜS" ; break;
            case "6" : result = "6 - GAR MEY.- 32 EVLER - BEŞKÖPRÜ - KAMPÜS" ; break;
            case "7" : result = "7 - KENT MEY. - YILDIZTEPE - YENİ TERMİNAL" ; break;
            case "9-A" : result = "9-A - MALTEPE -OZANLAR- YAZLIK" ; break;
            case "9-B" : result = "9-B - MALTEPE -ŞEKER- YAZLIK" ; break;
            case "12" : result = "12 - YAZLIK - KAMPÜS - ESENTEPE" ; break;
            case "14" : result = "14 - GAR MEY. - ULU S. - LOJMANLAR - B.EVLER" ; break;
            case "15" : result = "15 - KAMPÜS - YENİ CAMİİ. - YENİ STAD" ; break;
            case "17" : result = "17 - GÜNEŞLER - ADATIP HAST. - ŞİMŞEK EVLER" ; break;
            case "18" : result = "18 - ZİRAİ ALET. SAN.-ADATIP HAST.-YILDIZ" ; break;
            case "19" : result = "19 - OFİS GARAJ- SERDİVAN - ÇARK TURNİKE" ; break;
            case "19-K" : result = "19-K - SAKARYA PARK-HACIOĞLU-KAMPÜS" ; break;
            case "20" : result = "20 - KENT MEY. - ALTINOVA - 32 EVLER" ; break;
            case "20-A" : result = "20-A - KENT MEY.- SERDİVAN - BEŞKÖPRÜ" ; break;
            case "21-A" : result = "21-A - KAMPÜS - KARAMAN EKSPRES" ; break;
            case "21-B" : result = "21-B - KARAMAN - KAMPÜS EKSPRES" ; break;

            case "21-K" : result = "21-B - KARAMAN - KAMPÜS EKSPRES" ; break;
            case "21-C" : result = "21-C - UNKAPANI-S.ZAİM BULVARI-KARAMANS" ; break;
            case "21-D" : result = "21-D - UNKAPANI (671 SK)-İKİZCE GİRİŞİ-KARAMAN" ; break;

            case "22-A" : result = "22-A - KAMPÜS - CAMİLİ 1 EKSPRES" ; break;
           case "22-B" : result = "22-B - CAMİLİ 1 - KAMPÜS EKSPRES" ; break;
            case "22-K" : result = "22-K - CAMİLİ 1/2 - KAMPÜS EKSPRES" ; break;
            case "22-C" : result = "22-C - O.GARAJ-UNKAPANI-OZANLAR-CAMİLİ 1" ; break;
            case "22-D" : result = "22-D - O.GARAJ-S.KİRTETEPE CAD.-CAMİLİ 1" ; break;

            case "23" : result = "23 - OFİS GARAJ-CAMİLİ 2" ; break;
            case "24" : result = "24 - OFİS GARAJ-TOKİ-KORUCUK" ; break;
            case "25" : result = "25 - BAYTUR İDEALKENT - OFİS GARAJ" ; break;
            case "24-H" : result = "24-H - SAKARYA ARŞ.H.- KORUCUK ARŞ.H" ; break;
            case "24-K" : result = "24-K - KORUCUK - KAMPÜS" ; break;
            case "26" : result = "26 - KENT MEYDANI - ALTINOVA H.-KAMPÜS" ; break;
            case "27" : result = "27 - OFİS GARAJ- ATSO EVLERİ-KAMPÜS" ; break;
            case "28" : result = "28 - KUZEY TERMİNAL - YENİ TERMİNAL" ; break;
            case "29" : result = "29 - YENİ TERMİNAL - KAMPÜS" ; break;
            case "54-K" : result = "54-K - K.TERMİNAL-Y.TERMİNAL-C.TOPEL HAVALİMANI" ; break;
            default: result = busNumber ; break;

        }
        Log.d("tag" , result);
        return result;
    }



}
