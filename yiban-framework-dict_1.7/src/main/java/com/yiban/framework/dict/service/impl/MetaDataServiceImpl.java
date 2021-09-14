package com.yiban.framework.dict.service.impl;

import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.SearchFilter;

import com.google.common.collect.Lists;
import com.yiban.framework.core.domain.base.ValueText;
import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.framework.core.eum.DeleteEnum;
import com.yiban.framework.core.service.BaseService;
import com.yiban.framework.dict.dao.MetaDataDao;
import com.yiban.framework.dict.dao.MetaDataTypeDao;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.domain.MetaDataType;
import com.yiban.framework.dict.service.MetaDataService;

@Service
public class MetaDataServiceImpl extends BaseService implements MetaDataService {

	@Autowired
	private MetaDataDao metaDataDao;

	@Autowired
	private MetaDataTypeDao metaDataTypeDao;
	@Transactional
	@Override
	public void save(MetaData metaData) {
		metaDataDao.save(metaData);

	}
	
	@Override
	public List<MetaData> findAllMetaData() {
		return metaDataDao.findAllMetaData();
	}

	@Transactional
	@Override
	public void update(MetaData metaData) {
		metaDataDao.save(metaData);

	}

	@Override
	public MetaData findMetaDataById(long id) {
		return metaDataDao.findOne(id);
	}

	@Override
	public List<MetaData> findMetaDataByName(String name) {
		return metaDataDao.findMetaDataByName(name);
	}

	@Override
	public MetaData findMetaDataByValue(String value) {
		return metaDataDao.findMetaDataByValue(value);
	}

	@Override
	public MetaData findMetaDataByValueAndTypeId(String value, Long typeId) {
		return metaDataDao.findMetaDataByValueAndTypeId(value, typeId);
	}

	@Override
	public MetaData findMetaDataByValueAndTypeValue(String value, String typeVlue) {
		return metaDataDao.findMetaDataByValueAndTypeValue(value, typeVlue);
	}

	@Override
	public Page<MetaData> findAll(Collection<SearchFilter> searchFilters, Pageable pageable) {
		Specifications<MetaData> spec = Specifications.where(bySearchFilter(searchFilters, MetaData.class)).and(builtinSpecs.notDelete()).and(builtinSpecs.isActived());
		return metaDataDao.findAll(spec, pageable);
	}
	@Override
	public List<ValueTextable<String>> asValue() {
		List<ValueTextable<String>> result = Lists.newArrayList();
		List<MetaData> metaDatas = metaDataDao.findAll();
		if (metaDatas != null && metaDatas.size() > 0) {
			for (MetaData metaData : metaDatas) {
				result.add(new ValueText<>(metaData.getId().toString(), metaData.getName()));
			}
		}
		return result;
	}
	@Override
	public Page<MetaData> findAll(Integer dictType, Pageable pageable) {
		Specification<MetaData> specification = new Specification<MetaData>() {
			@Override
			public Predicate toPredicate(Root<MetaData> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = Lists.newArrayList();
				converSearchAll(predicates, dictType, root, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return metaDataDao.findAll(specification, pageable);
	}
	
	protected void converSearchAll(List<Predicate> predicates, Integer dictType, Root<MetaData> root, CriteriaBuilder cb) {
		if (dictType != null) {
			predicates.add(cb.equal(root.get("dictType"), dictType));
		}
		predicates.add(cb.equal(root.get("isDeleted"), DeleteEnum.NO.getValue()));
	}

	protected void converSearch(List<Predicate> predicates, Integer dictType, Root<MetaData> root, CriteriaBuilder cb) {
		if (dictType != null) {
			predicates.add(cb.equal(root.get("dictType"), dictType));
		}
		predicates.add(cb.equal(root.get("isDeleted"), DeleteEnum.NO.getValue()));
		predicates.add(cb.equal(root.get("isActived"), ActiveEnum.YES.getValue()));

	}
	@Transactional
	@Override
	public void delete(long id) {
		metaDataDao.delete(id);
		
	}
	@Override
	public List<ValueTextable<String>> valueAsList() {
		List<ValueTextable<String>> result = Lists.newArrayList();
		List<MetaData> metaDatas = metaDataDao.findAll();
		if (metaDatas != null && metaDatas.size() > 0) {
			for (MetaData metaData : metaDatas) {
				result.add(new ValueText<>(metaData.getValue().toString(), metaData.getName()));
			}
		}
		return result;
	}
	
	public List<ValueTextable<String>> NameAsList() {
		List<ValueTextable<String>> result = Lists.newArrayList();
		List<MetaData> metaDatas = metaDataDao.findAll();
		if (metaDatas != null && metaDatas.size() > 0) {
			for (MetaData metaData : metaDatas) {
				result.add(new ValueText<>(metaData.getValue().toString(), metaData.getName()));
			}
		}
		return result;
	}
	
	public List<ValueTextable<String>> getNameValueAsList() {
		List<ValueTextable<String>> result = Lists.newArrayList();
		List<MetaData> metaDatas = metaDataDao.findAll();
		if (metaDatas != null && metaDatas.size() > 0) {
			for (MetaData metaData : metaDatas) {
				result.add(new ValueText<>(metaData.getValue(), metaData.getName()));
			}
		}
		return result;
	}
	
	@Override
	public List<MetaData> findMetaDataByDataTypeValue(String type) {
		MetaDataType dataType = metaDataTypeDao.findMetaDataTypeByValue(type);
		if (dataType != null) {
			List<MetaData> metaDatas = metaDataDao.findMetaDataByDataType(dataType.getId());
			return metaDatas;
		}
		return null;
	}
	
	@Override
	public List<MetaData> findMetaDataByTypeId(Long id) {
		return metaDataDao.findMetaDataByTypeId(id);
	}

    @Override
    public List<MetaData> findByTypeIdOrderBySort(String typeValue) {
        MetaDataType dataType = metaDataTypeDao.findMetaDataTypeByValue(typeValue);
        if (dataType != null) {
            return metaDataDao.findByTypeIdOrderBySortAsc(dataType.getId());
        }
        return null;
    }
}
