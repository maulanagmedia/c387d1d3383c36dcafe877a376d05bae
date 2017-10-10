package gmedia.net.id.psplocationfinder.Utils;

/**
 * Created by Shinmaul on 10/9/2017.
 */

public class ServerURL {

    //private static final String baseURL = "http://192.168.12.181/psp/";
    private static final String baseURL = "http://api.putmasaripratama.co.id/";

    public static final String login = baseURL + "api/location/login/";
    public static final String getLocation = baseURL + "api/location/get_location/";
    public static final String saveLocation = baseURL + "api/location/save_location/";
    public static final String saveImages = baseURL + "api/location/insert_foto/";
    public static final String getImages = baseURL + "api/location/get_images/";
}
