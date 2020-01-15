package com.quickhandslogistics.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.quickhandslogistics.R;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class GalleryPicker {

    public static final int CAPTURE_IMAGE = 100;
    public static final int CAPTURE_VIDEO = 300;

    public static final int PICK_GALLERY = 200;
    public static final int CAMERA_PERMISSION_CODE = 1000;
    public static final int ALL_PERMISSION_CODE = 5000;
    public static final int GALLERY_PERMISSION_CODE = 2000;
    private static final String TAG = "GalleryPicker";
    private Activity mActivity;
    private Fragment mFragment;
    private GalleryPickerListener galleryPickerListener;
    private Uri mSelectedImage;
    private BottomSheetDialog bottomSheetDialog;
    private Media media = Media.IMAGE;
    private File mPhotoFile;
    public static String pictureFilePath;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,  Manifest.permission.READ_EXTERNAL_STORAGE};

    private GalleryPicker(Activity activity, Fragment fragment) {
        this.mFragment = fragment;
        this.mActivity = activity;
    }

    private GalleryPicker(Activity activity) {
        this.mActivity = activity;
    }

    public static GalleryPicker with(Activity activity, Fragment fragment) {
        return new GalleryPicker(activity, fragment);
    }

    public static GalleryPicker with(Activity activity) {
        return new GalleryPicker(activity);
    }

    private static Uri getImageUri(Context mContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), photo, "", null);
        return Uri.parse(path);
    }

    public GalleryPicker setListener(GalleryPickerListener galleryPickerListener) {
        this.galleryPickerListener = galleryPickerListener;
        return this;
    }

    public GalleryPicker setMedia(Media media) {
        this.media = media;
        return this;
    }

    public GalleryPicker showDialog() {
        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_image_chooser, null);
        bottomSheetDialog = new BottomSheetDialog(mActivity);
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
        initViews(dialogView);
        return this;
    }

    private void initViews(View view) {
        View imageCamera = view.findViewById(R.id.layout_camera);
        View imageGallery = view.findViewById(R.id.layout_gallery);

        if (!new PermissionHelper(mActivity).checkPermission(permissions)) {
            if (mFragment != null)
                mFragment.requestPermissions(permissions, ALL_PERMISSION_CODE);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mActivity.requestPermissions(permissions, ALL_PERMISSION_CODE);
            }
        }

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (mFragment != null) {
                        mFragment.requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
                        return;
                    }
                    requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA,  Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
                    return;
                }
                fireIntent(Option.CAMERA, CAPTURE_IMAGE);
            }
        });

        imageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (mFragment != null) {
                        mFragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, GALLERY_PERMISSION_CODE);
                        return;
                    }
                    requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, GALLERY_PERMISSION_CODE);
                    return;
                }
                fireIntent(Option.GALLERY, PICK_GALLERY);
            }
        });
    }

    public void fetch(int requestCode, Intent data) {
        String imagePath = "";
        switch (requestCode) {
            case PICK_GALLERY:
                mSelectedImage = data.getData();
                //cropImage(mSelectedImage);
                imagePath = getImagePath(mActivity, mSelectedImage);

                if (media == Media.IMAGE) {
                    imagePath = resizeAndCompressImageBeforeSend(mActivity, imagePath, mSelectedImage);
                }

                break;
            case CAPTURE_IMAGE:
                if (media == Media.IMAGE) {
                    try {
                        File imgFile = new  File(pictureFilePath);
                        if(!imgFile.exists())  {
                            Toast.makeText(mActivity, "Path not found", Toast.LENGTH_LONG).show();
                            return;
                        }

                        mSelectedImage = Uri.fromFile(imgFile);
                        imagePath = pictureFilePath;
                        imagePath = resizeAndCompressImageBeforeSend(mActivity, imagePath, mSelectedImage);
                    }catch (Exception e) {
                    }
                } else {
                    mSelectedImage = data.getData();
                    imagePath = getImagePath(mActivity, mSelectedImage);

                }
                break;
        }

        galleryPickerListener.onMediaSelected(imagePath, mSelectedImage, media == Media.IMAGE);
    }

    public void fetchUri(int requestCode, Uri data) {
        String imagePath = "";
        switch (requestCode) {
            case PICK_GALLERY:
                mSelectedImage = data;
                imagePath = getImagePath(mActivity, mSelectedImage);

                if (media == Media.IMAGE) {
                    imagePath = resizeAndCompressImageBeforeSend(mActivity, imagePath, mSelectedImage);
                }

                break;
            case CAPTURE_VIDEO:
                mSelectedImage = data;
                imagePath = getImagePath(mActivity, mSelectedImage);
                break;
        }

        galleryPickerListener.onMediaSelected(imagePath, mSelectedImage, media == Media.IMAGE);
    }

    private String getImagePath(Activity activity, Uri uri) {
        String mSelectedFilePath = FileUtilsNew.getPath(activity, uri);

        if (mSelectedFilePath == null) {
            mSelectedFilePath = FileUtilsNew.getPathFromRemoteUri(activity, uri);
        }
        return mSelectedFilePath;
    }

    public void onResultPermission(int requestCode, int[] grantResults) {
        if (requestCode == GalleryPicker.CAMERA_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        } else if (requestCode == GalleryPicker.GALLERY_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }
    public static final String APPLICATION_ID = "com.quickhandslogistics";
    private void dispatchTakePictureIntent(Activity activity, int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity, APPLICATION_ID + ".provider", photoFile);
                mPhotoFile = photoFile;

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                if (mFragment != null) mFragment.startActivityForResult(takePictureIntent, requestCode);
                else mActivity.startActivityForResult(takePictureIntent, requestCode);
                //activity.startActivityForResult(takePictureIntent, requestCode);
            }
        }
    }

    private void captureVideo(Activity activity, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        if (mFragment != null) mFragment.startActivityForResult(intent, requestCode);
        else mActivity.startActivityForResult(intent, requestCode);

    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        pictureFilePath = mFile.getAbsolutePath();
        return mFile;
    }

    public static String resizeAndCompressImageBeforeSend(Activity context, String filePath, Uri mSelectedImageUri) {
        int compressQuality = 80;
        int imageResolution = 800;

        Bitmap bmpPic = BitmapFactory.decodeFile(filePath);

        File file = new File(filePath);
        String filename = file.getName();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bos);

        if (bmpPic.getHeight() >= imageResolution && bmpPic.getWidth() >= imageResolution) {
            bmpPic = getResizedBitmap(bmpPic, imageResolution);
        }

        try {
            rotateImageIfRequired(context, bmpPic, mSelectedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream bmpFile = new FileOutputStream(context.getCacheDir() + filename);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return context.getCacheDir() + filename;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public void openCamera() {
        fireIntent(Option.CAMERA, CAPTURE_IMAGE);
    }

    public void openGallery() {
        fireIntent(Option.GALLERY, PICK_GALLERY);
    }

    public void fireIntent(Option option, int requestCode) {
        Intent intent = new Intent();
        if (option == Option.GALLERY) {
            intent.setAction(Intent.ACTION_PICK);

            if (media == Media.IMAGE) {
                intent.setType("image/*");
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            } else  {
                requestCode = CAPTURE_VIDEO;
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"video/*"});
                intent.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            }

            try {
                if (mFragment != null) mFragment.startActivityForResult(intent, requestCode);
                else mActivity.startActivityForResult(intent, requestCode);
            } catch (ActivityNotFoundException e) {
                Log.d(TAG, "fireIntent: Activity not found");
            }

        } else if (option == Option.CAMERA) {
            if (media == Media.IMAGE) {
                dispatchTakePictureIntent(mActivity, requestCode);
            } else  {
                captureVideo(mActivity, CAPTURE_VIDEO);
            }
        }
    }

    private enum Option {
        CAMERA,
        GALLERY
    }

    public enum Media {

        IMAGE("image/jpg"),
        VIDEO("video/*");
        private String media;

        Media(String media) {
            this.media = media;
        }

        public String getMedia() {
            return media;
        }
    }

    public interface GalleryPickerListener {
        void onMediaSelected(String imagePath, Uri uri, boolean isImage);
    }
}
