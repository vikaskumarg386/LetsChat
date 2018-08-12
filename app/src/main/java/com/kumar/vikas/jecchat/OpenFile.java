package com.kumar.vikas.jecchat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by vikas on 18/2/18.
 */

public class OpenFile {



        public static void DownLoadFile(Context context, Uri uri) throws IOException {
            // Create URI
            //File file=url;
           // Uri uri = Uri.fromFile(file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if(uri.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if(uri.toString().contains(".ppt") || uri.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if(uri.toString().contains(".xls") || uri.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if(uri.toString().contains(".zip") || uri.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if(uri.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if(uri.toString().contains(".wav") || uri.toString().contains(".mp3") || uri.toString().contains(".m4a")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if(uri.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if(uri.toString().contains(".jpg") || uri.toString().contains(".jpeg") || uri.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if(uri.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if(uri.toString().contains(".3gp") || uri.toString().contains(".mpg") || uri.toString().contains(".mpeg") || uri.toString().contains(".mpe") || uri.toString().contains(".mp4") || uri.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }





}
