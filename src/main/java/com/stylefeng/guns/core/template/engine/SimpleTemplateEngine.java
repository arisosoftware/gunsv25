package com.stylefeng.guns.core.template.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.resource.ClasspathResourceLoader;

import com.stylefeng.guns.core.template.config.ContextConfig;
import com.stylefeng.guns.core.template.config.ControllerConfig;
import com.stylefeng.guns.core.template.config.DaoConfig;
import com.stylefeng.guns.core.template.config.PageConfig;
import com.stylefeng.guns.core.template.config.ServiceConfig;
 
import com.stylefeng.guns.core.util.ToolUtil;

/**
 * 通用的模板生成引擎 , 看不惯2层抽象类，直接合并成一个
  */
public class SimpleTemplateEngine  {

	protected ContextConfig contextConfig; // 全局配置
	protected ControllerConfig controllerConfig; // 控制器的配置
	protected PageConfig pageConfig; // 页面的控制器
	protected DaoConfig daoConfig; // Dao配置
	protected ServiceConfig serviceConfig; // Service配置


	public void initConfig() {
		if (this.contextConfig == null) {
			this.contextConfig = new ContextConfig();
		}
		if (this.controllerConfig == null) {
			this.controllerConfig = new ControllerConfig();
		}
		if (this.pageConfig == null) {
			this.pageConfig = new PageConfig();
		}
		if (this.daoConfig == null) {
			this.daoConfig = new DaoConfig();
		}
		if (this.serviceConfig == null) {
			this.serviceConfig = new ServiceConfig();
		}

		this.controllerConfig.setContextConfig(this.contextConfig);
		this.controllerConfig.init();

		this.serviceConfig.setContextConfig(this.contextConfig);
		this.serviceConfig.init();

		this.daoConfig.setContextConfig(this.contextConfig);
		this.daoConfig.init();

		this.pageConfig.setContextConfig(this.contextConfig);
		this.pageConfig.init();
	}

	public PageConfig getPageConfig() {
		return pageConfig;
	}

	public void setPageConfig(PageConfig pageConfig) {
		this.pageConfig = pageConfig;
	}

	public ContextConfig getContextConfig() {
		return contextConfig;
	}

	public void setContextConfig(ContextConfig contextConfig) {
		this.contextConfig = contextConfig;
	}

	public ControllerConfig getControllerConfig() {
		return controllerConfig;
	}

	public void setControllerConfig(ControllerConfig controllerConfig) {
		this.controllerConfig = controllerConfig;
	}

	public DaoConfig getDaoConfig() {
		return daoConfig;
	}

	public void setDaoConfig(DaoConfig daoConfig) {
		this.daoConfig = daoConfig;
	}

	public ServiceConfig getServiceConfig() {
		return serviceConfig;
	}

	public void setServiceConfig(ServiceConfig serviceConfig) {
		this.serviceConfig = serviceConfig;
	}

	protected GroupTemplate groupTemplate;

	public SimpleTemplateEngine() {
		initBeetlEngine();
	}

	public void initBeetlEngine() {
		Properties properties = new Properties();
		properties.put("RESOURCE.root", "");
		properties.put("DELIMITER_STATEMENT_START", "<%");
		properties.put("DELIMITER_STATEMENT_END", "%>");
		properties.put("HTML_TAG_FLAG", "##");
		Configuration cfg = null;
		try {
			cfg = new Configuration(properties);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader();
		groupTemplate = new GroupTemplate(resourceLoader, cfg);
		groupTemplate.registerFunctionPackage("tool", new ToolUtil());
	}

	public void configTemplate(Template template) {
		template.binding("controller", this.getControllerConfig());
		template.binding("context",  this.getContextConfig());
		template.binding("dao",  this.getDaoConfig());
		template.binding("service",  this.getServiceConfig());
	}

	/*
	 * 
	 * 
	 * System.getProperties() can be overridden by calls to
	 * System.setProperty(String key, String value) or with command line parameters
	 * -Dfile.separator=/
	 * 
	 * File.separator gets the separator for the default filesystem.
	 * 
	 * FileSystems.getDefault() gets you the default filesystem.
	 * 
	 * FileSystem.getSeparator() gets you the separator character for the
	 * filesystem. Note that as an instance method you can use this to pass
	 * different filesystems to your code other than the default, in cases where you
	 * need your code to operate on multiple filesystems in the one JVM.
	 * 
	 */
	public void generateFile(String template, String filePath) throws BeetlException, FileNotFoundException  {
		Template pageTemplate = groupTemplate.getTemplate(template);
		configTemplate(pageTemplate);
		
		filePath = filePath.replaceAll("/+|\\\\+", File.pathSeparator);
		 
		File file = new File(filePath);
		file.mkdirs();
		pageTemplate.renderTo(new FileOutputStream(file));		 
	}

	public void start() throws BeetlException, FileNotFoundException {
		// 配置之间的相互依赖
		 this.initConfig();

		// 生成模板
		if ( this.contextConfig.getControllerSwitch()) {
			generateController();
		}
		if ( this.contextConfig.getIndexPageSwitch()) {
			generatePageHtml();
		}
		if ( this.contextConfig.getAddPageSwitch()) {
			generatePageAddHtml();
		}
		if ( this.contextConfig.getEditPageSwitch()) {
			generatePageEditHtml();
		}
		if ( this.contextConfig.getJsSwitch()) {
			generatePageJs();
		}
		if ( this.contextConfig.getInfoJsSwitch()) {
			generatePageInfoJs();
		}
		if ( this.contextConfig.getDaoSwitch()) {
			generateDao();
		}
		if ( this.contextConfig.getServiceSwitch()) {
			generateService();
		}

	}
 
	protected void generatePageEditHtml() throws BeetlException, FileNotFoundException {
		String path = ToolUtil.format(
				this.getContextConfig().getProjectPath() + getPageConfig().getPageEditPathTemplate(),
				this.getContextConfig().getBizEnName(), this.getContextConfig().getBizEnName());
		generateFile("gunsTemplate/page_edit.html.btl", path);
		System.out.println("生成编辑页面成功!");
	}

	
	protected void generatePageAddHtml() throws BeetlException, FileNotFoundException {
		String path = ToolUtil.format(
				this.getContextConfig().getProjectPath() + getPageConfig().getPageAddPathTemplate(),
				this.getContextConfig().getBizEnName(), this.getContextConfig().getBizEnName());
		generateFile("gunsTemplate/page_add.html.btl", path);
		System.out.println("生成添加页面成功!");
	}

	
	protected void generatePageInfoJs() throws BeetlException, FileNotFoundException {
		String path = ToolUtil.format(
				this.getContextConfig().getProjectPath() + getPageConfig().getPageInfoJsPathTemplate(),
				this.getContextConfig().getBizEnName(), this.getContextConfig().getBizEnName());
		generateFile("gunsTemplate/page_info.js.btl", path);
		System.out.println("生成页面详情js成功!");
	}

	
	protected void generatePageJs() throws BeetlException, FileNotFoundException {
		String path = ToolUtil.format(
				this.getContextConfig().getProjectPath() + getPageConfig().getPageJsPathTemplate(),
				this.getContextConfig().getBizEnName(), this.getContextConfig().getBizEnName());
		generateFile("gunsTemplate/page.js.btl", path);
		System.out.println("生成页面js成功!");
	}

	
	protected void generatePageHtml() throws BeetlException, FileNotFoundException {
		String path = ToolUtil.format(this.getContextConfig().getProjectPath() + getPageConfig().getPagePathTemplate(),
				this.getContextConfig().getBizEnName(), this.getContextConfig().getBizEnName());
		generateFile("gunsTemplate/page.html.btl", path);
		System.out.println("生成页面成功!");
	}

	
	protected void generateController() throws BeetlException, FileNotFoundException {
		String controllerPath = ToolUtil.format(
				this.getContextConfig().getProjectPath() + this.getControllerConfig().getControllerPathTemplate(),
				ToolUtil.firstLetterToUpper(this.getContextConfig().getBizEnName()));
		generateFile("gunsTemplate/Controller.java.btl", controllerPath);
		System.out.println("生成控制器成功!");
	}

	
	protected void generateDao() throws BeetlException, FileNotFoundException {
		String daoPath = ToolUtil.format(
				this.getContextConfig().getProjectPath() + this.getDaoConfig().getDaoPathTemplate(),
				ToolUtil.firstLetterToUpper(this.getContextConfig().getBizEnName()));
		generateFile("gunsTemplate/Dao.java.btl", daoPath);
		System.out.println("生成Dao成功!");

		String mappingPath = ToolUtil.format(
				this.getContextConfig().getProjectPath() + this.getDaoConfig().getXmlPathTemplate(),
				ToolUtil.firstLetterToUpper(this.getContextConfig().getBizEnName()));
		generateFile("gunsTemplate/Mapping.xml.btl", mappingPath);
		System.out.println("生成Dao Mapping xml成功!");
	}

	
	protected void generateService() throws BeetlException, FileNotFoundException {
		String servicePath = ToolUtil.format(
				this.getContextConfig().getProjectPath() + this.getServiceConfig().getServicePathTemplate(),
				ToolUtil.firstLetterToUpper(this.getContextConfig().getBizEnName()));
		generateFile("gunsTemplate/Service.java.btl", servicePath);
		System.out.println("生成Service成功!");

		String serviceImplPath = ToolUtil.format(
				this.getContextConfig().getProjectPath() + this.getServiceConfig().getServiceImplPathTemplate(),
				ToolUtil.firstLetterToUpper(this.getContextConfig().getBizEnName()));
		generateFile("gunsTemplate/ServiceImpl.java.btl", serviceImplPath);
		System.out.println("生成ServiceImpl成功!");
	}
}
