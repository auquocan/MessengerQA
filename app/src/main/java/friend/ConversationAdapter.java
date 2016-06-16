package friend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import chat.ChatMessage;
import quytrinh.quocan.quocan.messengerqa.MainActivity;
import quytrinh.quocan.quocan.messengerqa.R;

/**
 * Created by MyPC on 06/06/2016.
 */
public class ConversationAdapter extends ArrayAdapter<ChatMessage> {
    //Contructor
    public ConversationAdapter(Context context, int resource, List<ChatMessage> items) {  //Constructor
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row_friend_conversation, null);
        }

        ChatMessage p = getItem(position);


//        Firebase rootTemp = new Firebase(MainActivity.root + "/User/" + MainActivity.user_key);// create temp firebase root to get the object
//        rootTemp.child("avataUser").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String tmp_string;
//                tmp_string = dataSnapshot.getValue().toString();
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

        if (p != null) {

            TextView txt_name = (TextView) v.findViewById(R.id.textViewTenChat);
            if (MainActivity.user_key.equals(p.userEmail))// if this one send mess, set his text
                txt_name.setText(p.fullName_2);
            else
                txt_name.setText(p.fullName);


            ImageView img_conver = (ImageView) v.findViewById(R.id.imageConversation);
            Bitmap bitmap;
            if (MainActivity.user_key.equals(p.userEmail))// if this one send mess, set his image
                bitmap = StringToBitMap(p.imgUserChat_2);
            else
                bitmap = StringToBitMap(p.imgUserChat);
            img_conver.setImageBitmap(bitmap);

//            ImageView img_conver = (ImageView) v.findViewById(R.id.imageConversation);
//            Bitmap bitmap;
//            if (MainActivity.user_key.equals(p.whoSend))// if this one send mess, set his image
//                bitmap = StringToBitMap(p.imgUserChat_2);
//            else
//                bitmap = StringToBitMap(p.imgUserChat);
//            img_conver.setImageBitmap(bitmap);


            TextView txt_cur_chat = (TextView) v.findViewById(R.id.textViewCurrentChat);
            txt_cur_chat.setText(p.message);
        }

        return v;
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


