package friend;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.sileria.android.view.HorzListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import chat.ChatMessage;
import quytrinh.quocan.quocan.messengerqa.MainActivity;
import quytrinh.quocan.quocan.messengerqa.R;
import user.Object_User;

public class FriendList extends Activity {
    EditText edtFriendMail;
    Button btnAddFriend;
    ProgressBar progressSearching;
    ArrayList<Object_User> arrRequest;
    ArrayList<ChatMessage> arrConversaton;
    ListViewRequestAdapter adapterImg;
    HorzListView listviewImg;
    Object_User objectRequestUser;
    ChatMessage objectConversation;
    Point p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Mapping();

        //code
        //finding friend
        btnAddFriend.setVisibility(View.GONE);
        edtFriendMail.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    btnAddFriend.setVisibility(View.INVISIBLE);

                } else {
                    btnAddFriend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        //TODO: Receive Request
        MainActivity.root.child("Request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrRequest.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String CurrentString = postSnapshot.getKey().toString(); // Cut Key to 2 email
                    String[] separated = CurrentString.split("_aRECEIVEb_");// Cut Key to 2 email
                    if (separated[0].equals(MainActivity.user_key)) {// if someones send rq for me
                        AddUserRequestToListView(separated[1]);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //TODO: Receive Conversation
        MainActivity.root.child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //a@test*comANDb@test*com
                arrConversaton.clear();
                final ListView lv_conversation = (ListView) findViewById(R.id.listViewConversation);

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String CurrentString = postSnapshot.getKey().toString(); // Cut Key to 2 email
                    String[] separated = CurrentString.split("_AND_");// Cut Key to 2 email
                    if (separated[0].equals(MainActivity.user_key) || separated[1].equals(MainActivity.user_key)) {// if someones send rq for me
                        Firebase rootCT = new Firebase(MainActivity.root + "/Chat/" + CurrentString);

                        rootCT.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                objectConversation = dataSnapshot.getValue(ChatMessage.class);

                                arrConversaton.add(objectConversation);
                                Log.d("CONVER", objectConversation.fullName + " " + objectConversation.fullName_2 + " " + objectConversation.message);

                                ConversationAdapter conversationAdapter = new ConversationAdapter(FriendList.this, R.layout.row_friend_conversation, arrConversaton);
                                lv_conversation.setAdapter(conversationAdapter);
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

        //TODO:  When click button add friend
        progressSearching.setVisibility(View.GONE);
        btnAddFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnAddFriend.setVisibility(View.GONE);
                progressSearching.setVisibility(View.VISIBLE); // prgress bar turn on

                // Send request
                MainActivity.root.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        String userSearching = edtFriendMail.getText().toString().replace(".", "*");
                        //Toast.makeText(FriendList.this, MainActivity.user_key + "_" +  userSearching, Toast.LENGTH_SHORT).show();
                        if (MainActivity.user_key.equals(userSearching))
                            Toast.makeText(FriendList.this, "Kidding me ? Why do you type down your email???", Toast.LENGTH_SHORT).show();
                        else {
                            if (dataSnapshot.hasChild(userSearching) == true) // Exist user OK !!!
                            {
                                Map<String, Object> nickname = new HashMap<String, Object>();
                                nickname.put(userSearching + "_aRECEIVEb_" + MainActivity.user_key, 1); // a send to b a request
                                MainActivity.root.child("Request").updateChildren(nickname);
                                Toast.makeText(FriendList.this, "A request has been send to " + edtFriendMail.getText().toString(), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(FriendList.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressSearching.setVisibility(View.GONE);
                        btnAddFriend.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
                new CountDownTimer(6000, 1000) {   // count down time for progress bar

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        progressSearching.setVisibility(View.GONE);
                        btnAddFriend.setVisibility(View.VISIBLE);
                    }
                }.start();
            }
        });

        listviewImg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (p != null) {
                    if (arrRequest.size() > 0)
                        showPopup(FriendList.this, p, position);
                    else {
                        listviewImg.setAdapter(adapterImg);
                    }
                }
            }
        });

    }

    private void Mapping() {
        arrRequest = new ArrayList<Object_User>();
        arrConversaton = new ArrayList<ChatMessage>();
        edtFriendMail = (EditText) findViewById(R.id.editTextYourFriend);
        btnAddFriend = (Button) findViewById(R.id.buttonAddFriend);
        progressSearching = (ProgressBar) findViewById(R.id.progressBar);
        adapterImg = new ListViewRequestAdapter(FriendList.this, arrRequest);
        listviewImg = (HorzListView) findViewById(R.id.horizontal_lvImg);
    }

    private void AddUserRequestToListView(String userRequested) {

        final Firebase rootCT = new Firebase(MainActivity.root + "/User/" + userRequested);

        rootCT.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // listview Item la anh


                objectRequestUser = dataSnapshot.getValue(Object_User.class);
                // String imgRequest = objectRequestUser.avataUser.toString();
                arrRequest.add(objectRequestUser);


                AnimationSet set = new AnimationSet(true);
                Animation animation = new AlphaAnimation(0.0f, 1.0f);
                animation.setDuration(1000);
                set.addAnimation(animation);

                animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 50.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                        0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                animation.setDuration(500);
                set.addAnimation(animation);

                LayoutAnimationController controller = new LayoutAnimationController(
                        set, 0.5f);

                listviewImg.setLayoutAnimation(controller);

//                        adapter = new LazyAdapter(getActivity(), numResults, nodes, tabType);
//                        setListAdapter(adapter);
                listviewImg.setAdapter(adapterImg);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        listviewImg.getLocationOnScreen(location);
        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    // The method that displays the popup.
    private void showPopup(final Activity context, Point p, final int posit) {
//        int popupWidth = 1000;
//        int popupHeight = 1000;

        // Inflate the popup_layout.xml
        RelativeLayout viewGroup = (RelativeLayout) context.findViewById(R.id.popuplayout);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
//        popup.setWidth(popupWidth);
//        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 35;
        int OFFSET_Y = 300;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        //TODO: Set info
        ImageView imgUser = (ImageView) layout.findViewById(R.id.imageUserPopup);
        final String imgTemp = arrRequest.get(posit).avataUser;
        final Bitmap bitmap = StringToBitMap(imgTemp);
        imgUser.setImageBitmap(bitmap);

        final TextView txtName = (TextView) layout.findViewById(R.id.textViewNamePopup);
        final String nameTemp = arrRequest.get(posit).fullName;
        txtName.setText(": " + nameTemp);

        TextView txtSex = (TextView) layout.findViewById(R.id.textViewSexsualPopup);
        txtSex.setText(": " + arrRequest.get(posit).sexual);

        TextView txtPop = (TextView) layout.findViewById(R.id.textViewBirthdayPopup);
        txtPop.setText(": " + arrRequest.get(posit).dataOfBirth);

        //TODO: see fullsize imagePopup
        final Dialog nagDialog = new Dialog(FriendList.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setContentView(R.layout.show_fullsize_image);
        ImageView imgFullSize = (ImageView) nagDialog.findViewById(R.id.iv_preview_image);
        imgFullSize.setImageBitmap(bitmap);
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nagDialog.show();
            }
        });

        //When click accept friend request
        Button bntAccept = (Button) layout.findViewById(R.id.ButtonAccept);
        bntAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ChatMessage chat;
                chat = new ChatMessage();
                chat.userEmail = MainActivity.user_key;
                chat.message = "Let's talk";
                chat.imgUserChat = "aaa";
                chat.imgUserChat_2 = imgTemp;
                chat.fullName = "test";
                chat.fullName_2 = nameTemp;

                //TODO:  get name of user
                MainActivity.root.child("User").child(MainActivity.user_key).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        chat.fullName = dataSnapshot.getValue().toString();
                        //  Toast.makeText(FriendList.this, chat.fullName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                //TODO:  get avata of user
                final String userSendRequest = arrRequest.get(posit).userEmail.replace(".", "*"); // This is email of request sender
                MainActivity.root.child("User").child(MainActivity.user_key).child("avataUser").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        chat.imgUserChat = dataSnapshot.getValue().toString();

                        //TODO: put to firebase

                        MainActivity.root.child("Chat").child(MainActivity.user_key + "_AND_" + userSendRequest).setValue(chat, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null)
                                    Toast.makeText(FriendList.this, firebaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                //TODO: Remove Request after accepted
                MainActivity.root.child("Request").child(MainActivity.user_key + "_aRECEIVEb_" + userSendRequest).removeValue(new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                        if (firebaseError != null)
                            Toast.makeText(FriendList.this, firebaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                popup.dismiss();
            }
        });
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
