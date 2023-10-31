package com.unshareit.cleantest.clean.document;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.util.Pair;

import androidx.documentfile.provider.DocumentFile;

import com.unshareit.cleantest.clean.utils.ContextUtils;


public class DocumentFilePermission {
    public static final int AUTH_REQUEST_CODE = 0x101;
    public static final int AUTH_REQUEST_ANDROID_DATA_CODE = 0x102;
    public static final int AUTH_REQUEST_ANDROID_OBB_CODE = 0x103;
    public static final String ANDROID_DATA_URI = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata";
    public static final String ANDROID_OBB_URI = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fobb";
    public static final String ANDROID_DATA_TREE_URI = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata";
    public static final String ANDROID_TREE_URI = "content://com.android.externalstorage.documents/tree/primary%3AAndroid/document/primary%3AAndroid";
    public static final String ANDROID_DATA_PATH = "/storage/emulated/0/Android/data";
    public static final String ANDROID_OBB_PATH = "/storage/emulated/0/Android/obb";
    public static final String ANDROID_PATH = "/storage/emulated/0/Android";
    public static final String ANDROID_OBB_TREE_URI = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fobb/document/primary%3AAndroid%2Fobb";
//    public static Pair<Boolean, Boolean> checkSdcardAuth(Activity activity, ContentObject contentObject) {
//        boolean inSdCardFilePath = FilePathConverter.isInSdCardFilePath(FileOperatorHelper.getFilePath(contentObject));
//        if (inSdCardFilePath) {
//            return DocumentFilePermission.checkSdcardAuth(activity, FileStorageHelper.getSdCardVolumePath());
//        }
//        return Pair.create(false, false);
//    }

    /**
     * @param activity
     * @return Pair<A, B> A 是否需要使用documentFile方式访问， B 是否要显示授权框
     */
//    public static Pair<Boolean, Boolean> checkSdcardAuth(Activity activity, List<ContentObject> contentObjects) {
//        for (ContentObject contentObject : contentObjects) {
//            boolean inSdCardFilePath = FilePathConverter.isInSdCardFilePath(FileOperatorHelper.getFilePath(contentObject));
//            if (inSdCardFilePath) {
//                return DocumentFilePermission.checkSdcardAuth(activity, FileStorageHelper.getSdCardVolumePath());
//            }
//        }
//        return Pair.create(false, false);
//    }

    /**
     * @param filePath
     * @return Pair<A, B> A 是否需要使用documentFile方式访问， B 是否要显示授权框
     */
    public static Pair<Boolean, Boolean> checkAndroidDataOrObbAuth(String filePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (filePath.equals(ANDROID_DATA_PATH) && !isGrant(ContextUtils.getAppContext(), ANDROID_DATA_URI)) {
                return Pair.create(true, true);
            } else if (filePath.equals(ANDROID_OBB_PATH) && !isGrant(ContextUtils.getAppContext(), ANDROID_OBB_URI)) {
                return Pair.create(true, true);
            }
        }
        return Pair.create(false, false);
    }

    public static boolean isGrant(Context context, String uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            for (UriPermission persistedUriPermission : context.getContentResolver().getPersistedUriPermissions()) {
                if (persistedUriPermission.isReadPermission() && persistedUriPermission.getUri().toString().equals(uri)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * @param activity
     * @return Pair<A, B> A 是否需要使用documentFile方式访问， B 是否要显示授权框
     */
//    public static Pair<Boolean, Boolean> checkSdcardAuthForItems(Activity activity, List<ContentItem> contentObjects) {
//        for (ContentObject contentObject : contentObjects) {
//            boolean inSdCardFilePath = FilePathConverter.isInSdCardFilePath(FileOperatorHelper.getFilePath(contentObject));
//            if (inSdCardFilePath) {
//                return DocumentFilePermission.checkSdcardAuth(activity, FileStorageHelper.getSdCardVolumePath());
//            }
//        }
//        return Pair.create(false, false);
//    }

    /**
     * @param activity
     * @param path
     * @return Pair<A, B> A 是否需要使用documentFile方式访问， B 是否要显示授权框
     */
//    public static Pair<Boolean, Boolean> checkSdcardAuth(Activity activity, String path) {
//        boolean sdcardVolume = !TextUtils.isEmpty(path) && FileStorageHelper.isSdcardVolume(path);
//        if (sdcardVolume) {
//            Pair<Boolean, Boolean> checkSDCardPermission = FileStorageHelper.checkSDCardPermission();
//            boolean isWritable = checkSDCardPermission.first;
//            boolean supportAuth = checkSDCardPermission.second;
//            if (!isWritable) {
//                if (supportAuth) {
//                    boolean testSDCardDocumentWrite = FileStorageHelper.testSDCardDocumentWrite();
//                    if (!testSDCardDocumentWrite) {
//                        return Pair.create(true, true);
//                    }
//                    return Pair.create(true, false);
//                } else {
//                    return Pair.create(false, false);
//                }
//            } else {
//                if (TextUtils.isEmpty(FileSettings.getSDCardUri())) {
//                    return Pair.create(false, false);
//                } else {
//                    return Pair.create(true, false);
//                }
//            }
//        } else {
//            return Pair.create(false, false);
//        }
//    }

//    public static void checkSdcardPermission(Activity activity, String path) {
//        boolean sdcardVolume = !TextUtils.isEmpty(path) && FileStorageHelper.isSdcardVolume(path);
//        if (sdcardVolume) {
//            Pair<Boolean, Boolean> checkSDCardPermission = FileStorageHelper.checkSDCardPermission();
//            boolean isWritable = checkSDCardPermission.first;
//            boolean supportAuth = checkSDCardPermission.second;
//            if (!isWritable) {
//                if (supportAuth) {
//                    boolean testSDCardDocumentWrite = FileStorageHelper.testSDCardDocumentWrite();
//                    if (!testSDCardDocumentWrite) {
//                        showAuthDialog(activity, path);
//                    }
//                    FileStorageHelper.setIsDocumentUriVolume(true);
//                } else {
//                    FileStorageHelper.setIsDocumentUriVolume(false);
//                }
//            } else {
//                if (TextUtils.isEmpty(FileSettings.getSDCardUri())) {
//                    FileStorageHelper.setIsDocumentUriVolume(false);
//                } else {
//                    FileStorageHelper.setIsDocumentUriVolume(true);
//                }
//            }
//        } else {
//            FileStorageHelper.setIsDocumentUriVolume(false);
//        }
//    }

//    public static boolean shouldShowAuthDialog(Activity context, String path) {
//        Pair<Boolean, Boolean> checkSdcardAuth = DocumentFilePermission.checkSdcardAuth(context, path);
//        if (checkSdcardAuth != null) {
//            boolean shouldShowAuthDialog = checkSdcardAuth.second;
//            if (shouldShowAuthDialog) {
//                return true;
//            }
//        }
//        return false;
//    }

//    public static void showAuthDialog(final Activity context, final String path) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            SIDialog.getConfirmDialog().setMessage(context.getResources().getString(R.string.sdcard_storage_permission_desc))
//                    .setOnOkListener(new IDialog.OnOKListener() {
//                        @Override
//                        public void onOK() {
//                            try {
//                                StorageManager systemService = context.getSystemService(StorageManager.class);
//                                StorageVolume storageVolume = systemService.getStorageVolume(new File(path));
//                                if (storageVolume != null) {
//                                    context.startActivityForResult(storageVolume.createOpenDocumentTreeIntent(), AUTH_REQUEST_CODE);
//                                }
//                            } catch (Throwable e) {
//                                Logger.e("documentpermission", "OPEN_DOCUMENT_TREE 1: " + e.getMessage());
//                            }
//                        }
//                    }).show(context, "sdcard_permission_q");
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            try {
//                StorageManager systemService = context.getSystemService(StorageManager.class);
//                StorageVolume storageVolume = systemService.getStorageVolume(new File(path));
//                if (storageVolume != null) {
//                    context.startActivityForResult(storageVolume.createAccessIntent(null), AUTH_REQUEST_CODE);
//                }
//            } catch (Throwable e) {
//                Logger.e("documentpermission", "OPEN_DOCUMENT_TREE 2: " + e.getMessage());
//            }
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            SIDialog.getConfirmDialog().setMessage(context.getResources().getString(R.string.sdcard_storage_permission_desc))
//                    .setOnOkListener(new IDialog.OnOKListener() {
//                        @Override
//                        public void onOK() {
//                            try {
//                                final String ACTION_OPEN_DOCUMENT_TREE = "android.intent.action.OPEN_DOCUMENT_TREE";
//                                Intent it = new Intent(ACTION_OPEN_DOCUMENT_TREE);
//                                it.addCategory(Intent.CATEGORY_DEFAULT);
//                                context.startActivityForResult(it, AUTH_REQUEST_CODE);
//                            } catch (Throwable e) {
//                                Logger.e("documentpermission", "OPEN_DOCUMENT_TREE 3: " + e.getMessage());
//                            }
//                        }
//                    }).show(context, "sdcard_permission");
//        }
//    }

//    public static void showAndroidDataOrObbAuthDialog(final Activity context, final String uri, final int requestCode) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            SIDialog.getConfirmDialog().setMessage(requestCode == DocumentFilePermission.AUTH_REQUEST_ANDROID_DATA_CODE ?
//                    context.getResources().getString(R.string.android_data_permission_desc) : context.getResources().getString(R.string.android_obb_permission_desc))
//                    .setOnOkListener(new IDialog.OnOKListener() {
//                        @Override
//                        public void onOK() {
//                            DocumentFilePermission.startForSpecificUriRoot(context, uri, requestCode);
//                        }
//                    }).show(context, "sdcard_permission");
//        }
//    }

//    public static void onActivityResult(final Activity context, int requestCode, int resultCode, Intent resultData) {
//        if (requestCode != AUTH_REQUEST_CODE || resultCode != Activity.RESULT_OK || resultData == null)
//            return;
//        Uri uri = resultData.getData();
//        if (uri == null)
//            return;
//        Logger.v("Storage", "Storage path:" + uri.getPath());
//
//        SFile fsWrapper = SFile.create(DocumentFile.fromTreeUri(ObjectStore.getContext(), uri));
//        String path = fsWrapper.toFile().getAbsolutePath();
//        List<StorageVolumeHelper.Volume> volumes = StorageVolumeHelper.getVolumeList(context);
//        StorageVolumeHelper.Volume volume = null;
//        for (StorageVolumeHelper.Volume v : volumes) {
//            if (!path.contains(v.mPath))
//                continue;
//            volume = v;
//        }
//        if (volume == null) {
//            TaskHelper.exec(new TaskHelper.UITask() {
//
//                @Override
//                public void callback(Exception e) {
//                    String path = FileStorageHelper.getSdCardVolumePath();
//                    showAuthDialog(context, path);
//                }
//            }, 0, 500);
//            SafeToast.showToast(R.string.setting_authprompt_fail_prompt, Toast.LENGTH_LONG);
//            return;
//        }
//        String documentPath = DocumentFile.fromTreeUri(ObjectStore.getContext(), uri).getUri().toString();
//        DocumentFileCache.putPathUri(path, documentPath);
//        FileSettings.setSDCardUri(documentPath);
//        try {
//            if (Build.VERSION.SDK_INT >= 19)
//                context.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        } catch (Exception e) {}
//    }

//    public static boolean onAuthAndroidFolderActivityResult(final Activity context, final int requestCode, int resultCode, Intent resultData) {
//        if ((requestCode != AUTH_REQUEST_ANDROID_DATA_CODE && requestCode != AUTH_REQUEST_ANDROID_OBB_CODE) || resultCode != Activity.RESULT_OK || resultData == null)
//            return false;
//        Uri uri = resultData.getData();
//        if (uri == null)
//            return false;
//        Logger.v("onAuthAndroidFolderActivityResult", "onAuthAndroidFolderActivityResult Storage path:" + uri.getPath());
//        SFile fsWrapper = SFile.create(DocumentFile.fromTreeUri(ObjectStore.getContext(), uri));
//        final String path = fsWrapper.toFile().getAbsolutePath();
//        boolean authSuccess = false;
//        try {
//            if (Build.VERSION.SDK_INT >= 19)
//                context.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        } catch (Exception e) {
//        }
//
//        if (requestCode == AUTH_REQUEST_ANDROID_DATA_CODE) {
//            authSuccess = DocumentFilePermission.isGrant(ObjectStore.getContext(), ANDROID_DATA_URI);
//        } else if (requestCode == AUTH_REQUEST_ANDROID_OBB_CODE) {
//            authSuccess = DocumentFilePermission.isGrant(ObjectStore.getContext(), ANDROID_OBB_URI);
//        }
//        if (!authSuccess) {
//            TaskHelper.exec(new TaskHelper.UITask() {
//
//                @Override
//                public void callback(Exception e) {
//                    String path = "";
//                    if (requestCode == AUTH_REQUEST_ANDROID_DATA_CODE) {
//                        path = ANDROID_DATA_URI;
//                    } else if (requestCode == AUTH_REQUEST_ANDROID_OBB_CODE) {
//                        path = ANDROID_OBB_URI;
//                    }
//                    showAndroidDataOrObbAuthDialog(context, path, requestCode);
//                }
//            }, 0, 500);
//            SafeToast.showToast(R.string.setting_authprompt_fail_prompt, Toast.LENGTH_LONG);
//            return false;
//        }
//        String documentPath = DocumentFile.fromTreeUri(ObjectStore.getContext(), uri).getUri().toString();
//        DocumentFileCache.putPathUri(path, documentPath);
//        return true;
////        FileSettings.setSDCardUri(documentPath);
//    }

    /**
     * 大于等于R
     * @return
     */
    public static boolean isHigherAndroidR() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }

    public static void startForSpecificUriRoot(Activity context, String contentUri, int requestCode) {
        Uri uri1 = Uri.parse(contentUri);
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri1);
        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        intent1.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.getUri());
        context.startActivityForResult(intent1, requestCode);
    }
}
