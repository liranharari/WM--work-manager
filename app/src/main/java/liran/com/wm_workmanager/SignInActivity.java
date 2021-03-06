package liran.com.wm_workmanager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Random;

public class SignInActivity extends Activity {

    private EditText editMail, editPassword;
    private Button signIn;
    private String code;
    private EditText editWorkerPricing, editManagerPricing, editField1, editField2, editField3, editField4, editField5, editField6;
    private final String SIGNUP_URL = "http://workmanager-2016.appspot.com/api/signup?";
    private final String GETCODES_URL = "http://workmanager-2016.appspot.com/api/getcodes?";
    private Context context;
    private Intent workAc;
    private SharedPreferences sharedPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editMail=(EditText) findViewById(R.id.edit_sign_in_mail);
        editPassword=(EditText) findViewById(R.id.edit_sign_in_password);
        editManagerPricing=(EditText) findViewById(R.id.edit_sign_in_manager_pricing);
        editWorkerPricing=(EditText) findViewById(R.id.edit_sign_in_worker_pricing);
        editField1=(EditText) findViewById(R.id.edit_sign_in_field1);
        editField2=(EditText) findViewById(R.id.edit_sign_in_field2);
        editField3=(EditText) findViewById(R.id.edit_sign_in_field3);
        editField4=(EditText) findViewById(R.id.edit_sign_in_field4);
        editField5=(EditText) findViewById(R.id.edit_sign_in_field5);
        editField6=(EditText) findViewById(R.id.edit_sign_in_field6);

        signIn= (Button) findViewById(R.id.btn_save_sign_in);

        context=this;
        sharedPrefs=this.getSharedPreferences("userSharedPrefs", 0);


        workAc=new Intent(this, WorkActivity.class);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code=generateRandomCode();

               /* Toast.makeText(getApplicationContext(), "new user "+ editMail.getText().toString()+
                                "added succesfully"+code, Toast.LENGTH_LONG).show();
*/
                Utils.showProgressDialog(v.getContext(), "Loading...");

                VollyRequestToSignIn(editMail.getText().toString(), editPassword.getText().toString(), code, editWorkerPricing.getText().toString(),
                        editManagerPricing.getText().toString(), editField1.getText().toString(), editField2.getText().toString(),
                        editField3.getText().toString(), editField4.getText().toString(), editField5.getText().toString(), editField6.getText().toString());




            }
        });
    }


    private void VollyRequestToSignIn(final String mail, String password, String code,
                                      String WorkerPricing, String ManagerPricing, String Field1, String Field2,
                                      String Field3, String Field4, String Field5, String Field6){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = SIGNUP_URL +"mail="+mail+"&"+
                "password="+password+
                "&code="+code+
                "&managerPricing="+ManagerPricing+
                "&workerPricing="+WorkerPricing+
                "&field1="+Field1+
                "&field2="+Field2+
                "&field3="+Field3+
                "&field4="+Field4+
                "&field5="+Field5+
                "&field6="+Field6;

        url = url.replaceAll(" ", "%20");
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override

            public void onResponse(JSONObject response) {
                try {
                    SharedPreferences.Editor editor = getSharedPreferences("userSharedPrefs", MODE_PRIVATE).edit();
                    editor.putInt(Utils.isLogin, WorkActivity.MANAGER_LOGIN);
                    editor.commit();
                    showCodeAlertDialog(mail);

                } catch (Exception e) {
                    e.printStackTrace();

                }
                Utils.cancelProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.cancelProgressDialog();
                Toast.makeText(context, "Failed sign up", Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(request);

    }

    private void showCodeAlertDialog(final String mail)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("שים לב!"+"\n"+"הקוד להתחברות עובדים הינו:"+"\n"+code)
                .setCancelable(false)
                .setPositiveButton("הבנתי", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){
                        workAc.putExtra("user", mail);
                        startActivity(workAc);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String generateRandomCode()
    {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String NUMCHAR="0123456789";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 5) {
            int charindex = (int) (rnd.nextFloat() * SALTCHARS.length());
            int numindex = (int) (rnd.nextFloat() * NUMCHAR.length());
            if(salt.length()==2 || salt.length()==4)
                salt.append(NUMCHAR.charAt(numindex));
            else
                salt.append(SALTCHARS.charAt(charindex));
        }
        String saltStr = salt.toString();
        return saltStr;
    }



}
