package getu.app.com.getu.hepler;

        import android.Manifest;
        import android.app.Activity;
        import android.content.pm.PackageManager;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.widget.Toast;

        import getu.app.com.getu.util.Constant;

/**
 * Created by abc on 25/01/2018.
 */

public class PermissionAll {

    public boolean checkLocationPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context , Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constant.MY_PERMISSIONS_REQUEST_LOCATION);

            return false;
        } else {
            return true;
        }
    }

    public boolean chackCameraPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CAMERA)) {
            // Toast.makeText(activity, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions((Activity) activity,
                    new String[]{Manifest.permission.CAMERA},
                    Constant.MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.CAMERA}, Constant.RequestPermissionCode);
            return true;
        }
    } // camera parmission
}
