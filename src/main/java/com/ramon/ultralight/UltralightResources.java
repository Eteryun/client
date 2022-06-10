package com.ramon.ultralight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.eteryun.util.HttpClient;
import com.eteryun.util.ZipExtensions;

import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;

public class UltralightResources {
	private static Minecraft minecraft = Minecraft.getInstance();
	private static Logger logger = UltralightEngine.getLogger();
	private static String sdkUrl = "http://assets.eteryun.com.br/ultralight/";
	public static File ultralightRoot = new File(minecraft.gameDirectory, "ultralight");
	public static File ultralightNatives = new File(ultralightRoot, "natives");
	public static File ultralightCache = new File(ultralightRoot, "cache");
	private static String version = "0.4.12";
	private static String[] libraries = new String[] { "UltralightCore.dll", "glib-2.0-0.dll", "gmodule-2.0-0.dll",
			"gobject-2.0-0.dll", "gio-2.0-0.dll", "gstreamer-full-1.0.dll", "gthread-2.0-0.dll", "WebCore.dll",
			"Ultralight.dll", "AppCore.dll", "ultralight-java.dll" };
	
	public static void loadLibraries() {
		try {
			for (String library : UltralightResources.libraries) {
				File libraryFile = new File(ultralightNatives, library);
				System.load(libraryFile.getAbsolutePath());
			}
		} catch (Exception e) {
			Minecraft.crash(new CrashReport("Não foi possivel carregar as bibliotecas", e));
		}
	}
	
	public static void downloadLibraries() {
		try {
			if (ultralightCache.exists())
				ultralightCache.delete();

			File versionFile = new File(ultralightRoot, "VERSION_SDK");

			if (versionFile.exists()) {
				FileReader versionReader = new FileReader(versionFile);
				BufferedReader versionBuffer = new BufferedReader(versionReader);
				String versionInFile = versionBuffer.readLine();
				versionBuffer.close();

				if (versionInFile.equals(version))
					return;
			}

			if (!ultralightRoot.exists())
				ultralightRoot.mkdir();

			logger.info("Fazendo download das bibliotecas...");

			String downloadUrl = sdkUrl + version + "/win-x64.zip";
			File resourceZipFile = new File(ultralightRoot, "libraries.zip");
			HttpClient.download(downloadUrl, resourceZipFile);

			logger.info("Extraindo as bibliotecas");
			ZipExtensions.extractZip(resourceZipFile, ultralightRoot);
			FileWriter versionWrite = new FileWriter(versionFile, true);
			versionWrite.write(version);
			versionWrite.close();

			logger.info("Deletando arquivos temporarios");
			resourceZipFile.delete();

			logger.info("Bibliotecas carregas com sucesso");
		} catch (Exception e) {
			Minecraft.crash(new CrashReport("Não foi possivel realizar o download das bibliotecas", e));
		}
	}
}
