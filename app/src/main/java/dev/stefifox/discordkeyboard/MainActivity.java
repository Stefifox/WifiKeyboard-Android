package dev.stefifox.discordkeyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText ip = findViewById(R.id.ipinput);
        final EditText port = findViewById(R.id.portinput);
        final Button connect = findViewById(R.id.connectbutton);
        final TextView status = findViewById(R.id.status);
        final LinearLayout buttonList = findViewById(R.id.buttonlist);

        status.setText("disconnected");
        status.setTextColor(getColor(R.color.disconnect));

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://" + ip.getText() + ":" + port.getText();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + "/connect", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            JSONArray buttons = new JSONArray(obj.getJSONObject("configs").getJSONArray("buttons").toString());
                            System.out.println(buttons.toString());
                            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                            statusC = true;
                            status.setText("connected");
                            status.setTextColor(getColor(R.color.connected));
                            buttonList.removeAllViews(); //Clear all views
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
                        Toast.makeText(MainActivity.this,"Connection error", Toast.LENGTH_SHORT).show();
                        statusC = false;
                        url = "http://";
                        status.setText("disconnected");
                        status.setTextColor(getColor(R.color.disconnect));
                    }
                });
                MySingleton.getInstance(MainActivity.this).addToRequestQueue(request);
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
                    Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                Toast.makeText(MainActivity.this,"Connection error", Toast.LENGTH_SHORT).show();
                statusC = false;
                status.setText("disconnected");
                status.setTextColor(getColor(R.color.disconnect));
            }
        });
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(request);
    }
}