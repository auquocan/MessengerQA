package chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import photo.GridViewActivity;
import quytrinh.quocan.quocan.messengerqa.MainActivity;
import quytrinh.quocan.quocan.messengerqa.R;
import user.Object_User;


public class Chat_Screen extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private ImageView buttonSend;
    private ImageView buttonVoice;
    public static String outputFile = null; // path
    public static MediaRecorder myRecorder;
    public static ChatMessage mess = null;
    private MediaPlayer myPlayer = new MediaPlayer();
    public static String idConversation; //id conversation
    int MY_PERMISSIONS_REQUEST_EXTERNAL = 1;
    int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 2 ;
    //variable for navigation
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__screen_);

        //Mapping
        buttonSend = (ImageView) findViewById(R.id.send);
        buttonVoice = (ImageView) findViewById(R.id.voice);
        listView = (ListView) findViewById(R.id.msgview);
        chatText = (EditText) findViewById(R.id.msg);


// TODO: 16/07/2016 Permission.
        GrantPermissionExternal();
        GrantPermissionRecord();
        //TODO: Get bundle: idConversation
        Bundle bd = getIntent().getBundleExtra("data");
        idConversation = bd.getString("idCONVERSATION");

        //TODO: Set Adapter for ListView
        chatArrayAdapter = new ChatArrayAdapter(Chat_Screen.this, R.layout.right);

        // TODO: Receive Conversation
        MainActivity.root.child("Chat").child(idConversation).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatMessage object = null;
                chatArrayAdapter.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    object = postSnapshot.getValue(ChatMessage.class);

//                    if (object.typeMess.equals("1")) {
                    showMessOnListView(object);
//                    } else if (object.typeMess.equals("3"))
//                        Toast.makeText(Chat_Screen.this, "voice", Toast.LENGTH_SHORT).show();

                }
                listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                listView.setAdapter(chatArrayAdapter);
                mess = object; // keep data.

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });


        //TODO: Navigation bar
        drawerLayout = (DrawerLayout) findViewById(R.id.myDrawableLayoutChat);
        navigationView = (NavigationView) findViewById(R.id.myNavigationChat);
        toolbar = (Toolbar) findViewById(R.id.myToolbarChat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);// hide label of app
        navigationView.setItemIconTintList(null); // hiển thị đúng màu

        SetImageHeader();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuPhoTo:
                        Intent iPhoto = new Intent(Chat_Screen.this, GridViewActivity.class);
                        String idPhotoSendToGridActivity;
                        String CurrentString = idConversation; // Cut Key to 2 email
                        String[] separated = CurrentString.split("_AND_");// Cut Key to 2 email
                        if (separated[0].equals(MainActivity.user_key)) {// if someones send rq for me
                            idPhotoSendToGridActivity = separated[1];
                        } else {
                            idPhotoSendToGridActivity = separated[0];
                        }
                        Bundle bd = new Bundle();
                        bd.putString("idPhoto", idPhotoSendToGridActivity);
                        iPhoto.putExtra("data", bd);//truyền gói bundle

                        startActivity(iPhoto);//open Photo
                        break;
                    case R.id.menuLove:
                        Toast.makeText(Chat_Screen.this, "This function will come soon ^^", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menuCall:
                        Toast.makeText(Chat_Screen.this, "This function will come soon  ^^", Toast.LENGTH_SHORT).show();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.END);
                return false;
            }
        });

        // Todo: send text
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mess.time = DateFormat.getDateTimeInstance().format(new Date());
                    mess.message = chatText.getText().toString();
                    mess.typeMess = "1";
                    //TODO: identify who send the message
                    mess.whoSend = MainActivity.user_key;

                    //TODO: push to fire base
                    if (!chatText.getText().toString().equals(""))
                        return pushMessToFireBase(mess, idConversation);
                }
                return false;
            }
        });
        // Todo: send text
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mess.time = DateFormat.getDateTimeInstance().format(new Date());
                mess.message = chatText.getText().toString();
                mess.typeMess = "1";
//                //TODO: identify who send he message
//                String tmp_email = mess.userEmail;
//                String tmp_email_2 = mess.userEmail_2;
//                mess.userEmail = MainActivity.user_key;
//                if(mess.userEmail.equals(tmp_email_2))
//                    mess.userEmail_2 = tmp_email;

                //TODO: identify who send the message
                mess.whoSend = MainActivity.user_key;

                //TODO: push to fire base
                if (!chatText.getText().toString().equals(""))
                    pushMessToFireBase(mess, idConversation);


            }
        });

        // Todo: send voice
        buttonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: open dialog fragment
                DialogRecordingCustom dialogCustom = new DialogRecordingCustom();
                //dialogCustom.setArguments(bundle);
                dialogCustom.show(getFragmentManager(), "OK");
                //todo: recording voice
                outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qam.3gpp";
                myRecorder = new MediaRecorder();

                myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                myRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

                // Nén âm thanh
                myRecorder.setAudioChannels(1);
                myRecorder.setAudioSamplingRate(4000);
                myRecorder.setAudioEncodingBitRate(4000);


                myRecorder.setOutputFile(outputFile);
                start(v);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qamVoice.3gpp";
                // TODO: 16/07/2016 : cover string to byte
                String tempString = ChatArrayAdapter.chatMessageList.get(position).message;
                byte[] voiceByte = Base64.decode(tempString, Base64.DEFAULT);
                FileOutputStream stream = null;
                try {
                    stream = new FileOutputStream(tempPath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    stream.write(voiceByte);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // TODO: 16/07/2016: Play voice Message
                play(tempPath);
            }
        });
        //to scroll the list view to bottom on data change
//        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//                listView.setSelection(chatArrayAdapter.getCount() - 1);
//            }
//        });
    }

    private boolean pushMessToFireBase(ChatMessage objectMess, String idConversation) {
        //TODO: push to firebase

        MainActivity.root.child("Chat").child(idConversation).push().setValue(objectMess, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null)
                    Toast.makeText(Chat_Screen.this, firebaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }

    private boolean showMessOnListView(ChatMessage objectMess) {

        chatArrayAdapter.add(new ChatMessage(objectMess));// must create new ChatMessage,

        chatText.setText("");

        Log.d("SSS", Integer.toString(chatArrayAdapter.getCount()).toString());


        return true;
    }

    private void SetImageHeader() {
        //Log.d("OKOK", idConversation);
        View hView = navigationView.getHeaderView(0);
        final ImageView avata = (ImageView) hView.findViewById(R.id.naviAvataHeadChat);
        final TextView nameFriend = (TextView) hView.findViewById(R.id.textNameHeadChat);
        String idUser = null;
        String CurrentString = idConversation; // Cut Key to 2 email
        String[] separated = CurrentString.split("_AND_");// Cut Key to 2 email
        if (separated[0].equals(MainActivity.user_key)) {// if someones send rq for me
            idUser = separated[1];
        } else {
            idUser = separated[0];
        }
        // TODO: Receive Conversation
        MainActivity.root.child("User").child(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object_User object;
                object = dataSnapshot.getValue(Object_User.class);
                Bitmap tmp = StringToBitMap(object.avataUser);
                avata.setImageBitmap(tmp);
                nameFriend.setText(object.fullName);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    ///TODO: onclick event in XML
    public void onInfoClick(View v) {
        if (v.getId() == R.id.imageInfo) {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }

    ///TODO: onclick event in XML
    public void onbackChatClick(View v) {
        if (v.getId() == R.id.imageBackChat) {
            finish();//close current screen
        }
    }

    @Override
    public void onBackPressed() {
        finish();//close current screen
        super.onBackPressed();
    }

    // todo: Start Record.
    public void start(View view) {
        try {
            myRecorder.prepare();
            myRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // TODO: 16/07/2016 : play voice message
    public void play(String tempPath) {
        try {
            myPlayer.stop();
            myPlayer.reset();
            myPlayer.setDataSource(tempPath);
            myPlayer.prepare();
            myPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: 16/07/2016 permission @Override
    private void GrantPermissionExternal()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
    // TODO: 16/07/2016 permission @Override
    private void GrantPermissionRecord()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
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

}
