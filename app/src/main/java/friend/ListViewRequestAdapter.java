package friend;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import quytrinh.quocan.quocan.messengerqa.R;
import user.Object_User;

/**
 * Created by MyPC on 25/05/2016.
 */
public class ListViewRequestAdapter extends BaseAdapter {
    private ArrayList<Object_User> arr;
    private Context context;

    public ListViewRequestAdapter(Context context, ArrayList<Object_User> arr) {
        this.arr = arr;
        this.context = context;
    }

    @Override
    public int getCount() {return arr.size();}

    @Override
    public Object getItem(int arg0) {
        return arr.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View convertView, ViewGroup arg2) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_horizontal_listview,
                    null);
        }

        ImageView imgItem = (ImageView) convertView.findViewById(R.id.imgItem);
        Bitmap bitmapRequest  = StringToBitMap(arr.get(arg0).avataUser);
        imgItem.setImageBitmap(bitmapRequest);

        return convertView;
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