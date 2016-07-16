package chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import photo.GridItem;
import quytrinh.quocan.quocan.messengerqa.MainActivity;
import quytrinh.quocan.quocan.messengerqa.R;

/**
 * Created by MyPC on 23/06/2016.
 */
public class DialogRecordingCustom extends android.app.DialogFragment {
    GridItem photoObject;
    Animation animFadein;
    ImageView imgRecording;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.cutom_dialog_recording, null);
        alertDialog.setView(view);
        imgRecording = (ImageView) view.findViewById(R.id.imageRecording);

        // todo : load the animation
        animFadein = AnimationUtils.loadAnimation(getActivity(),
                R.anim.rotate);
//        animFadein.setAnimationListener(this);
        animFadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imgRecording.startAnimation(animFadein);


        //todo: set negative and positive
        alertDialog.setPositiveButton("Send Voice", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //todo: covert record file to String
                stop();
                //String tempout = Environment.getExternalStorageDirectory().getAbsolutePath() + "/quocanokmen.3gpp";
                byte[] tmpByte = FileLocal_To_Byte(Chat_Screen.outputFile);
                String voiceString = Base64.encodeToString(tmpByte, Base64.DEFAULT);
                Chat_Screen.mess.time = DateFormat.getDateTimeInstance().format(new Date());
                Chat_Screen.mess.message = voiceString ;
                Chat_Screen.mess.typeMess = "3";
                //TODO: identify who send the message
                Chat_Screen.mess.whoSend = MainActivity.user_key;

                //TODO: push to fire base

                pushMessToFireBase(Chat_Screen.mess, Chat_Screen.idConversation);


                //  byte[] a = Base64.decode(tempString, Base64.DEFAULT);
                // FileOutputStream stream = null;
//                try {
//                    stream = new FileOutputStream(tempout);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    stream.write(a);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        stream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }


//                MainActivity.root.child("Photo").child(MainActivity.user_key).push().setValue(photoObject, new Firebase.CompletionListener() {
//                    @Override
//                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
//                        if (firebaseError != null)
//                            Toast.makeText(getActivity(), firebaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });

                //
                //  GridViewActivity.txtPhto.setVisibility(View.GONE);// set to visible when have a picture
            }

        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        Dialog dialog = alertDialog.create();
        return dialog;
    }

    // TODO: 16/07/2016 push voice string to firebase
    private boolean pushMessToFireBase(ChatMessage objectMess, String idConversation) {

        MainActivity.root.child("Chat").child(idConversation).push().setValue(objectMess, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null)
                    Toast.makeText(getActivity(), firebaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }
    // todo: Stop Record.
    public void stop() {
        try {
            Chat_Screen.myRecorder.stop();
            Chat_Screen.myRecorder.release();
            Chat_Screen.myRecorder = null;


        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    //CONVERT FILE LOCAL TO BYTE[]
    public byte[] FileLocal_To_Byte(String path) {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
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
