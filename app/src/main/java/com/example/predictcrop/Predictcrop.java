package com.example.predictcrop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Predictcrop extends AppCompatActivity {
    Button three;
    EditText edt1;
    private static final String url = "jdbc:mysql://192.168.43.196:3306/smart_farming_db";
    private static final String user = "sagar";
    private static final String pass = "sagar";
    public String ph_data = "";
    public int y;
    public float z;
    public double a;
    public float b;
    public String soil_moisture = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictcrop);
        testDB_phmoisture();
        testDB();
    }

    public void testDB_phmoisture() {
        TextView tv3 = this.findViewById(R.id.txt3);
       // TextView tv4 = this.findViewById(R.id.txt4);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Bundle bundle = getIntent().getExtras();
            String ph_data = bundle.getString("p");
            String[] separated = ph_data.split(":");
            tv3.setText("ph : " + separated[1]);
            //tv4.setText("soil-moisture : "+separated[0]);
        } catch (Exception e) {
            e.printStackTrace();
            tv3.setText(e.toString());
            //tv4.setText(e.toString());
        }
    }
    public void testDB() {
        final TextView  tv5 = this.findViewById(R.id.txt5);
        three = findViewById(R.id.but3);
        three.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                edt1 = findViewById(R.id.edttxt1);
                String edt1_s= edt1.getText().toString();
                Bundle bundle = getIntent().getExtras();
                String ph_data = bundle.getString("p");
                String[] separated = ph_data.split(":");
                /*string ph_data=5;*/
                /*System.out.print(separated);*/
                float ph_data_f = Float.parseFloat(separated[1]);
                y =  (int)ph_data_f;
                z=ph_data_f - y;
                a=ph_data_f - z;
                b=ph_data_f + (1-z);
                if (z>0.5){
                    a = ph_data_f-(z-0.5);
                    b=ph_data_f + (1-z);
                }
                else{
                    a=ph_data_f - z;
                    b= (float) (ph_data_f + (0.5-z));
                }

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection(url, user, pass);

                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Database Connection Success",
                            Toast.LENGTH_SHORT);
                    toast.show();

                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("select distinct crop_name from prediction_table where ph_data BETWEEN '"+a+"' and '"+b+"' and soil_type='"+edt1_s+"'  ");

                    String result = "";
                    while (rs.next()) {
                        result += rs.getString(1) + "\n";
                    }
                    tv5.setText("Crops according to your soil type and ph are :\n\n" + result);
                    tv5.setMovementMethod(new ScrollingMovementMethod());
                } catch (Exception e) {
                    e.printStackTrace();
                    tv5.setText(e.toString());
                }
            }
        });

    }
}
