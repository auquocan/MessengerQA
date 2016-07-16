package photo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import quytrinh.quocan.quocan.messengerqa.MainActivity;
import quytrinh.quocan.quocan.messengerqa.R;

/**
 * Created by MyPC on 23/06/2016.
 */
public class DialogCustom extends android.app.DialogFragment {
    ImageView imgPhotoUpload;
    EditText edtTittle;
    GridItem photoObject;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.cutom_dialog, null);
        alertDialog.setView(view);
        imgPhotoUpload = (ImageView) view.findViewById(R.id.imageUpload);
        edtTittle = (EditText) view.findViewById(R.id.EditTextTittle);

        final String dataPhoto = getArguments().getString("data"); // get data from GridViewActivity
        Bitmap bitmapPhoto = StringToBitMap(dataPhoto);
        imgPhotoUpload.setImageBitmap(bitmapPhoto); // set image to dialog


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String tittle = edtTittle.getText().toString();
                photoObject = new GridItem(dataPhoto, tittle); //set data to this object
                MainActivity.root.child("Photo").child(MainActivity.user_key).push().setValue(photoObject, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null)
                            Toast.makeText(getActivity(), firebaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getActivity(),"Uploaded", Toast.LENGTH_SHORT).show();
              //
                //  GridViewActivity.txtPhto.setVisibility(View.GONE);// set to visible when have a picture
            }

        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        Dialog dialog = alertDialog.create();
        return dialog;
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
