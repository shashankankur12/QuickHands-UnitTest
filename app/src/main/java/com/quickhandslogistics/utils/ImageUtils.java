package com.quickhandslogistics.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageUtils {

    public static File saveSignatureTemporary(Bitmap bitmap, int quality, Context context) throws IOException {
        String imageFileName = "TEMP_" + System.currentTimeMillis() + ".jpg";
        File fileDir = context.getExternalFilesDir(null);
        File dest = new File(fileDir, imageFileName);

        OutputStream out = new FileOutputStream(dest);
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, out);

        out.close();
        bitmap.recycle();

        return dest;
    }
}
