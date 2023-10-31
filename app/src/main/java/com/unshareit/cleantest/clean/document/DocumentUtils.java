package com.unshareit.cleantest.clean.document;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.unshareit.cleantest.clean.utils.ContextUtils;
import com.unshareit.cleantest.clean.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DocumentUtils {
    private static List<String> folderList;
    public static DocumentFile getDocumentFile(String filePath,boolean isFolder){
        DocumentFile documentFile = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && filePath.contains("Android/data") || filePath.contains("android/data")){
            if (isFolder){
                documentFile = getDocumentFileTree(filePath);
            }else {
                documentFile = getDocumentFileFromSingleUri(filePath);
            }
        }
        return documentFile;
    }

    public static DocumentFile getDocumentFileFromSingleUri(String str) {
        if (str.endsWith("/")) {
            str = str.substring(0, str.length() - 1);
        }
        return DocumentFile.fromSingleUri(ContextUtils.getAppContext(), Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + str.replace("/storage/emulated/0/", "").replace("/", "%2F")));
    }

    public static String changeToUri3(String str) {
        if (str.endsWith("/")) {
            str = str.substring(0, str.length() - 1);
        }
        return "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + str.replace("/storage/emulated/0/", "").replace("/", "%2F");
    }

    public static DocumentFile getDocumentFileTree(String str){
        Log.e("CleanFileDocument","uri==>"+changeToUri3(str));
        DocumentFile documentFile = DocumentFile.fromTreeUri(ContextUtils.getAppContext(), Uri.parse(changeToUri3(str)));
        return documentFile;
    }

    public static boolean isDirectoryExists(String str){
        DocumentFile documentFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && str.contains("Android/data") || str.contains("android/data")){
            documentFile = getDocumentFileTree(str);
            return documentFile.exists();
        }
        return FileUtils.isFileExist(str);
    }

    public static boolean isDirectoryEmpty(String str){
        DocumentFile documentFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && str.contains("Android/data") || str.contains("android/data")){
            documentFile = getDocumentFileTree(str);
            return isEmptyFolder(str,documentFile);
        }
        return FileUtils.isEmptyFolder(new File(str));
    }

    private static boolean isEmptyFolder(String path, DocumentFile documentFile){
        String[] str = path.trim().replace("/storage/emulated/0/Android/data/","").split("/");
        List<String> list = Arrays.asList(str);
        if (folderList == null || folderList.isEmpty()){
            list = Arrays.asList(str);
            folderList = new ArrayList<>(list);
        }else if (folderList.size() > 0){
            folderList.remove(0);
        }

        if (folderList != null && folderList.size() > 0){
            for (int i=0; i<folderList.size(); i++){
                if (TextUtils.isEmpty(folderList.get(i))){
                    continue;
                }
                DocumentFile childDocument = documentFile.findFile(folderList.get(i));
                if (childDocument == null){
                    continue;
                }
                if (folderList.get(i).equals(childDocument.getName())){
                    if (childDocument.isDirectory()){
                        isEmptyFolder(path,childDocument);
                    }
                }
            }
        }else if (documentFile.isDirectory() && documentFile.listFiles().length == 0){
            return true;
        }

        return false;
    }

    public static boolean isFileExists(String path){
        DocumentFile documentFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && path.contains("Android/data") || path.contains("android/data")){
            documentFile = getDocumentFileFromSingleUri(path);
            return documentFile.exists();
        }
        return FileUtils.isFileExist(path);
    }

    public static boolean isFile(String path){
        if (isDocument(path)){
            DocumentFile documentFile = getDocumentFileFromSingleUri(path);
            return documentFile != null && documentFile.isFile();
        }
        File file = new File(path);
        return file != null && file.isFile();
    }

    public static boolean isDocument(String filePath){
        if (TextUtils.isEmpty(filePath)){
            return false;
        }
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && filePath.contains("Android/data") || filePath.contains("android/data");
    }

    public static boolean isAndroidRDoc(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && DataPermissionUtils.isGrant(ContextUtils.getAppContext());
    }

    public static boolean isAndroidR(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }

    public static String getFolderName(String filePath){
        if (filePath.contains("/storage/emulated/0/Android/data/")){
            String[] paths = filePath.replace("/storage/emulated/0/Android/data/","").split("/");
            if (paths == null || paths.length == 0)
                return null;
            return paths[0];
        }
        return null;
    }

    /**
     * 获取docuemntFile文件夹大小
     * @param sFile
     * @return
     */
    public static long getDocumentFolderSize(SFile sFile){
        SFile[] flist = sFile.listFiles();
        if (flist != null && flist.length != 0) {
            long size = 0L;
            int fLen = flist.length;

            for(int i = 0; i < fLen; ++i) {
                size += flist[i].length();
            }
            return size;
        } else {
            return 0L;
        }
    }

    /**
     * 删除文件
     * @param file
     * @return
     */
    public static boolean deleteDocument(File file){
        Log.e("DealCleanFile","deleteDocument-file==>"+file);
        if (file == null)
            return false;
        String path = file.getAbsolutePath();
        if (!TextUtils.isEmpty(path) && DocumentUtils.isDocument(path)){
            if (path.contains("sdcard/Android/data")){
                path = path.replace("sdcard","storage/emulated/0");
            }
            CleanFileDocument cleanFileDocument = new CleanFileDocument(path,file.isDirectory());
            DocumentFile documentFile = cleanFileDocument.fileConvertDocument(file,file.isDirectory());
            Log.e("DealCleanFile","deleteDocument-document==>"+documentFile);
            if (documentFile != null){
                Log.e("DealCleanFile","deleteDocument-document==>清理");
                return documentFile.delete();
            }
            return deleteFolder(path);
        }
        return deleteFolder(path);
    }

    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        if (files == null){
            return false;
        }
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param filePath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public static boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

}
