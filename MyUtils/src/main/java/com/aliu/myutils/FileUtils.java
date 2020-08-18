package com.aliu.myutils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Locale;

/**
 * utils for file process.
 *
 * some code is from apache common io package.
 *
 * @author yqg1817
 */
public class FileUtils {

  public static final String ASSETS_THEME = "assets_android://";

  private static class ErrCode {

    public static final int RESULT_OK = 0;
    public static final int ERR_FAIL = 1;
    public static final int ERR_INVALID_PARAMETER = 2;
    public static final int ERR_NO_MEMORY = 3;

    public static final int ERR_UNSUPPORTED = 4;
    public static final int ERR_NOT_READY = 5;
    public static final int ERR_BUSY = 6;
    public static final int ERR_NO_DISK = 11;
  }

  public static final long MVE_SAVE_MIN_SPACE = (500 * 1024);
  /**
   * The number of bytes in a kilobyte.
   */
  public static final long ONE_KB = 1024;

  /**
   * The number of bytes in a kilobyte.
   *
   * @since 2.4
   */
  public static final BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);

  /**
   * The number of bytes in a megabyte.
   */
  public static final long ONE_MB = ONE_KB * ONE_KB;

  /**
   * The number of bytes in a megabyte.
   *
   * @since 2.4
   */
  public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);

  /**
   * The file copy buffer size (30 MB)
   */
  public static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;

  /**
   * The number of bytes in a gigabyte.
   */
  public static final long ONE_GB = ONE_KB * ONE_MB;

  /**
   * The number of bytes in a gigabyte.
   *
   * @since 2.4
   */
  public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);

  /**
   * The number of bytes in a terabyte.
   */
  public static final long ONE_TB = ONE_KB * ONE_GB;

  /**
   * The number of bytes in a terabyte.
   *
   * @since 2.4
   */
  public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);

  /**
   * The number of bytes in a petabyte.
   */
  public static final long ONE_PB = ONE_KB * ONE_TB;

  /**
   * The number of bytes in a petabyte.
   *
   * @since 2.4
   */
  public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);

  /**
   * The number of bytes in an exabyte.
   */
  public static final long ONE_EB = ONE_KB * ONE_PB;

  /**
   * The number of bytes in an exabyte.
   *
   * @since 2.4
   */
  public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);

  /**
   * The number of bytes in a zettabyte.
   */
  public static final BigInteger ONE_ZB =
      BigInteger.valueOf(ONE_KB).multiply(BigInteger.valueOf(ONE_EB));

  /**
   * The number of bytes in a yottabyte.
   */
  public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);

  /**
   * An empty array of type <code>File</code>.
   */
  public static final File[] EMPTY_FILE_ARRAY = new File[0];

  /**
   * The UTF-8 character set, used to decode octets in URLs.
   */
  public static final Charset UTF8 = Charset.forName("UTF-8");

  // -----------------------------------------------------------------------

  /**
   *
   */
  public static void deleteFolderSubFiles(String path, FilenameFilter filter) {
    File fileFolder = new File(path);

    if (fileFolder.exists() && fileFolder.isDirectory()) {
      File[] fileList = fileFolder.listFiles(filter);
      if (null == fileList || fileList.length <= 0) return;

      for (int i = 0; i < fileList.length; i++) {
        if (fileList[i].isFile()) {// only delete file.no directory.
          fileList[i].delete();
        }
      }
    }
    return;
  }

  /**
   * <p>
   * if not dir/dir not empty ,return false;
   * </p>
   */
  public static boolean isDirEmpty(String path) {
    if (!TextUtils.isEmpty(path)) {
      File f = new File(path);
      if (f.exists() && f.isDirectory()) {
        File[] files = f.listFiles();
        if (files == null) return true;
        return files.length <= 0;
      }
    }
    return false;
  }

  /**
   * <p>
   * get a free file name, auto increase index.
   * </p>
   */
  public static String getFreeFileName(String strPath, String strPrefix, String strExt,
      int iStartIndex) {
    if (null == strPath || null == strPrefix || null == strExt) return null;

    String strFreeFileName = null;

    if (!strPath.endsWith(File.separator)) {
      strPath += File.separator;
    }

    String strPrefixNew = null;
    if (!strPrefix.endsWith("_")) {
      strFreeFileName = strPath + strPrefix + strExt;
      if (!isFileExisted(strFreeFileName)) return strFreeFileName;

      strPrefixNew = strPrefix + "_";
      if (iStartIndex == 0) iStartIndex = 1;
    } else {
      strPrefixNew = strPrefix;
    }

    int i = iStartIndex;
    while (true) {
      strFreeFileName = String.format(Locale.US, "%s%s%d%s", strPath, strPrefixNew, i, strExt);
      if (!isFileExisted(strFreeFileName)) break;
      i++;
    }
    return strFreeFileName;
  }

  /**
   *
   */
  public static boolean isValidFileName(String strFileName, String strInvalidCharset) {
    if (null == strFileName || 0 >= strFileName.length() || null == strInvalidCharset) return false;

    int iFileNameLen = strFileName.length();
    int iCharsetLen = strInvalidCharset.length();
    for (int i = 0; i < iFileNameLen; i++) {
      for (int j = 0; j < iCharsetLen; j++) {
        if (strFileName.charAt(i) == strInvalidCharset.charAt(j)) return false;
      }
    }
    return true;
  }

  /**
   *
   */
  public static boolean isValidFileName(String strFileName) {
    if (TextUtils.isEmpty(strFileName)) return false;

    boolean bCanCreate = false;
    File f = null;
    try {
      f = new File(strFileName);
      if (f.exists()) {
        bCanCreate = f.isFile();
      } else {
        bCanCreate = f.createNewFile();
        if (f.exists()) {
          f.delete();
        }
      }
    } catch (Throwable e) {
      Log.e("exception", "IOException=" + e.getMessage());
    }
    return bCanCreate;
  }

  /**
   *
   */
  public static int checkFileSystemBeforeSave(String strOutputPath) {
    boolean bPathCreated = createMultilevelDirectory(strOutputPath);
    if (!bPathCreated) {
      return ErrCode.ERR_INVALID_PARAMETER;
    }

    long lFreeSpace = getFreeSpace(strOutputPath);
    if (lFreeSpace > 0 && lFreeSpace <= MVE_SAVE_MIN_SPACE) {
      return ErrCode.ERR_NO_DISK;
    }

    return ErrCode.RESULT_OK;
  }

  /**
   *
   */
  public static boolean isFileExisted(String strFullFileName) {
    if (TextUtils.isEmpty(strFullFileName)) return false;

    if (strFullFileName.startsWith(ASSETS_THEME)) {
      String strAssetsFile = strFullFileName.substring(ASSETS_THEME.length());
      if (TextUtils.isEmpty(strAssetsFile)) return false;
      return ResourceUtils.isAssetsFileExisted(ResourceUtils.mAssetsManager, strAssetsFile);
    } else {
      File file = new File(strFullFileName);
      return (file.isFile() && file.exists());
    }
  }

  /**
   *
   */
  public static boolean isDirectoryExisted(String strDiretory) {
    if (TextUtils.isEmpty(strDiretory)) return false;

    File file = new File(strDiretory);
    return (file.exists() && file.isDirectory());
  }

  /**
   *
   */
  public static boolean deleteFiles(String[] filePaths) {
    if (filePaths != null) {
      for (String filepath : filePaths) {
        deleteFile(filepath);
      }
    }
    return true;
  }

  /**
   *
   */
  public static boolean deleteFile(String strFullFileName) {
    if (TextUtils.isEmpty(strFullFileName)) return false;

    File file = new File(strFullFileName);
    if (file.isFile()) {
      try {
        file.delete();
      } catch (Exception exception) {
        return false;
      }
      return true;
    }
    return false;
  }

  /**
   *
   */
  public static boolean deleteDirectory(String dir) {
    if (!dir.endsWith(File.separator)) {
      dir = dir + File.separator;
    }
    File dirFile = new File(dir);
    if (!dirFile.exists() || !dirFile.isDirectory()) {
      return false;
    }
    boolean flag = true;
    File[] files = dirFile.listFiles();
    if (files == null) return true;

    for (int i = 0; i < files.length; i++) {
      if (files[i].isFile()) {
        flag = deleteFile(files[i].getAbsolutePath());
        if (!flag) {
          break;
        }
      } else {
        flag = deleteDirectory(files[i].getAbsolutePath());
        if (!flag) {
          break;
        }
      }
    }

    if (!flag) {
      return false;
    }

    if (dirFile.delete()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   *
   */
  public static boolean copyDirectory(String srcPath, String destPath) {
    if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(destPath)) return false;

    if (!srcPath.endsWith(File.separator)) {
      srcPath = srcPath + File.separator;
    }

    if (!destPath.endsWith(File.separator)) {
      destPath = destPath + File.separator;
    }

    File dirFile = new File(srcPath);
    if (!dirFile.exists() || !dirFile.isDirectory()) {
      return false;
    }

    boolean flag = true;
    String strFileLists[] = dirFile.list();
    File file = null;
    if (strFileLists != null) {
      for (int i = 0; i < strFileLists.length; i++) {
        file = new File(srcPath + strFileLists[i]);
        if (file.isFile()) {
          flag = copyFile(srcPath + strFileLists[i], destPath + strFileLists[i]);
          if (!flag) {
            break;
          }
        } else {
          createMultilevelDirectory(destPath + strFileLists[i] + File.separator);
          flag = copyDirectory(srcPath + strFileLists[i], destPath + strFileLists[i]);
          if (!flag) {
            break;
          }
        }
      }
    }

    return flag;
  }

  /**
   *
   */
  public static boolean renameFile(String strSrc, String strTo) {
    if (TextUtils.isEmpty(strSrc) || TextUtils.isEmpty(strTo)) return false;

    File fileSrc = new File(strSrc);
    File fileTo = new File(strTo);
    if (fileSrc.isFile() && fileSrc.renameTo(fileTo)) {
      return true;
    }
    return false;
  }

  /**
   * rename 文件名中的字符串地址
   */
  public static boolean renameFile(String srcPath, String targetStr, String destStr) {
    if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(targetStr)) return false;
    File dirFile = new File(srcPath);
    if (!dirFile.exists() || !dirFile.isDirectory()) {
      return false;
    }

    String strFileLists[] = dirFile.list();
    File file;
    String tempFilePath;
    boolean succ = true;
    for(String filePath : strFileLists){
      file = new File(srcPath + filePath);
      if(file.isFile() && filePath.contains(targetStr)){
        tempFilePath = srcPath + filePath.replace(targetStr,destStr);
        succ = succ & renameFile(srcPath + filePath, tempFilePath);
        if(!succ){
          return false;
        }
      }
    }
    return succ;
  }

  public static boolean folderExit(String folderPath){
    if(TextUtils.isEmpty(folderPath)){
      return false;
    }

    File file = new File(folderPath);
    if(null != file && file.exists()){
      return true;
    }
    return false;
  }

  /**
   * <p>
   * always override old dest file
   * </p>
   */
  public static boolean copyFile(String strSrcFile, String strDestFile) {
    if (strSrcFile == null || strDestFile == null || strSrcFile.equals(strDestFile)) return false;

    File srcFile = new File(strSrcFile);

    if (!srcFile.exists()) {
      return false;
    } else if (!srcFile.isFile()) return false;

    File destFile = new File(strDestFile);
    if (!destFile.exists() || !destFile.isDirectory()) {//如果路径对应的文件不存在，就新建一个
      FileUtils.createMultilevelDirectory(FileUtils.getFileParentPath(strDestFile));
    }

    int byteread = 0;
    InputStream in = null;
    OutputStream out = null;

    byte[] buffer = new byte[4096];// 4K
    try {
      in = new FileInputStream(srcFile);
      out = new FileOutputStream(destFile);
      while ((byteread = in.read(buffer)) != -1) {
        out.write(buffer, 0, byteread);
      }
      out.flush();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (out != null) out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        if (in != null) in.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   *
   */
  public static boolean copyFile(InputStream in, OutputStream out) {
    if (in == null || out == null) return false;

    int byteread = 0;
    boolean bRet = false;
    byte[] buffer = new byte[4096];// 4K
    try {
      while ((byteread = in.read(buffer)) != -1) {
        out.write(buffer, 0, byteread);
      }
      out.flush();
      bRet = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return bRet;
  }

  /**
   *
   */
  public static long fileSize(String strFullFileName) {
    if (TextUtils.isEmpty(strFullFileName)) return 0;

    File file = new File(strFullFileName);
    return sizeOf(file);
  }

  /**
   *
   */
  public static long fileLastModified(String strFullFileName) {
    if (TextUtils.isEmpty(strFullFileName)) return 0;

    File file = new File(strFullFileName);
    if (file.isFile()) {
      return file.lastModified();
    }
    return 0;
  }

  /**
   *
   */
  public static String fileLastModifiedTime(String strFullFileName) {
    long lastModifiedTime = fileLastModified(strFullFileName);
    if (lastModifiedTime <= 0) return null;
    String strLastModifiedTimed;
    Calendar c3 = Calendar.getInstance();
    c3.setTimeInMillis(lastModifiedTime);
    int year = c3.get(Calendar.YEAR);
    int month = c3.get(Calendar.MONTH) + 1; //
    int date = c3.get(Calendar.DATE); //
    int hour = c3.get(Calendar.HOUR_OF_DAY); //
    int minute = c3.get(Calendar.MINUTE); //
    int second = c3.get(Calendar.SECOND); //
    strLastModifiedTimed =
        year + "." + month + "." + date + "/" + hour + "." + minute + "." + second;
    return strLastModifiedTimed;
  }

  /**
   *
   */
  public static boolean createMultilevelDirectory(String strPath) {
    if (TextUtils.isEmpty(strPath)) return false;

    File dir = null;
    dir = new File(strPath);
    if (dir.exists() && dir.isDirectory()) {
      return true;
    }

    //make dirs
    dir.mkdirs();

    //check again
    File dirNew = new File(strPath);
    return dirNew.exists() && dirNew.isDirectory();
  }

  /**
   *
   */
  public static String getFileType(String strPath) {
    if (TextUtils.isEmpty(strPath)) return null;
    String strType;
    int iLastDot = strPath.lastIndexOf(".");
    if (iLastDot < 0) return null;

    strType = strPath.substring(iLastDot + 1).toUpperCase();
    return strType;
  }

  /**
   *
   */
  public static long getFileDate(String strPath) {
    File file = new File(strPath);
    long date = 0;
    if (file.exists()) {
      date = file.lastModified();
    }
    return date;
  }

  /**
   *
   */
  public static long getFreeSpace(String strPath) {
    if (TextUtils.isEmpty(strPath)) return 0;

    String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    if (strPath.startsWith(storagePath)) {
      return new File(storagePath).getUsableSpace();
    }

    File file = new File(strPath);
    while (!file.exists()) {
      file = file.getParentFile();
      if (file == null) {
        file = new File(File.separator);
        break;
      }

      if (file.getAbsolutePath().equals(File.separator)) break;
    }

    return file.getUsableSpace();
  }

  public static String ext(String filename) {
    int index = filename.lastIndexOf(".");

    if (index == -1) {
      return null;
    }
    String result = filename.substring(index + 1);
    return result;
  }

  /**
   * Counts the size of a directory recursively (sum of the length of all
   * files).
   *
   * @param directory directory to inspect, must not be {@code null}
   * @return size of directory in bytes, 0 if directory is security
   * restricted, a negative number when the real total is greater than
   * {@link Long#MAX_VALUE}.
   * @throws NullPointerException if the directory is {@code null}
   */
  public static long sizeOfDirectory(File directory) {
    try {
      checkDirectory(directory);
    } catch (Exception ex) {
      // directory not exist.
      return 0;
    }

    final File[] files = directory.listFiles();
    if (files == null) { // null if security restricted
      return 0L;
    }
    long size = 0;

    for (final File file : files) {
      try {
        if (!isSymlink(file)) {
          size += sizeOf(file);
          if (size < 0) {
            break;
          }
        }
      } catch (IOException ioe) {
        // Ignore exceptions caught when asking if a File is a symlink.
      }
    }

    return size;
  }

  /**
   * Returns the size of the specified file or directory. If the provided
   * {@link File} is a regular file, then the file's length is returned. If
   * the argument is a directory, then the size of the directory is calculated
   * recursively. If a directory or subdirectory is security restricted, its
   * size will not be included.
   *
   * @param file the regular file or directory to return the size of (must not
   * be {@code null}).
   * @return the length of the file, or recursive size of the directory,
   * provided (in bytes).
   * @throws NullPointerException if the file is {@code null}
   * @throws IllegalArgumentException if the file does not exist.
   * @since 2.0
   */
  public static long sizeOf(File file) {

    if (!file.exists()) {
      // String message = file + " does not exist";
      // throw new IllegalArgumentException(message);
      return 0;
    }

    if (file.isDirectory()) {
      return sizeOfDirectory(file);
    } else {
      return file.length();
    }
  }

  /**
   * Checks that the given {@code File} exists and is a directory.
   *
   * @param directory The {@code File} to check.
   * @throws IllegalArgumentException if the given {@code File} does not exist or is not a
   * directory.
   */
  private static void checkDirectory(File directory) {
    if (!directory.exists()) {
      throw new IllegalArgumentException(directory + " does not exist");
    }
    if (!directory.isDirectory()) {
      throw new IllegalArgumentException(directory + " is not a directory");
    }
  }

  /**
   * Determines whether the specified file is a Symbolic Link rather than an
   * actual file.
   * <p>
   * Will not return true if there is a Symbolic Link anywhere in the path,
   * only if the specific file is.
   * <p>
   * <b>Note:</b> the current implementation always returns {@code false} if
   * the system is detected as Windows using
   *
   * @param file the file to check
   * @return true if the file is a Symbolic Link
   * @throws IOException if an IO error occurs while checking the file
   * @since 2.0
   */
  public static boolean isSymlink(File file) throws IOException {
    if (file == null) {
      throw new NullPointerException("File must not be null");
    }
    // if (FilenameUtils.isSystemWindows()) {
    // return false;
    // }
    File fileInCanonicalDir = null;
    if (file.getParent() == null) {
      fileInCanonicalDir = file;
    } else {
      File canonicalDir = file.getParentFile().getCanonicalFile();
      fileInCanonicalDir = new File(canonicalDir, file.getName());
    }

    if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Returns a human-readable version of the file size, where the input
   * represents a specific number of bytes.
   * <p>
   * If the size is over 1GB, the size is returned as the number of whole GB,
   * i.e. the size is rounded down to the nearest GB boundary.
   * </p>
   * <p>
   * Similarly for the 1MB and 1KB boundaries.
   * </p>
   *
   * @param size the number of bytes
   * @return a human-readable display value (includes units - EB, PB, TB, GB,
   * MB, KB or bytes)
   * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 -
   * should the rounding be changed?</a>
   */
  // See https://issues.apache.org/jira/browse/IO-226 - should the rounding be
  // changed?
  public static String byteCountToDisplaySize(long size) {
    return byteCountToDisplaySize(BigInteger.valueOf(size));
  }

  /**
   * Returns a human-readable version of the file size, where the input
   * represents a specific number of bytes.
   * <p>
   * If the size is over 1GB, the size is returned as the number of whole GB,
   * i.e. the size is rounded down to the nearest GB boundary.
   * </p>
   * <p>
   * Similarly for the 1MB and 1KB boundaries.
   * </p>
   *
   * @param size the number of bytes
   * @return a human-readable display value (includes units - EB, PB, TB, GB,
   * MB, KB or bytes)
   * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 -
   * should the rounding be changed?</a>
   * @since 2.4
   */
  // See https://issues.apache.org/jira/browse/IO-226 - should the rounding be
  // changed?
  public static String byteCountToDisplaySize(BigInteger size) {
    String displaySize;

    if (size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
      displaySize = String.valueOf(size.divide(ONE_EB_BI)) + " EB";
    } else if (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
      displaySize = String.valueOf(size.divide(ONE_PB_BI)) + " PB";
    } else if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
      displaySize = String.valueOf(size.divide(ONE_TB_BI)) + " TB";
    } else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
      displaySize = String.valueOf(size.divide(ONE_GB_BI)) + " GB";
    } else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
      displaySize = String.valueOf(size.divide(ONE_MB_BI)) + " MB";
    } else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
      displaySize = String.valueOf(size.divide(ONE_KB_BI)) + " KB";
    } else {
      displaySize = String.valueOf(size) + " bytes";
    }
    return displaySize;
  }

  /**
   * 格式化文件大小，显示KB/MB/GB
   *
   * @return String
   */
  public static String formatFileSize(long size) {
    long SIZE_KB = 1024;
    long SIZE_MB = SIZE_KB * 1024;
    long SIZE_GB = SIZE_MB * 1024;

    if (size < SIZE_KB) {
      return String.format(Locale.US, "%d B", (int) size);
    } else if (size < SIZE_MB) {
      return String.format(Locale.US, "%.2f KB", (float) size / SIZE_KB);
    } else if (size < SIZE_GB) {
      return String.format(Locale.US, "%.2f MB", (float) size / SIZE_MB);
    } else {
      return String.format(Locale.US, "%.2f GB", (float) size / SIZE_GB);
    }
  }

  public static long getDirFileTime(String prjPath, boolean bFirst) {
    long resultTime = 0l;
    if (!TextUtils.isEmpty(prjPath) && isDirectoryExisted(prjPath)) {
      File pDir = new File(prjPath);
      File[] fs = pDir.listFiles();
      if (fs == null) return pDir.lastModified();

      for (File f : fs) {
        long t = f.lastModified();
        if (bFirst) {
          if (t < resultTime || resultTime == 0) {
            resultTime = t;
          }
        } else {
          if (t > resultTime) {
            resultTime = t;
          }
        }
      }
    }
    return resultTime;
  }

  /**
   * 数据库扫描时，将数据库中保存的绝对路径转换成不带SDCARD路径的相对路径。
   */
  public static String getRelativePath(String path) {
    if (TextUtils.isEmpty(path)) {
      return "";
    }

    int index =
        TextUtils.indexOf(path, Environment.getExternalStorageDirectory().getAbsolutePath());
    if (index >= 0) {
      return TextUtils.substring(path, index + 1, path.length());
    }

    return path;
  }

  /**
   * get the file name only. ex:(sdcard/test/test.jpg)=test
   */
  public static String getFileName(String fullFilePath) {
    File f = new File(fullFilePath);
    String strFileName = "";
    if (f != null) {
      strFileName = f.getName();
      if (!TextUtils.isEmpty(strFileName)) {
        int pos = strFileName.lastIndexOf(".");
        if (pos > 0) {
          strFileName = strFileName.substring(0, pos);
        }
      }
    }
    return strFileName;
  }

  /**
   * get the file name only. ex:(sdcard/test/test.jpg)=test
   */
  public static String getFileNameWithFormat(String fullFilePath) {
    File f = new File(fullFilePath);
    String strFileName = "";
    if (f != null) {
      strFileName = f.getName();
    }
    return strFileName;
  }

  public static String getFileNameWithExt(String fullFilePath) {

    if (fullFilePath == null) return null;
    if (fullFilePath.endsWith(File.separator)) return "";

    String strFileName = fullFilePath;
    int pos = strFileName.lastIndexOf(File.separator);
    if (pos > 0) {
      strFileName = strFileName.substring(pos + 1);
    }
    return strFileName;
  }

  /**
   * write string to dest file.
   */
  public static void writeStringToFile(String fileName, String content) {
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(fileName));
      writer.write(content);
    } catch (IOException e) {
    } finally {
      try {
        if (writer != null) writer.close();
      } catch (IOException e) {
      }
    }
  }

  public static void saveBitmap(Bitmap bitmap, String destPath) {
    try {
      OutputStream fOut = null;
      File file = new File(destPath);
      fOut = new FileOutputStream(file);

      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
      fOut.flush();
      fOut.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * <p>
   * compare two file's content. if not equal then return false; else return
   * true;<br>
   * if backup file not exit, means no backup, so should return true.
   * </p>
   */
  public static boolean isFileContentEqual(String oldFilePath, String newFilePath) {
    // check does the two file exist.
    if (!TextUtils.isEmpty(oldFilePath) && !TextUtils.isEmpty(newFilePath)) {
      File oldFile = new File(oldFilePath);
      File newFile = new File(newFilePath);
      if (oldFile.exists() && !newFile.exists()) {
        return true;
      } else if (!oldFile.exists()) {
        return false;
      }
      FileInputStream oldInStream = null;
      FileInputStream newInStream = null;
      try {
        oldInStream = new FileInputStream(oldFile);
        newInStream = new FileInputStream(newFile);

        int oldStreamLen = oldInStream.available();
        int newStreamLen = newInStream.available();
        // check the file size first.
        if (oldStreamLen > 0 && oldStreamLen == newStreamLen) {
          // read file data with a buffer.
          int cacheSize = 128;
          byte[] data1 = new byte[cacheSize];
          byte[] data2 = new byte[cacheSize];
          do {
            int readSize = oldInStream.read(data1);
            newInStream.read(data2);

            for (int i = 0; i < cacheSize; i++) {
              if (data1[i] != data2[i]) {
                return false;
              }
            }
            if (readSize == -1) {
              break;
            }
          } while (true);
          return true;
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          if (oldInStream != null) oldInStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        try {
          if (newInStream != null) newInStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return false;
  }

  public static String getFileParentPath(String path) {
    String outpath = "";
    if (!TextUtils.isEmpty(path)) {
      try {
        int index = path.lastIndexOf(File.separator);
        if (index > 0) {
          outpath = path.substring(0, index) + "/";
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return outpath;
  }

  public static String getFileNameFromAbPath(String path) {
    String outpath = "";
    if (!TextUtils.isEmpty(path)) {
      try {
        int index = path.lastIndexOf(File.separator) + 1;
        int index2 = path.lastIndexOf('.');
        if (index > 0 && index2 > 0 && index < index2) {
          outpath = path.substring(index, index2);
        }
      } catch (Exception ex) {
      }
    }
    return outpath;
  }

  public static String getFileExtFromAbPath(String path) {
    String outpath = "";
    if (!TextUtils.isEmpty(path)) {
      try {
        int index = path.lastIndexOf('.');
        if (index > 0) {
          outpath = path.substring(index);
        }
      } catch (Exception ex) {
      }
    }
    return outpath;
  }

  /**
   * <p>
   *
   * </p>
   *
   * @param strFileName only name not include path part.
   */
  public static String removeInvalidFileNameCharacter(String strFileName) {
    if (TextUtils.isEmpty(strFileName)) return strFileName;
    StringBuffer sb = new StringBuffer();
    char ch = 0;
    int nTitleLen = strFileName.length();
    for (int i = 0; i < nTitleLen; i++) {
      ch = strFileName.charAt(i);
      switch (ch) {
        case '/':
        case '\\':
        case ':':
        case '*':
        case '?':
        case '<':
        case '>':
        case '|':
        case '\r':
        case '\n':
          break;
        default:
          sb.append(ch);
      }
    }

    return sb.toString();
  }

  /**
   * Get a file path from a Uri. This will get the the path for Storage Access
   * Framework Documents, as well as the _data field for the MediaStore and
   * other file-based ContentProviders.
   *
   * @param context The context.
   * @param uri The Uri to query.
   */
  @SuppressLint("NewApi") public static String getPath(final Context context, final Uri uri) {

    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    try {
      // DocumentProvider
      if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
          final String docId = DocumentsContract.getDocumentId(uri);
          final String[] split = docId.split(":");
          final String type = split[0];

          if ("primary".equalsIgnoreCase(type)) {
            return Environment.getExternalStorageDirectory() + "/" + split[1];
          }

        }
        // DownloadsProvider
        else if (isDownloadsDocument(uri)) {

          final String id = DocumentsContract.getDocumentId(uri);
          final Uri contentUri =
              ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                  Long.valueOf(id));

          return getDataColumn(context, contentUri, null, null);
        }
        // MediaProvider
        else if (isMediaDocument(uri)) {
          final String docId = DocumentsContract.getDocumentId(uri);
          final String[] split = docId.split(":");
          final String type = split[0];

          Uri contentUri = null;
          if ("image".equals(type)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
          } else if ("video".equals(type)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
          } else if ("audio".equals(type)) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
          }

          final String selection = "_id=?";
          final String[] selectionArgs = new String[] {
              split[1]
          };

          return getDataColumn(context, contentUri, selection, selectionArgs);
        }
      }
      // MediaStore (and general)
      else if ("content".equalsIgnoreCase(uri.getScheme())) {

        // Return the remote address
        if (isGooglePhotosUri(uri)) return uri.getLastPathSegment();

        return getDataColumn(context, uri, null, null);
      }
      // File
      else if ("file".equalsIgnoreCase(uri.getScheme())) {
        return uri.getPath();
      }
    } catch (Exception e) {
    }

    return null;
  }

  /**
   * Get the value of the data column for this Uri. This is useful for
   * MediaStore Uris, and other file-based ContentProviders.
   *
   * @param context The context.
   * @param uri The Uri to query.
   * @param selection (Optional) Filter used in the query.
   * @param selectionArgs (Optional) Selection arguments used in the query.
   * @return The value of the _data column, which is typically a file path.
   */
  public static String getDataColumn(Context context, Uri uri, String selection,
      String[] selectionArgs) {

    Cursor cursor = null;
    final String column = "_data";
    final String[] projection = {
        column
    };

    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        final int index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(index);
      }
    } finally {
      if (cursor != null) cursor.close();
    }
    return null;
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is ExternalStorageProvider.
   */
  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is Google Photos.
   */
  public static boolean isGooglePhotosUri(Uri uri) {
    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
  }
}
