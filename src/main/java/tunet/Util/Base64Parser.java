package tunet.Util;

import javax.annotation.Nullable;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Pattern;

public class Base64Parser {

    //creates an image file from base64
    //returns path of the file
    public static String createImageFile(@Nullable String base64img, String mail, String type){
        if (base64img == null || base64img.equals("")) return null;
        String[] strings = base64img.split(",");
        String extension;
        switch (strings[0]) {//check file's extension
            case "data:image/jpeg;base64":
                extension = "jpeg";
                break;
            case "data:image/png;base64":
                extension = "png";
                break;
            case "data:audio/mpeg;base64":
                extension = "mpeg";
                break;
            default:
                extension = "jpg";
                break;

        }
        //convert base64 string to binary data
        byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);

        String path = "src\\main\\resources\\images\\" + type + mail + "." + extension;
        File file = new File(path);

        if(file.exists()){
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file, false))) {
                outputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                outputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //returns directory
        return path;
    }
    public static String createImageFile(@Nullable String base64img, String mail, String type,int id){
        if (base64img == null || base64img.equals("")) return null;
        String[] strings = base64img.split(",");

        String[] s1 = strings[0].split("/");
        String[] s2 = s1[1].split(";");

        String extension = s2[0];
//        switch (strings[0]) {//check file's extension
//            case "data:image/jpeg;base64":
//                extension = "jpeg";
//                break;
//            case "data:image/png;base64":
//                extension = "png";
//                break;
//            case "data:audio/mpeg;base64":
//                extension = "mpeg";
//                break;
//            default:
//                extension = "jpg";
//                break;
//
//        }



        //convert base64 string to binary data
        byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);

        String path = "src\\main\\resources\\images\\" + type + mail + "id" + id + "." + extension;
        File file = new File(path);

        if(file.exists()){
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file, false))) {
                outputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                outputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //returns directory
        return path;
    }

    //returns base64 of file in the path
    public static String convertToBase64(String path) throws IOException {
        if (path == null || path.equals("")) return null;

        byte[] byteData = Files.readAllBytes(Paths.get(path));
        String base64 =  Base64.getEncoder().encodeToString(byteData);
        String prefix = getPrefix(path);
        return prefix + base64;
    }

    //returns prefix of the base64
    private static String getPrefix(String path) {
        String[] strings = path.split(Pattern.quote("."));
        String prefix;
        switch (strings[1]) {//check image's extension

            case "jpeg":
                prefix = "data:image/jpeg;base64,";
                break;
            case "png":
                prefix = "data:image/png;base64,";
                break;
            case "jpg":
                prefix = "data:image/jpg;base64,";
                break;
            default:
                prefix = "data:audio/mpeg;base64,";
                break;
        }
        return prefix;
    }

    public static void deletePath(String path){
        try{
            File file = new File(path);
            file.delete();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
