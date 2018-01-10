package com.example.x.bunuscustomer;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.x.bunuscustomer.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.example.x.bunuscustomer.fragments.FragmentScanner;
import com.example.x.bunuscustomer.retrofit.App;
import com.example.x.bunuscustomer.retrofit.Otvet;
import com.example.x.bunuscustomer.retrofit.buyQR.QrExample;
import com.google.zxing.Result;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BuyActivity extends AppCompatActivity implements  TextWatcher{

    private static final String APP_PREFERENCES = "config";
    private static final String APP_PREFERENCES_TOKEN = "token";
    private static final String APP_PREFERENCES_ID = "id";
    private static final String APP_PREFERENCES_PHONE = "phone";
    private static final String APP_PREFERENCES_IMG = "img";
    private static final String TAG = "RESULR";
    private SharedPreferences mSettings;
    private boolean isShow = false;
    Dialog dialog = null;
    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;
    private ViewGroup mainLayout;
    private TextView resultTextView;
    private QRCodeReaderView qrCodeReaderView;
    private CheckBox flashlightCheckBox;
    private CheckBox enableDecodingCheckBox;
    private PointsOverlayView pointsOverlayView;

    TextView tvIdUser, tvColFriends, tvColBalls, tvSale, tvColBuy,tvitog;
    EditText etPrice;
    int loyalty = 1;
    double col_balls = 0;
    double sum_buy = 0;
    double sale;
    int col_Buy;
    int status_company_user = 0;
    double step1, step2, step3, step4;
    double step1Col, step2Col, step3Col, step4Col;

    private static Fragment fragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    Button button;

    String myId, myToken;
    RelativeLayout rel_buy_dialog;

    private ZXingScannerView mScannerView;
    ImageView img_error_top, img_error_rigt, img_error_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_buy);



        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String token = mSettings.getString(APP_PREFERENCES_TOKEN, "");
        if(token.length()>2){

            try {
                init();
                transaction();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    transaction();
                } else {
                    requestCameraPermission();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }else {
            Intent intent = new Intent(BuyActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void transaction(){
        fragment = new FragmentScanner(this);
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }



    @Override protected void onPause() {
        super.onPause();
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        try {
            if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
                return;
            }

            if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
                transaction();
                //initQRCodeReaderView();
            } else {
                Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
//    @Override public void onQRCodeRead(String text, PointF[] points) {
//
//        try {
//            String pText1 = new String(text.getBytes("UTF-8"), "UTF-8");
//            String x1 = pText1;
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        if(text.length()>50 && text.contains("VTEME2017")){
//
//            try{
//                String pText = new String(text.getBytes("UTF-8"), "UTF-8");
//                String x = pText;
//
//                App.getApi().getUsrtInfoQR(myId, myToken, x).enqueue(new Callback<List<QrExample>>() {
//                    @Override
//                    public void onResponse(Call<List<QrExample>> call, Response<List<QrExample>> response) {
//
//                        try {
//                            List<QrExample> list = response.body();
//                            QrExample qr = list.get(0);
//
//
//                            status_company_user = Integer.parseInt(qr.getCompanyUser().getStatus_company_user());
//                            tvIdUser.setText(qr.getUserinfo().getId());
//                            tvColFriends.setText(qr.getCompanyUser().getColFriend());
//                            tvColBalls.setText(qr.getCompanyUser().getColBal());
//                            tvColBuy.setText(qr.getCompanyUser().getColBuy());
//                            col_balls = Double.parseDouble(qr.getCompanyUser().getColBal());
//                            if (Integer.parseInt(qr.getLoyalty()) == 1){
//                                sale = Double.parseDouble(qr.getSale());
//                                tvSale.setText(qr.getSale());
//
//                            }
//                            else if(Integer.parseInt(qr.getLoyalty()) == 2) {
//                                tvSale.setText(qr.getLoyaltyStep().getStep1());
//                                step1 = Double.parseDouble(qr.getLoyaltyStep().getStep1());
//                                step2 = Double.parseDouble(qr.getLoyaltyStep().getStep2());
//                                step3 = Double.parseDouble(qr.getLoyaltyStep().getStep3());
//                                step4 = Double.parseDouble(qr.getLoyaltyStep().getStep4());
//
//                                step1Col = Double.parseDouble(qr.getLoyaltyStep().getStep1Col());
//                                step2Col = Double.parseDouble(qr.getLoyaltyStep().getStep2Col());
//                                step3Col = Double.parseDouble(qr.getLoyaltyStep().getStep3Col());
//                                step4Col = Double.parseDouble(qr.getLoyaltyStep().getStep4Col());
//
//                                loyalty = 2;
////                                sum_buy = Double.parseDouble(qr.getCompanyUser().getSumBuy());
////
////                                if(sum_buy>=0 && sum_buy< step2Col) {
////                                    sale = step1;
////                                }else if(sum_buy>step2Col && sum_buy < step3Col) sale = step2;
////                                else if(sum_buy>step3Col && sum_buy< step4Col) sale = step3;
////                                else sale = step4;
//
//                                //tvSale.setText(String.valueOf(sale));
//
//                            }else {
//                                int col_buy = Integer.parseInt(qr.getCompanyUser().getColBuy());
//                                if(col_buy>=0 && col_buy<Integer.parseInt(qr.getLoyaltyStep().getStep2Col())) {
//                                    tvSale.setText(qr.getLoyaltyStep().getStep1());
//                                    sale = Double.parseDouble(qr.getLoyaltyStep().getStep1());
//                                }
//                                else if(col_buy>Integer.parseInt(qr.getLoyaltyStep().getStep2Col()) && col_buy<Integer.parseInt(qr.getLoyaltyStep().getStep3Col())){
//                                    tvSale.setText(qr.getLoyaltyStep().getStep2());
//                                    sale = Double.parseDouble(qr.getLoyaltyStep().getStep2());
//                                }else if(col_buy>Integer.parseInt(qr.getLoyaltyStep().getStep3Col()) && col_buy<Integer.parseInt(qr.getLoyaltyStep().getStep4Col())){
//                                    tvSale.setText(qr.getLoyaltyStep().getStep3());
//                                    sale = Double.parseDouble(qr.getLoyaltyStep().getStep3());
//                                }else{
//                                    tvSale.setText(qr.getLoyaltyStep().getStep4());
//                                    sale = Double.parseDouble(qr.getLoyaltyStep().getStep4());
//                                }
//                                loyalty = 3;
//                                col_Buy = Integer.parseInt(qr.getCompanyUser().getColBuy());
//                                tvSale.setText(String.valueOf(sale));
//                            }
//
//                            if (dialog != null && !dialog.isShowing()) dialog.show();
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<QrExample>> call, Throwable t) {
//                        String s = "";
//                    }
//                });
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
//
//
//        pointsOverlayView.setPoints(points);
//    }

    private void requestCameraPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Snackbar.make(button, "Camera access is required to display the camera preview.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        ActivityCompat.requestPermissions(BuyActivity.this, new String[] {
                                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, MY_PERMISSION_REQUEST_CAMERA);
                    }
                }).show();
            } else {
                Snackbar.make(button, "Permission is not available. Requesting camera permission.",
                        Snackbar.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, MY_PERMISSION_REQUEST_CAMERA);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void init(){
        //mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        myId = mSettings.getString(APP_PREFERENCES_ID,"");
        myToken = mSettings.getString(APP_PREFERENCES_TOKEN,"");
        createDialog();
        button = (Button) findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    App.getApi().getStatus(myId,myToken).enqueue(new Callback<Otvet>() {
                        @Override
                        public void onResponse(Call<Otvet> call, Response<Otvet> response) {
                            if(Integer.parseInt(response.body().getMessage())==0){
                                startActivity(new Intent(BuyActivity.this, InfoShop.class));
                            }else {
                                rel_buy_dialog.setVisibility(View.VISIBLE);
                                //Toast.makeText(button.getContext(), "Требуется приобрести платный пакет.",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Otvet> call, Throwable t) {
                            Toast.makeText(button.getContext(), "Ошибка соединения с сервером...",Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.imageView5);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });

        rel_buy_dialog = (RelativeLayout) findViewById(R.id.rel_buy_dialog);

        img_error_bg = (ImageView) findViewById(R.id.img_bg_error_buy);
        img_error_rigt = (ImageView) findViewById(R.id.img_right_error_buy);
        img_error_top = (ImageView) findViewById(R.id.img_top_error_buy);

        Picasso.with(img_error_bg.getContext()).load(R.drawable.bg_rel_buy_error);
        Picasso.with(img_error_rigt.getContext()).load(R.drawable.bottom);
        Picasso.with(img_error_top.getContext()).load(R.drawable.top);

        ImageView button = (ImageView) findViewById(R.id.img_button_close_error_buy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rel_buy_dialog.getVisibility()==View.VISIBLE) rel_buy_dialog.setVisibility(View.GONE);
            }
        });


    }

    private void initQRCodeReaderView() {
        View content = getLayoutInflater().inflate(R.layout.content_buy, mainLayout, true);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        myId = mSettings.getString(APP_PREFERENCES_ID,"");
        myToken = mSettings.getString(APP_PREFERENCES_TOKEN,"");

        qrCodeReaderView = (QRCodeReaderView) content.findViewById(R.id.qrdecoderview);
        resultTextView = (TextView) content.findViewById(R.id.result_text_view);
        flashlightCheckBox = (CheckBox) content.findViewById(R.id.flashlight_checkbox);
        enableDecodingCheckBox = (CheckBox) content.findViewById(R.id.enable_decoding_checkbox);
        pointsOverlayView = (PointsOverlayView) content.findViewById(R.id.points_overlay_view);
        FrameLayout frame = (FrameLayout) content.findViewById(R.id.main_container);



        createDialog();





        qrCodeReaderView.setAutofocusInterval(2000L);
        //qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setBackCamera();
        flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                qrCodeReaderView.setTorchEnabled(isChecked);
            }
        });
        enableDecodingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                qrCodeReaderView.setQRDecodingEnabled(isChecked);
            }
        });
        qrCodeReaderView.startCamera();
    }


    public void exit(){
        SharedPreferences.Editor editor1 = mSettings.edit();
        editor1.putString(APP_PREFERENCES_TOKEN,"");
        editor1.apply();
        startActivity(new Intent(this,LoginActivity.class));
    }

    public void getInfo(String text){
        dialog.show();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
            if(dialog != null && !dialog.isShowing()) dialog.hide();
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                moveTaskToBack(true); return true;
            }
            return super.onKeyDown(keyCode, event);
    }

    public void createDialog(){

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_buy);

        tvIdUser = (TextView) dialog.findViewById(R.id.tvIdUser);
        tvColFriends = (TextView) dialog.findViewById(R.id.tvColFriends);
        tvColBalls = (TextView) dialog.findViewById(R.id.tvColBalls);
        tvSale = (TextView) dialog.findViewById(R.id.tvSale);
        tvColBuy = (TextView) dialog.findViewById(R.id.tvColBuy);
        tvitog = (TextView) dialog.findViewById(R.id.tvItog);

        etPrice = (EditText) dialog.findViewById(R.id.editText6);
        etPrice.addTextChangedListener(this);

        Button btnSend = (Button) dialog.findViewById(R.id.button5);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getApi().setBuyTransaktion(tvIdUser.getText().toString(),
                        myId,myToken,etPrice.getText().toString()).enqueue(new Callback<Otvet>() {
                    @Override
                    public void onResponse(Call<Otvet> call, Response<Otvet> response) {
                        etPrice.setText("0");
                        if(Integer.parseInt(response.body().getCode())==201) Toast.makeText(BuyActivity.this, "Покупка успешно оформлена", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Otvet> call, Throwable t) {
                        Toast.makeText(BuyActivity.this, "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        isShow = true;
    }

    public void getItog(){

    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String s = editable.toString();
        //tvitog.setText(s);
        try{
            switch (loyalty){
                case 1:
                    double price = Double.parseDouble(s);
                    double price1 = price * (sale/100);
                    price = price - price1;
                    double no_balls = price;
                    if(status_company_user==1){
                        price = price - col_balls;
                    }
                    if(price>0){
                        tvitog.setText(String.valueOf(price));
                        tvColBalls.setText(String.valueOf(col_balls));
                    }
                    else {
                        if(status_company_user==1){
                           double xz = col_balls - no_balls;
                            tvColBalls.setText(String.valueOf(xz));
                        }
                        tvitog.setText("0");

                    }
                    break;
                case 2:
                    getSum(s);
                    break;
                case 3:
                    getNakopit(s);
                    break;

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getSum(String s){
        double price = Double.parseDouble(s);
        double sum_buy = price;
        double priceStep = 0;
        if(sum_buy>=0 && sum_buy< step2Col) {
            priceStep = step1;
        }else if(sum_buy>=step2Col && sum_buy < step3Col) priceStep = step2;
        else if(sum_buy>=step3Col && sum_buy< step4Col) priceStep = step3;
        else priceStep = step4;



        tvSale.setText(String.valueOf(priceStep));
        double price1 = price * (priceStep/100);
        price = price - price1;
        double no_balls = price;
        if(status_company_user==1){
            price = price - col_balls;
        }
        if(price>0){
            tvitog.setText(String.valueOf(price));
            tvColBalls.setText(String.valueOf(col_balls));
        }
        else {
            if(status_company_user==1){
                double xz = col_balls - no_balls;
                tvColBalls.setText(String.valueOf(xz));
            }
            tvitog.setText("0");

        }
    }

    private void getNakopit(String s){
        double price = Double.parseDouble(s);
        double price1 = price * (sale/100);
        price = price - price1;
        double no_balls = price;
        if(status_company_user==1){
            price = price - col_balls;
        }
        if(price>0){
            tvitog.setText(String.valueOf(price));
            tvColBalls.setText(String.valueOf(col_balls));
        }
        else {
            if(status_company_user==1){
                double xz = col_balls - no_balls;
                tvColBalls.setText(String.valueOf(xz));
            }
            tvitog.setText("0");

        }
    }

//    @Override
//    public void handleResult(Result result) {
//
//        Log.v(TAG, result.getText()); // Prints scan results
//        Log.v(TAG, result.getBarcodeFormat().toString());
//
//        if(result.getBarcodeFormat().toString()=="qrcode") {}
//
//        // Prints the scan format (qrcode, pdf417 etc.)
//
//        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
//    }

    public void setResult(Result rawResult){

        Result res = rawResult;
        if(res.getBarcodeFormat().toString().contains("EAN_13")) strish(res.getText());
        else if(res.getBarcodeFormat().toString().contains("QR_CODE")) qr(res.getText());
        int i = 0;

    }

    private void strish(String text) {
        String id = "";
        if(text.contains("2017")){
            String t = text;
            id  = t.substring(0,5)+ t.substring(8, t.length()-1);
            String dsd ="";
        }
        else {
            String col_id = String.valueOf(text.toCharArray()[11]);
            id = text.substring(0, Integer.parseInt(col_id));
        }

        try{
            //String pText = new String(text.getBytes("UTF-8"), "UTF-8");
            // x = pText;

            App.getApi().getUsrtInfoQRShtrih(myId, myToken, id).enqueue(new Callback<List<QrExample>>() {
                @Override
                public void onResponse(Call<List<QrExample>> call, Response<List<QrExample>> response) {

                    try {
                        List<QrExample> list = response.body();
                        QrExample qr = list.get(0);


                        status_company_user = Integer.parseInt(qr.getCompanyUser().getStatus_company_user());
                        tvIdUser.setText(qr.getUserinfo().getId());
                        tvColFriends.setText(qr.getCompanyUser().getColFriend());
                        tvColBalls.setText(qr.getCompanyUser().getColBal());
                        tvColBuy.setText(qr.getCompanyUser().getColBuy());
                        col_balls = Double.parseDouble(qr.getCompanyUser().getColBal());
                        if (Integer.parseInt(qr.getLoyalty()) == 1){
                            sale = Double.parseDouble(qr.getSale());
                            tvSale.setText(qr.getSale());

                        }
                        else if(Integer.parseInt(qr.getLoyalty()) == 2) {
                            tvSale.setText(qr.getLoyaltyStep().getStep1());
                            step1 = Double.parseDouble(qr.getLoyaltyStep().getStep1());
                            step2 = Double.parseDouble(qr.getLoyaltyStep().getStep2());
                            step3 = Double.parseDouble(qr.getLoyaltyStep().getStep3());
                            step4 = Double.parseDouble(qr.getLoyaltyStep().getStep4());

                            step1Col = Double.parseDouble(qr.getLoyaltyStep().getStep1Col());
                            step2Col = Double.parseDouble(qr.getLoyaltyStep().getStep2Col());
                            step3Col = Double.parseDouble(qr.getLoyaltyStep().getStep3Col());
                            step4Col = Double.parseDouble(qr.getLoyaltyStep().getStep4Col());

                            loyalty = 2;
//                                sum_buy = Double.parseDouble(qr.getCompanyUser().getSumBuy());
//
//                                if(sum_buy>=0 && sum_buy< step2Col) {
//                                    sale = step1;
//                                }else if(sum_buy>step2Col && sum_buy < step3Col) sale = step2;
//                                else if(sum_buy>step3Col && sum_buy< step4Col) sale = step3;
//                                else sale = step4;

                            //tvSale.setText(String.valueOf(sale));

                        }else {
                            int col_buy = Integer.parseInt(qr.getCompanyUser().getColBuy());
                            if(col_buy>=0 && col_buy<Integer.parseInt(qr.getLoyaltyStep().getStep2Col())) {
                                tvSale.setText(qr.getLoyaltyStep().getStep1());
                                sale = Double.parseDouble(qr.getLoyaltyStep().getStep1());
                            }
                            else if(col_buy>Integer.parseInt(qr.getLoyaltyStep().getStep2Col()) && col_buy<Integer.parseInt(qr.getLoyaltyStep().getStep3Col())){
                                tvSale.setText(qr.getLoyaltyStep().getStep2());
                                sale = Double.parseDouble(qr.getLoyaltyStep().getStep2());
                            }else if(col_buy>Integer.parseInt(qr.getLoyaltyStep().getStep3Col()) && col_buy<Integer.parseInt(qr.getLoyaltyStep().getStep4Col())){
                                tvSale.setText(qr.getLoyaltyStep().getStep3());
                                sale = Double.parseDouble(qr.getLoyaltyStep().getStep3());
                            }else{
                                tvSale.setText(qr.getLoyaltyStep().getStep4());
                                sale = Double.parseDouble(qr.getLoyaltyStep().getStep4());
                            }
                            loyalty = 3;
                            col_Buy = Integer.parseInt(qr.getCompanyUser().getColBuy());
                            tvSale.setText(String.valueOf(sale));
                        }

                        if (dialog != null && !dialog.isShowing()) dialog.show();
                    }catch (Exception e){
                        Log.e("dsds",e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<List<QrExample>> call, Throwable t) {
                    String s = "";
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
        int i = 0;

    }

    private void qr(String text){
        if(text.length()>50 && text.contains("VTEME2017")){
//
            try{
                //String pText = new String(text.getBytes("UTF-8"), "UTF-8");
                // x = pText;

                App.getApi().getUsrtInfoQR(myId, myToken, text).enqueue(new Callback<List<QrExample>>() {
                    @Override
                    public void onResponse(Call<List<QrExample>> call, Response<List<QrExample>> response) {

                        try {
                            List<QrExample> list = response.body();
                            QrExample qr = list.get(0);


                            status_company_user = Integer.parseInt(qr.getCompanyUser().getStatus_company_user());
                            tvIdUser.setText(qr.getUserinfo().getId());
                            tvColFriends.setText(qr.getCompanyUser().getColFriend());
                            tvColBalls.setText(qr.getCompanyUser().getColBal());
                            tvColBuy.setText(qr.getCompanyUser().getColBuy());
                            col_balls = Double.parseDouble(qr.getCompanyUser().getColBal());
                            if (Integer.parseInt(qr.getLoyalty()) == 1){
                                sale = Double.parseDouble(qr.getSale());
                                tvSale.setText(qr.getSale());

                            }
                            else if(Integer.parseInt(qr.getLoyalty()) == 2) {
                                tvSale.setText(qr.getLoyaltyStep().getStep1());
                                step1 = Double.parseDouble(qr.getLoyaltyStep().getStep1());
                                step2 = Double.parseDouble(qr.getLoyaltyStep().getStep2());
                                step3 = Double.parseDouble(qr.getLoyaltyStep().getStep3());
                                step4 = Double.parseDouble(qr.getLoyaltyStep().getStep4());

                                step1Col = Double.parseDouble(qr.getLoyaltyStep().getStep1Col());
                                step2Col = Double.parseDouble(qr.getLoyaltyStep().getStep2Col());
                                step3Col = Double.parseDouble(qr.getLoyaltyStep().getStep3Col());
                                step4Col = Double.parseDouble(qr.getLoyaltyStep().getStep4Col());

                                loyalty = 2;
//                                sum_buy = Double.parseDouble(qr.getCompanyUser().getSumBuy());
//
//                                if(sum_buy>=0 && sum_buy< step2Col) {
//                                    sale = step1;
//                                }else if(sum_buy>step2Col && sum_buy < step3Col) sale = step2;
//                                else if(sum_buy>step3Col && sum_buy< step4Col) sale = step3;
//                                else sale = step4;

                                //tvSale.setText(String.valueOf(sale));

                            }else {
                                int col_buy = Integer.parseInt(qr.getCompanyUser().getColBuy());
                                if(col_buy>=0 && col_buy<Integer.parseInt(qr.getLoyaltyStep().getStep2Col())) {
                                    tvSale.setText(qr.getLoyaltyStep().getStep1());
                                    sale = Double.parseDouble(qr.getLoyaltyStep().getStep1());
                                }
                                else if(col_buy>Integer.parseInt(qr.getLoyaltyStep().getStep2Col()) && col_buy<Integer.parseInt(qr.getLoyaltyStep().getStep3Col())){
                                    tvSale.setText(qr.getLoyaltyStep().getStep2());
                                    sale = Double.parseDouble(qr.getLoyaltyStep().getStep2());
                                }else if(col_buy>Integer.parseInt(qr.getLoyaltyStep().getStep3Col()) && col_buy<Integer.parseInt(qr.getLoyaltyStep().getStep4Col())){
                                    tvSale.setText(qr.getLoyaltyStep().getStep3());
                                    sale = Double.parseDouble(qr.getLoyaltyStep().getStep3());
                                }else{
                                    tvSale.setText(qr.getLoyaltyStep().getStep4());
                                    sale = Double.parseDouble(qr.getLoyaltyStep().getStep4());
                                }
                                loyalty = 3;
                                col_Buy = Integer.parseInt(qr.getCompanyUser().getColBuy());
                                tvSale.setText(String.valueOf(sale));
                            }

                            if (dialog != null && !dialog.isShowing()) dialog.show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<QrExample>> call, Throwable t) {
                        String s = "";
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private boolean checkStatus(){
        final boolean[] bl = {false};
        try {
            App.getApi().getStatus(myId,myToken).enqueue(new Callback<Otvet>() {
                @Override
                public void onResponse(Call<Otvet> call, Response<Otvet> response) {
                    if(Integer.parseInt(response.body().getMessage())==0){
                        bl[0] = true;
                    }else bl[0] = false;
                }

                @Override
                public void onFailure(Call<Otvet> call, Throwable t) {
                    bl[0] = false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            bl[0] = false;
        }
        return bl[0];
    }
}
