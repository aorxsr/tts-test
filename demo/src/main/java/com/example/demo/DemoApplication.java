package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUtility;
import com.iflytek.cloud.speech.SynthesizeToUriListener;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpeechUtility.createUtility(SpeechConstant.APPID + "=" + APPID);
		ConfigurableApplicationContext applicationContext = SpringApplication.run(DemoApplication.class, args);
	}

	static JSONObject tokenJSON;

	@GetMapping(value = "/getToken")
	@ResponseBody
	public String getToken() {
		Map<String, Object> param = new HashMap<>(3);
		param.put("grant_type", "client_credentials");
		param.put("client_id", "rLFab5M4cOUpFVqPIUbg72Kr");
		param.put("client_secret", "EEhUKSiUGfS5nGv0QfsPF7or1wE6UnC4");

		String json = HttpClientUtil.httpGetWithJSON("https://openapi.baidu.com/oauth/2.0/token", param);
		tokenJSON = JSONObject.parseObject(json);
		return tokenJSON.getString("access_token");
	}

	@GetMapping(value = "/download/get")
	public void downloadFile(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		Map<String, String> param = new HashMap<>(3);
		String tsnUrl = "https://tsn.baidu.com/text2audio";
		// 要转语音的文本, 要两次编码
		param.put("tex", encoder(encoder("我叫付飞虎")));
		// 发音人选择, 基础音库：0为度小美，1为度小宇，3为度逍遥，4为度丫丫，
		// 精品音库：5为度小娇，103为度米朵，106为度博文，110为度小童，111为度小萌，默认为度小美
		param.put("per", "0");
		// 语速，取值0-15，默认为5中语速
		param.put("spd", "5");
		// 音调，取值0-15，默认为5中语调
		param.put("pit", "5");
		// 音量，取值0-9，默认为5中音量
		param.put("vol", "5");
		// 用户唯一标识, 建议机器码
		param.put("cuid", "primary");
		// token
		if (tokenJSON == null) {
			getToken();
		}
		param.put("tok", tokenJSON.getString("access_token"));
		// 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav
		param.put("aue", "3");

		param.put("lan", "zh");
		param.put("ctp", "1");

		HttpClientUtil.download3(tsnUrl + "?tex=" + encoder(encoder("我叫付飞虎")) + "&per=0&spd=5&pit=5&vol=5&cuid=primary&tok=" + tokenJSON.getString("access_token") + "&aue=3&lan=zh&ctp=1", Collections.EMPTY_MAP, "F:/data/我叫付飞虎.mp3");
		ResponseFileUtil.response(request, response, "F:/data/我叫付飞虎.mp3", "我叫付飞虎.mp3");
	}

	private static String encoder(String text) throws UnsupportedEncodingException {
		return URLEncoder.encode(text, "UTF-8");
	}

	/**************************讯飞************************/
	// 合成webapi接口地址
	private static final String WEBTTS_URL = "https://api.xfyun.cn/v1/service/v1/tts";
	// 应用APPID（必须为webapi类型应用，并开通语音合成服务，参考帖子如何创建一个webapi应用：http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=36481）
	private static final String APPID = "5d675f5c";
	// 接口密钥（webapi类型应用开通合成服务后，控制台--我的应用---语音合成---相应服务的apikey）
	private static final String API_KEY = "4daffdb258b67e6438fb6f512d00c720";
	// 待合成文本
	private static final String TEXT = "我叫付飞虎";
	// 音频编码(raw合成的音频格式pcm、wav,lame合成的音频格式MP3)
	private static final String AUE = "lame";
	// 采样率
	private static final String AUF = "audio/L16;rate=16000";
	// 语速（取值范围0-100）
	private static final String SPEED = "50";
	// 音量（取值范围0-100）
	private static final String VOLUME = "50";
	// 音调（取值范围0-100）
	private static final String PITCH = "50";
	// 发音人（登陆开放平台https://www.xfyun.cn/后--我的应用（必须为webapi类型应用）--添加在线语音合成（已添加的不用添加）--发音人管理---添加发音人--修改发音人参数）
	private static final String VOICE_NAME = "xiaoyan";
	// 引擎类型
	private static final String ENGINE_TYPE = "intp65";
	// 文本类型（webapi是单次只支持1000个字节，具体看您的编码格式，计算一下具体支持多少文字）
	private static final String TEXT_TYPE = "text";

	/**
	 * 组装http请求头
	 */
	private static Map<String, String> buildHttpHeader() throws UnsupportedEncodingException {
		String curTime = System.currentTimeMillis() / 1000L + "";
		String param = "{\"auf\":\"" + AUF + "\",\"aue\":\"" + AUE + "\",\"voice_name\":\"" + VOICE_NAME + "\",\"speed\":\"" + SPEED + "\",\"volume\":\"" + VOLUME + "\",\"pitch\":\"" + PITCH + "\",\"engine_type\":\"" + ENGINE_TYPE + "\",\"text_type\":\"" + TEXT_TYPE + "\"}";
		String paramBase64 = new String(Base64.getEncoder().encode(param.getBytes("UTF-8")));
		String checkSum = DigestUtils.md5Hex(API_KEY + curTime + paramBase64);
		Map<String, String> header = new HashMap<String, String>();
		header.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		header.put("X-Param", paramBase64);
		header.put("X-CurTime", curTime);
		header.put("X-CheckSum", checkSum);
		header.put("X-Appid", APPID);
		return header;
	}

	@GetMapping(value = "/xunfei/download")
	public String xunFeiDownload(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		String filePath = "";
		String fileName = "";

		Map<String, String> header = buildHttpHeader();

		Map<String, Object> resultMap = com.example.demo.HttpUtil.doPost2(WEBTTS_URL, header, "text=" + URLEncoder.encode(TEXT, "utf-8"));
		System.out.println("占用内存大小： " + URLEncoder.encode(TEXT, "utf-8").getBytes().length);
		if ("audio/mpeg".equals(resultMap.get("Content-Type"))) { // 合成成功
			if ("raw".equals(AUE)) {
				filePath = "F:\\data\\" + resultMap.get("sid") + ".wav";
				fileName = resultMap.get("sid") + ".wav";

				com.example.demo.FileUtil.save("F:\\data\\", resultMap.get("sid") + ".wav", (byte[]) resultMap.get("body"));
			} else {
				filePath = "F:\\data\\" + resultMap.get("sid") + ".mp3";
				fileName = resultMap.get("sid") + ".mp3";

				FileUtil.save("F:\\data\\", resultMap.get("sid") + ".mp3", (byte[]) resultMap.get("body"));
			}
			System.out.println("合成 WebAPI 调用成功，音频保存位置：" + filePath);
		} else { // 合成失败
			System.out.println("合成 WebAPI 调用失败，错误信息：" + resultMap.get("body").toString());//返回code为错误码时，请查询https://www.xfyun.cn/document/error-code解决方案
		}

		ResponseFileUtil.response(request, response, filePath, fileName);
		return null;
	}

	@GetMapping(value = "/xunfei/hecheng")
	public void hechenglocal() {
		//1.创建SpeechSynthesizer对象
		SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer();
		//2.合成参数设置，详见《MSC Reference Manual》SpeechSynthesizer 类
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
		mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速，范围0~100
		mTts.setParameter(SpeechConstant.PITCH, "50");//设置语调，范围0~100
		mTts.setParameter(SpeechConstant.VOLUME, "50");//设置音量，范围0~100
		//3.开始合成
		//设置合成音频保存位置（可自定义保存位置），默认保存在“./tts_test.pcm”
		mTts.synthesizeToUri("测试测试测试", "./tts_test.pcm", new SynthesizeToUriListener() {
			//progress为合成进度0~100
			@Override
			public void onBufferProgress(int progress) {
				System.out.println(progress);
			}

			//会话合成完成回调接口
			//uri为合成保存地址，error为错误信息，为null时表示合成会话成功
			@Override
			public void onSynthesizeCompleted(String uri, SpeechError error) {
				if (null == error) {
					System.out.println("成功:" + uri);
				} else {
					System.out.println("失败:" + error);
				}
			}

			@Override
			public void onEvent(int i, int i1, int i2, int i3, Object o, Object o1) {
				System.out.println("i=" + i + " i1=" + i1 + " i2=" + i2 + " i3=" + i3 + " o=" + o + " o1=" + o1);
			}
		});

	}


}
