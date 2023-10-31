package com.unshareit.cleantest.clean.document;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;


import com.unshareit.cleantest.clean.utils.ContextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CleanFileDocument extends SFile {
    private File mFile;
    private DocumentFile documentFile;
    private String filePath;
    private Uri contentUri;
    private List<String> apkPathList;
    private List<String> tempList;
    private long size=0;

    public CleanFileDocument(String path, boolean isFolder) {
        if (DataPermissionUtils.isGrant(ContextUtils.getAppContext()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            documentFile = DocumentUtils.getDocumentFile(path,isFolder);
        }
        filePath = path;
        Log.e("CleanFileDocument","是否授权==>"+DataPermissionUtils.isGrant(ContextUtils.getAppContext())+" 创建documentFile==>"+documentFile);
        if (documentFile == null){
            mFile = new File(path);
        }else{
            contentUri = documentFile.getUri();
        }
    }

    public CleanFileDocument(DocumentFile documentFile, String filePath){
        this.filePath = filePath;
        this.documentFile = documentFile;
        contentUri = documentFile.getUri();
    }

    private CleanFileDocument(File file) {
        this.mFile = file;
        filePath = file.getAbsolutePath();
    }

    private CleanFileDocument(DocumentFile documentFile) {
        this.documentFile = documentFile;
        contentUri = documentFile.getUri();
    }


    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public boolean canRead() {
        return false;
    }

    @Override
    public boolean exists() {
        if (documentFile != null){
            return documentFile.exists();
        }

        if (mFile != null){
            return mFile.exists();
        }
        return false;
    }

    @Override
    public boolean isDirectory() {
        if (documentFile != null){
            return documentFile.isDirectory();
        }

        if (mFile != null){
            return mFile.isDirectory();
        }
        return false;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public SFile[] listFiles() {
        Log.e("CleanFileDocument","listFiles==>"+filePath+" isDocument==>"+isDocument(filePath));
        if (isDocument(filePath)){
            return documentFiles();
        }
        return files();
    }


    private SFile[] documentFiles(){
        if (this.documentFile == null) {
            return null;
        } else {
            DocumentFile[] files = this.documentFile.listFiles();
            if (files == null) {
                return null;
            } else {
                List<SFile> results = new ArrayList();
                DocumentFile[] var3 = files;
                int var4 = files.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    DocumentFile file = var3[var5];
                    results.add(new CleanFileDocument(file));
                }

                return (SFile[])results.toArray(new SFile[results.size()]);
            }
        }
    }

    private SFile[] files(){
        if (mFile == null){
            return null;
        }

        if (!mFile.exists() || !mFile.isDirectory()){
            return null;
        }
        File[] files = mFile.listFiles();
        if (files == null) {
            return null;
        } else {
            List<SFile> results = new ArrayList();
            File[] var3 = files;
            int var4 = files.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File file = var3[var5];
                results.add(new CleanFileDocument(file));
            }
            return (SFile[])results.toArray(new SFile[results.size()]);
        }
    }


    @Override
    public SFile findFile(String name) {
        return null;
    }

    @Override
    public String[] list() {
        return new String[0];
    }

    @Override
    public SFile[] listFiles(Filter filter) {
        return new SFile[0];
    }

    @Override
    public SFile getParent() {
        return null;
    }

    @Override
    public String getAbsolutePath() {
        return filePath;
    }

    @Override
    public String getName() {
        if (documentFile != null){
            return documentFile.getName();
        }

        if (mFile != null){
            return mFile.getName();
        }
        return null;
    }

    @Override
    public long length() {
        if (documentFile != null){
            return documentFile.length();
        }
        if (mFile != null){
            return mFile.length();
        }
        return 0;
    }

    @Override
    public void setLastModified(long last) {

    }

    @Override
    public long lastModified() {
        return 0;
    }

    @Override
    public boolean mkdir() {
        return false;
    }

    @Override
    public boolean mkdirs() {
        return false;
    }

    @Override
    public boolean createFile() {
        return false;
    }

    @Override
    public boolean delete() {
        if (documentFile != null){
            return documentFile.delete();
        }
        if (mFile != null){
            return mFile.delete();
        }
        return false;
    }

    @Override
    public boolean renameTo(SFile target) {
        return false;
    }

    @Override
    public File toFile() {
        return mFile;
    }

    @Override
    public void open(OpenMode mode) throws FileNotFoundException {

    }

    @Override
    public void seek(OpenMode mode, long offset) throws IOException {

    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return 0;
    }

    @Override
    public int read(byte[] buffer, int start, int length) throws IOException {
        return 0;
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {

    }

    @Override
    public void close() {

    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public OutputStream getOutputStream(boolean append) throws IOException {
        return null;
    }

    @Override
    public boolean isSupportRename() {
        return false;
    }

    @Override
    public Uri toUri() {
        return contentUri;
    }

    public DocumentFile getDocumentFile(){
        return documentFile;
    }

    public boolean isDocument(String filePath){
        return DocumentUtils.isDocument(filePath) && documentFile != null;
    }

    public boolean isFile(){
        if (documentFile != null){
            return documentFile.isFile();
        }

        if (mFile != null){
            return mFile.isFile();
        }
        return false;
    }

    /**
     * 扫描DocumentFile文件路径
     * @param path
     * @param documentFile
     * @return
     */
    public DocumentFile scanDocumentFile(String path, DocumentFile documentFile){
        try{
            if (documentFile == null || !documentFile.isDirectory()){
                return null;
            }
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String[] str = path.replace(rootPath+"/Android/data/","").split("/");
            List<String> list;
            if (tempList == null || tempList.isEmpty()){
                list = Arrays.asList(str);
                tempList = new ArrayList<>(list);
            }else if (tempList.size() > 0){
                tempList.remove(0);
            }
            Log.e("CleanFileDocument","size===>"+tempList.size()+" 文件存在==>"+documentFile.exists()+" 文件夹==>"+documentFile.isDirectory());
            if (tempList != null && tempList.size() > 0){
                for (int i=0; i<tempList.size(); i++){
                    Log.e("CleanFileDocument","key===>"+tempList.get(i));
                    DocumentFile childDocument = documentFile.findFile(tempList.get(i));
                    Log.e("CleanFileDocument","childDocument===>"+childDocument);
                    if (childDocument == null){
                        return null;
                    }
                    if (tempList.get(i).equals(childDocument.getName())){
                        if (childDocument.isDirectory()){
                            return scanDocumentFile(path,childDocument);
                        }
                    }
                }
            }else if (documentFile.exists()){
                Log.e("CleanFileDocument","返回对象=====>");
                for (DocumentFile documentFile1: documentFile.listFiles()){
                    Log.e("CleanFileDocument","名称=====>"+documentFile1.getName());
                }
                return documentFile;
            }
        }catch (Exception e){}

        Log.e("CleanFileDocument","返回空=====>");
        return null;
    }

    /**
     * 将File转DocumentFile
     * @param file
     * @param isFolder
     * @return
     */
    public DocumentFile fileConvertDocument(File file, boolean isFolder){
        if (file == null || !DocumentUtils.isAndroidRDoc()){
            return null;
        }
        String filePath = file.getAbsolutePath();
        if (filePath.contains("sdcard/Android/data")){
            filePath = filePath.replace("sdcard","storage/emulated/0");
        }
        DocumentFile documentFile = DocumentUtils.getDocumentFile(filePath,isFolder);
        if (documentFile == null){
            return null;
        }
        if (documentFile.isFile()){
            return documentFile;
        }
        return scanDocumentFile(filePath,documentFile);
    }

    /**
     * 获取documentFile大小
     * @param documentFile
     * @return
     */
    public long getDocumentSize(DocumentFile documentFile){
        long fileSize = 0;
        if (documentFile == null)
            return fileSize;
        DocumentFile[] documentFiles = documentFile.listFiles();
        if (documentFiles != null){
            if (documentFiles.length <= 0){
                fileSize += documentFile.length();
                return fileSize;
            }
            for (DocumentFile childDoc: documentFiles){
                if (childDoc == null)
                    continue;
                size += childDoc.length();
                if (childDoc.isDirectory()){
                    return getDocumentSize(childDoc);
                }else {
//                    size += childDoc.length();
                    fileSize = size;
                }
            }
        }else {
            fileSize += documentFile.length();
        }
        size = 0;
        return fileSize;
    }

}
