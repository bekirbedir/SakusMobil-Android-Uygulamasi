package bekirbedir.sakusmobilapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class WebViewClockActivty extends AppCompatActivity implements AdapterView.OnItemClickListener {
    GridView gridView ;
    ArrayList<Otobus> busList = new ArrayList<Otobus>();
    String url = "https://sakus.sakarya.bel.tr/Sbb/SakusOtobusSaat?hatNo=";
    String busNumber = "";
    WebView webview;
    Button returnHomePage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        gridView = (GridView) findViewById(R.id.gridview);
        returnHomePage = (Button) findViewById(R.id.returnHomePage);
        webview = (WebView) findViewById(R.id.webviewClock);

         OtobusAdapter otobusAdapter = new OtobusAdapter(getApplicationContext());
        gridView.setAdapter(otobusAdapter);
        gridView.setOnItemClickListener(this);

        returnHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homePage = new Intent(getApplicationContext() , MapsActivity.class);
                homePage.putExtra("lastBusNumber",busNumber);
                startActivity(homePage);
            }
        });

        Intent myIntent = getIntent(); // gets the previously created intent
        busNumber = myIntent.getStringExtra("lastBusNumber");
        webViewRun(busNumber);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String currentBusNumber = (String) ( (TextView) view.findViewById(R.id.btnOtobus) ).getText();
        busNumber = currentBusNumber;
        webViewRun(busNumber);

    }

    public void webViewRun(String busNumber){
        webview.getSettings().setJavaScriptEnabled(true);
        String targetUrl = url + busNumber;
        webview.loadUrl(targetUrl);

        final ProgressDialog progress = ProgressDialog.show(this, "Hareket Saatleri", "Yükleniyor....", true);
        progress.show();
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String targetUrl) {
                super.onPageFinished(view, targetUrl);

                progress.dismiss();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Bir hata oluştu lütfe daha sonra yeniden deneyin.", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }

}
