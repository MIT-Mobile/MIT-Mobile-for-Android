package edu.mit.mitmobile2.qrreader.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;

import edu.mit.mitmobile2.qrreader.models.QrReaderResult;
import edu.mit.mitmobile2.shared.logging.LoggingManager;

/**
 * Created by serg on 6/19/15.
 */
public class ScannerImageUtils {

    public static final String getCachePath() {
        String root = Environment.getExternalStorageDirectory().toString();
        return root + "/MIT temp";
    }

    public static void saveScannedImage(Context context, byte[] data, QrReaderResult result) {
        File dir = new File(getCachePath());
        dir.mkdirs();

        String fileName = String.valueOf(result.getDate().getTime()) + ".jpg";
        File file = new File(dir, fileName);

        if (file.exists()) {
            file.delete();
        }

        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            try {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (OutOfMemoryError e) {
                LoggingManager.Timber.e(e, "Not enough free memory to rotate scanned image");
            }
            // FileOutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            result.setImageName(fileName);
        } catch (Exception e) {
            LoggingManager.Timber.e(e, "Error saving scanned image");
        }
    }

    public static boolean removeScannedImage(QrReaderResult result) {
        if (TextUtils.isEmpty(result.getImageName())) {
            return false;
        }

        File dir = new File(getCachePath());

        File file = new File(dir, result.getImageName());
        if (file.exists()) {
            return file.delete();
        }

        return false;
    }
}
