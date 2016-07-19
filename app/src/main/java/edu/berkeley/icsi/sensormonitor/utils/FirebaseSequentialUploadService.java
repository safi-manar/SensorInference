package edu.berkeley.icsi.sensormonitor.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ioreyes on 7/8/16.
 */
public class FirebaseSequentialUploadService extends Service {
    public static final String PATHS_EXTRA = "FirebaseUploadService_PATHS_EXTRA";
    public static final String DELETE_EXTRA = "FirebaseUploadService_DELETE_EXTRA";

    private final List<File> TO_UPLOAD = new LinkedList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.hasExtra(PATHS_EXTRA)) {
            String[] filePaths = intent.getStringArrayExtra(PATHS_EXTRA);
            boolean deleteOnSuccess = intent.hasExtra(DELETE_EXTRA) && intent.getBooleanExtra(DELETE_EXTRA, false);

            TO_UPLOAD.clear();
            for(String path : filePaths) {
                TO_UPLOAD.add(new File(path));
            }

            attemptUpload(deleteOnSuccess);
        }

        return 0;
    }

    /**
     * Sequentially upload files
     * @param deleteOnSucceess Set to true to delete files that are successfully uploaded
     */
    private void attemptUpload(final boolean deleteOnSucceess) {
        if(!TO_UPLOAD.isEmpty()) {
            final File file = TO_UPLOAD.remove(0);

            UploadTask upload = FirebaseWrapper.upload(file);
            upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Delete the local copy of the successfully-uploaded file and try the next one
                    if(deleteOnSucceess) {
                        file.delete();
                    }
                    attemptUpload(deleteOnSucceess);
                }
            });
            upload.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Skip the failure and try the next file
                    attemptUpload(deleteOnSucceess);
                }
            });
        }
        else {
            stopSelf();
        }
    }
}

