package com.yiban.rec.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.FieldMeta;

/**
 * @author swing
 * @date 2018年7月17日 上午9:18:37 类说明
 */
public class FieldMetaUtil {
	public static List<Map<String, Object>> getConfigMeta() {
        //按照添加顺序排序
		Map<String, List<Map<String, Object>>> metaMap = new LinkedHashMap<>();
		Class<AppRuntimeConfig> c = AppRuntimeConfig.class;
		Field[] fields = c.getDeclaredFields();
		Set<FieldMetaGroupEnum> groupSet =loadGroup();
		for(FieldMetaGroupEnum groupEnum:groupSet){
			List<Map<String, Object>> metaLis = metaMap.get(groupEnum.getName());
			if (metaLis == null) {
				metaLis = new ArrayList<>();
				
			}
			for (Field f : fields) {
				FieldMeta meta = f.getAnnotation(FieldMeta.class);
				if(meta.group().getName().equals(groupEnum.getName())){
					Map<String, Object> config = new HashMap<>(10);
					config.put("name", meta.name());
					config.put("key", f.getName());
					config.put("type", meta.type().getName());
					config.put("defaultValue", meta.defaultValue());
					config.put("options", meta.options());
					config.put("sort", meta.group().getSort());
					metaLis.add(config);
				}
			}
			metaMap.put(groupEnum.getName(), metaLis);
		}
		
		List<Map<String, Object>> list = new ArrayList<>();
		Iterator<Entry<String, List<Map<String, Object>>>> ite = metaMap.entrySet().iterator();
		int id = 0;
		while (ite.hasNext()) {
			Entry<String, List<Map<String, Object>>> en = ite.next();
			Map<String, Object> item = new HashMap<>();
			item.put("id", id++);
			item.put("groupName", en.getKey());
			item.put("metaList", en.getValue());
			list.add(item);
		}
		return list;
	}

	private static Set<FieldMetaGroupEnum> loadGroup() {
		Class<AppRuntimeConfig> c = AppRuntimeConfig.class;
		Field[] fields = c.getDeclaredFields();
		Set<FieldMetaGroupEnum> groupSet= new TreeSet<>(new Comparator<FieldMetaGroupEnum>() {
			public int compare(FieldMetaGroupEnum obj1, FieldMetaGroupEnum obj2) {
				return obj1.getSort() - obj2.getSort();
			}
		});
		// 分组
		for (Field f : fields) {
			FieldMeta meta = f.getAnnotation(FieldMeta.class);
			groupSet.add(meta.group());
		}
		return groupSet;
	}
	public static void main(String[] args) {
		Set<FieldMetaGroupEnum> list =FieldMetaUtil.loadGroup();
		for(FieldMetaGroupEnum eu:list){
			System.out.println(eu.getName());
		}
	}
}
