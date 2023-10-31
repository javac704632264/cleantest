package com.unshareit.cleantest.clean.document;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class SFile {
    public static boolean documentUseAssetMethod = true;

    public SFile() {
    }

//    public static void setSupportRenameTo(SFile root, boolean support) {
//        if (root instanceof SFileDocumentImpl) {
//            SFileDocumentImpl.setSupportRenameTo(support);
//        }
//
//    }

//    public static SFile create(File f) {
//        return new SFileOriginalImpl(f);
//    }

//    public static SFile create(DocumentFile doc) {
//        return new SFileDocumentImpl(doc);
//    }

//    public static SFile create(String path) {
//        Context context = ObjectStore.getContext();
//        Uri uri = Uri.parse(path);
//        return (SFile)(isDocumentUriSafety(context, uri) ? new SFileDocumentImpl(uri, false) : new SFileOriginalImpl(path));
//    }

//    public static SFile createFolder(String path) {
//        Context context = ObjectStore.getContext();
//        Uri uri = Uri.parse(path);
//        return (SFile)(isDocumentUriSafety(context, uri) ? new SFileDocumentImpl(uri, true) : new SFileOriginalImpl(path));
//    }

//    public static SFile createFolder(String path, boolean toNearestPath) {
//        Context context = ObjectStore.getContext();
//        Uri uri = Uri.parse(path);
//        return (SFile)(isDocumentUriSafety(context, uri) ? new SFileDocumentImpl(uri, true, toNearestPath) : new SFileOriginalImpl(path));
//    }

//    public static SFile create(SFile fs, String name) {
//        if (fs instanceof SFileOriginalImpl) {
//            return new SFileOriginalImpl((SFileOriginalImpl)fs, name);
//        } else {
//            return fs instanceof SFileDocumentImpl ? new SFileDocumentImpl((SFileDocumentImpl)fs, name) : null;
//        }
//    }

//    public static SFile createUnique(SFile fs, String name) {
//        String ext = FileUtils.getExtension(name);
//        String baseName = FileUtils.getBaseName(name);
//        SFile uniquePath = null;
//        String uniqueFileName = name;
//        int postfix = 0;
//
//        while(true) {
//            uniquePath = create(fs, uniqueFileName);
//            if (uniquePath == null || !uniquePath.exists()) {
//                return uniquePath;
//            }
//
//            ++postfix;
//            uniqueFileName = baseName + " (" + postfix + ")" + (ext.length() > 0 ? "." + ext : ext);
//        }
//    }

//    public static SFile createUniqueFolder(SFile fs, String name) {
//        SFile uniquePath = null;
//        String uniqueFileName = name;
//        int postfix = 0;
//
//        while(true) {
//            uniquePath = create(fs, uniqueFileName);
//            if (uniquePath == null || !uniquePath.exists()) {
//                return uniquePath;
//            }
//
//            ++postfix;
//            uniqueFileName = name + "_" + postfix;
//        }
//    }

    public abstract boolean canWrite();

    public abstract boolean canRead();

    public abstract boolean exists();

    public abstract boolean isDirectory();

    public abstract boolean isHidden();

    public abstract SFile[] listFiles();

    public abstract SFile findFile(String var1);

    public abstract String[] list();

    public abstract SFile[] listFiles(SFile.Filter var1);

    public abstract SFile getParent();

    public abstract String getAbsolutePath();

    public abstract String getName();

    public abstract long length();

    public abstract void setLastModified(long var1);

    public abstract long lastModified();

    public abstract boolean mkdir();

    public abstract boolean mkdirs();

    public abstract boolean createFile();

    public abstract boolean delete();

    public abstract boolean renameTo(SFile var1);

    public abstract File toFile();

    public abstract void open(SFile.OpenMode var1) throws FileNotFoundException;

    public abstract void seek(SFile.OpenMode var1, long var2) throws IOException;

    public abstract int read(byte[] var1) throws IOException;

    public abstract int read(byte[] var1, int var2, int var3) throws IOException;

    public abstract void write(byte[] var1, int var2, int var3) throws IOException;

    public abstract void close();

    public abstract InputStream getInputStream() throws IOException;

    public abstract OutputStream getOutputStream() throws IOException;

    public abstract OutputStream getOutputStream(boolean var1) throws IOException;

    public abstract boolean isSupportRename();

    public abstract Uri toUri();

//    public boolean checkRenameTo(SFile dst) {
//        return dst instanceof SFileOriginalImpl;
//    }

    public boolean renameTo(String displayName) {
        throw new IllegalArgumentException("only document support rename(display) method!");
    }

//    public static boolean isDocument(SFile file) {
//        return file instanceof SFileDocumentImpl;
//    }

    public static boolean isDocumentUri(String uriString) {
//        Assert.notNull(uriString);
        Uri uri = Uri.parse(uriString);
        return "content".equals(uri.getScheme());
    }

    private static boolean isDocumentUriSafety(Context context, Uri uri) {
        try {
            return DocumentFile.isDocumentUri(context, uri);
        } catch (NoClassDefFoundError var3) {
            return false;
        }
    }

    public interface Filter {
        boolean accept(SFile var1);
    }

    public static enum OpenMode {
        Read,
        Write,
        RW;

        private OpenMode() {
        }
    }
}
