package edu.udg.exit.heartrate.Utils;

import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import edu.udg.exit.heartrate.Global;
import edu.udg.exit.heartrate.TodoApp;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.*;
import java.util.Date;

public class Storage {

    ////////////////
    // Attributes //
    ////////////////

    private String fileName;
    private FileOutputStream fos;
    private BufferedWriter bw;

    ///////////////////////
    // Lifecycle Methods //
    ///////////////////////

    public Storage(){
        this.fileName = null;
        this.fos = null;
        this.bw = null;
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Create & open a file with the given fileName.
     * @param ctx - Application context
     * @param fileName - Name of the file
     */
    public void createFile(Context ctx, String fileName){
        try {
            this.fileName = fileName;
            fos = ctx.openFileOutput(fileName, ctx.MODE_PRIVATE);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
        } catch(FileNotFoundException e) {
            fos = null;
            Log.d("Storage", e.getMessage());
        }
    }

    /**
     * Close the current file.
     */
    public void closeFile(){
        if(fos != null){
            try {
                bw.close();
                bw = null;
                fos.close();
                fos = null;
            } catch (IOException e) {
                Log.d("Storage", e.getMessage());
            }
        }
    }

    public void writeToFile(String text){
        if(bw != null){
            try {
                for(int i=0; i<text.length(); i++){
                    bw.append(text.charAt(i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the file with the given file name.
     * @param ctx - Context
     * @param fileName - Name of the file
     * @return File
     */
    public File getFile(Context ctx, String fileName) {
        String path = ctx.getFilesDir() + "/" + fileName;
        File file = new File(path);
        if(file.exists()) return file;
        else return null;
    }

    public void uploadFile(Context ctx){
        String path = ctx.getFilesDir() + "/" + fileName;
        File file = new File(path);
        if(file.exists()) uploadFile(ctx, file);
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Gets mime type from the url of a file.
     * @param url - Url of the file
     * @return Mime Type of the file.
     */
    private String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return null;
    }

    private void uploadFile(final Context ctx, File file){
        Log.d("File", "start upload");
        if (file != null){
            String type = getMimeType(file.getPath());
            Log.d("File", "type: " + type);
            MediaType mediaType = MediaType.parse(type);
            RequestBody reqFile = RequestBody.create(mediaType, file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

            Call<ResponseBody> call = ((TodoApp) ctx.getApplicationContext()).getApiService().getFileService().uploadFile(body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful())
                        Toast.makeText(ctx, "File uploaded!", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(ctx, "Failed to upload the file!", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println(call.toString());
                    System.out.println(t.getMessage());
                    System.out.println(t.getLocalizedMessage());
                    t.printStackTrace();
                    Toast.makeText(ctx, "BAD CONNECTION", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(ctx, "FAILED TO UPLOAD THE FILE", Toast.LENGTH_LONG).show();
        }
    }

}
