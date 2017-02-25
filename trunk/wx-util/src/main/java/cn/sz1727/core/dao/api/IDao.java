package cn.sz1727.core.dao.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.sz1727.core.exception.DaoException;

/**
 * 数据访问层基础接口
 * 
 * cn.sz1727.core.dao.api.IDao.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public interface IDao<T, ID extends Serializable> {
	/**
	 * 管理和持久化一个实体
	 * 
	 * @param entity
	 * @return 持久化的实体
	 * @throws DaoException
	 */
	public T save(T entity) throws DaoException;

	/**
	 * 合并实体的状态到当前的持久化上下文
	 * 
	 * @param entity
	 * @return 合并后的实体
	 * @throws DaoException
	 */
	public T update(T entity) throws DaoException;

	/**
	 * 从当前持久化上下文删除一个实体
	 * 
	 * @param entity
	 * @throws DaoException
	 */
	public void delete(T entity) throws DaoException;

	/**
	 * 根据实体PK从持久层查找实体，PK通常为逻辑ID
	 * 
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public T findById(ID id) throws DaoException;

	/**
	 * 根据一组实体的PK值从持久层查找实体
	 * 
	 * @param ids
	 * @return
	 * @throws DaoException
	 */
	public List<T> findByIds(Collection<ID> ids) throws DaoException;

	/**
	 * 从持久层查找所有实体
	 * 
	 * @return
	 * @throws DaoException
	 */
	public List<T> findAll() throws DaoException;

	/**
	 * 从持久层查找所有的实体，并根据指定的属性排序
	 * 
	 * @param sortOrders
	 *            排序属性，通常为【属性值=升序或降序】，升序为<code>true</code>,降序为<code>false</code>
	 * @return
	 * @throws DaoException
	 */
	public List<T> findAll(Map<String, Boolean> sortOrders) throws DaoException;

	/**
	 * 同步持久化上下文中的内容到持久层
	 * 
	 * @throws DaoException
	 */
	public void flush() throws DaoException;

	/**
	 * 获取实体引用
	 * 
	 * @param clazz
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public T getReference(Class<T> clazz, ID id) throws DaoException;

	/**
	 * 生成ID
	 * 
	 * @param sequenceName
	 * @return
	 */
	public Long generateID(String sequenceName);
}
