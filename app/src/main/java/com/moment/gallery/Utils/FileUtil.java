package com.moment.gallery.Utils;

import java.io.File;

public class FileUtil {
    private static String[] imageNames;
    private static boolean isImageFile = false;


    public String[] getImageNames(String folderPath) {
        File file = new File(folderPath);

        String[] files = file.list();

        int imageFileNums = 0;
        for (int i = 0; i < files.length; i++) {
            File file1 = new File(folderPath + "/" + files[i]);

            if (!file1.isDirectory()) {
                if (isImageFile(file1.getName())) {
                    imageFileNums++;
                }
            }
        }
        imageNames = new String[imageFileNums];
        int j = 0;
        for (int i = 0; i < files.length; i++) {
            File file1 = new File(folderPath + "/" + files[i]);

            if (!file1.isDirectory()) {
                if (isImageFile(file1.getName())) {
                    imageNames[j] = file1.getName();
                    j++;
                }
            }
        }
        return imageNames;
    }

    private boolean isImageFile(String fileName) {
        String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (fileEnd.equalsIgnoreCase("jpg")) {
            return true;
        } else if (fileEnd.equalsIgnoreCase("png")) {
            return true;
        } else if (fileEnd.equalsIgnoreCase("bmp")) {
            return true;
        } else {
            return false;
        }
    }


}
