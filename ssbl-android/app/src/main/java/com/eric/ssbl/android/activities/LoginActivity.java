package com.eric.ssbl.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LoginActivity extends Activity {

    private final Context _context = this;
    private File _loginFile;
    private JSONObject _loginObj;
    private ProgressDialog _loading;
    private View _registerPrompt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _loginFile = new File(getFilesDir(), "yummy.hunnymustard");

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        // check for stored login info
        if (_loginFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(_loginFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                _loginObj = (JSONObject) ois.readObject();
                ((EditText) findViewById(R.id.login_username)).setText(_loginObj.getString("username"));
                ((EditText) findViewById(R.id.login_password)).setText(_loginObj.getString("password"));
                ((CheckBox) findViewById(R.id.login_remember_me)).setChecked(true);
                new HttpLogin().execute();
            } catch (Exception e) {
                _loginFile.delete(); // The file may have issues, do not cause infinite loop of failure for users
                e.printStackTrace();
            }
        }
    }

    public void loginAccount(View view) {

        String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

        byte[] hashed = DigestUtils.sha1(DigestUtils.sha1(password.getBytes()));
        String hashedPassword = bytesToHex(hashed).toUpperCase();
//        NameValuePair login = new BasicNameValuePair(username, hashedPassword);
        NameValuePair login = new BasicNameValuePair("buddy", "*975B2CD4FF9AE554FE8AD33168FBFC326D2021DD");
        _loading = ProgressDialog.show(getActivity(), getString(R.string.logging_in), getString(R.string.chill_out), true);

        new HttpLogin().execute(login);


//        _loginObj = new JSONObject();
//
//        try {
//            _loginObj.put("username", ((EditText) findViewById(R.id.login_username)).getText());
//            _loginObj.put("password", ((EditText) findViewById(R.id.login_password)).getText());
//            new HttpLogin().execute();
//        } catch (Exception e) {
//            Toast.makeText(this, getString(R.string.sww_error), Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }

//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
//
//        Intent i = new Intent(this, ProfileActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        builder.setContentIntent(pi);
//
//        NotificationManager notifMngr =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        // Builds the notification and issues it.
//        notifMngr.notify(1, builder.build());
    }

    private void rememberMe() {

        try {
            _loginFile.delete();
            _loginFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(_loginFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(_loginObj);
            oos.close();
            fos.close();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.remember_me_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void promptRegister(View view) {
        LayoutInflater li = LayoutInflater.from(this);
        _registerPrompt = li.inflate(R.layout.prompt_register, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb
                .setView(_registerPrompt)
                .setTitle("Register an account")
                .setCancelable(true)
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _loading = ProgressDialog.show(getActivity(), getString(R.string.registering), getString(R.string.chill_out), true);

                        String email = ((EditText) _registerPrompt.findViewById(R.id.prompt_register_email)).getText().toString();
                        String username = ((EditText) _registerPrompt.findViewById(R.id.prompt_register_username)).getText().toString();
                        String password = ((EditText) _registerPrompt.findViewById(R.id.prompt_register_password)).getText().toString();
                        String confirm = ((EditText) _registerPrompt.findViewById(R.id.prompt_register_confirm_password)).getText().toString();

                        String emailRegex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
                        if (!email.matches(emailRegex)) {
                            _loading.dismiss();
                            Toast.makeText(_context, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String usernameRegex = "^(?=.{8,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
                        if (!username.matches(usernameRegex)) {
                            _loading.dismiss();
                            Toast.makeText(_context, "Please enter a valid username", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!password.equals(confirm)) {
                            _loading.dismiss();
                            Toast.makeText(_context, "Passwords do not match", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        byte[] hashed = DigestUtils.sha1(DigestUtils.sha1(password.getBytes()));
                        String hashedPassword = bytesToHex(hashed).toUpperCase();

                        User u = new User();
                        u.setEmail(email);
                        u.setUsername(username);
                        u.setPassword("*" + hashedPassword);

                        new HttpRegister().execute(u);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        adb.create().show();
    }

    private void goToMain() {

        // set the current user in the general manager
        if (((CheckBox) findViewById(R.id.login_remember_me)).isChecked())
            rememberMe();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private Activity getActivity() {
        return this;
    }

    private class HttpLogin extends AsyncTask<NameValuePair, Void, Void> {

        private User curUser;

        private void httpLogin(NameValuePair login) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/auth/login");

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url.toString());
                System.out.println("url: " + url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");
                request.addHeader("username", login.getName());
                request.addHeader("password", login.getValue());

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                if (jsonString.length() == 0)
                    return;

                ObjectMapper om = new ObjectMapper();
                curUser = om.readValue(jsonString, User.class);
            } catch (Exception e) {
                curUser = null;
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(NameValuePair... params) {
            httpLogin(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            _loading.dismiss();

            if (curUser != null) {
                DataManager.setCurUser(curUser);
                goToMain();
            }
            else {
                Toast.makeText(_context, "Incorrect login info", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class HttpRegister extends AsyncTask<User, Void, Void> {

        private User curUser;

        private void httpRegister(User newUser) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/auth/register");

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");

                ObjectMapper om = new ObjectMapper();
                StringEntity body = new StringEntity(om.writeValueAsString(newUser));
                request.setEntity(body);

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                if (jsonString.length() == 0)
                    return;

                curUser = om.readValue(jsonString, User.class);

            } catch (Exception e) {
                curUser = null;
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(User... params) {

            httpRegister(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            _loading.dismiss();

            if (curUser != null) {
                DataManager.setCurUser(curUser);
                goToMain();
            }
            else {
                Toast.makeText(_context, "Error registering :(", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
