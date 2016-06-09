package chat;

import android.app.Activity;
import android.os.Bundle;
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

import quytrinh.quocan.quocan.messengerqa.MainActivity;
import quytrinh.quocan.quocan.messengerqa.R;


public class Chat_Screen extends Activity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Mapping
        buttonSend = (Button) findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.msgview);
        chatText = (EditText) findViewById(R.id.msg);


        //TODO: Receive Message
        MainActivity.root.child("Request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String CurrentString = postSnapshot.getKey().toString(); // Cut Key to 2 email
                    String[] separated = CurrentString.split("_AND_");// Cut Key to 2 email
                    if (separated[0].equals(MainActivity.user_key) || separated[1].equals(MainActivity.user_key)) {// if someones send rq for me
                        Firebase rootCT = new Firebase(MainActivity.root + "/Chat/" + CurrentString);
                        rootCT.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ChatMessage msg = dataSnapshot.getValue(ChatMessage.class);
                                Toast.makeText(Chat_Screen.this, msg.time, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Set Adapter
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);


        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

//        //to scroll the list view to bottom on data change
//        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//                listView.setSelection(chatArrayAdapter.getCount() - 1);
//            }
//        });
    }

    private boolean sendChatMessage() {
//        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
//        chatText.setText("");
//        side = !side;
 return true;
    }
}