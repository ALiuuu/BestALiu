package com.aliu.myutils;

import android.app.Application;
import android.content.res.AssetManager;
import android.text.TextUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * for process the resource which refered by the app
 * 
 * @author yqg1817
 * 
 */
public class ResourceUtils {

	private static ArrayList<String> mChangedFilesList = new ArrayList<String>();
	public static AssetManager mAssetsManager;
	public static void attachApplication(Application application) {
		mAssetsManager = application.getAssets();
	}


	public ArrayList<String> getChangedFileList() {
		return mChangedFilesList;
	}
	
	private boolean isSameFileData(String desFileName, String AssetURI,
			AssetManager am) {
		FileInputStream dst = null;
		InputStream is = null;
		boolean bIsSame = false;
		do {
			try {
				File file = new File(desFileName);
				is = am.open(AssetURI, AssetManager.ACCESS_RANDOM);
				long lSrcLen = is.available();
				long lDstLen = file.length();
				if (lSrcLen != lDstLen) {
					break;
				}
				long lFileSize = lSrcLen;
				byte[] bufferSrc = new byte[128];
				byte[] bufferDst = new byte[128];
				dst = new FileInputStream(file);
				// check header
				lDstLen = dst.read(bufferDst);
				lSrcLen = is.read(bufferSrc);
				if (lSrcLen != lDstLen) {
					break;
				}

				for (int i = 0; i < lSrcLen; i++) {
					if (bufferDst[i] != bufferSrc[i]) {
						break;
					}
				}

				/*
				 * //if performance can be accepted, we can open next code.
				 * firstly, disable it. cfchen@20120516 //check middle par of
				 * file long lMid = lFileSize / 2; long lSkip = lMid - lSrcLen;
				 * if(lSkip <= 0) { //do not need check middle part bIsSame =
				 * true; break; } is.skip(lMid); dst.skip(lMid);
				 * 
				 * lDstLen = dst.read(bufferDst); lSrcLen = is.read(bufferSrc);
				 * if(lSrcLen != lDstLen) { break; }
				 * 
				 * for(int i= 0; i < lSrcLen; i++) { if(bufferDst[i] !=
				 * bufferSrc[i]) { break; } }
				 * 
				 * lSkip = (lFileSize - lMid - lSrcLen - 128); //check tailer
				 * if(lSkip <= 0) { //do not need check middle part bIsSame =
				 * true; break; }
				 * 
				 * is.skip(lMid); dst.skip(lMid);
				 * 
				 * lDstLen = dst.read(bufferDst); lSrcLen = is.read(bufferSrc);
				 * if(lSrcLen != lDstLen) { break; }
				 * 
				 * for(int i= 0; i < lSrcLen; i++) { if(bufferDst[i] !=
				 * bufferSrc[i]) { break; } }
				 */
				bIsSame = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (false);

		try {
			if (is != null)
				is.close();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (dst != null)
				dst.close();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return bIsSame;
	}

	private static boolean isSameData(InputStream is,File file){
		boolean bIsSame = true;
		
		long lSrcLen = 0;
		long lDstLen = 0;
		try {
			lSrcLen = is.available();
			lDstLen = file.length();
			if (lSrcLen != lDstLen) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		long lFileSize = lSrcLen;
		byte[] bufferSrc = new byte[128];
		byte[] bufferDst = new byte[128];
		FileInputStream dst = null;
		try {
			dst = new FileInputStream(file);
			// check header
			lDstLen = dst.read(bufferDst);
			lSrcLen = is.read(bufferSrc);
			if (lSrcLen != lDstLen) {
				return false;
			}

			for (int i = 0; i < lSrcLen; i++) {
				if (bufferDst[i] != bufferSrc[i]) {
					return false;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(dst != null) {
				try {
					dst.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		

		bIsSame = true;
		return true;
	}
	
	private void cleanUnusedFilesFromAssets(String strAssetDir, String strDestDir) {
		AssetManager assetManager = null;
		try {
			//add file into arraylist in dest path
			File file = new File(strDestDir);
			String fileLists[] = file.list();
			if(fileLists == null)
				return;
			ArrayList<String> destLists = new ArrayList<String>();
			int nCount = fileLists.length;
			for(int i=0; i < nCount; i++) {
				destLists.add(fileLists[i]);
			}
			
			//compare filename and left unused file in arraylist
			assetManager = mAssetsManager;
			String assetsList[] = null;
			assetsList = assetManager.list(strAssetDir);
			int nAssetsCount = assetsList.length;
			String strAssetCopy = null;
			for(int j=0; j < nAssetsCount; j++) {
				strAssetCopy = assetsList[j];
				nCount = destLists.size();
				for(int i=0; i < nCount; i++) {
					if(destLists.get(i).endsWith(strAssetCopy)) {
						destLists.remove(i);
						break;
					}
				}
			}
			
			//delete unused file in arraylist
			nCount = destLists.size();
			String strFilePath;
			for(int i=0; i < nCount; i++) {
				strFilePath = strDestDir + File.separator + destLists.get(i);
				FileUtils.deleteFile(strFilePath);
				//file is removed
				mChangedFilesList.add(strFilePath);
			}
			destLists.clear();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

//	//need to check the time cost. it seems cost much more.
//	public boolean copyAsserts(Context context, String strCopyDir[], String strDestPath) {
//		if (null == context || strCopyDir == null || strCopyDir.length == 0)
//			return false;
//		long stime = System.currentTimeMillis();
//		long lStart = System.currentTimeMillis();
//		mContext = context.getApplicationContext();
//		for (String path : strCopyDir) {
//			//before copy, check whether exist unused file
////			cleanUnusedFilesFromAssets(context, path, Constants.APP_PRIVATE_ROOT_PATH + path);
//			copyFileOrDir(path, strDestPath, true);
//		}
//
//		long passTime = System.currentTimeMillis() - stime;
//		LogUtils.e("time", "totalTime = " + passTime);
//		long lEnd = System.currentTimeMillis();
//		LogUtils.e("copyAsserts", "cost:" + (lEnd - lStart));
//		return true;
//	}

	public static void copyFolder(String path, String destRootPath) {

		if(destRootPath.endsWith(File.separator)) {
			destRootPath = destRootPath.substring(0, destRootPath.length() - 1);
		}

		AssetManager assetManager = mAssetsManager;
		String assets[] = null;
		try {
			assets = assetManager.list(path);
			if (assets == null || assets.length == 0) {
				copyFile(path, destRootPath);
			} else {
				File dir = new File(destRootPath);
				if (!dir.exists()) {
					FileUtils.createMultilevelDirectory(dir.getAbsolutePath());
				}

				String strAssetsFile = null;
				boolean bIsFile = false;
				for (int i = 0; i < assets.length; ++i) {
					strAssetsFile = path + File.separator + assets[i];
					InputStream stream = null;
					try {
						stream = assetManager.open(strAssetsFile);
						bIsFile = (stream != null);
					} catch(Exception e) {
						bIsFile = false;;
					} finally {
						try {
							if(stream != null)
								stream.close();
						} catch(Exception e) {

						}
					}

					if (bIsFile) {
						//copy file
						copyFile(strAssetsFile, destRootPath);
					} else {
						//copy sub folder
						copyFolder(strAssetsFile,
								destRootPath + File.separator + assets[i]);
					}
				}

			}
		} catch (IOException ex) {
		}
	}

	private static void copyFile(String filename, String destRootPath) {
		if(filename == null)
			return;

		String strOnlyFileName = FileUtils.getFileNameWithExt(filename);
		String newFileName = destRootPath + File.separator + strOnlyFileName;
		
		boolean bCopyResult = copyFileFromAssets(filename, newFileName, mAssetsManager);
		if(bCopyResult) {
			mChangedFilesList.add(newFileName);
		}
	}
	
	public static boolean isAssetsFileExisted(AssetManager assetManager, String srcFile) {
		if (null == assetManager || TextUtils.isEmpty(srcFile))
			return false;
		boolean bFileExisted = false;
		InputStream in = null;
		try {
			in = assetManager.open(srcFile);
			bFileExisted = (in != null);
		} catch(Exception e) {
			
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return bFileExisted;
	}
			
	public static boolean copyFileFromAssets(String srcFile, String destFile,
			AssetManager assetManager) {
		if (null == assetManager || TextUtils.isEmpty(srcFile)
				|| TextUtils.isEmpty(destFile))
			return false;
		String destDir = FileUtils.getFileParentPath(destFile);
		File destDirFile = new File(destDir);
		if (!destDirFile.exists() || !destDirFile.isDirectory()) {
			FileUtils.createMultilevelDirectory(destDir);
		}
		InputStream inAssets = null;
		InputStream in = null;
		OutputStream fout = null;
		BufferedOutputStream bos = null;
		try {
			
			in = inAssets = assetManager.open(srcFile);
			if (!in.markSupported()) {
				in = new BufferedInputStream(in, 16 * 1024);
				in.mark(1024);
			}

			String newFileName = destFile;
			File f = new File(newFileName);
			if (f.exists() && isSameData(in, f)) {
				return false;// same file and data, do not need copy
			}
			
			in.reset();
			fout = new FileOutputStream(destFile);
			byte[] buffer = new byte[2048];
			bos = new  BufferedOutputStream(fout,  buffer.length);
			int read;
			while ((read = in.read(buffer)) != -1) {
				bos.write(buffer, 0, read);
			}
		} catch (Exception e) {
//			LogUtils.e("tag", e.getMessage());
			return false;
		} finally {
			try {
				if(bos != null) {
					bos.flush();
					bos.close();
				}
			} catch(Throwable e) {
				
			}
			
			try {
				if(fout != null) {
					fout.flush();
					fout.close();
				}
			} catch(Throwable e) {
				
			}
			
			try {
				if(in != null && in != inAssets) {
					in.close();
				}
			} catch(Throwable e) {
				
			}
			
			try {
				if(inAssets != null) {
					inAssets.close();
				}
			} catch(Throwable e) {
				
			}
			
		}

		return true;
	}
}
