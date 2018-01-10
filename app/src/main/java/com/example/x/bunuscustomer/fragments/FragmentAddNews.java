package com.example.x.bunuscustomer.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.x.bunuscustomer.AddNewsActivity;
import com.example.x.bunuscustomer.*;
import com.example.x.bunuscustomer.fragments.classes.ActionObject;
import com.example.x.bunuscustomer.fragments.classes.AddNewsOblect;
import com.example.x.bunuscustomer.handlers.AddNewsHandler;
import com.example.x.bunuscustomer.retrofit.App;
import com.example.x.bunuscustomer.retrofit.Otvet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by mobi app on 01.07.2017.
 */

public class FragmentAddNews extends Fragment {

    AddNewsActivity addNewsActivity;
    ActionObject object = new ActionObject();
    AddNewsOblect addNewsOblect = new AddNewsOblect();
    AddNewsHandler handler ;
    View v;
    private static final int RESULT_SELECT_IMAGE = 1;

    private static final String APP_PREFERENCES = "config";
    private static final String APP_PREFERENCES_TOKEN = "token";
    private static final String APP_PREFERENCES_ID = "id";
    private static final String APP_PREFERENCES_PHONE = "phone";
    private static final String APP_PREFERENCES_IMG = "img";
    private SharedPreferences mSettings;
    String myId,myToken;

    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;


    Dialog dialogSelectImage = null;

    public FragmentAddNews(AddNewsActivity addNewsActivity){
        this.addNewsActivity = addNewsActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_news, container, false);

        mSettings = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        myId = mSettings.getString(APP_PREFERENCES_ID,"");
        myToken = mSettings.getString(APP_PREFERENCES_TOKEN,"");

        handler = new AddNewsHandler(this);
        object.setIsEdit(new ObservableBoolean(true));
        binding.setVariable(BR.infoAction, object);
        binding.setVariable(BR.handlerAddNews,handler);
        binding.setVariable(BR.addProgress, addNewsOblect);

        v = binding.getRoot();
        return  v;
    }

    public void selectImage(){

        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallaryIntent, RESULT_SELECT_IMAGE);
    }

    public void takePhoto() {
        try {
            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photo));
            imageUri = Uri.fromFile(photo);
            startActivityForResult(intent, TAKE_PICTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null){

//
            //set the selected image to image variable
            Uri image = data.getData();
            String img = image.toString();
            //img = img.replace("content://", "file:///");

            object.setBitmap(new ObservableField<Uri>(image));
            object.setIsBitmap(new ObservableBoolean(true));

            Bitmap bitmap;
            try {
                File file = new File(image.toString());
                bitmap = MediaStore.Images.Media.getBitmap(addNewsActivity.getContentResolver(), image);
                bitmap =  decodeSampledBitmapFromResource(getPath(image), 300, 225);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //compress the image to jpg format
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            /*
            * encode image to base64 so that it can be picked by saveImage.php file
            * */
                String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);


                object.setImg(new ObservableField<String>(encodeImage));
                //actionObjects.get(position).setBitmap();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            imageView.setImageURI(image);
//            imageViewOb.setImageURI(image);

        }else if(requestCode==TAKE_PICTURE){
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = imageUri;
                object.setBitmap(new ObservableField<Uri>(selectedImage));
                object.setIsBitmap(new ObservableBoolean(true));
                this.getActivity().getContentResolver().notifyChange(selectedImage, null);
                //ImageView imageView = (ImageView) v.findViewById(R.id.ImageView);
                ContentResolver cr = this.getActivity().getContentResolver();
                Bitmap bitmap;
                try {
                    bitmap = android.provider.MediaStore.Images.Media
                            .getBitmap(cr, selectedImage);
                    bitmap =  decodeSampledBitmapFromResource(selectedImage.getPath().toString(), 300, 225);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    //compress the image to jpg format
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            /*
            * encode image to base64 so that it can be picked by saveImage.php file
            * */
                    String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

                    object.setImg(new ObservableField<String>(encodeImage));
                } catch (Exception e) {
                    Toast.makeText(this.getActivity(), "Failed to load", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Camera", e.toString());
                }
            }
        }
    }


    public void addNews(){

        try {
            object.setIsProgress(new ObservableBoolean(true));

            App.getApi().addNews(object.getTime().get(),
                    myId,
                    object.getName().get(),
                    object.getText().get(),
                    object.getImg().get()).enqueue(new Callback<Otvet>() {
                @Override
                public void onResponse(Call<Otvet> call, Response<Otvet> response) {
                    object.setIsProgress(new ObservableBoolean(false));
                    if(Integer.parseInt(response.body().getCode())==201) Toast.makeText(addNewsActivity, response.body().getMessage(),Toast.LENGTH_SHORT).show();
                    else if(Integer.parseInt(response.body().getCode())==202) {
                        showDialog("2");
                    }else Toast.makeText(addNewsActivity, response.body().getMessage(),Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(Call<Otvet> call, Throwable t) {
                    object.setIsProgress(new ObservableBoolean(false));
                    Toast.makeText(addNewsActivity, "Ваша новость отправлена на модерацию...",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialog(String s){

        final Dialog dialog = new Dialog(this.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_news);


        TextView text = (TextView) dialog.findViewById(R.id.textView36);
        if(Integer.parseInt(s)==1) text.setText(getResources().getString(R.string.text_news));
        else text.setText(getResources().getString(R.string.text_news_2));
        TextView tvDONE = (TextView) dialog.findViewById(R.id.textView37);
        TextView tvClose = (TextView) dialog.findViewById(R.id.textView38);

        tvDONE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                dialog.dismiss();
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



        dialog.show();

    }

    public void sendMessage(){
        App.getApi().sendAddNews(myId, myToken).enqueue(new Callback<Otvet>() {
            @Override
            public void onResponse(Call<Otvet> call, Response<Otvet> response) {
                Toast.makeText(addNewsActivity, response.body().getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Otvet> call, Throwable t) {
                Toast.makeText(addNewsActivity,"Проверьте подключение к инткрнету...",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showDialogSelectImage(){

        dialogSelectImage = new Dialog(this.getActivity());
        dialogSelectImage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSelectImage.setContentView(R.layout.alert_select_image);

        TextView tvGalery = (TextView) dialogSelectImage.findViewById(R.id.select_image_galery);
        TextView tvPhotos = (TextView) dialogSelectImage.findViewById(R.id.select_image_photos);

        tvGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSelectImage.dismiss();
                selectImage();

            }
        });

        tvPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSelectImage.dismiss();
                takePhoto();
            }
        });

        dialogSelectImage.show();

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);


        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = addNewsActivity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }


}
