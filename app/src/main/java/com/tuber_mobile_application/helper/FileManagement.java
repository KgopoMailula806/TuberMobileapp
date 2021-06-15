package com.tuber_mobile_application.helper;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.widget.Toast;

import com.tuber_mobile_application.Controllers.DocumentController;
import com.tuber_mobile_application.Controllers.TutorDocumentController;
import com.tuber_mobile_application.Models.Document;
import com.tuber_mobile_application.Models.TutorDocument;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FileManagement
{
    public static final int REQUEST_CODE_IMAGE = 69;
    public static final int REQUEST_CODE_CV = 6919;
    public static final int REQUEST_CODE_PC = 96;
    public static final int REQUEST_CODE_AC = 9196;

    private static AtomicReference<Retrofit> retrofit;

    public static  String makeBase64(InputStream is) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int flag = 0;

        while ((flag = is.read(buffer)) != -1)
        {
            byteArrayOutputStream.write(buffer,0,flag);
        }

        byte[] fileData = byteArrayOutputStream.toByteArray();
        String data = Base64.encodeToString(fileData,fileData.length);
        return data;
    }

    public static Bitmap makeImageFromBase64(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
        return bitmap;
    }

    public static  boolean isOurRequestCode(int requestCode){
        return (requestCode == REQUEST_CODE_PC || requestCode == REQUEST_CODE_IMAGE || requestCode == REQUEST_CODE_CV|| requestCode == REQUEST_CODE_AC);
    }

    public static String getFileName(Cursor cursor){
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(nameIndex);

    }

    public static int size(Cursor cursor){

        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        int size = (int) cursor.getLong(sizeIndex);
        cursor.moveToFirst();
       return size;
    }

    public static String getExtension(String name)
    {
        return name.substring(name.lastIndexOf("."));
    }

    public static void setUpRetrofit()
    {
        retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build());


    }

    public static int sendFile(int id, int size, String ext, String data, final Activity activity, final String filename){

        Document doc = new Document();
        doc.setDocumentData(data);
        doc.setExtension(ext);
        doc.setId(id);
        doc.setSize(size);

        final int[] dbId = new int[1];
        setUpRetrofit();
        DocumentController documentController = retrofit.get().create(DocumentController.class);
        Call<Integer> call = documentController.UploadDocument(doc);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                if(response.isSuccessful())
                {
                    dbId[0] = response.body();
                    Toast.makeText(activity, filename + " uploaded", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

        return dbId[0];
    }

    public static int uploadTutorDocument(TutorDocument tutorDocument)
    {
        final int[] dbId = new int[1];
        setUpRetrofit();

        TutorDocumentController tutorDocumentController = retrofit.get().create(TutorDocumentController.class);
        Call<Integer> call = tutorDocumentController.uploadTutorDocument(tutorDocument);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    dbId[0] = response.body();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });


        return dbId[0];
    }
}
