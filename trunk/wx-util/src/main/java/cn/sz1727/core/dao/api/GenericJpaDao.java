package cn.sz1727.core.dao.api;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.type.LongType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sz1727.core.exception.DaoException;
import cn.sz1727.core.exception.MallRuntimeException;
import cn.sz1727.core.i18n.AbstractErrorMessageFactory;
import cn.sz1727.core.i18n.CoreErrorMessages;
import cn.sz1727l.core.util.XMapUtil;

/**
 * JPA数据持久层基类
 * 
 * cn.sz1727.core.dao.api.GenericJpaDao.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public abstract class GenericJpaDao<T extends Identifiable<ID>, ID extends Serializable> implements IDao<T, ID> {
	private static final Logger LOG = LoggerFactory.getLogger(GenericJpaDao.class);

	private static final String ASC = "ASC";
	private static final String DESC = "DESC";

	private Class<T> persistentClass;

	private EntityManager entityManager;

	public GenericJpaDao() {
		persistentClass = getGenericType();
	}

	public GenericJpaDao(Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public T save(T entity) throws DaoException {
		if (entity == null) {
			LOG.error(CoreErrorMessages.create().argsNullJpa("save()").getMessage());
			throw new DaoException(CoreErrorMessages.create().argsNullJpa("save()"));
		}

		try {
			getEntityManager().persist(entity);
		} catch (Exception e) {
			LOG.error(CoreErrorMessages.create().persistenceJpa("save()", e.getMessage()).getMessage(), e);
			throw new DaoException(CoreErrorMessages.create().persistenceJpa("save()", e.getMessage()), e);
		}
		return entity;
	}

	@Override
	public T update(T entity) throws DaoException {

		if (entity == null) {
			LOG.error(CoreErrorMessages.create().argsNullJpa("update()").getMessage());
			throw new DaoException(CoreErrorMessages.create().argsNullJpa("update()"));
		}

		try {
			T mergedEntity = getEntityManager().merge(entity);
			return mergedEntity;
		} catch (Exception e) {
			LOG.error(CoreErrorMessages.create().persistenceJpa("update()", e.getMessage()).getMessage(), e);
			throw new DaoException(CoreErrorMessages.create().persistenceJpa("update()", e.getMessage()), e);
		}
	}

	@Override
	public void delete(T entity) throws DaoException {

		if (entity == null) {
			LOG.error(CoreErrorMessages.create().argsNullJpa("delete()").getMessage());
			throw new DaoException(CoreErrorMessages.create().argsNullJpa("delete()"));
		}

		try {
			if (Identifiable.class.isAssignableFrom(persistentClass)) {
				getEntityManager().remove(getEntityManager().getReference(entity.getClass(), ((Identifiable<ID>) entity).getIdentifier()));
			} else {
				entity = getEntityManager().merge(entity);
				getEntityManager().remove(entity);
			}
		} catch (Exception e) {
			LOG.error(CoreErrorMessages.create().persistenceJpa("delete()", e.getMessage()).getMessage(), e);
			throw new DaoException(CoreErrorMessages.create().persistenceJpa("delete()", e.getMessage()), e);
		}
	}

	@Override
	public T findById(ID id) throws DaoException {

		if (id == null) {
			LOG.error(CoreErrorMessages.create().argsNullJpa("findById()").getMessage());
			throw new DaoException(CoreErrorMessages.create().argsNullJpa("findById()"));
		}

		try {
			T entity = getEntityManager().find(persistentClass, id);
			return entity;
		} catch (Exception e) {
			LOG.error(CoreErrorMessages.create().persistenceJpa("findById()", e.getMessage()).getMessage(), e);
			throw new DaoException(CoreErrorMessages.create().persistenceJpa("findById()", e.getMessage()), e);
		}
	}

	@Override
	public List<T> findByIds(Collection<ID> ids) throws DaoException {
		if (ids == null || ids.isEmpty()) {
			return Collections.<T> emptyList();
		}

		List<T> entities = new ArrayList<T>();
		try {
			for (ID id : ids) {
				T entity = getEntityManager().find(persistentClass, id);
				if (entity == null) {
					LOG.debug("Entity: {} cannot be found", id);
				}
				entities.add(entity);
			}
			return entities;
		} catch (Exception e) {
			LOG.error(CoreErrorMessages.create().persistenceJpa("findByIds()", e.getMessage()).getMessage(), e);
			throw new DaoException(CoreErrorMessages.create().persistenceJpa("findByIds()", e.getMessage()), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll() throws DaoException {
		try {
			return getEntityManager().createQuery("SELECT x from " + getPersistentClass().getSimpleName() + " x").getResultList();
		} catch (Exception e) {
			LOG.error(CoreErrorMessages.create().persistenceJpa("findAll()", e.getMessage()).getMessage(), e);
			throw new DaoException(CoreErrorMessages.create().persistenceJpa("findAll()", e.getMessage()), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll(Map<String, Boolean> sortOrders) throws DaoException {
		try {
			StringBuffer jpql = new StringBuffer("SELECT x from " + getPersistentClass().getSimpleName() + " x ");

			if (XMapUtil.isEmpty(sortOrders)) {
				return getEntityManager().createQuery(jpql.toString()).getResultList();
			}

			jpql.append("ORDER BY");
			int index = 0;
			for (Map.Entry<String, Boolean> entry : sortOrders.entrySet()) {
				String sortProp = entry.getKey();
				String sortOrder = entry.getValue() ? ASC : DESC;
				jpql.append(" x.").append(sortProp).append(" ").append(sortOrder);
				if (index < sortOrders.size() - 1) {
					jpql.append(",");
				}
				index++;
			}
			LOG.debug("findAll(sortOrders) ... hql >>> {}", jpql.toString());
			return getEntityManager().createQuery(jpql.toString()).getResultList();
		} catch (Exception e) {
			LOG.error(CoreErrorMessages.create().persistenceJpa("findAll(sortOrders)", e.getMessage()).getMessage(), e);
			throw new DaoException(CoreErrorMessages.create().persistenceJpa("findAll(sortOrders)", e.getMessage()), e);
		}
	}

	@Override
	public void flush() throws DaoException {
		try {
			getEntityManager().flush();
		} catch (Exception e) {
			LOG.error(CoreErrorMessages.create().persistenceJpa("flush()", e.getMessage()).getMessage(), e);
			throw new DaoException(CoreErrorMessages.create().persistenceJpa("flush()", e.getMessage()), e);
		}
	}

	@Override
	public T getReference(Class<T> clazz, ID id) throws DaoException {
		try {
			return getEntityManager().getReference(clazz, id);
		} catch (Exception e) {
			LOG.error(CoreErrorMessages.create().persistenceJpa("getReference()", e.getMessage()).getMessage(), e);
			throw new DaoException(CoreErrorMessages.create().persistenceJpa("getReference()", e.getMessage()), e);
		}
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	public void setPersistentClass(Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Class<T> getGenericType() {
		Class clazz = getClass();
		ParameterizedType type = null;

		do {
			try {
				type = (ParameterizedType) clazz.getGenericSuperclass();
			} catch (ClassCastException e) {
				LOG.warn(e.getMessage());
			}
			clazz = clazz.getSuperclass();
		} while (type == null && clazz != null);

		if (type == null) {
			throw new MallRuntimeException(AbstractErrorMessageFactory.createStaticErrorMessage("Cannot get the generic type", -1));
		}

		return (Class<T>) type.getActualTypeArguments()[0];
	}

	/**
	 * 生成ID
	 * 
	 * @param sequenceName
	 * @return
	 */
	@Override
	public Long generateID(String sequenceName) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(sequenceName);
		sb.append(".nextval as id from DUAL");
		Query q = this.getEntityManager().createNativeQuery(sb.toString());
		q.unwrap(SQLQuery.class).addScalar("id", LongType.INSTANCE);
		//
		return (Long) q.getSingleResult();
	}
}
