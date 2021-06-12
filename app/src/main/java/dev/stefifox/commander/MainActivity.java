package dev.stefifox.commander;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static String url = "http://";
    public static boolean statusC = false;
    public static int serverVersionCode = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText ip = findViewById(R.id.ipinput);
        final EditText port = findViewById(R.id.portinput);
        final Button connect = findViewById(R.id.connectbutton);
        final TextView status = findViewById(R.id.status);
        final TextView footer = findViewById(R.id.madeby);
        final LinearLayout buttonList = findViewById(R.id.buttonlist);
        final ImageView infob = findViewById(R.id.infobutton);

        footer.setText(footer.getText() + " - V. " + BuildConfig.VERSION_NAME);

        if(!url.equals(loadIp())){
            url = loadIp();
            String[] temp = url.split(":");
            //System.out.println(temp[1].substring(2) + " " + temp[2]);
            connectRequest(temp[1].substring(2), temp[2]);
            ip.setText(temp[1].substring(2));
            port.setText(temp[2]);
        }

        status.setText(getText(R.string.disc));
        status.setTextColor(getColor(R.color.disconnect));

        infob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/Stefifox/WifiKeyboard-Android/blob/main/README.md";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectRequest("" + ip.getText(), "" + port.getText());
            }
        });
    }

    private void request(String id){
        final TextView status = findViewById(R.id.status);
        String reqUrl = url + "/key?id=" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, reqUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    Toast.makeText(MainActivity.this, getText(R.string.done), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                Toast.makeText(MainActivity.this,getText(R.string.c_error), Toast.LENGTH_SHORT).show();
                statusC = false;
                status.setText(getText(R.string.disc));
                status.setTextColor(getColor(R.color.disconnect));
            }
        });
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(request);
    }

    private void connectRequest(String ip, String port){
        //Declaring object
        final TextView status = findViewById(R.id.status);
        final LinearLayout buttonList = findViewById(R.id.buttonlist);
        //Setting URL
        url = "http://" + ip+ ":" + port;
        //Setting the request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + "/connect", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    JSONArray buttons = new JSONArray(obj.getJSONObject("configs").getJSONArray("buttons").toString());
                    System.out.println(buttons.toString());
                    Toast.makeText(MainActivity.this, getText(R.string.connected), Toast.LENGTH_SHORT).show();
                    if(obj.getInt("version_code") > serverVersionCode){
                        status.setText(getText(R.string.lowerversion));
                        status.setTextColor(getColor(R.color.disconnect));
                        return;
                    }
                    if(obj.getInt("version_code") < serverVersionCode){
                        status.setText(getText(R.string.lowerserver));
                        status.setTextColor(getColor(R.color.disconnect));
                        return;
                    }

                    statusC = true;
                    status.setText(getText(R.string.connected));
                    status.setTextColor(getColor(R.color.connected));
                    buttonList.removeAllViews(); //Clear all views
                    save(url);
                    for(int i = 0; i < buttons.length(); i++){
                        JSONObject temp = buttons.getJSONObject(i);
                        Button btn = new Button(MainActivity.this); //Making a button
                        btn.setText(temp.getString("name")); //Setting Text of button
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    request(String.valueOf(temp.getInt("id"))); //Adding the request
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        buttonList.addView(btn); //Add button on view
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                Toast.makeText(MainActivity.this,getText(R.string.c_error), Toast.LENGTH_SHORT).show();
                statusC = false;
                url = "http://";
                save(url);
                status.setText(getText(R.string.disc));
                status.setTextColor(getColor(R.color.disconnect));
            }
        });
        //Adding request to queue
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(request);
    }

    private String loadIp(){
        SharedPreferences load = getSharedPreferences("ip", MODE_PRIVATE);
        String url = load.getString("ip_", this.url);
        return url;
    }

    private void save (String value){
        SharedPreferences save = getSharedPreferences("ip", MODE_PRIVATE);
        SharedPreferences.Editor saveEdit = save.edit();
        saveEdit.putString("ip_", value);
        saveEdit.apply();
    }

}