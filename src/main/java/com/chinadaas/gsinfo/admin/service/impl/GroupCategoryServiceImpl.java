package com.chinadaas.gsinfo.admin.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.chinadaas.gsinfo.admin.dao.IDAO;
import com.chinadaas.gsinfo.admin.dao.GroupCategoryDAO;
import com.chinadaas.gsinfo.admin.service.BaseService;
import com.chinadaas.gsinfo.admin.service.GroupCategoryService;
import com.chinadaas.gsinfo.admin.vo.GroupCategory;

/**
 * GroupCategory service实现类
 * projectName: gsinfo30-admin<br>
 * copyright: Copyright (c) 2014<br>
 * company: 北京中数智汇科技有限公司<br>
 *
 * desc: TODO<br>
 * date: 2014年7月8日 下午3:05:37<br>
 * @author 开发者真实姓名[裔传洲]
 */
public class GroupCategoryServiceImpl extends BaseService<GroupCategory> implements GroupCategoryService{
	
	private static final Logger logger = LoggerFactory.getLogger(GroupCategoryServiceImpl.class);
	
	@Autowired
	private GroupCategoryDAO groupCategoryDAO;


	public void setGroupCategoryDAO(GroupCategoryDAO groupCategoryDAO) {
		this.groupCategoryDAO = groupCategoryDAO;
	}

	@Override
	protected IDAO<GroupCategory> getDao() {
		return groupCategoryDAO;
	}

	@Override
	protected String getListSQL(Map param) {
		return null;
	}

	@Override
	protected String getSelectSQL(Map param) {
		return null;
	}

}
