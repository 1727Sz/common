package cn.sz1727.core.dao.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.sz1727.core.exception.DaoException;

/**
 * cn.sz1727.core.dao.api.IQueryDao.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public interface IQueryDao<T, ID extends Serializable> extends IDao<T, ID> {

	/**
	 * 根据指定的查询数量，执行任意的HQL查询语句，返回查询结果集 设置<code>start</code>和<code>end</code>
	 * 参数为-1，返回整个结果集
	 * 
	 * @param query
	 *            HQL查询语句
	 * @param parameters
	 *            查询参数
	 * @param start
	 *            结果集起始元素游标
	 * @param end
	 *            结果集结束元素游标
	 * @return
	 */
	public List<T> executeGenericQuery(String query, Map<String, Object> parameters, int start, int end) throws DaoException;
}
