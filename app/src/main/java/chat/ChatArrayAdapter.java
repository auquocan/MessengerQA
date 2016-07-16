package chat;

/**
 * Created by MyPC on 13/05/2016.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import quytrinh.quocan.quocan.messengerqa.MainActivity;
import quytrinh.quocan.quocan.messengerqa.R;


class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private TextView timeDate;
    public static List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;

    @Override
    public void clear() {
        chatMessageList.clear();
    }

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        //Log.d("SSS",chatMessageObj.message);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if (chatMessageObj.userEmail.equals(MainActivity.user_key)) {
//            row = inflater.inflate(R.layout.left, parent, false);
//        }else{
//            row = inflater.inflate(R.layout.right, parent, false);
//        }

        if (chatMessageObj.whoSend.equals(MainActivity.user_key))
            row = inflater.inflate(R.layout.left, parent, false);
        else
            row = inflater.inflate(R.layout.right, parent, false);


        chatText = (TextView) row.findViewById(R.id.msgr);
        if (chatMessageObj.typeMess.equals("1")) // type txt message
            chatText.setText(chatMessageObj.message);
        else if (chatMessageObj.typeMess.equals("3")) // type voice message
        {
            chatText.setTypeface(null, Typeface.BOLD_ITALIC);
            chatText.setText("Voice message, click to hear.");
        }


        timeDate = (TextView) row.findViewById(R.id.textViewShowDate);
        timeDate.setText(chatMessageObj.time);
        return row;
    }
}
