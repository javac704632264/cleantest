package com.unshareit.cleantest.clean.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.unshareit.cleantest.clean.document.SFile;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class FileUtils {
    private static final String TAG = "FileUtils";
    public static final String PREFIX_PHOTO = "image/";
    public static final String PREFIX_VIDEO = "video/";
    public static final String PREFIX_APK = "application/";
    public static final String PREFIX_MUSIC = "audio/";
    public static final String PREFIX_CONTACT = "text/x-vcard";
    private static final int MAX_LENGTH_FILE_NAME = 255;
    private static final int MAX_LENGTH_UNIQUE_FILE_NAME = 240;
//    private static List<Volume> sVolumeList;
    private static final String FILE_NOMEDIA = ".nomedia";

    private FileUtils() {
    }

//    public static String getMimeType(File file) {
//        return getMimeType(file.getName());
//    }

//    public static String getMimeType(String fileName) {
//        String ext = LocaleUtils.toLowerCaseIgnoreLocale(getExtension(fileName));
//        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
//    }

    public static String makePath(String parent, String child) {
        if (parent == null) {
            return child;
        } else if (child == null) {
            return parent;
        } else {
            return parent + (!parent.endsWith(File.separator) ? File.separator : "") + child;
        }
    }

    public static String getExtension(String filename) {
        String extension = "";
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf(46);
            if (dot > -1 && dot < filename.length() - 1) {
                extension = filename.substring(dot + 1);
            }
        }

        return extension;
    }

    public static String getUrlExtension(String filename) {
        String extension = "";
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf(46);
            if (dot > -1 && dot < filename.length() - 1) {
                int end = filename.lastIndexOf(63);
                if (end < dot) {
                    end = filename.length();
                }

                extension = filename.substring(dot + 1, end);
            }
        }

        return extension;
    }

    public static String getBaseName(String path) {
        if (path == null) {
            return null;
        } else {
            int index = path.lastIndexOf(File.separatorChar);
            if (index >= 0) {
                path = path.substring(index + 1);
            }

            index = path.lastIndexOf(46);
            if (index >= 0) {
                path = path.substring(0, index);
            }

            return path;
        }
    }

    public static String getFileName(String path) {
        if (path == null) {
            return "";
        } else {
            int index = path.lastIndexOf(File.separatorChar);
            if (index < 0) {
                index = path.lastIndexOf(92);
            }

            return index < 0 ? path : path.substring(index + 1);
        }
    }

    public static String getParentPath(String path) {
        File file = new File(path);
        return file.getParent();
    }

//    public static String getLocation(String filePath) {
//        if (TextUtils.isEmpty(filePath)) {
//            return null;
//        } else {
//            if (sVolumeList == null) {
//                sVolumeList = StorageVolumeHelper.getVolumeList(ObjectStore.getContext());
//            }
//
//            String location = getParentPath(filePath);
//            Iterator var2 = sVolumeList.iterator();
//
//            Volume volume;
//            do {
//                if (!var2.hasNext()) {
//                    return location;
//                }
//
//                volume = (Volume)var2.next();
//            } while(!location.startsWith(volume.mPath));
//
//            location = "/SDCard" + location.substring(volume.mPath.length());
//            return location;
//        }
//    }

    public static String getParentName(String path) {
        File file = new File(path);
        File parentFile = file.getParentFile();
        return parentFile == null ? null : parentFile.getName();
    }

//    public static long getFileSize(File f) {
//        if (f != null && f.exists()) {
//            return f.isFile() ? f.length() : getFolderSize(f);
//        } else {
//            return 0L;
//        }
//    }

    public static long getSpecialFileFolderSize(File root) {
        File[] flist = root.listFiles();
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

//    public static long getFolderSize(String path) {
//        return getFolderSize(SFile.create(path));
//    }

//    public static long getFolderSize(File folder) {
//        return folder == null ? -1L : getFolderSize(SFile.create(folder));
//    }

    public static long getFolderSize(SFile folder) {
        if (folder != null && folder.isDirectory()) {
            long size = 0L;

            try {
                SFile[] files = folder.listFiles();
                if (files != null) {
                    SFile[] var4 = files;
                    int var5 = files.length;

                    for(int var6 = 0; var6 < var5; ++var6) {
                        SFile file = var4[var6];
                        size += file.isDirectory() ? getFolderSize(file) : file.length();
                    }
                }
            } catch (Exception var8) {
                Log.d("FileUtils", var8.toString());
            }

            return size;
        } else {
            return -1L;
        }
    }

//    public static boolean isNoMediaFolder(String path) {
//        if (StringUtils.isEmpty(path)) {
//            return false;
//        } else {
//            File folder = new File(path);
//            if (!folder.isDirectory()) {
//                folder = folder.getParentFile();
//            }
//
//            return (new File(folder, ".nomedia")).exists() || folder.getParentFile() != null && isNoMediaFolder(folder.getParentFile().getPath());
//        }
//    }

//    public static boolean isHideFile(String filePath) {
//        if (StringUtils.isEmpty(filePath)) {
//            return false;
//        } else {
//            File file = new File(filePath);
//            return file.isHidden() || file.getParentFile() != null && isHideFile(file.getParentFile().getPath());
//        }
//    }

    public static String concatFilePaths(String... paths) {
        StringBuilder builder = new StringBuilder();
        boolean lastWithSeparator = false;
        String[] var3 = paths;
        int var4 = paths.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String path = var3[var5];
            if (!TextUtils.isEmpty(path.trim())) {
                if (builder.length() > 0) {
                    boolean firstWithSeparator = path.indexOf(File.separatorChar) == 0;
                    if (firstWithSeparator && lastWithSeparator) {
                        path = path.substring(1);
                    } else if (!firstWithSeparator && !lastWithSeparator) {
                        builder.append(File.separatorChar);
                    }
                }

                builder.append(path);
                lastWithSeparator = path.lastIndexOf(File.separatorChar) == path.length() - 1;
            }
        }

        return builder.toString();
    }

//    public static String extractFileName(String fileName) {
//        int MAX_FILENAME_LEN = true;
//        if (fileName.length() < 80) {
//            return fileName;
//        } else {
//            String baseName = getBaseName(fileName);
//            String ext = getExtension(fileName);
//            if (ext.length() + 1 >= 80) {
//                return fileName.substring(0, 80);
//            } else {
//                int end = 80 - (ext.length() + 1);
//                return baseName.substring(0, end) + "." + ext;
//            }
//        }
//    }

    public static void move(SFile src, SFile dst) throws IOException {
        copy(src, dst);
        src.delete();
    }

    public static void fastCopy(SFile src, SFile target) throws Exception {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;

        try {
            inStream = (FileInputStream)src.getInputStream();
            outStream = (FileOutputStream)target.getOutputStream();
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0L, in.size(), out);
        } catch (Throwable var10) {
            throw new Exception("fastCopy failed!", var10);
        } finally {
            StreamUtils.close(inStream);
            StreamUtils.close(in);
            StreamUtils.close(outStream);
            StreamUtils.close(out);
        }

    }

    public static void copy(SFile srcFile, SFile dstFile) throws IOException {
        if (srcFile == null) {
            throw new RuntimeException("source file is null.");
        } else if (!srcFile.exists()) {
            throw new RuntimeException("source file[" + srcFile.getAbsolutePath() + "] is not exists.");
        } else {
            try {
                srcFile.open(SFile.OpenMode.Read);
                dstFile.open(SFile.OpenMode.Write);
                byte[] buffer = new byte[16384];

                int bytesRead;
                while((bytesRead = srcFile.read(buffer)) != -1) {
                    dstFile.write(buffer, 0, bytesRead);
                }
            } finally {
                srcFile.close();
                dstFile.close();
            }

        }
    }

//    public static void moveFolder(SFile src, SFile dst) throws Exception {
//        try {
//            copyExt(src, dst);
//            removeFolder(src);
//        } catch (Exception var3) {
//            removeFolder(dst);
//            throw var3;
//        }
//    }

//    public static void copyExt(SFile src, SFile dst) throws Exception {
//        if (src.isDirectory()) {
//            copyFolder(src, dst);
//        } else {
//            fastCopy(src, dst);
//        }
//
//    }

//    public static void copyFolder(SFile src, SFile dst) throws Exception {
//        if (!dst.exists() && !dst.mkdir()) {
//            throw new IOException("dst mkdir failed! dst : " + dst.getAbsolutePath());
//        } else {
//            String[] var2 = src.list();
//            int var3 = var2.length;
//
//            for(int var4 = 0; var4 < var3; ++var4) {
//                String f = var2[var4];
//                copyExt(SFile.create(src, f), SFile.create(dst, f));
//            }
//
//        }
//    }

    public static final void removeFolderDescents(SFile parent) {
//        removeFolderDescents(parent, false);
    }

    public static final void removeMediaFolderDescents(SFile parent) {
//        removeFolderDescents(parent, true);
    }

//    private static final void removeFolderDescents(SFile parent, boolean scan) {
//        if (parent != null && parent.exists()) {
//            SFile[] files = parent.listFiles();
//            if (files != null) {
//                SFile[] var3 = files;
//                int var4 = files.length;
//
//                for(int var5 = 0; var5 < var4; ++var5) {
//                    SFile item = var3[var5];
//                    boolean isDir = item.isDirectory();
//                    if (isDir) {
//                        removeFolderDescents(item, scan);
//                    }
//
//                    item.delete();
//                    if (!isDir && scan) {
//                        notifyMediaFileScan(item);
//                    }
//                }
//
//            }
//        }
//    }

//    public static final void removeFolder(SFile parent) {
//        removeFolder(parent, false);
//    }
//
//    public static final void removeMediaFolder(SFile parent) {
//        removeFolder(parent, true);
//    }

//    private static final void removeFolder(SFile parent, boolean scan) {
//        if (parent != null && parent.exists()) {
//            SFile[] files = parent.listFiles();
//            if (files != null) {
//                SFile[] var3 = files;
//                int var4 = files.length;
//
//                for(int var5 = 0; var5 < var4; ++var5) {
//                    SFile item = var3[var5];
//                    if (item.isDirectory()) {
//                        removeFolder(item, scan);
//                    } else {
//                        item.delete();
//                        if (scan) {
//                            notifyMediaFileScan(item);
//                        }
//                    }
//                }
//            }
//
//            parent.delete();
//        }
//    }

    public static final boolean removeFile(SFile file) {
        if (file != null && !file.isDirectory() && file.exists()) {
            if (!file.delete()) {
                return false;
            } else {
//                notifyMediaFileScan(file);
                return true;
            }
        } else {
            return false;
        }
    }

//    public static final void notifyMediaFileScan(final SFile file) {
//        if (file != null) {
//            TaskHelper.execByIoThreadPoll(new RunnableWithName("FileUtils#removeMedia") {
//                public void execute() {
//                    MediaUtils.scanFileForDel(ObjectStore.getContext(), file.toFile());
//                }
//            });
//        }
//    }

    public static long getDataStorageAvailableSize() {
        File root = Environment.getDataDirectory();
        return getStorageAvailableSize(root.getAbsolutePath());
    }

    public static long getExternalStorageAvailableSize() {
        String status = Environment.getExternalStorageState();
        if (!status.equals("mounted")) {
            return 0L;
        } else {
            File path = Environment.getExternalStorageDirectory();
            return getStorageAvailableSize(path.getAbsolutePath());
        }
    }

    public static long getSDTotalSize() {
        String status = Environment.getExternalStorageState();
        if (!status.equals("mounted")) {
            return 0L;
        } else {
            File path = Environment.getExternalStorageDirectory();
            return getStorageTotalSize(path.getAbsolutePath());
        }
    }

//    public static long getCurrentExternalStorageAvailableSize(Context context) {
//        String path = getExternalStorage(context);
//        return getStorageAvailableSize(path);
//    }

    public static long getStorageAvailableSize(String filePath) {
        try {
            StatFs stat = new StatFs(filePath);
            long blockSize = (long)stat.getBlockSize();
            long availableBlocks = (long)stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } catch (Exception var6) {
            return 0L;
        }
    }

    public static long getStorageTotalSize(String filePath) {
        try {
            StatFs stat = new StatFs(filePath);
            long blockSize = (long)stat.getBlockSize();
            long totalBlocks = (long)stat.getBlockCount();
            return totalBlocks * blockSize;
        } catch (Exception var6) {
            return 0L;
        }
    }

//    public static String getExternalStorage(Context context) {
//        Volume currentVolume = StorageVolumeHelper.getVolume(context);
//        return currentVolume.mPath;
//    }

    public static String getCacheDirectory(Context context, String cacheFileName) {
        File cacheDir = null;
        if (Build.VERSION.SDK_INT >= 19) {
            long size = 0L;
            File[] var5 = context.getExternalCacheDirs();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                File file = var5[var7];
                if (file != null) {
                    long availableSize = getStorageAvailableSize(file.getAbsolutePath());
                    if (availableSize > size) {
                        size = availableSize;
                        cacheDir = file;
                    }
                }
            }
        } else {
            cacheDir = context.getExternalCacheDir();
        }

        if (cacheDir == null || !cacheDir.canWrite()) {
            cacheDir = context.getCacheDir();
        }

        File cacheDirectory = new File(cacheDir, cacheFileName);
        return cacheDirectory.mkdirs() || cacheDirectory.exists() && cacheDirectory.isDirectory() ? cacheDirectory.getAbsolutePath() : cacheDir.getAbsolutePath();
    }

//    public static List<String> getAllExternalStorage(Context ctx) {
//        List<String> storages = new ArrayList();
//        List<Volume> volumes = StorageVolumeHelper.getVolumeList(ctx);
//        if (volumes.size() == 0) {
//            storages.add(Environment.getExternalStorageDirectory().getAbsolutePath());
//        } else {
//            for(int i = 0; i < volumes.size(); ++i) {
//                storages.add(((Volume)volumes.get(i)).mPath);
//            }
//        }
//
//        return storages;
//    }

    @TargetApi(19)
    public static File getPrivateExtAppDir(Context context, String root) {
        File f = null;
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                File[] dirs = context.getExternalFilesDirs((String)null);
                File[] var4 = dirs;
                int var5 = dirs.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    File dir = var4[var6];
                    if (dir != null && dir.getAbsolutePath().startsWith(root)) {
                        f = dir;
                        break;
                    }
                }
            } catch (NoSuchMethodError var10) {
            } catch (SecurityException var11) {
            } catch (NullPointerException var12) {
            }
        }

        if (f == null) {
            try {
                if (Build.VERSION.SDK_INT < 19) {
                    context.getExternalFilesDir((String)null);
                }
            } catch (NoSuchMethodError var8) {
            } catch (SecurityException var9) {
            }

            f = getDefaultPrivateExtAppDir(context, root);
        }

        return f;
    }

    public static File getDefaultPrivateExtAppDir(Context context, String root) {
        return new File(root, "/Android/data/" + context.getPackageName());
    }

    public static String requestValidFileName(String parentFolder, String fileName) {
        String filePath = makePath(parentFolder, fileName);
        if (filePath.length() <= 255) {
            return fileName;
        } else {
            String ext = getExtension(fileName);
            String baseName = getBaseName(fileName);
            int overLength = filePath.length() - 240;
            if (baseName != null && baseName.length() > overLength) {
                baseName = baseName.substring(0, baseName.length() - overLength);
                return baseName + fileName.hashCode() + (ext.length() > 0 ? "." + ext : ext);
            } else {
                return fileName;
            }
        }
    }

    public static boolean isSDCardMounted() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            Log.i("FileUtils", "MEDIA_MOUNTED+++");
            return true;
        } else {
            Log.i("FileUtils", "MEDIA_UNMOUNTED---");
            return false;
        }
    }

    public static boolean isFileExist(String path) {
        if (path != null && path.length() > 0) {
            File f = new File(path);
            return f.exists();
        } else {
            return false;
        }
    }

    public static boolean isEmptyFolder(File folder) {
        if (folder != null && !folder.isFile()) {
            String[] list = folder.list();
            return list == null || list.length == 0;
        } else {
            return false;
        }
    }

//    public static void createNoMediaFile(SFile dir) {
//        SFile noMedia = SFile.create(dir, ".nomedia");
//        if (!noMedia.exists()) {
//            noMedia.createFile();
//        }
//
//    }

//    public static boolean removeNoMediaFile(SFile dir) {
//        SFile noMedia = SFile.create(dir, ".nomedia");
//        return noMedia.exists() ? noMedia.delete() : false;
//    }

    public static boolean isAssetFile(String pathInAssetsDir) {
        return !TextUtils.isEmpty(pathInAssetsDir) && pathInAssetsDir.startsWith("file:///android_asset");
    }

    public static boolean isLocalFileUri(String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith("file://");
    }

    public static List<File> findFileByName(String dir, final String fileName) {
        if (!TextUtils.isEmpty(dir) && !TextUtils.isEmpty(fileName)) {
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                return null;
            } else {
                final List<String> childDirs = new ArrayList();
                File[] files = dirFile.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        if (file.isDirectory()) {
                            childDirs.add(file.getAbsolutePath());
                        }

                        return file.isFile() && file.getName().equals(fileName);
                    }
                });
                List<File> results = new ArrayList(Arrays.asList(files));
                Iterator var6 = childDirs.iterator();

                while(var6.hasNext()) {
                    String childDir = (String)var6.next();
                    results.addAll(findFileByName(childDir, fileName));
                }

                return results;
            }
        } else {
            return null;
        }
    }

    public static List<File> findFileByName(List<String> dirs, String fileName) {
        return findFileByName(dirs, fileName, false);
    }

    public static List<File> findFileByName(List<String> dirs, String fileName, boolean isSortDate) {
        List<File> files = new ArrayList();
        Iterator var4 = dirs.iterator();

        while(var4.hasNext()) {
            String dir = (String)var4.next();
            List<File> result = findFileByName(dir, fileName);
            if (result != null) {
                files.addAll(result);
            }
        }

        Log.e("FileUtils", files.toString());
        if (isSortDate) {
            Collections.sort(files, new FileUtils.FileDateComparator());
        }

        return files;
    }

    public static class FileDateComparator implements Comparator<File> {
        public FileDateComparator() {
        }

        public int compare(File f1, File f2) {
            return Long.valueOf(f1.lastModified()).intValue() - Long.valueOf(f2.lastModified()).intValue();
        }
    }
}
