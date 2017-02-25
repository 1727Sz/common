package cn.sz1727.core.dao.api;

import java.io.Serializable;
import java.util.Map;

import cn.sz1727.core.exception.DaoException;

/**
 * cn.sz1727.core.dao.api.IPageableQueryDao.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public interface IPageableQueryDao<T extends Identifiable<ID>, ID extends Serializable> {

	Pageable<T> pagingQuery(String query, Map<String, Object> parameters, int currentPage, int pageSize) throws DaoException;

	Pageable<T> pagingQuery(IDynamicParams<T, ID> dynamicParams, int currentPage, int pageSize) throws DaoException;

}
