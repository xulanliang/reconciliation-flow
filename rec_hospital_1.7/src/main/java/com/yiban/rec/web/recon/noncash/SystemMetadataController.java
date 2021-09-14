package com.yiban.rec.web.recon.noncash;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.domain.MetaDataType;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.dict.service.MetaDataTypeService;
import com.yiban.rec.domain.SystemMetadata;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.SystemMetadataService;
/**
 * 订单来源于系统来源关联
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/admin/systemMetadata")
public class SystemMetadataController extends CurrentUserContoller {

	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private SystemMetadataService systemMetadataService;
	
	@Autowired
	private MetaDataTypeService metaDataTypeService;
	
	
	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataTypeService.valueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		return autoView("reconciliation/systemMetadata");
	}
	
	@RestController
	@RequestMapping({"/admin/systemMetadata/data"})
	class SystemMetadataDataController extends BaseController {
		
		@GetMapping
		public WebUiPage<MetaData> findMetaData() {
			//查询订单来源的id
			MetaDataType vo = metaDataTypeService.findMetaDataTypeByValue("bill_source");
			Sort sort = new Sort(new Order(Direction.DESC, "id"), new Order(Direction.ASC, "name"));
			Page<MetaData> metaData = metaDataService.findAll(vo.getId().intValue(), getRequestPageabledWithInitSort(sort));
			return toWebUIPage(metaData);
		}
		/**
		 * 查询详情绑定情况
		 */
		@GetMapping("/{metaDataCode}/systemCode")
		public List<MetaData> systemCode(String metaDataCode){
			//原始系统编码数据
			MetaDataType vo = metaDataTypeService.findMetaDataTypeByValue("System_Code");
			List<MetaData> metaList = metaDataService.findMetaDataByTypeId(vo.getId());
			//select t.* from t_meta_data t LEFT JOIN t_meta_data_type t2 on t.type_id=t2.id where t2.`name`='系统编码';
			//过滤数据/数据赋值
			filterList(metaDataCode,metaList);
			//filterList(systemList,metaList);
			return metaList;
		}
		
		private void filterList(String metaDataCode,List<MetaData> metaList) {
			//系统和字典关联表中数据
			List<SystemMetadata> systemList = systemMetadataService.findAll();
			for(int i = metaList.size() - 1; i >= 0; i--) {
				for(SystemMetadata z:systemList) {
					if(z.getSystemCode().equals(metaList.get(i).getValue())&&!z.getMetaDataCode().equals(metaDataCode)) {
						metaList.remove(i);
						break;
					}
					if(z.getMetaDataCode().equals(metaDataCode)&&z.getSystemCode().equals(metaList.get(i).getValue())) {
						metaList.get(i).setChecked(true);
					}
				}
			}
		}
		
		/**
		 * 修改/保存
		 * @param metaDataCode
		 * @param codes
		 * @return
		 */
		@GetMapping("/{metaDataCode}/save")
		public ResponseResult save(String metaDataCode,String codes) {
			ResponseResult rs=ResponseResult.success("绑定成功");
			try {
				List<SystemMetadata> systemList=new ArrayList<>();
				//先删除
				systemMetadataService.deleteData(metaDataCode);
				//循环注入
				String[] list = codes.split(",");
				for(String v:list) {
					SystemMetadata vo=new SystemMetadata();
					vo.setMetaDataCode(metaDataCode);
					vo.setSystemCode(v);
					systemList.add(vo);
				}
				//保存
				systemMetadataService.save(systemList);
			} catch (Exception e) {
				e.printStackTrace();
				rs=ResponseResult.failure("保存异常:"+e.getMessage());
			}
			return rs;
		}
		
		@Logable(operation = "删除字典数据")
		@DeleteMapping("/{id}")
		public ResponseResult delete(@PathVariable Long id) {
			try {
				MetaData metaVo = metaDataService.findMetaDataById(id);
				metaDataService.delete(id);
				//删除字典订单来源数据,再删除关联表(订单来源字典与系统来源配置表)
				systemMetadataService.deleteData(metaVo.getValue());
			} catch (Exception e) {
				if(e instanceof DataIntegrityViolationException){
					return ResponseResult.failure("删除失败,请检查该字典是否被使用");
				}
				return ResponseResult.failure("删除失败,服务器错误");
			}
			return ResponseResult.success("删除成功");
		}
		
	}
}
