package com.unshareit.cleantest.clean.utils;

import android.util.Log;

import com.unshareit.cleantest.clean.document.SFile;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreamUtils {
    private static final String TAG = "StreamUtils";
    private static final int BUFFER_SIZE = 65536;

    private StreamUtils() {
    }

    public static void inputStreamToOutputStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[65536];

        int r;
        while((r = input.read(buffer)) != -1) {
            output.write(buffer, 0, r);
        }

    }

    public static String inputStreamToString(InputStream is, boolean sourceIsUTF8) throws IOException {
        InputStreamReader isr = sourceIsUTF8 ? new InputStreamReader(is, Charset.forName("UTF-8")) : new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer sb = new StringBuffer();

        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        return sb.toString().trim();
    }

    public static int readBuffer(InputStream input, byte[] buffer) throws IOException {
        return readBuffer(input, buffer, 0, buffer.length);
    }

    public static int readBuffer(InputStream input, byte[] buffer, int offset, int length) throws IOException {
        int sum;
        int r;
        for(sum = 0; length > 0 && (r = input.read(buffer, offset, length)) != -1; length -= r) {
            sum += r;
            offset += r;
        }

        return sum;
    }

    public static void writeStringToFile(String str, SFile file) throws IOException {
        try {
            file.open(SFile.OpenMode.Write);
            byte[] buffer = str.getBytes("UTF-8");
            file.write(buffer, 0, buffer.length);
        } finally {
            file.close();
        }

    }

    public static String readStringFromFile(SFile file) throws IOException {
        return readStringFromFile(file, 2147483647);
    }

    public static String readStringFromFile(SFile file, int count) throws IOException {
        String var4;
        try {
            file.open(SFile.OpenMode.Read);
            int bufferSize = Math.min((int)file.length(), count);
            byte[] buffer = new byte[bufferSize];
            file.read(buffer, 0, bufferSize);
            var4 = new String(buffer);
        } finally {
            file.close();
        }

        return var4;
    }

    public static String readStringFromRaw(int rawId) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;

        try {
            inputStream = ContextUtils.getAppContext().getResources().openRawResource(rawId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line;
            try {
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException var11) {
                Log.e("StreamUtils", "read file error!", var11);
            }
        } catch (Exception var12) {
            Log.e("StreamUtils", "read file error!", var12);
        } finally {
            close(inputStream);
        }

        return sb.toString();
    }

    public static byte[] readBufferFromFile(SFile file, long offset, int length) throws IOException {
        if (file != null && file.length() > offset) {
            byte[] var6;
            try {
                file.open(SFile.OpenMode.Read);
                file.seek(SFile.OpenMode.Read, offset);
                int size = Math.min((int)(file.length() - offset), length);
                byte[] buffer = new byte[size];
                file.read(buffer);
                var6 = buffer;
            } finally {
                file.close();
            }

            return var6;
        } else {
            return null;
        }
    }

    public static int[] readHashLinesFromFile(File file, boolean isSort) throws IOException {
        List<Integer> list = new ArrayList();
        FileReader reader = null;
        BufferedReader br = null;

        try {
            reader = new FileReader(file);
            br = new BufferedReader(reader);
            String line = null;

            while((line = br.readLine()) != null) {
                list.add(line.hashCode());
            }
        } catch (IOException var9) {
            throw var9;
        } finally {
            close(reader);
            close(br);
        }

        if (isSort) {
            Collections.sort(list);
        }

        int[] array = new int[list.size()];

        for(int i = 0; i < list.size(); ++i) {
            array[i] = (Integer)list.get(i);
        }

        return array;
    }

    public static int[] readIntArrayFromFile(File file) throws IOException {
        FileInputStream fis = null;

        int[] var2;
        try {
            fis = new FileInputStream(file);
            var2 = readIntArrayFromInputStream(fis);
        } catch (IOException var6) {
            throw var6;
        } finally {
            close(fis);
        }

        return var2;
    }

    public static int[] readIntArrayFromInputStream(InputStream is) throws IOException {
        int length = is.available();
        length = length % 4 == 0 ? length / 4 : length / 4 + 1;
        int[] array = new int[length];
        DataInputStream dis = null;

        try {
            dis = new DataInputStream(is);

            for(int i = 0; i < array.length; ++i) {
                array[i] = dis.readInt();
            }
        } catch (IOException var8) {

            throw var8;
        } finally {
            close(dis);
        }

        return array;
    }

    public static void writeIntArrayToFile(File file, int[] array, int len) throws IOException {
        FileOutputStream fos = null;
        DataOutputStream dos = null;

        try {
            fos = new FileOutputStream(file);
            dos = new DataOutputStream(fos);

            for(int i = 0; i < len; ++i) {
                dos.writeInt(array[i]);
            }
        } catch (IOException var9) {
            throw var9;
        } finally {
            close(fos);
            close(dos);
        }

    }

    public static void writeStreamToFile(InputStream in, SFile file) throws IOException {
        BufferedInputStream input = null;

        try {
            input = new BufferedInputStream(in);
            file.open(SFile.OpenMode.Write);
            byte[] buffer = new byte[16384];

            int bytesRead;
            while((bytesRead = input.read(buffer)) != -1) {
                file.write(buffer, 0, bytesRead);
            }
        } finally {
            close(input);
            file.close();
        }

    }

    public static void writeFileToStream(SFile file, OutputStream output) throws IOException {
        try {
            file.open(SFile.OpenMode.Read);
            byte[] b = new byte[4096];

            int r;
            while((r = file.read(b)) != -1) {
                output.write(b, 0, r);
            }

            output.flush();
        } finally {
            file.close();
        }
    }

    public static void close(Closeable object) {
        if (object != null) {
            try {
                object.close();
            } catch (Throwable var2) {
            }
        }

    }
}
