package org.springside.modules.utils.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.utils.text.Charsets;

import com.google.common.annotations.Beta;

/**
 * IO相关工具集
 * 
 * 建议使用Apache Commons IO, 在未引入Commons IO时可以用本类做最基本的事情.
 * 
 * 代码基本从Apache Commmons IO中化简移植, 固定encoding为UTF8.
 * 
 * 1. 安静关闭Closeable对象
 * 
 * 2. 读出InputStream/Reader内容到String 或 List<String>(from Commons IO)
 * 
 * 3. 将String写到OutputStream/Writer(from Commons IO)
 * 
 * 4. InputStream/Reader与OutputStream/Writer之间复制的copy(from Commons IO)
 * 
 * 
 * @author calvin
 */
@Beta
public class IOUtil {
	private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private static final int EOF = -1;

	private static final String CLOSE_ERROR_MESSAGE = "IOException thrown while closing Closeable.";

	/**
	 * 在final中安静的关闭, 不再往外抛出异常避免影响原有异常，最常用函数. 同时兼容Closeable为空未实际创建的情况.
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				logger.warn(CLOSE_ERROR_MESSAGE, e);
			}
		}
	}

	/**
	 * For JDK6 which ZipFile is not Closeable.
	 */
	public static void closeQuietly(ZipFile zipfile) {
		if (zipfile != null) {
			try {
				zipfile.close();
			} catch (IOException e) {
				logger.warn(CLOSE_ERROR_MESSAGE, e);
			}
		}
	}

	/**
	 * For JDK6 which Socket is not Closeable.
	 */
	public static void closeQuietly(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				logger.warn(CLOSE_ERROR_MESSAGE, e);
			}
		}
	}

	/**
	 * 简单读取InputStream到String.
	 */
	public static String toString(InputStream input) throws IOException {
		InputStreamReader reader = new InputStreamReader(input, Charsets.UTF_8);
		return toString(reader);
	}

	/**
	 * 简单读取Reader到String
	 */
	public static String toString(Reader reader) throws IOException {
		StringBuilderWriter sw = new StringBuilderWriter();
		copy(reader, sw, new char[DEFAULT_BUFFER_SIZE]);
		return sw.toString();
	}

	/**
	 * 简单读取Reader的每行内容到List<String>
	 */
	public static List<String> readLines(final InputStream input) throws IOException {
		return readLines(new InputStreamReader(input, Charsets.UTF_8));
	}

	/**
	 * 简单读取Reader的每行内容到List<String>
	 */
	public static List<String> readLines(final Reader input) throws IOException {
		final BufferedReader reader = toBufferedReader(input);
		final List<String> list = new ArrayList<String>();
		String line = reader.readLine();
		while (line != null) {
			list.add(line);
			line = reader.readLine();
		}
		return list;
	}

	/**
	 * 简单写入String到OutputStream.
	 */
	public static void write(final String data, final OutputStream output) throws IOException {
		if (data != null) {
			output.write(data.getBytes(Charsets.UTF_8));
		}
	}

	/**
	 * 简单写入String到Writer.
	 */
	public static void write(final String data, final Writer output) throws IOException {
		if (data != null) {
			output.write(data);
		}
	}

	/**
	 * 在Reader与Writer间复制内容
	 */
	public static long copy(final Reader input, final Writer output) throws IOException {
		return copy(input, output, new char[DEFAULT_BUFFER_SIZE]);
	}

	/**
	 * 在InputStream与OutputStream间复制内容
	 */
	public static long copy(final InputStream input, final OutputStream output) throws IOException {
		return copy(input, output, new byte[DEFAULT_BUFFER_SIZE]);
	}

	/**
	 * 在InputStream与Writer间复制内容
	 */
	public static void copy(final InputStream input, final Writer output) throws IOException {
		final InputStreamReader in = new InputStreamReader(input, Charsets.UTF_8);
		copy(in, output);
	}

	/**
	 * 在Reader与OutputStream间复制内容
	 */
	public static void copy(final Reader input, final OutputStream output) throws IOException {
		final OutputStreamWriter out = new OutputStreamWriter(output, Charsets.UTF_8);
		copy(input, out);
		out.flush();
	}

	private static long copy(final Reader input, final Writer output, final char[] buffer) throws IOException {
		long count = 0;
		int n;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	private static long copy(final InputStream input, final OutputStream output, final byte[] buffer)
			throws IOException {
		long count = 0;
		int n;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	private static BufferedReader toBufferedReader(final Reader reader) {
		return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
	}

	/**
	 * 在临时目录创建临时文件，命名为${prefix}${random.nextLong()}${suffix}
	 */
	public static File createTempDir(String prefix, String suffix) throws IOException {
		return File.createTempFile(prefix, suffix);
	}

}
