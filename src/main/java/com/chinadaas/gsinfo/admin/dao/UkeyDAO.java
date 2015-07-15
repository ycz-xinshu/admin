package com.chinadaas.gsinfo.admin.dao;

import com.chinadaas.gsinfo.admin.vo.Ukey;

/**
 * Ukey持久化层私有模板
 * projectName: gsinfo30-admin<br>
 * copyright: Copyright (c) 2014<br>
 * company: 北京中数智汇科技有限公司<br>
 *
 * desc: TODO<br>
 * date: 2014年7月8日 下午3:02:48<br>
 * @author 开发者真实姓名[裔传洲]
 */
public interface UkeyDAO extends IDAO<Ukey>{

	public String getIdUserByUkeyNo(String ukeyNo);

}
