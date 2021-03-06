package com.foxinmy.weixin4j.http.weixin;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.http.HttpParams;
import com.foxinmy.weixin4j.http.HttpRequest;
import com.foxinmy.weixin4j.model.Consts;

/**
 * 微信ssl请求
 *
 * @className WeixinSSLRequestExecutor
 * @author jinyu(foxinmy@gmail.com)
 * @date 2015年8月17日
 * @since JDK 1.6
 * @see
 */
public class WeixinSSLRequestExecutor extends WeixinRequestExecutor {

	private final SSLContext sslContext;

	public WeixinSSLRequestExecutor(String password, InputStream inputStream)
			throws WeixinException {
		try {
			KeyStore keyStore = KeyStore.getInstance(Consts.PKCS12);
			keyStore.load(inputStream, password.toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory
					.getInstance(Consts.SunX509);
			kmf.init(keyStore, password.toCharArray());
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), null,
					new java.security.SecureRandom());
		} catch (Exception e) {
			throw new WeixinException("Key load error", e);
		}
	}

	public WeixinSSLRequestExecutor(SSLContext sslContext) {
		this.sslContext = sslContext;
	}

	public SSLContext getSSLContext() {
		return sslContext;
	}

	@Override
	protected WeixinResponse doRequest(HttpRequest request)
			throws WeixinException {
		HttpParams params = null;
		if (request.getParams() != null) {
			params = HttpParams.copy(request.getParams())
					.setSslContext(sslContext).build();
		} else {
			params = HttpParams.custom().setSslContext(sslContext).build();
		}
		request.setParams(params);
		return super.doRequest(request);
	}
}
