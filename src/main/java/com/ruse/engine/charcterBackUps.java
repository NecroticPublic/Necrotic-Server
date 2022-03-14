package com.ruse.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Depreciated PLEASE DON'T USE THIS. It's terrible. It will lag your system beyond belief.
 * If you're looking for a backup system, use cron to backup files into an archive. It's better.
 * Crimmypoo
 */

/**
 * Creates a ZIP archive of the character directory
 *
 * @author Octave
 * @date FEB 2 2013
 */
public class charcterBackUps  {
	/**
	 * Character file directory
	 */
	private static final String INPUT_DIRECTORY = "./data/saves/characters";

	/**
	 * Archive directory
	 */
	private static final String OUTPUT_DIRECTORY = "./archive/";

	public static long backUpTimer = System.currentTimeMillis();

	public static void backUpTimer() {
		if(System.currentTimeMillis() - backUpTimer > 3600000) {
			startBackupService();
			backUpTimer = System.currentTimeMillis();
		} else {
	//		System.out.print("It's not my time yet.");
		}

	}



	public static void main(String[] args) throws InterruptedException {
		boolean run = true;
		try {
			System.out.println("Character backup engine is started at " + getTime());
			while(run) {
				backUpTimer();
			//	System.out.println("Loop life?");
				Thread.sleep(60000);
			}
		} catch (Exception ex) {
		//	System.out.println("Are we catching?");
			run = false;
			System.exit(1);
		}
	}


	/**
	 * Starts the backup procedure
	 */
	public static void startBackupService() {
		long startTime = System.currentTimeMillis();
		System.out.println("Starting backup service . . .");
		System.out.println("\tInput directory: " + INPUT_DIRECTORY);
		System.out.println("\tOutput directory: " + OUTPUT_DIRECTORY);
		try {
			zipFolder("./data/saves/", OUTPUT_DIRECTORY
					+ "/Character Archive [" + getTime() + "].zip");
			System.out.println("\tSuccessfully archived " + total + " files");
		} catch (Exception e) {
			System.out.println("\tAn error has occured: " + e);
		}
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("\tProcedure took: " + duration + "ms");
		System.out.println();
		total = 0;
	}

	/**
	 * Formats the time and date for use in filename
	 *
	 * @return Formatted time
	 */
	private static String getTime() {
		Date getDate = new Date();
		String timeFormat = "M\u2215d\u2215yy h\u02D0mma";
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
		return sdf.format(getDate);
	}

	/**
	 * Prepares the folder to be archived
	 *
	 * @param srcFolder
	 *            - Source folder to ZIP
	 * @param destZipFile
	 *            - Destination ZIP archive
	 */
	private static void zipFolder(String srcFolder, String destZipFile)
			throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
	}

	/**
	 * Adds file to ZIP archive
	 *
	 * @param path
	 *            - File directory

	 *            - ZIP archive
	 */
	private static void addFileToZip(String path, String srcFile,
									 ZipOutputStream zip) throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
			in.close(); //added this
		}
		total++;
	}

	/**
	 * Adds folder to ZIP archive
	 *
	 * @param path
	 *            - File directory
	 * @param srcFolder
	 *            - Source folder
	 *            - ZIP archive
	 */
	private static void addFolderToZip(String path, String srcFolder,
									   ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/"
						+ fileName, zip);
			}
		}
	}

	public static int total = 0;
}