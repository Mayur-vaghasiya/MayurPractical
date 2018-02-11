package com.example.mayurpractical.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.mayurpractical.R;
import com.example.mayurpractical.SharedPreferenceUtility;
import com.example.mayurpractical.adapter.CountryAdapter;
import com.example.mayurpractical.apploader.ApplicationLoader;
import com.example.mayurpractical.custom_views.CustomProgressDialog;
import com.example.mayurpractical.databasehelper.DatabaseHandler;
import com.example.mayurpractical.listener.ClickListener;
import com.example.mayurpractical.model.CountryModel;
import com.example.mayurpractical.retro.RetrofitInstance;
import com.example.mayurpractical.service.GetCountryDataService;
import com.example.mayurpractical.util.CleanableEditText;
import com.example.mayurpractical.util.CommonUtility;
import com.example.mayurpractical.util.NetworkStatus;
import com.example.mayurpractical.util.Staticdatautility;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCountry;
    private ArrayList<CountryModel.Result> countryList = null;
    private CountryAdapter countryAdapter = null;
    private GetCountryDataService service = null;
    private Activity activity = null;
    private LinearLayoutManager layoutManager = null;
    private CustomProgressDialog progress = null;
    private CleanableEditText search;
    private FloatingActionButton fab;
    private DatabaseHandler dhelper;
    private AppCompatImageView aciv_flag;
    private String stringPhotoImage = "", photo_image_name = "";
    private byte[] photo;
    private SharedPreferenceUtility preferenceUtility = null;
    private  ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = MainActivity.this;



        preferenceUtility = ApplicationLoader.getAppLoader().getPreferencesUtility();
        dhelper = new DatabaseHandler(this);


        search = (CleanableEditText) findViewById(R.id.search);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerViewCountry = (RecyclerView) findViewById(R.id.rvcountryList);
        layoutManager = new LinearLayoutManager(activity, layoutManager.VERTICAL, false);
        recyclerViewCountry.setLayoutManager(layoutManager);

        if (NetworkStatus.getConnectivityStatusString(activity)) {

            getcountryList();

        } else {

            setRecyclerViewData();
            Toast.makeText(activity, "OffLine", Toast.LENGTH_LONG).show();
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (null != countryList || null != countryList) {
                    String text = search.getText().toString().toLowerCase(Locale.getDefault());
                    countryAdapter.filters(text);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


        recyclerViewCountry.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerViewCountry, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                CommonUtility.hideKeyboard(activity);
                showSaveDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SaveDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

    }

    public void getcountryList() {
        service = RetrofitInstance.getRetrofitInstance().create(GetCountryDataService.class);
        Call<CountryModel> call = service.getJSON();
        progress = new CustomProgressDialog(activity).
                setStyle(CustomProgressDialog.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f)
                .show();
        call.enqueue(new Callback<CountryModel>() {
            @Override
            public void onResponse(Call<CountryModel> call, Response<CountryModel> response) {

                countryList = (ArrayList<CountryModel.Result>) response.body().getRestResponse().getResult();

                for (CountryModel.Result result : countryList) {
                    int rowCount = dhelper.existRecord(result);
                    if (rowCount > 0) {

                    } else {
                        dhelper.addCountry(result);
                    }
                }
                setRecyclerViewData();
                progress.dismiss();
                Toast.makeText(activity, "Sync Data with Server", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<CountryModel> call, Throwable t) {
                progress.dismiss();
                setRecyclerViewData();
                Toast.makeText(MainActivity.this, "Sync Fail!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setRecyclerViewData() {

        countryList = dhelper.getAllcountry();
        countryAdapter = new CountryAdapter(new WeakReference<Context>(activity), countryList);
        recyclerViewCountry.setAdapter(countryAdapter);
    }

    private void showSaveDialog(int position) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        final AppCompatTextView actv_name = (AppCompatTextView) dialog.findViewById(R.id.tv_name);
        actv_name.setSelected(true);
        final AppCompatTextView actv_codetwo = (AppCompatTextView) dialog.findViewById(R.id.tv_codetwo);
        final AppCompatTextView actv_namethree = (AppCompatTextView) dialog.findViewById(R.id.tv_codethree);
        final AppCompatImageView aciv_photo = (AppCompatImageView) dialog.findViewById(R.id.aciv_flag);


        actv_name.setText("CountryName : " + countryList.get(position).getName().toString());
        actv_codetwo.setText("AlphaCode2 : " + countryList.get(position).getAlpha2Code().toString());
        actv_namethree.setText("AlphaCode3 : " + countryList.get(position).getAlpha3Code().toString());
        byte[] image = countryList.get(position).getImage();
        if (null != image) {
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            aciv_photo.setImageBitmap(bmp);
        }
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.roundedcorner);
        dialog.show();
    }


    private void SaveDialog() {

        final Dialog dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_addcountry);
        final AppCompatEditText acet_name = (AppCompatEditText) dialog.findViewById(R.id.edt_search_name);
        final AppCompatEditText acet_codetwo = (AppCompatEditText) dialog.findViewById(R.id.edt_codetwo);
        final AppCompatEditText acet_namethree = (AppCompatEditText) dialog.findViewById(R.id.edt_codethree);
        aciv_flag = (AppCompatImageView) dialog.findViewById(R.id.aciv_flag);

        aciv_flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectImage();
                getImageFromGallery();
            }
        });

        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CountryModel.Result result = new CountryModel.Result();

                result.setName(acet_name.getText().toString().trim());
                result.setAlpha2Code(acet_codetwo.getText().toString().trim());
                result.setAlpha3Code(acet_namethree.getText().toString().trim());
                result.setImage(photo);

                dhelper.addCountry(result);
                countryList.add(result);
                countryAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.roundedcorner);
        dialog.show();
    }


    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private void getImageFromGallery() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    if (bitmap != null) {
                        aciv_flag.setImageBitmap(bitmap);
                        InputStream iStream = null;
                        iStream = activity.getContentResolver().openInputStream(resultUri);
                        photo = CommonUtility.getBytes(iStream);
                        stringPhotoImage = Base64.encodeToString(photo, Base64.DEFAULT);
                        photo_image_name = "";
                        Log.e("stringPhotoImage", stringPhotoImage);

                    } else {
                        aciv_flag.setImageResource(R.drawable.ico_image_proof);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case Staticdatautility.GALLERY_IMAGE:

                for (int i = 0; i < permissions.length; i++) {

                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) || permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            preferenceUtility.setString(Staticdatautility.STORAGE, getString(R.string.true_false));

                        }
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {

                        if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) || permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            preferenceUtility.setString(Staticdatautility.STORAGE, getString(R.string.false_true));
                        }
                    }
                }

                if (preferenceUtility.getString(Staticdatautility.STORAGE).equals(getString(R.string.true_false)) && requestCode == Staticdatautility.GALLERY_IMAGE) {

                    getImageFromGallery();

                } else if (requestCode == Staticdatautility.REQUEST_CODE1) {

                    Toast.makeText(activity, getString(R.string.denied_permission), Toast.LENGTH_SHORT).show();

                }

                break;

            default: {

                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            }
        }
    }

}
