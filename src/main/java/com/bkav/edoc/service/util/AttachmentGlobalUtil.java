package com.bkav.edoc.service.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.bkav.edoc.service.kernel.string.StringPool;
import com.bkav.edoc.service.kernel.util.Validator;

public class AttachmentGlobalUtil {

	private String SPERATOR = File.separator;

	String DEFAULT_ROOT_PATH = SPERATOR;

	public String getAttachmentPath() {
		String folderName = "attachment";
		return getSaveFilePath(folderName);
	}

	public long saveToFile(String targetPath, InputStream is)
			throws IOException {

		if (Validator.isNull(is)) {
			return -1;
		}

		File attachmentFile = new File(targetPath);

		File parentFile = attachmentFile.getParentFile();
		if (!parentFile.exists()) {
			if (!parentFile.mkdirs()) {
				return -1;
			}
		}

		if (!attachmentFile.exists()) {

			if (!attachmentFile.createNewFile()) {
				return -1;
			}

		}
		OutputStream os = new FileOutputStream(attachmentFile);

		// Save an attachment into Attachment folder

		long bytesReaded = 0;
		int read = 0;

		byte[] bytes = new byte[3072];

		while ((read = is.read(bytes)) != -1) {

			os.write(bytes, 0, read);
			bytesReaded += read;

		}

		is.close();
		os.close();
		// long bytesRead = (long) is.available();

		// IOUtils.copy(is, outputStream);

		// IOUtils.closeQuietly(is);

		// IOUtils.closeQuietly(outputStream);

		return bytesReaded;
	}

	/**
	 * Input : String base64 Output : InputStream
	 * */
	public InputStream convertBase64ToIS(String base64) {

		// InputStream is = new
		// ByteArrayInputStream(Base64.decodeBase64(base64));
		InputStream is = new ByteArrayInputStream(base64.getBytes());

		return is;

	}

	public InputStream convertBase64ToIS(byte[] base64) {

		// InputStream is = new
		// ByteArrayInputStream(Base64.decodeBase64(base64));
		InputStream is = new ByteArrayInputStream(base64);

		return is;

	}

	public String getSaveFilePath(String folderName) {

		String rootPath = StringPool.BLANK;

		/*
		 * edXMLConfig result = null; try { result = edXMLConfigLocalServiceUtil
		 * .findByKey(EdXMLConfigKey.ATACHMENT_FOLDER_ROOT); } catch (Exception
		 * ex) { _log.error(ex); }
		 */

		String setupPath = PropsUtil.get(EdXMLConfigKey.ATACHMENT_FOLDER_ROOT);

		if (Validator.isNull(setupPath)) {

			rootPath = DEFAULT_ROOT_PATH;
		} else {

			rootPath = setupPath;// result.getValue();

		}
		if (!rootPath.endsWith(SPERATOR)) {

			rootPath += SPERATOR;
		}

		File parent = new File(rootPath);// .getParentFile();

		File targetFodler = new File(parent.getAbsolutePath() + SPERATOR
				+ folderName + SPERATOR);

		if (!targetFodler.exists()) {
			if (!targetFodler.mkdirs()) {
//				_log.("Can't create dir with" + targetFodler.getAbsolutePath());
			}
		}

		return targetFodler.getAbsolutePath();

	}

//	private static Log _log = LogFactoryUtil.getLog(AttachmentGlobalUtil.class);

}
