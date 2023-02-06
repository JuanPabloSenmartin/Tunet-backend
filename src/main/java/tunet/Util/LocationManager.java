package tunet.Util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class LocationManager {
    public static int getDistance(double[] location1, double[] location2){
        if (location1 == null || location1.length == 0 || location2 == null || location2.length == 0) return -1;

        return calculateDistance(location1[0], location1[1], location2[0], location2[1]);
    }
    private static int calculateDistance(double lat1, double lon1, double lat2, double lon2){
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;

            return (int) dist;
        }
    }
    private static double[] getValues(String location1, String location2) throws IOException {
        double[] data1 = getLatLong(location1);
        double[] data2 = getLatLong(location2);
        return new double[]{data1[0], data1[1], data2[0], data2[1]};
    }
    public static double[] getLatLong(String location) throws IOException {
//        URL url = new URL("http://api.positionstack.com/v1/forward");
        String ur = "http://api.positionstack.com/v1/forward?access_key=73d6d6e133dcc0f3fe2aedbdcde23698&query=" + location;
        URL url = new URL(ur);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

//        Map<String, String> parameters = new HashMap<>();
//        String key = "73d6d6e133dcc0f3fe2aedbdcde23698";
//        parameters.put("access_key", key);
//        parameters.put("query", location);
//
//        con.setDoOutput(true);
//        DataOutputStream out = new DataOutputStream(con.getOutputStream());
//        out.writeBytes(getParamsString(parameters));
//        out.flush();
//        out.close();


        int status = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        con.disconnect();
        System.out.println(content.toString());
        String[] str = content.toString().split(":");

        return new double[]{Double.parseDouble(str[2].split(",")[0]),Double.parseDouble(str[3].split(",")[0])};
    }
    public static String getCoordinates(String location){
        double[] values;
        try {
            values = getLatLong(location);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return values[0] + "," + values[1];
    }
}
