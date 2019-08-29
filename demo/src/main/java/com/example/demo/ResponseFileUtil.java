package com.example.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * 文件传输响应
 * Created by admin on 2017/6/28.
 */
public class ResponseFileUtil {

	/**
	 * 文件相应设置
	 *
	 * @param response
	 * @param filePath 文件的完整路径，包含文件名称
	 * @param fileName 文件名称，包含后缀
	 */
	public static void response(HttpServletRequest request, HttpServletResponse response, String filePath, String fileName) {
		OutputStream os = null;
		InputStream in = null;
		try {
			response.reset();
			os = response.getOutputStream();
			String headerFileName = "";
			if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
				headerFileName = URLEncoder.encode(fileName, "utf-8");
			} else {
				headerFileName = new String(fileName.getBytes("utf-8"), "ISO8859-1");
			}
			response.setHeader("Content-disposition", "attachment;filename=\"" + headerFileName + "\"");
//			response.setContentType("application/octet-stream;charset=UTF-8");// 设置类型
			response.setHeader("Pragma", "No-cache");// 设置头
			response.setHeader("Cache-Control", "no-cache");// 设置头
			response.setDateHeader("Expires", 0);// 设置日期头
			File file = new File(filePath);
			in = new FileInputStream(file);
			response.setHeader("Content-Length", in.available() + "");
			byte[] bytes = new byte[2048];
			int length = 0;
			while ((length = in.read(bytes)) > 0) {
				os.write(bytes, 0, length);
			}
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 响应文件流
	 *
	 * @param request
	 * @param response
	 * @param inputStream
	 * @param fileName
	 */
	public static void response(HttpServletRequest request, HttpServletResponse response, InputStream inputStream, String fileName) {
		OutputStream os = null;
		try {
			response.reset();
			os = response.getOutputStream();
			String headerFileName = "";
			if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
				headerFileName = URLEncoder.encode(fileName, "utf-8");
			} else {
				headerFileName = new String(fileName.getBytes("utf-8"), "ISO8859-1");
			}
			response.setHeader("Content-disposition", "attachment;filename=\"" + headerFileName + "\"");
			response.setContentType("application/octet-stream;charset=UTF-8");// 设置类型
			response.setHeader("Pragma", "No-cache");// 设置头
			response.setHeader("Cache-Control", "no-cache");// 设置头
			response.setDateHeader("Expires", 0);// 设置日期头
			byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = inputStream.read(bytes)) > 0) {
				os.write(bytes, 0, length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
