package com.loopeer.android.librarys.imagegroupview.utils;

import java.io.File;
import java.io.FileInputStream;

public class FileUtils {

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {

        }
    }

    public static long getFileSizes(File f) throws Exception {
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
        } else {
            f.createNewFile();
            System.out.println("文件不存在");
        }
        return s;
    }

    public static boolean fileIsAvaliableImage(File f) throws Exception {
        long s;
        if (f.exists()) {
            FileInputStream fis;
            fis = new FileInputStream(f);
            s = fis.available();
        } else {
            f.createNewFile();
            return false;
        }
        return s > 1000 * 2;
    }
}
