package user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import friend.FriendList;
import quytrinh.quocan.quocan.messengerqa.MainActivity;
import quytrinh.quocan.quocan.messengerqa.R;

public class User_Infomation extends Activity {
    EditText edtFullName, edtDateBirth;
    Button btnConfirm;
    RadioGroup rdbGroupSexual;
    RadioButton rdbSexual;
    ImageView imgAvata, test;
    int REQUEST_CAMERA = 1; // code of intent
    int SELECT_PHOTO = 3; // code inten gallery
    int REQUEST_CROP = 2;
    private Uri picUri;
    Object_User user;
    Bitmap thePic = null;
    ByteArrayOutputStream baos;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__infomation);
        //Setup Firebase on Android
        Firebase.setAndroidContext(this);
        //Mapping
        Mapping();
        //Chọn hộp thoại ngày tháng
        final Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        final Calendar calendarSetDateFromDialog = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        final DatePickerDialog datePickerDialog = new DatePickerDialog(User_Infomation.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarSetDateFromDialog.set(year, monthOfYear, dayOfMonth);
                edtDateBirth.setText(dateFormat.format(calendarSetDateFromDialog.getTime()));
            }
        }, year, month, date);
        edtDateBirth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                datePickerDialog.show();
                return false;
            }
        });


        //Get Picture
        imgAvata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Camera", "Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(User_Infomation.this);
                builder.setTitle("Get your picture from...");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0: {
                                try {
                                    //use standard intent to capture an image
                                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    //we will handle the returned data in onActivityResult
                                    startActivityForResult(captureIntent, REQUEST_CAMERA);
                                } catch (ActivityNotFoundException anfe) {
                                    //display an error message
                                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                                    Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                                    toast.show();
                                }
                                break;
                            }
                            case 1: {
                                Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                                break;
                            }

                        }

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });
        //Confirm Clicked
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new Object_User();
                //set Name String to user variable
                user.fullName = edtFullName.getText().toString();
                //set dataOfBirth String to user variable
                user.dataOfBirth = dateFormat.format(calendarSetDateFromDialog.getTime());
                //set Sexual String to user variable
                int selectedId = rdbGroupSexual.getCheckedRadioButtonId();
                rdbSexual = (RadioButton) findViewById(selectedId);
                user.sexual = rdbSexual.getText().toString();
                //set Image String to user variable
                if (thePic == null) { // if didnt set picture, we use the default picture
                    BitmapDrawable drawable = (BitmapDrawable) imgAvata.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    user.avataUser = BitMapToString(bitmap);
                } else {

                    user.avataUser = BitMapToString(thePic);
                }

                user.userEmail = MainActivity.user_key;//get email to use for key

                //Upload to firebase
                MainActivity.root.child("User").child(MainActivity.user_key).setValue(user, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null)
                            Toast.makeText(User_Infomation.this, "Your profile has been updated", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(User_Infomation.this, firebaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

             //   test = (ImageView) findViewById(R.id.imageViewTest);
//                // get avata of user
//
//                MainActivity.root.child("User").child(MainActivity.user_key).child("avataUser").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(final DataSnapshot dataSnapshot) {
//                        String tmpa;
//                        tmpa = dataSnapshot.getValue().toString();
//                      //  Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
//                        Bitmap bitmap = StringToBitMap(tmpa);
//                        test.setImageBitmap(bitmap);
//                    }
//
//                    @Override
//                    public void onCancelled(FirebaseError firebaseError) {
//                    }
//                });


                gotoFriendList();// go to friendlist activity
            }



        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //** Out Of onCreate
    private void Mapping() {
        edtFullName = (EditText) findViewById(R.id.editTextFullName);
        edtDateBirth = (EditText) findViewById(R.id.editTextBirthDay);
        imgAvata = (ImageView) findViewById(R.id.imageUser);
        rdbGroupSexual = (RadioGroup) findViewById(R.id.radioGroup);
        btnConfirm = (Button) findViewById(R.id.ui_buttonConfirm);

    }


    //Go to camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == REQUEST_CAMERA) {
                //get the Uri for the captured image
                picUri = data.getData();
                //carry out the crop operation
                performCrop();
            }
            if (requestCode == SELECT_PHOTO) {
                picUri = data.getData();
                performCrop();
            }
            //user is returning from cropping the image
            if (requestCode == REQUEST_CROP) {
                //get the returned data
                Bundle extras = data.getExtras();
                //get the cropped bitmap
                thePic = extras.getParcelable("data");

                //display the returned cropped image
                imgAvata.setImageBitmap(thePic);
            }
        }

    }

    /**
     * Helper method to carry out crop operation
     */
    private void performCrop() {
        //take care of exceptions
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 100);
            cropIntent.putExtra("aspectY", 100);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 250);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_CROP);
        }
        //respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    //BitMap to String
    public String BitMapToString(Bitmap bitmap) {
        baos = new ByteArrayOutputStream();
        thePic.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "User_Infomation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://quytrinh.quocan.quocan.messengerqa/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "User_Infomation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://quytrinh.quocan.quocan.messengerqa/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    //String to BitMap
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void gotoFriendList() {
        finish();//close current screen
        Intent i = new Intent(User_Infomation.this, FriendList.class);
        startActivity(i);//mở màn hình mới
    }
}
