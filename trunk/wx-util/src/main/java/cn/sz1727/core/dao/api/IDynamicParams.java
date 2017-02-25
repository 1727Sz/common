package cn.sz1727.core.dao.api;

import java.io.Serializable;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * cn.sz1727.core.dao.api.IDynamicParams.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public interface IDynamicParams<T extends Identifiable<ID>, ID extends Serializable> extends Serializable {

	Predicate buildWhereClause(Root<T> type, CriteriaBuilder criteriaBuilder);

	List<Order> buildOrderClause(Root<T> type, CriteriaBuilder criteriaBuilder);
}
