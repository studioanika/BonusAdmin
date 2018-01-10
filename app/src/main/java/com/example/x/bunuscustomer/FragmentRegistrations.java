package com.example.x.bunuscustomer;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.x.bunuscustomer.classes.Model;
import com.example.x.bunuscustomer.*;
import com.example.x.bunuscustomer.classes.OrganizationsObject;
import com.example.x.bunuscustomer.classes.SpinnerCategory;
import com.example.x.bunuscustomer.classes.SpinnerTime;
import com.example.x.bunuscustomer.classes.TestObject;
import com.example.x.bunuscustomer.handlers.RegistrationHandler;
import com.example.x.bunuscustomer.retrofit.App;
import com.example.x.bunuscustomer.retrofit.Otvet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by mobi app on 28.06.2017.
 */

public class FragmentRegistrations extends Fragment {

    OrganizationsObject organizationsObjectReg;
    TestObject testObject = new TestObject();
    SpinnerCategory spinnerCategory = new SpinnerCategory();
    SpinnerTime spinnerTime = new SpinnerTime();
    SpinnerTime spinnerTime2 = new SpinnerTime();
    Model model = new Model();
    RegistrationHandler handler;
    private static final int RESULT_SELECT_IMAGE = 1;
    LinearLayout view;

    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;


    RegistrationActivity activity;
    View v;


    public FragmentRegistrations(RegistrationActivity activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false);

        organizationsObjectReg = new OrganizationsObject();
        handler = new RegistrationHandler(this);
        binding.setVariable(BR.organizationObjectRegs, organizationsObjectReg);
        binding.setVariable(BR.test,testObject);
        binding.setVariable(BR.spinnerCategory, spinnerCategory);
        binding.setVariable(BR.spinnerTime1,spinnerTime);
        binding.setVariable(BR.spinnerTime2,spinnerTime2);
        binding.setVariable(BR.handlerRegistration,handler);

        binding.setVariable(BR.spinnerLoyality, model);




        v = binding.getRoot();
        return  v;
    }
    public void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this.getActivity(), v);
        popupMenu.inflate(R.menu.popupmenu); // Для Android 4.0
        // для версии Android 3.0 нужно использовать длинный вариант
        // popupMenu.getMenuInflater().inflate(R.menu.popupmenu,
        // popupMenu.getMenu());

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Toast.makeText(PopupMenuDemoActivity.this,
                        // item.toString(), Toast.LENGTH_LONG).show();
                        // return true;
                        switch (item.getItemId()) {

                            case R.id.menu1:
                                organizationsObjectReg.setLoyalty_id(new ObservableInt(1));
                                organizationsObjectReg.setLoyalty_text(new ObservableField<String>("Фиксированная"));
                                organizationsObjectReg.setLoyalty_is(new ObservableBoolean(true));
                                return true;
                            case R.id.menu2:
                                organizationsObjectReg.setLoyalty_id(new ObservableInt(2));
                                organizationsObjectReg.setLoyalty_text(new ObservableField<String>("От суммы покупки"));
                                organizationsObjectReg.setLoyalty_is(new ObservableBoolean(false));

                                return true;
                            case R.id.menu3:
                                organizationsObjectReg.setLoyalty_id(new ObservableInt(3));
                                organizationsObjectReg.setLoyalty_text(new ObservableField<String>("Накопительная"));
                                organizationsObjectReg.setLoyalty_is(new ObservableBoolean(false));
                                return true;

                            default:
                                return false;
                        }
                    }
                });


        popupMenu.show();
    }

    public void clickFriend(){
        if(organizationsObjectReg.getIsFriend().get()) organizationsObjectReg.setIsFriend(new ObservableBoolean(false));
        else organizationsObjectReg.setIsFriend(new ObservableBoolean(true));
    }

    public void clickOferta(){
        if(organizationsObjectReg.getIsOferta().get()) organizationsObjectReg.setIsOferta(new ObservableBoolean(false));
        else organizationsObjectReg.setIsOferta(new ObservableBoolean(true));
    }

    public void selectImage(){
        //open album to select image
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallaryIntent, RESULT_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null){
            //set the selected image to image variable
            Uri image = data.getData();
            String img = image.toString();
            //adveristing.setImg(new ObservableField<Uri>(image));
            organizationsObjectReg.setBitmap(new ObservableField<Uri>(image));
            organizationsObjectReg.setIsImg(new ObservableBoolean(true));
            Bitmap bitmap;
            try {
                File file = new File(image.toString());
                bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), image);
                bitmap =  decodeSampledBitmapFromResource(getPath(image), 512, 512);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //compress the image to jpg format
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            /*
            * encode image to base64 so that it can be picked by saveImage.php file
            * */
                String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);
                organizationsObjectReg.setImage(new ObservableField<String>(encodeImage));
                Toast.makeText(activity, "Фото добавлено, на модерации...", Toast.LENGTH_SHORT).show();
                //adveristing.setBitmap(new ObservableField<String>(encodeImage));
                organizationsObjectReg.setImage(new ObservableField<String>(encodeImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
//            imageView.setImageURI(image);
//            imageViewOb.setImageURI(image);

        }
    }

    public void reg(){
       if(!getValid()){
       }else{
           activity.progress();
           App.getApi().regisnrations(organizationsObjectReg.getEmail().get(),
                   organizationsObjectReg.getName().get(),
                   organizationsObjectReg.getIndex().get(),
                   organizationsObjectReg.getCity().get(),
                   organizationsObjectReg.getStreet().get(),
                   organizationsObjectReg.getHome().get(),
                   organizationsObjectReg.getKv().get(),
                   spinnerCategory.getPosition().get(),
                   spinnerTime.getType()[spinnerTime.getPosition().get()],
                   spinnerTime2.getType()[spinnerTime2.getPosition().get()],
                   organizationsObjectReg.getPhone().get(),
                   String.valueOf(organizationsObjectReg.getLoyalty_id().get()),
                   organizationsObjectReg.getFixed_price().get(),
                   organizationsObjectReg.getFriend_frice().get(),
                   md5(organizationsObjectReg.getPassword1().get()),
                   organizationsObjectReg.getImage().get(),
                   organizationsObjectReg.getBuy1().get(),
                   organizationsObjectReg.getBuy2().get(),
                   organizationsObjectReg.getBuy3().get(),
                   organizationsObjectReg.getBuy4().get(),
                   organizationsObjectReg.getProc1().get(),
                   organizationsObjectReg.getProc2().get(),
                   organizationsObjectReg.getProc3().get(),
                   organizationsObjectReg.getProc4().get(),
                   organizationsObjectReg.getUnp().get(),
                   organizationsObjectReg.getDate().get(),
                   organizationsObjectReg.getInfo().get()

           ).enqueue(new Callback<Otvet>() {
               @Override
               public void onResponse(Call<Otvet> call, Response<Otvet> response) {
                   activity.progress();
                   Otvet otvet = response.body();
                   if(otvet.getCode().equals("201")){
                       activity.toast("Ваша компания отправлена на модерацию.");
                       startActivity(new Intent(activity, LoginActivity.class));
                   }else activity.toast(response.body().getMessage());

               }

               @Override
               public void onFailure(Call<Otvet> call, Throwable t) {
                   activity.toast("Проверьте подключение к интернету...");
                   activity.progress();
               }
           });
       }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void showDialog(){

        final Dialog dialog = new Dialog(this.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_steps);

        TextView tv_1 = (TextView) dialog.findViewById(R.id.textView13);
        TextView tv_2 = (TextView) dialog.findViewById(R.id.textView133);

        if(organizationsObjectReg.getLoyalty_id().get()==2){
            tv_1.setText(this.getResources().getString(R.string.alert1));
            tv_2.setText(this.getResources().getString(R.string.alert2));
        }else {
            tv_1.setText(this.getResources().getString(R.string.alert1_1));
            tv_2.setText(this.getResources().getString(R.string.alert2_2));
        }

        final EditText step1 = (EditText) dialog.findViewById(R.id.editText5);
        final EditText step2 = (EditText) dialog.findViewById(R.id.editText4);
        final EditText step3 = (EditText) dialog.findViewById(R.id.editText3);
        final EditText step4 = (EditText) dialog.findViewById(R.id.editText);

        final EditText proc1 = (EditText) dialog.findViewById(R.id.editText52);
        final EditText proc2 = (EditText) dialog.findViewById(R.id.editText41);
        final EditText proc3 = (EditText) dialog.findViewById(R.id.editText31);
        final EditText proc4 = (EditText) dialog.findViewById(R.id.editText2);

        TextView tvDONE = (TextView) dialog.findViewById(R.id.textView15);
        TextView tvClose = (TextView) dialog.findViewById(R.id.textView14);

        tvDONE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                organizationsObjectReg.setBuy1(new ObservableField<String>(step1.getText().toString()));
                organizationsObjectReg.setBuy2(new ObservableField<String>(step2.getText().toString()));
                organizationsObjectReg.setBuy3(new ObservableField<String>(step3.getText().toString()));
                organizationsObjectReg.setBuy4(new ObservableField<String>(step4.getText().toString()));

                organizationsObjectReg.setProc1(new ObservableField<String>(proc1.getText().toString()));
                organizationsObjectReg.setProc2(new ObservableField<String>(proc2.getText().toString()));
                organizationsObjectReg.setProc3(new ObservableField<String>(proc3.getText().toString()));
                organizationsObjectReg.setProc4(new ObservableField<String>(proc4.getText().toString()));

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

    public void showDialogOferta(){

        final Dialog dialog = new Dialog(this.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_oferta);


        TextView tvClose = (TextView) dialog.findViewById(R.id.textView40);



        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



        dialog.show();

    }

    public boolean getValid(){
        boolean x = true;
        if(organizationsObjectReg.getPassword1().get().equals(organizationsObjectReg.getPassword2().get())) {}
        else {
            Toast.makeText(activity,"Пароли должны совпадать...", Toast.LENGTH_SHORT).show();
            x = false;

        }
        if(organizationsObjectReg.getEmail().get()!=""){}
        else {
            Toast.makeText(activity,"E-mail не может быть пустым...", Toast.LENGTH_SHORT).show();
            x = false;
        }

        return x;
    }

    public void showDialogSelectImage(){

        final Dialog dialogSelectImage = new Dialog(activity);
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
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
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

}
