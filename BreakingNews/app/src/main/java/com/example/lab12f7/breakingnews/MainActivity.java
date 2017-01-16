package com.example.lab12f7.breakingnews;

import android.location.Location;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    ListView newsListView;
    ArrayAdapter<News> aa;
    ArrayList<News> newsList = new ArrayList<News>();
    private static final String TAG = "BREAKINGNEWS";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        newsListView = (ListView)this.findViewById(R.id.list);
        int layoutID = android.R.layout.simple_list_item_1;
        aa = new ArrayAdapter<News>(this, layoutID , newsList);
        newsListView.setAdapter(aa);
        refreshNews();


        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                String title = newsList.get(position).getDetails();
                String msg = newsList.get(position).getDes();
                adb.setTitle(title);
                adb.setMessage(msg);
                adb.setPositiveButton("Ok", null);
                adb.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                refreshNews();
                break;
            default: return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void refreshNews(){
        // Get the XML
        URL url;
        try {
            String quakeFeed = getString(R.string.news_feed);
            url = new URL(quakeFeed);
            URLConnection connection;
            connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Parse the earthquake feed.
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();
                // Clear the old earthquakes
                newsList.clear();
                // Get a list of each earthquake entry.
                NodeList nl = docEle.getElementsByTagName("item");
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0 ; i < nl.getLength(); i++) {
                        Element item = (Element)nl.item(i);
                        Element title = (Element) item.getElementsByTagName("title").item(0);
                        Element de = (Element) item.getElementsByTagName("description").item(0);


                        String details = title.getFirstChild().getNodeValue();
                        String des = de.getFirstChild().getNodeValue();

                        News news = new News(details, des);
                        addNewQuake(news);
                    }
                }
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "MalformedURLException", e);
        } catch (IOException e) {
            Log.d(TAG, "IOException", e);
        } catch (ParserConfigurationException e) {
            Log.d(TAG, "Parser Configuration Exception", e);
        } catch (SAXException e) {
            Log.d(TAG, "SAX Exception", e);
        }
        finally {
        }
    }

    private void addNewQuake(News news) {
        newsList.add(news); // Add the new quake to our list of earthquakes.
        aa.notifyDataSetChanged(); // Notify the array adapter of a change.
    }
}
