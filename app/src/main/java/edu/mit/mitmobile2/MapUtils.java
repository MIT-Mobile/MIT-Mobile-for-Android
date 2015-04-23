package edu.mit.mitmobile2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;

public class MapUtils {

    public static Tile getTileFromNextZoomLevel(int x, int y, int zoom) {

        String source = "http://m.mit.edu/api/arcgis/WhereIs_Base_Topo/MapServer/tile/{z}/{y}/{x}";

        final String topLeftTileUrl = source.replace("{z}", "" + (zoom + 1)).replace("{x}", "" + (x * 2)).replace("{y}", "" + (y * 2));
        final String topRightTileUrl = source.replace("{z}", "" + (zoom + 1)).replace("{x}", "" + (x * 2 + 1)).replace("{y}", "" + (y * 2));
        final String bottomLeftTileUrl = source.replace("{z}", "" + (zoom + 1)).replace("{x}", "" + (x * 2)).replace("{y}", "" + (y * 2 + 1));
        final String bottomRightTileUrl = source.replace("{z}", "" + (zoom + 1)).replace("{x}", "" + (x * 2 + 1)).replace("{y}", "" + (y * 2 + 1));

        final Bitmap[] tiles = new Bitmap[4];

        Thread t1 = new Thread() {

            @Override
            public void run() {
                tiles[0] = getBitmapFromURL(topLeftTileUrl);
            }
        };
        t1.start();

        Thread t2 = new Thread() {

            @Override
            public void run() {
                tiles[1] = getBitmapFromURL(topRightTileUrl);
            }
        };
        t2.start();

        Thread t3 = new Thread() {

            @Override
            public void run() {
                tiles[2] = getBitmapFromURL(bottomLeftTileUrl);
            }
        };
        t3.start();

        Thread t4 = new Thread() {

            @Override
            public void run() {
                tiles[3] = getBitmapFromURL(bottomRightTileUrl);
            }
        };
        t4.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            Timber.e(e, "Failed");
        }

        byte[] tile = mergeBitmaps(tiles, Bitmap.CompressFormat.PNG); // PNG is a lot slower, use it only if you really need to

        return tile == null ? TileProvider.NO_TILE : new Tile(256, 256, tile);
    }

    public static byte[] mergeBitmaps(Bitmap[] parts, Bitmap.CompressFormat format) {

        // Check if all the bitmap are null (if so return null) :
        boolean allNulls = true;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] != null) {
                allNulls = false;
                break;
            }
        }

        if (allNulls) return null;

        Bitmap tileBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tileBitmap);
        Paint paint = new Paint();
        for (int i = 0; i < parts.length; i++) {

            if (parts[i] == null) {

                parts[i] = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
            }
            canvas.drawBitmap(parts[i], parts[i].getWidth() * (i % 2), parts[i].getHeight() * (i / 2), paint);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        tileBitmap.compress(format, 100, stream);
        byte[] bytes = stream.toByteArray();

        return bytes;
    }

    public static Bitmap getBitmapFromURL(String urlString) {

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());

            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }
}
