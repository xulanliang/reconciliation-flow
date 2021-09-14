/**
 * Copyright © 2018 深圳市巨鼎医疗设备有限公司
 */
package com.yiban.rec.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;


/**
 * xml util
 * 
 * @author Zhouych
 * @date 2018年6月14日 下午1:58:32 <br/>
 * @since JDK 1.8
 */
public class XmlUtil {

	private static ConcurrentMap<Class<?>, JAXBContext> jaxbContexts = new ConcurrentHashMap<Class<?>, JAXBContext>();

	/**
	 * Java Object->Xml without encoding.
	 * 
	 * @throws JAXBException
	 */
	public static String toXml(Object root) throws JAXBException {
		Class<?> clazz = root.getClass();
		return toXml(root, clazz, null);
	}

	/**
	 * Java Object->Xml with encoding.
	 * 
	 * @throws JAXBException
	 */
	public static String toXml(Object root, String encoding) throws JAXBException {
		Class<?> clazz = root.getClass();
		return toXml(root, clazz, encoding);
	}

	/**
	 * Java Object->Xml with encoding.
	 * 
	 * @throws JAXBException
	 */
	public static String toXml(Object root, Class<?> clazz, String encoding) throws JAXBException {
		StringWriter writer = new StringWriter();
		createMarshaller(clazz, encoding).marshal(root, writer);
		return writer.toString();
	}

	/**
	 * Java Collection->Xml without encoding, 特别支持Root Element是Collection的情形.
	 * 
	 * @throws JAXBException
	 */
	public static String toXml(Collection<?> root, String rootName, Class<?> clazz) throws JAXBException {
		return toXml(root, rootName, clazz, null);
	}

	/**
	 * Java Collection->Xml with encoding, 特别支持Root Element是Collection的情形.
	 * 
	 * @throws JAXBException
	 */
	public static String toXml(Collection<?> root, String rootName, Class<?> clazz, String encoding)
			throws JAXBException {
		CollectionWrapper wrapper = new CollectionWrapper();
		wrapper.collection = root;

		JAXBElement<CollectionWrapper> wrapperElement = new JAXBElement<CollectionWrapper>(new QName(rootName),
				CollectionWrapper.class, wrapper);

		StringWriter writer = new StringWriter();
		createMarshaller(clazz, encoding).marshal(wrapperElement, writer);

		return writer.toString();
	}

	/**
	 * Xml->Java Object.
	 * 
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml, Class<T> clazz) throws JAXBException {
		StringReader reader = new StringReader(xml);
		return (T) createUnmarshaller(clazz).unmarshal(reader);
	}

	/**
	 * 创建Marshaller并设定encoding(可为null). 线程不安全，需要每次创建或pooling。
	 * 
	 * @throws JAXBException
	 */
	public static Marshaller createMarshaller(Class<?> clazz, String encoding) throws JAXBException {
		JAXBContext jaxbContext = getJaxbContext(clazz);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		if (StringUtils.isNotBlank(encoding)) {
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
		}
		return marshaller;
	}

	/**
	 * 创建UnMarshaller. 线程不安全，需要每次创建或pooling。
	 * 
	 * @throws JAXBException
	 */
	public static Unmarshaller createUnmarshaller(Class<?> clazz) throws JAXBException {
		JAXBContext jaxbContext = getJaxbContext(clazz);
		return jaxbContext.createUnmarshaller();
	}

	protected static JAXBContext getJaxbContext(Class<?> clazz) throws JAXBException {
		ValidateUtil.notNull(clazz, "'clazz' must not be null");
		JAXBContext jaxbContext = jaxbContexts.get(clazz);
		if (jaxbContext == null) {
			jaxbContext = JAXBContext.newInstance(clazz, CollectionWrapper.class);
			jaxbContexts.putIfAbsent(clazz, jaxbContext);
		}
		return jaxbContext;
	}

	public static String withoutXmlHeadAndTrim(String xml) {
		return org.springframework.util.StringUtils.trimAllWhitespace(StringUtil.replace(xml, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>",
				StringUtils.EMPTY));
	}

	/**
	 * 封装Root Element 是 Collection的情况.
	 */
	public static class CollectionWrapper {

		@XmlAnyElement
		protected Collection<?> collection;
	}

}
