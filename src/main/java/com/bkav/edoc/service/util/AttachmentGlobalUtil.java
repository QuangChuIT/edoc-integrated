package com.bkav.edoc.service.util;

import java.io.*;

import com.bkav.edoc.service.center.DynamicService;
import com.bkav.edoc.service.kernel.string.StringPool;
import com.bkav.edoc.service.kernel.util.Validator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.util.Base64;

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
				log.error("Can't create dir with" + targetFodler.getAbsolutePath());
			}
		}

		return targetFodler.getAbsolutePath();

	}

	public byte[] parseBase64ISToBytes(InputStream base64IS) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int readed = 0;
			while ((readed = base64IS.read(buffer, 0, buffer.length)) != -1) {

				baos.write(buffer, 0, readed);
			}
		} catch (Exception ex) {
			log.error(ex);
		} finally {
			base64IS.close();
		}
		byte[] bytes = baos.toByteArray();
		baos.close();

		if (!Base64.isBase64(bytes[0])) {
			bytes = Base64.encodeBase64(bytes);
		}
		return bytes;
	}

	public InputStream getFileIS(String filePath) throws IOException {

		File file = new File(filePath);

		if (file.exists()) {
			return new FileInputStream(file);
		}

		return null;
	}

	private static final Log log = LogFactory.getLog(AttachmentGlobalUtil.class);

}
