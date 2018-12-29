package com.nbcb.myron.bsen.utils;


import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.*;

public class XmlUtil {
	private static String itemStr = "Item";

	public static String encode(Object o) {
		Document d = DocumentHelper.createDocument();
		Element root = encodeNode("xml", o);
		d.add(root);
		return d.asXML();
	}

	private static Element encodeNode(String strKey, Object o) {
		DocumentFactory factory = DocumentFactory.getInstance();
		Element e = factory.createElement(strKey);

		if (o instanceof Map) {
			Map m = (Map) o;
			Iterator i = m.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry en = (Map.Entry) i.next();
				String key = (String) en.getKey();
				Object value = (Object) en.getValue();
				e.add(encodeNode(key, value));
			}
			return e;
		} else if (o instanceof List) {
			List list = (List) o;
			for (int i = 0; i < list.size(); i++) {
				e.add(encodeNode(itemStr, list.get(i)));
			}
			return e;
		} else if (o instanceof Integer) {
			e.setText(String.valueOf(o));
			return e;
		} else if (o instanceof String) {
			e.addCDATA((String) o);
			return e;
		} else if(o instanceof Double){
			e.setText(String.valueOf(o));
			return e;
		}else if(o instanceof Long){
			e.setText(String.valueOf(o));
			return e;
		} else {
			return null;
		}
	}

	public static Map decode(String str) throws DocumentException {

		Document doc = null;

		doc = DocumentHelper.parseText(str); // 将字符串转为XML

		Element rootElt = doc.getRootElement(); // 获取根节点

		if (allItem(rootElt)) {
			throw new DocumentException("根节点下不允许有item元素");
		}

		return  (Map) decodeNode(rootElt);
	
	}

	private static Object decodeNode(Element e) throws DocumentException {
		if (!hasChild(e)) {
			return e.getTextTrim();
		}
		boolean bAllItem = allItem(e);
		
		String key = e.getName();
	//	System.out.println(key);
		
		
		if (bAllItem) {
			//System.out.println("都是item元素");
			List list = new ArrayList();
			Iterator i = e.elementIterator();
			while (i.hasNext()) {
				list.add(decodeNode((Element) i.next()));
			}
			return list;
		} else {
			Map tmpM = new HashMap();
			Iterator i = e.elementIterator();
			while (i.hasNext()) {
				Element tmpE = (Element) i.next();
				String tmpKey = tmpE.getName();
			//	System.out.println(tmpKey);
				tmpM.put(tmpKey, decodeNode(tmpE));
			}
			return tmpM;
		}
	}

	private static boolean hasChild(Element e) {
		Iterator i = e.elementIterator();
		if (i.hasNext())
			return true;
		return false;
	}

	private static boolean allItem(Element e) throws DocumentException {
		Iterator i = e.elementIterator();
		int total = e.elements().size();
		int item = 0;
		while (i.hasNext()) {
			Element tmpE = (Element) i.next();
			if (tmpE.getName().equals(itemStr))
				item++;
		}
		if (item == 0) {
			return false;
		}
		if (item < total) {
			throw new DocumentException("元素中即有ITEM元素，又有其他字段的属性");
		}
		return true;
	}

	/**
	 * org.dom4j.Element 转  com.alibaba.fastjson.JSONObject
	 * @param node
	 * @return
	 */
	public static JSONObject elementToJSONObject(Element node) {
		JSONObject result = new JSONObject();
		// 当前节点的名称、文本内容和属性
		List<Attribute> listAttr = node.attributes();// 当前节点的所有属性的list
		for (Attribute attr : listAttr) {// 遍历当前节点的所有属性
			result.put(attr.getName(), attr.getValue());
		}
		// 递归遍历当前节点所有的子节点
		List<Element> listElement = node.elements();// 所有一级子节点的list
		if (!listElement.isEmpty()) {
			for (Element e : listElement) {// 遍历所有一级子节点
				if (e.attributes().isEmpty() && e.elements().isEmpty()) // 判断一级节点是否有属性和子节点
					result.put(e.getName(), e.getTextTrim());// 沒有则将当前节点作为上级节点的属性对待
				else {
					if (!result.containsKey(e.getName())) // 判断父节点是否存在该一级节点名称的属性
						result.put(e.getName(), new JSONArray());// 没有则创建
					((JSONArray) result.get(e.getName())).add(elementToJSONObject(e));// 将该一级节点放入该节点名称的属性对应的值中
				}
			}
		}
		return result;
	}
	/**
	 * org.dom4j.Element 转  com.alibaba.fastjson.JSONObject
	 * @param node
	 * @return
	 */
	public static Map<String,Object> elementToMap(Element node) {
		Map<String,Object> result = new HashMap<>();
		// 当前节点的名称、文本内容和属性
		List<Attribute> listAttr = node.attributes();// 当前节点的所有属性的list
		for (Attribute attr : listAttr) {// 遍历当前节点的所有属性
			result.put(attr.getName(), attr.getValue());
		}
		// 递归遍历当前节点所有的子节点
		List<Element> listElement = node.elements();// 所有一级子节点的list
		if (!listElement.isEmpty()) {
			for (Element e : listElement) {// 遍历所有一级子节点
				if (e.attributes().isEmpty() && e.elements().isEmpty()) // 判断一级节点是否有属性和子节点
					result.put(e.getName(), e.getTextTrim());// 沒有则将当前节点作为上级节点的属性对待
				else {
					if (!result.containsKey(e.getName())) // 判断父节点是否存在该一级节点名称的属性
						result.put(e.getName(), new JSONArray());// 没有则创建
					((JSONArray) result.get(e.getName())).add(elementToMap(e));// 将该一级节点放入该节点名称的属性对应的值中
				}
			}
		}
		return result;
	}

	public static void main(String[] args)throws Exception {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("ErrCode", "0000");
		map.put("ErrMsg", "推送成功");
		String strstr=XmlUtil.encode(map);
		 System.out.println(strstr);
	}

}
