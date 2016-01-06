package com.example.dhart.adbutlerjsontest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    //Replace this string with the URL provided in the JSON ad tag.
    final String url = "http://servedbyadbutler.com.dan.dev/adserve/;ID=107878;size=300x250;setID=86133;type=json;click=CLICK_MACRO_PLACEHOLDER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Display text
        TextView tv = (TextView) findViewById(R.id.textView);

        //Get the JSON response from the AdButler URL
        getJSONObject(new VolleyCallback() {

            //This method is called if there is a response
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //If the response was successful, place the ad on the page.
                    if (result.getString("status").equals("SUCCESS")) {
                        //Get the placement info from the JSON response, specifically the redirect URL and image URL
                        JSONObject placement = result.getJSONObject("placements").getJSONObject("placement_1");
                        String imgurl = placement.getString("image_url");
                        final String clickurl = placement.getString("redirect_url");

                        //Download the image to place in the app, and set it to redirect when clicked
                        ImageView img = (ImageView) findViewById(R.id.imageView);
                        new DownloadImageTask(img).execute(imgurl);
                        img.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                openWebURL(clickurl);
                            }
                        });
                    }

                }catch(JSONException e){
                    //Handle any JSON errors how you'd like
                    System.err.println(e);
                }
            }
        });

    }

    //Accepts a VolleyCallback function and performs the Volley URL request
    public void getJSONObject(final VolleyCallback callback){
        RequestQueue queue = Volley.newRequestQueue(this);
        //Define the request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){

            }

        });
        //Add the request to the Volley request queue
        queue.add(request);
    }

    //Redirect to the specified URL
    public void openWebURL(String url){
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browse);
    }

}

//This class handles the image download asynchronously
class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView image;

    public DownloadImageTask(ImageView bmImage) {
        this.image = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        image.setImageBitmap(result);
    }
}

//Defines VolleyCallback, used to perform actions on the JSONRequest response
interface VolleyCallback{
    void onSuccess(JSONObject result);
}