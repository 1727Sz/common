package cn.sz1727.core.dao.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sz1727.core.exception.DaoException;
import cn.sz1727.core.i18n.CoreErrorMessages;
import cn.sz1727l.core.util.XCollectionUtil;
import cn.sz1727l.core.util.XMapUtil;
import cn.sz1727l.core.util.XStringUtil;

/**
 * JPA数据持久层基础查询类
 * 
 * cn.sz1727.core.dao.api.GenericJpaQueryDao.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class GenericJpaQueryDao<T extends Identifiable<ID>, ID extends Serializable> extends GenericJpaDao<T, ID> implements IQueryDao<T, ID>, IPageableQueryDao<T, ID>, IDynamicQuery<T, ID> {

	private static final Logger LOG = LoggerFactory.getLogger(GenericJpaQueryDao.class);

	public GenericJpaQueryDao() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> executeGenericQuery(String query, Map<String, Object> parameters, int start, int end) throws DaoException {

		if (XStringUtil.isBlank(query)) {
			LOG.error(CoreErrorMessages.create().sqlNullJpa("executeGenericQuery()").getMessage());
			throw new DaoException(CoreErrorMessages.create().sqlNullJpa("executeGenericQuery()"));
		}

		List<T> result = null;
		try {
			Query q = this.getEntityManager().createQuery(query);

			if (XMapUtil.isNotEmpty(parameters)) {
				for (Map.Entry<String, Object> entry : parameters.entrySet()) {
					String key = entry.getKey();
					if (XStringUtil.isNotEmpty(key)) {
						q.setParameter(key, entry.getValue());
					}
				}
			}

			if (start * end < 0 || (start == -1 && end == -1)) {
				result = q.getResultList();
			} else {
				result = q.setFirstResult(start).setMaxResults(end).getResultList();
			}
		} catch (Exception e) {
			LOG.error(CoreErrorMessages.create().persistenceJpa("executeGenericQuery()", e.getMessage()).getMessage(), e);
			throw new DaoException(CoreErrorMessages.create().persistenceJpa("executeGenericQuery()", e.getMessage()), e);
		}
		return result == null ? new ArrayList<T>(0) : result;
	}

	@Override
	public Pageable<T> pagingQuery(String query, Map<String, Object> parameters, int currentPage, int pageSize) throws DaoException {
		int total = fetchDataSize(query, parameters);
		if (total > 0) {
			Pageable<T> pageable = new Pageable<T>(total, pageSize, currentPage);
			List<T> result = null;
			if (pageable.getStartIndex() == 0 && pageable.getPageSize() < 0) {
				// Query all data, when startIndex == 0 and pageSize < 0
				result = executeGenericQuery(query, parameters, -1, -1);
			} else {
				result = executeGenericQuery(query, parameters, pageable.getStartIndex(), pageable.getPageSize());
			}
			pageable.setItems(result);
			return pageable;
		}
		return null;
	}

	@Override
	public Pageable<T> pagingQuery(IDynamicParams<T, ID> dynamicParams, int currentPage, int pageSize) throws DaoException {
		int total = fetchDataSize(dynamicParams);
		if (total > 0) {
			Pageable<T> pageable = new Pageable<T>(total, pageSize, currentPage);
			TypedQuery<T> query = createDynamicQuery(dynamicParams);

			List<T> result = null;
			if (pageable.getStartIndex() == 0 && pageable.getPageSize() < 0) {
				// Query all data, when startIndex == 0 and pageSize < 0
				result = query.getResultList();
			} else {
				result = query.setFirstResult(pageable.getStartIndex()).setMaxResults(pageable.getPageSize()).getResultList();
			}
			pageable.setItems(result);
			return pageable;
		}
		return null;
	}

	@Override
	public TypedQuery<T> createDynamicQuery(IDynamicParams<T, ID> dynamicParams) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getPersistentClass());
		Root<T> root = criteriaQuery.from(getPersistentClass());
		criteriaQuery.select(root).where(dynamicParams.buildWhereClause(root, criteriaBuilder));
		if (XCollectionUtil.isNotEmpty(dynamicParams.buildOrderClause(root, criteriaBuilder))) {
			criteriaQuery.orderBy(dynamicParams.buildOrderClause(root, criteriaBuilder));
		}
		TypedQuery<T> query = getEntityManager().createQuery(criteriaQuery);
		return query;
	}

	// ~ Help methods
	// --------------------------------------------------------------------------------------

	private int fetchDataSize(IDynamicParams<T, ID> dynamicParams) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<T> root = criteriaQuery.from(getPersistentClass());
		criteriaQuery.select(criteriaBuilder.count(root)).where(dynamicParams.buildWhereClause(root, criteriaBuilder));
		if (XCollectionUtil.isNotEmpty(dynamicParams.buildOrderClause(root, criteriaBuilder))) {
			criteriaQuery.orderBy(dynamicParams.buildOrderClause(root, criteriaBuilder));
		}
		TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);
		Long size = query.getSingleResult();
		return size.intValue();
	}

	private int getIndexOfFrom(String query) {
		int fromIdx = query.indexOf("from");
		int bracketIdx = query.indexOf("(");
		if (bracketIdx == -1 || bracketIdx > fromIdx) {
			return fromIdx;
		}
		StringTokenizer tokenizer = new StringTokenizer(query, "()", true);
		int brackets = 0;
		fromIdx = 0;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if ("(".equals(token))
				brackets++;
			if (")".equals(token))
				brackets--;
			if (token.indexOf("from") != -1 && brackets == 0) {
				return fromIdx + token.indexOf("from");
			}
			fromIdx += token.length();
		}
		return -1;
	}

	private int fetchDataSize(String query, Map<String, Object> parameters) throws DaoException {
		LOG.info("Fetching data size...");
		LOG.info("Original query: " + query);

		String trimmedQuery = query.toLowerCase().trim();

		if (trimmedQuery.startsWith("select") || trimmedQuery.startsWith("from")) {
			int fromIdx = getIndexOfFrom(trimmedQuery);

			if (fromIdx != -1) {
				String s1 = "";

				if (trimmedQuery.startsWith("from")) {
					s1 = "*";
				} else {
					s1 = query.substring("select".length(), fromIdx).trim();
				}

				if (s1.toLowerCase().indexOf("distinct") == -1 && (s1.indexOf(",") != -1 || s1.indexOf("(") != -1)) {
					s1 = "*";
				} else if (s1.toLowerCase().startsWith("distinct")) {
					if (s1.substring("distinct".length()).trim().toLowerCase().startsWith("new")) {
						// get constructor args
						int op = s1.indexOf("(");// opening parenthesis
						int cp = s1.lastIndexOf(")");// closing parenthesis
						if (op == -1 || cp == -1) {
							throw new RuntimeException("Can't parse constructor expression from query " + trimmedQuery);
						}
						String constructorArgs = s1.substring(op + 1, cp);
						// see if there are several of them, take whichever goes
						// first
						StringTokenizer st = new StringTokenizer(constructorArgs, ",", false);
						s1 = "distinct " + st.nextToken();
					} else {
						StringTokenizer st = new StringTokenizer(s1.substring("distinct".length()).trim(), ",", false);
						// take only first one
						s1 = "distinct " + st.nextToken();
					}
				}
				String s2 = query.substring(fromIdx);
				int orderIdx = new String(s2).toLowerCase().indexOf("order by");
				if (orderIdx != -1) {
					s2 = s2.substring(0, orderIdx);
				}

				// remove fetch from query
				// assuming that fetch will never be at 0 position or last
				// statement
				int fetchIdx = s2.toLowerCase().indexOf(" fetch ");
				int fetchSize = " fetch ".length();
				while (fetchIdx != -1) {
					s2 = s2.substring(0, fetchIdx) + s2.substring(fetchIdx + fetchSize - 1);
					fetchIdx = s2.toLowerCase().indexOf(" fetch ");
				}

				trimmedQuery = "select " + "count(" + s1 + ") " + s2;
			}
		}
		LOG.info("Fetching result set size. Query is: " + trimmedQuery);
		int size = 0;
		if (trimmedQuery != null) {
			List<?> result = executeGenericQuery(trimmedQuery, parameters, -1, -1);
			size = result == null || result.isEmpty() ? 0 : ((Long) result.get(0)).intValue();
		}
		LOG.info("Fetched data size - " + size);
		return size;
	}
}
