package chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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
    private Button buttonSend;
    ChatMessage mess = null;

    String idConversation; //id conversation
    //variable for navigation
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    String imgFriendChat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__screen_);

        //Mapping
        buttonSend = (Button) findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.msgview);
        chatText = (EditText) findViewById(R.id.msg);


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
                    showMessOnListView(object);

                }
                listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                listView.setAdapter(chatArrayAdapter);
                mess = object;

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


        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mess.time = DateFormat.getDateTimeInstance().format(new Date());
                    mess.message = chatText.getText().toString();

                    //TODO: identify who send the message
                    mess.whoSend = MainActivity.user_key;

                    //TODO: push to fire base
                    if (!chatText.getText().toString().equals(""))
                        return pushMessToFireBase(mess, idConversation);
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mess.time = DateFormat.getDateTimeInstance().format(new Date());
                mess.message = chatText.getText().toString();

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


//        side = !side;
        return true;
    }

    private void SetImageHeader() {
        Log.d("OKOK", idConversation);
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
