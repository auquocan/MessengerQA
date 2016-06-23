package photo;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import quytrinh.quocan.quocan.messengerqa.MainActivity;
import quytrinh.quocan.quocan.messengerqa.R;

public class GridViewActivity extends ActionBarActivity {
    int REQUEST_CAMERA = 1; // code of intent
    int SELECT_PHOTO = 3; // code inten gallery
    int REQUEST_CROP = 2;
    private Uri picUri;
    private Bitmap thePic = null;
    private static final String TAG = GridViewActivity.class.getSimpleName();

    private GridView mGridView;
    private ProgressBar mProgressBar;

    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> arrGridPhoto;

    Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview_photo);

        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnUpload = (Button) findViewById(R.id.buttonUpPhoto);
        //Initialize with empty data
        arrGridPhoto = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_photo, arrGridPhoto);

        //Start download
        mProgressBar.setVisibility(View.VISIBLE);
        int result = 0;
        // TODO: Receive Photo
        MainActivity.root.child("Photo").child(MainActivity.user_key).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GridItem object = null;
                arrGridPhoto.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    object = postSnapshot.getValue(GridItem.class);
                    arrGridPhoto.add(object);


                }
                mGridView.setAdapter(mGridAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        mProgressBar.setVisibility(View.GONE);
        mGridView.setAdapter(mGridAdapter);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Camera", "Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(GridViewActivity.this);
                builder.setTitle("Get your picture from...");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0: {
                                try {
                                    //use standard intent to capture an image
                                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    //we will handle the returned data in onActivityResult
                                    startActivityForResult(captureIntent, REQUEST_CAMERA);
                                } catch (ActivityNotFoundException anfe) {
                                    //display an error message
                                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                                    Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                                    toast.show();
                                }
                                break;
                            }
                            case 1: {
                                Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                                break;
                            }

                        }

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });

        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(GridViewActivity.this, DetailsActivity.class);
                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image);

                // Interesting data to pass across are the thumbnail size/location, the
                // resourceId of the source bitmap, the picture description, and the
                // orientation (to avoid returning back to an obsolete configuration if
                // the device rotates again in the meantime)

                int[] screenLocation = new int[2];
                imageView.getLocationOnScreen(screenLocation);

                //Pass the image title and url to DetailsActivity
                intent.putExtra("left", screenLocation[0]).
                        putExtra("top", screenLocation[1]).
                        putExtra("width", imageView.getWidth()).
                        putExtra("height", imageView.getHeight()).
                        putExtra("title", item.getTitle()).
                        putExtra("image", item.getImage());

                //Start details activity
                startActivity(intent);
            }
        });
    }


    //Go to camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == REQUEST_CAMERA) {
                //get the Uri for the captured image
                picUri = data.getData();
                //carry out the crop operation
                performCrop();
            }
            if (requestCode == SELECT_PHOTO) {
                picUri = data.getData();
                performCrop();
            }
            //user is returning from cropping the image
            if (requestCode == REQUEST_CROP) {
                //get the returned data
                Bundle extras = data.getExtras();
                //get the cropped bitmap
                thePic = extras.getParcelable("data");

                //todo: open dialog fragment
                DialogCustom dialogCustom = new DialogCustom();
                Bundle bundle = new Bundle();
                bundle.putString("data", BitMapToString(thePic));
                dialogCustom.setArguments(bundle);
                dialogCustom.show(getFragmentManager(), "OK");

                //display the returned cropped image
                // imgUpLoad.setImageBitmap(thePic);
            }
        }

    }

    /**
     * Helper method to carry out crop operation
     */
    private void performCrop() {
        //take care of exceptions
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 100);
            cropIntent.putExtra("aspectY", 100);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 250);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_CROP);
        }
        //respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
            toast.show();
        }
    }


    //BitMap to String
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos;
        baos = new ByteArrayOutputStream();
        thePic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}