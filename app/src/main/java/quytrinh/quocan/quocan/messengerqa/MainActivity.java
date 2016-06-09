package quytrinh.quocan.quocan.messengerqa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    Button btnLogin, btnRegister;
    EditText edtPassword;
    public static EditText edtUserMail;
    TextView txtvForgotPassword;
    public static String user_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  for Title Bar
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        // for fullscreen
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        //Setup Firebase on Android
        Firebase.setAndroidContext(this);
        //Read & Write to your Firebase Database

        //https://qa-messenger.firebaseio.com/
        root = new Firebase("https://testqamess.firebaseio.com/");
        //Mapping
        Mapping();
        edtUserMail.setText("test@test.com");
        edtPassword.setText("123456");
        //Register Button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.length() >= 6) {
                    root.createUser(edtUserMail.getText().toString(), edtPassword.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                        // Successfully create
                        @Override
                        public void onSuccess(Map<String, Object> result) {

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
                        }
                    });
                } else
                    Toast.makeText(MainActivity.this, "Password/Email is not available", Toast.LENGTH_LONG).show();
            }
        });
        //Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Successfully Login
                root.authWithPassword(edtUserMail.getText().toString(), edtPassword.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        user_key = edtUserMail.getText().toString().replace(".", "*");
                        Toast.makeText(MainActivity.this, "Hi " + edtUserMail.getText().toString() , Toast.LENGTH_SHORT).show();
                        doLogin() ;
                    }

                    // Somgthing wrong when Login
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
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
        btnLogin = (Button) findViewById(R.id.buttonLogin);
        btnRegister = (Button) findViewById(R.id.buttonRegister);
        edtUserMail = (EditText) findViewById(R.id.editTextUserName);
        edtPassword = (EditText) findViewById(R.id.editTextPassword);
        txtvForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);

    }

}
