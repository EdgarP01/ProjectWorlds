package com.gmail.trentech.pjw.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.gmail.trentech.pjw.Main;

public class Zip {

	public static void save(String zipFileName, File directory){
		Main.getLog().info("Creating world backup..");
		File backupDir = new File("backup");
        	if (!backupDir.isDirectory()) {
        		backupDir.mkdirs();
        	}

		String zipFile = backupDir.getAbsolutePath() + File.separator + zipFileName + ".zip";

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
			ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
			addDir(directory, zipOutputStream);
			zipOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addDir(File directory, ZipOutputStream zipOutputStream) throws IOException {
		File backupDir = new File("backup");
		File[] files = directory.listFiles();
		byte[] buffer = new byte[1024];

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				addDir(files[i], zipOutputStream);
				continue;
			}
			
			FileInputStream fileInputStream = new FileInputStream(files[i]);
			zipOutputStream.putNextEntry(new ZipEntry(files[i].getAbsolutePath().replace(backupDir.getAbsolutePath(), "")));

			int length;

			while ((length = fileInputStream.read(buffer)) > 0) {
				zipOutputStream.write(buffer, 0, length);
			}

			zipOutputStream.closeEntry();
			fileInputStream.close();
		}
	}
}
