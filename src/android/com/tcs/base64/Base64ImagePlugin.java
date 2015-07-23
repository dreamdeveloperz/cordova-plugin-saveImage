/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcs.base64;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.os.Environment;
import android.content.Context;
import android.app.Application
import org.apache.commons.codec.binary.Base64;

/**
 * This class echoes a string called from JavaScript.
 */
public class Base64ImagePlugin extends CordovaPlugin {

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action The action to execute.
     * @param args JSONArry of arguments for the plugin.
     * @param callbackId The callback id used when calling back into JavaScript.
     * @return A PluginResult object with a status and message.
     */
    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    public PluginResult execute(String action, JSONArray args, String callbackId) {
        sApplication = this;
        System.out.println(action);
        Context context = getContext();
        if (!action.equals("saveImage")) {
            return new PluginResult(PluginResult.Status.INVALID_ACTION);
        }

        try {
            System.out.println(args.getString(0));
            System.out.println(args.getJSONObject(1).toString());
            String b64String = args.getString(0);
            if (b64String.startsWith("data:image")) {
                b64String = b64String.substring(22);
            } else {
                b64String = args.getString(0);
            }
            JSONObject params = args.getJSONObject(1);

            //Optional parameter
            String filename = params.has("filename")
                    ? params.getString("filename")
                    : "b64Image_" + System.currentTimeMillis() + ".png";
            String storagetype = params.has("externalStorage") ? Environment.getExternalStorageDirectory() : context.getFilesDir().getAbsolutePath();
            String folder = params.has("folder")
                    ? params.getString("folder")
                    : storagetype + "/Pictures";

            Boolean overwrite = params.has("overwrite")
                    ? params.getBoolean("overwrite")
                    : false;

            return this.saveImage(b64String, filename, folder, overwrite, callbackId);

        } catch (JSONException e) {

            e.printStackTrace();
            return new PluginResult(PluginResult.Status.JSON_EXCEPTION, e.getMessage());

        } catch (InterruptedException e) {
            e.printStackTrace();
            return new PluginResult(PluginResult.Status.ERROR, e.getMessage());
        }

    }

    private PluginResult saveImage(String b64String, String fileName, String dirName, Boolean overwrite, String callbackId) throws InterruptedException, JSONException {

        try {

            //Directory and File
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dirName, fileName);

            //Avoid overwriting a file
            if (!overwrite && file.exists()) {
                return new PluginResult(PluginResult.Status.OK, "File already exists!");
            }

            //Decode Base64 back to Binary format
            byte[] decodedBytes = Base64.decodeBase64(b64String.getBytes());

            //Save Binary file to phone
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.write(decodedBytes);
            fOut.close();

            return new PluginResult(PluginResult.Status.OK, "Saved successfully!");

        } catch (FileNotFoundException e) {
            return new PluginResult(PluginResult.Status.ERROR, "File not Found!");
        } catch (IOException e) {
            return new PluginResult(PluginResult.Status.ERROR, e.getMessage());
        }

    }
}
