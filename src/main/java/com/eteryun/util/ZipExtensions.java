package com.eteryun.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtensions {
	public static void extractZip(InputStream zipStream, File folder) throws IOException {
		if (!folder.exists())
			folder.mkdirs();

		ZipInputStream zipInput = new ZipInputStream(zipStream);
		ZipEntry zipEntry = zipInput.getNextEntry();

		while (zipEntry != null) {
			if (zipEntry.isDirectory()) {
				zipEntry = zipInput.getNextEntry();
				continue;
			}

			File newFile = new File(folder, zipEntry.getName());
			new File(newFile.getParent()).mkdirs();

			FileOutputStream output = new FileOutputStream(newFile);

			zipInput.transferTo(output);

			zipEntry = zipInput.getNextEntry();
		}

		zipInput.closeEntry();
		zipInput.close();
	}

	public static void extractZip(File zipFile, File folder) throws IOException {
		extractZip(new FileInputStream(zipFile), folder);
	}
}
