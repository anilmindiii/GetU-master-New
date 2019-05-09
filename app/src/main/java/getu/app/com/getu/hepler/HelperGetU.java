package getu.app.com.getu.hepler;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class HelperGetU {

    // Turn drawable into byte array.
    public static byte[] getFileDataFromBitmap(Context context, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
