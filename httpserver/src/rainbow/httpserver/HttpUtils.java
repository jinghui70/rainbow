package rainbow.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.ImmutableMap;

public abstract class HttpUtils {

	public static Map<String, String> mime = ImmutableMap.<String, String>builder() // mime map
			.put("aac", "audio/aac") // AAC audio
			.put("avi", "video/x-msvideo") // AVI: Audio Video Interleave
			.put("azw", "application/vnd.amazon.ebook") // Amazon Kindle eBook format
			.put("bmp", "image/bmp") // Windows OS/2 Bitmap Graphics
			.put("css", "text/css") // Cascading Style Sheets (CSS)
			.put("csv", "text/csv") // Comma-separated values (CSV)
			.put("doc", "application/msword") // Microsoft Word
			.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
			.put("epub", "application/epub+zip") // Electronic publication (EPUB)
			.put("gif", "image/gif") // Graphics Interchange Format (GIF)
			.put("htm", "text/html") // HyperText Markup Language (HTML)
			.put("html", "text/html") // HyperText Markup Language (HTML)
			.put("ico", "image/vnd.microsoft.icon") // Icon format
			.put("jpeg", "image/jpeg") // JPEG images
			.put("jpg", "image/jpeg") // JPEG images
			.put("js", "text/javascript") // JavaScript
			.put("json", "application/json") // JSON format
			.put("mp3", "audio/mpeg") // MP3 audio
			.put("mp4", "video/mp4") // MP4 video
			.put("mpg", "video/mpeg") // MPEG Video
			.put("mpeg", "video/mpeg") // MPEG Video
			.put("png", "image/png") // Portable Network Graphics
			.put("pdf", "application/pdf") // Adobe Portable Document Format (PDF)
			.put("ppt", "application/vnd.ms-powerpoint") // Microsoft PowerPoint
			.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation")
			.put("rar", "application/x-rar-compressed") // RAR archive
			.put("rtf", "application/rtf") // Rich Text Format (RTF)
			.put("svg", "image/svg+xml") // Scalable Vector Graphics (SVG)
			.put("swf", "application/x-shockwave-flash") // Small web format (SWF) or Adobe Flash document
			.put("tar", "application/x-tar") // Tape Archive (TAR)
			.put("ttf", "font/ttf") // TrueType Font
			.put("txt", "text/plain") // Text, (generally ASCII or ISO 8859-n)
			.put("vsd", "application/vnd.visio") // Microsoft Visio
			.put("wav", "audio/wav") // Waveform Audio Format
			.put("weba", "audio/webm") // WEBM audio
			.put("webm", "video/webm") // WEBM video
			.put("webp", "image/webp") // WEBP image
			.put("xls", "application/vnd.ms-excel") // Microsoft Excel
			.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").put("xml", "text/xml") // XML
			.put("zip", "application/zip") // ZIP archive
			.put("7z", "application/x-7z-compressed") // 7-zip archive
			.build();

	public static String getMimeType(String fileName) {
		int index = fileName.lastIndexOf('.');
		String result = null;
		if (index > 0) {
			fileName = fileName.substring(index + 1).toLowerCase();
			result = mime.get(fileName);
		}
		return result == null ? "applicatoin/octet-stream" : result;
	}

	public static void writeJsonBack(HttpServletResponse response, Object result) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try (Writer writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
			String content = JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect,
					SerializerFeature.WriteEnumUsingName, SerializerFeature.WriteDateUseDateFormat);
			// content = AES.encode(request, content);
			writer.write(content);
		}
	}

	/**
	 * 返回流内容
	 * 
	 * @param response
	 * @param stream
	 * @param mimeType
	 * @throws IOException
	 */
	public static void writeStreamBack(HttpServletResponse response, InputStream stream) throws IOException {
		OutputStream outStream = response.getOutputStream();
		try (InputStream is = stream) {
			int len = 0;
			byte[] buffer = new byte[8192];
			while ((len = is.read(buffer)) > 0) {
				outStream.write(buffer, 0, len);
			}
		}
	}

}
