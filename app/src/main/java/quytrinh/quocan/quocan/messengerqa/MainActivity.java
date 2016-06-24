package quytrinh.quocan.quocan.messengerqa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import friend.FriendList;
import user.User_Infomation;

public class MainActivity extends Activity {
    public static Firebase root;
    public static String user_key;
    Button btnLogin, btnRegister;
    EditText edtPassword;
    CheckBox checkBox;
    ProgressBar progressBarMain;
    TextView txtvForgotPassword;
    public static EditText edtUserMail;
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Setup Firebase on Android
        Firebase.setAndroidContext(this);
        //Read & Write to your Firebase Database

        //https://qa-messenger.firebaseio.com/
        root = new Firebase("https://testqamess.firebaseio.com/");
        //Mapping
        Mapping();


        //todo: remember me
        sharedPreferences = getSharedPreferences("DataLogin", MODE_PRIVATE);
        edtUserMail.setText(sharedPreferences.getString("username", ""));
        edtPassword.setText(sharedPreferences.getString("password", ""));
        if (!edtUserMail.getText().toString().equals("")) {
            user_key = edtUserMail.getText().toString().replace(".", "*");
            doLogin();
        }
        //Register Button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRegister.setVisibility(View.GONE);
                btnLogin.setVisibility(View.GONE);
                progressBarMain.setVisibility(View.VISIBLE);
                if (edtPassword.length() >= 6) {
                    root.createUser(edtUserMail.getText().toString(), edtPassword.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                        // Successfully create
                        @Override
                        public void onSuccess(Map<String, Object> result) {

                            //todo: remember user whn checkbox clicked
                            if (checkBox.isChecked()) {

                                String user = edtUserMail.getText().toString();
                                String pass = edtPassword.getText().toString();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username", user);
                                editor.putString("password", pass);

                                editor.commit();
                            }
                            doRegister();
                            user_key = edtUserMail.getText().toString().replace(".", "*");
                            Toast.makeText(getApplicationContext()
                                    , "Successfully created user account with uid: " + result.get("uid")
                                    , Toast.LENGTH_LONG).show();
                        }

                        // Something wrong when creating
                        @Override
                        public void onError(FirebaseError firebaseError) {
                            Toast.makeText(getApplicationContext()
                                    , firebaseError.getMessage()
                                    , Toast.LENGTH_LONG).show();
                            progressBarMain.setVisibility(View.GONE);
                            btnRegister.setVisibility(View.VISIBLE);
                            btnLogin.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Password/Email is not available", Toast.LENGTH_LONG).show();
                    progressBarMain.setVisibility(View.GONE);
                    btnRegister.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                }
            }
        });

        //Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRegister.setVisibility(View.GONE);
                btnLogin.setVisibility(View.GONE);
                progressBarMain.setVisibility(View.VISIBLE);
                // Successfully Login
                root.authWithPassword(edtUserMail.getText().toString(), edtPassword.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        user_key = edtUserMail.getText().toString().replace(".", "*");
                        Toast.makeText(MainActivity.this, "Welcome to QA Messenger", Toast.LENGTH_SHORT).show();

                        //todo: remember user whn checkbox clicked
                        if (checkBox.isChecked()) {

                            String user = edtUserMail.getText().toString();
                            String pass = edtPassword.getText().toString();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", user);
                            editor.putString("password", pass);

                            editor.commit();
                        }
                        doLogin();
                    }

                    // Somgthing wrong when Login
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        progressBarMain.setVisibility(View.GONE);
                        btnRegister.setVisibility(View.VISIBLE);
                        btnLogin.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),
                                firebaseError.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        txtvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.resetPassword(edtUserMail.getText().toString(), new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),
                                "Password has been sent to your email",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Toast.makeText(getApplicationContext(),
                                firebaseError.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    //////////Out of OnCreate //////////

    //
    public void doRegister() {
        finish();//close current screen
        Intent i = new Intent(MainActivity.this, User_Infomation.class);
        startActivity(i);//mở màn hình mới
    }

    public void doLogin() {
        finish();//close current screen
        Intent i = new Intent(MainActivity.this, FriendList.class);
        startActivity(i);//mở màn hình mới
    }

    //Mapping
    private void Mapping() {
        progressBarMain = (ProgressBar) findViewById(R.id.progressBarMain);
        btnLogin = (Button) findViewById(R.id.buttonLogin);
        btnRegister = (Button) findViewById(R.id.buttonRegister);
        edtUserMail = (EditText) findViewById(R.id.editTextUserName);
        edtPassword = (EditText) findViewById(R.id.editTextPassword);
        txtvForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
        checkBox = (CheckBox) findViewById(R.id.checkBoxRemember);

    }

}
