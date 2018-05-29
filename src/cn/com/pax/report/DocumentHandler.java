package cn.com.pax.report;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;


public class DocumentHandler {
	private Configuration configuration = null;
	
	public DocumentHandler() {
		configuration = new Configuration(Configuration.VERSION_2_3_26);
		configuration.setDefaultEncoding("UTF-8");
	}
	
	public boolean createDoc(String dir, String filename, String savePath, Map<String, Object> sDate) {
		Template template = null;
		try {
			configuration.setDirectoryForTemplateLoading(new File(dir));
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
			template = configuration.getTemplate(filename);
			
			File outFile = new File(savePath);
			Writer outWriter = null;
			
			outWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
			
			template.process(sDate, outWriter);
			outWriter.flush();
			outWriter.close();
			return true;
		} catch (TemplateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}
}
