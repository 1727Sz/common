package cn.sz1727.core.dao.api;

import java.io.Serializable;

import javax.persistence.TypedQuery;

/**
 * cn.sz1727.core.dao.api.IDynamicQuery.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public interface IDynamicQuery<T extends Identifiable<ID>, ID extends Serializable> {

	TypedQuery<T> createDynamicQuery(IDynamicParams<T, ID> dynamicParams);

}
