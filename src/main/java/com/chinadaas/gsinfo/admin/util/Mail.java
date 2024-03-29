package com.chinadaas.gsinfo.admin.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * projectName: gsinfo30-admin<br>
 * copyright: Copyright (c) 2014<br>
 * company: 北京中数智汇科技有限公司<br>
 *
 * desc: 邮件工具类<br>
 * date: 2014年9月1日 下午2:52:50<br>
 * @author 开发者真实姓名[裔传洲]
 */
public class Mail {
	
	private static final Logger logger = LoggerFactory.getLogger(Mail.class);

	// 定义发件人、收件人、SMTP服务器、用户名、密码、主题、内容等
	private String displayName;
	private String to;
	private String from;
	private String smtpServer;
	private String username;
	private String password;
	private String subject;
	private String content;
	// 服务器是否要身份认证
	private boolean ifAuth;
	private String filename = "";
	private String title;
	private String temporaryPassword;

	@SuppressWarnings("rawtypes")
	// 用于保存发送附件的文件名的集合
	private Vector file = new Vector();
	private final String baseName = this.getClass().getName();

	/**
	 * 默认构造函数
	 * @param to
	 * @param title
	 * @param temporaryPassword
	 */
	public Mail(String to, String title, String temporaryPassword) {
		this.title = title;
		this.temporaryPassword = temporaryPassword;
		this.ifAuth = true;
		this.to = to;
		this.initialize();
	}

	/**
	 * 默认构造函数 初始化SMTP服务器地址、发送者E-mail地址、用户名、密码、接收者、主题、内容
	 * @param smtpServer
	 * @param from
	 * @param displayName
	 * @param username
	 * @param password
	 * @param to
	 * @param subject
	 * @param content
	 */
	public Mail(String smtpServer, String from, String displayName,
			String username, String password, String to, String subject,
			String content) {
		this.smtpServer = smtpServer;
		this.from = from;
		this.displayName = displayName;
		this.ifAuth = true;
		this.username = username;
		this.password = password;
		this.to = to;
		this.subject = subject;
		this.content = content;
	}
	
	/**
	 * 默认构造函数 初始化SMTP服务器地址、发送者E-mail地址、接收者、主题、内容
	 * @param smtpServer
	 * @param from
	 * @param displayName
	 * @param to
	 * @param subject
	 * @param content
	 */
	public Mail(String smtpServer, String from, String displayName, String to,
			String subject, String content) {
		this.smtpServer = smtpServer;
		this.from = from;
		this.displayName = displayName;
		this.ifAuth = false;
		this.to = to;
		this.subject = subject;
		this.content = content;
	}

	/**
	 * desc: 初始化<br>
	 * date: 2014年8月26日 下午1:42:21<br>
	 * @author 开发者真实姓名[裔传洲]
	 */
	private void initialize() {
		ResourceBundle bundle = Resource.getResourceBundle(baseName);
		this.smtpServer = bundle.getString("smtp_server");
		this.username = bundle.getString("user_name");
		this.password = bundle.getString("password");
		this.from = bundle.getString("from");
		this.displayName = bundle.getString("display_name");
		this.subject = bundle.getString("subject");
		this.content = bundle.getString("content");
		MessageFormat str = new MessageFormat(this.content);
		String[] replaced = new String[] { title, this.to, temporaryPassword, this.to };
		this.content = str.format(replaced);
	}

	/**
	 * desc: 获取properties属性<br>
	 * date: 2014年8月26日 下午1:43:42<br>
	 * @author 开发者真实姓名[裔传洲]
	 * @param fileName
	 * @return
	 */
	public Properties getProperties(String fileName) {
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream(fileName);
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		}
		return p;
	}

	/**
	 * desc: 发送邮件<br>
	 * date: 2014年8月26日 下午1:44:15<br>
	 * @author 开发者真实姓名[裔传洲]
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> send() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "success");
		String message = "邮件发送成功！";
		Session session = null;
		Properties props = System.getProperties();

		props.put("mail.smtp.host", smtpServer);
		if (ifAuth) { // 服务器需要身份认证
			props.put("mail.smtp.auth", "true");
			SmtpAuth smtpAuth = new SmtpAuth(username, password);
			session = Session.getDefaultInstance(props, smtpAuth);
		} else {
			props.put("mail.smtp.auth", "false");
			session = Session.getDefaultInstance(props, null);
		}
		session.setDebug(false);
		Transport trans = null;
		try {
			Message msg = new MimeMessage(session);
			try {
				Address from_address = new InternetAddress(from, displayName);
				msg.setFrom(from_address);
			} catch (java.io.UnsupportedEncodingException e) {
				logger.error(e.getMessage());
			}
			InternetAddress[] address = { new InternetAddress(to) };
			msg.setRecipients(Message.RecipientType.TO, address);

			Multipart mp = new MimeMultipart();
			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setContent(content, "text/html;charset=gb2312");

			sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
			msg.setSubject("=?GB2312?B?"
					+ enc.encode(subject.getBytes("gb2312")) + "?=");

			mp.addBodyPart(mbp);
			if (!file.isEmpty()) {// 有附件
				@SuppressWarnings("rawtypes")
				Enumeration efile = file.elements();
				while (efile.hasMoreElements()) {
					mbp = new MimeBodyPart();
					filename = efile.nextElement().toString(); // 选择出每一个附件名
					FileDataSource fds = new FileDataSource(filename); // 得到数据源
					mbp.setDataHandler(new DataHandler(fds)); // 得到附件本身并至入BodyPart
					mbp.setFileName(fds.getName()); // 得到文件名同样至入BodyPart
					mp.addBodyPart(mbp);
				}
				file.removeAllElements();
			}
			// Multipart加入到信件
			msg.setContent(mp);
			// 设置信件头的发送日期
			msg.setSentDate(new Date());
			// 发送信件
			msg.saveChanges();
			trans = session.getTransport("smtp");
			trans.connect(smtpServer, username, password);
			trans.sendMessage(msg, msg.getAllRecipients());
			trans.close();

		} catch (AuthenticationFailedException e) {
			map.put("state", "failed");
			message = "邮件发送失败！错误原因：\n" + "身份验证错误!";
			logger.error(e.getMessage());
		} catch (MessagingException e) {
			message = "邮件发送失败！错误原因：\n" + e.getMessage();
			map.put("state", "failed");
			logger.error(e.getMessage());
			Exception ex = e.getNextException();
			if (ex != null) {
				logger.error(ex.getMessage());
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		map.put("message", message);

		return map;
	}

	/**
	 * 设置SMTP服务器地址
	 */
	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	/**
	 * 设置发件人的地址
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * 设置显示的名称
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * 设置服务器是否需要身份认证
	 */
	public void setIfAuth(boolean ifAuth) {
		this.ifAuth = ifAuth;
	}

	/**
	 * 设置E-mail用户名
	 */
	public void setUserName(String username) {
		this.username = username;
	}

	/**
	 * 设置E-mail密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 设置接收者
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * 设置主题
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * 设置主体内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 该方法用于收集附件名
	 */
	@SuppressWarnings("unchecked")
	public void addAttachfile(String fname) {
		file.addElement(fname);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getTemporaryPassword() {
		return temporaryPassword;
	}

	public void setTemporaryPassword(String temporaryPassword) {
		this.temporaryPassword = temporaryPassword;
	}

}