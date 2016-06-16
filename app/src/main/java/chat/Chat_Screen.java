package chat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

import quytrinh.quocan.quocan.messengerqa.MainActivity;
import quytrinh.quocan.quocan.messengerqa.R;


public class Chat_Screen extends Activity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    ChatMessage mess = null;

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
        final String idConversation = bd.getString("idCONVERSATION");

        //TODO: Set Adapter for ListView
        chatArrayAdapter = new ChatArrayAdapter(Chat_Screen.this, R.layout.right);


        // TODO: Receive Conversation
        MainActivity.root.child("Chat").child(idConversation).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatMessage object = null;
                chatArrayAdapter.clear();
                Log.d("NGOAI", String.valueOf(dataSnapshot.getChildrenCount()));
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d("NOI", String.valueOf(postSnapshot.getChildrenCount()));
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


        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mess.time = DateFormat.getDateTimeInstance().format(new Date());
                    mess.message = chatText.getText().toString();

                    //TODO: identify who send the message
                    mess.whoSend = MainActivity.user_key;

                    //TODO: push to fire base
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
        return  true;
    }

    private boolean showMessOnListView(ChatMessage objectMess) {

        chatArrayAdapter.add(new ChatMessage(objectMess));// must create new ChatMessage,

        chatText.setText("");

        Log.d("SSS", Integer.toString(chatArrayAdapter.getCount()).toString());


//        side = !side;
        return true;
    }
}