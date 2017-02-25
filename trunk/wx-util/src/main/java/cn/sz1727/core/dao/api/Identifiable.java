package cn.sz1727.core.dao.api;

import java.io.Serializable;

import org.apache.commons.collections.Transformer;

/**
 * 实现该接口的实体，表明使用逻辑ID或业务ID作为主键
 * 
 * cn.sz1727.core.dao.api.Identifiable.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public interface Identifiable<ID extends Serializable> extends Serializable {
	/**
	 * Get ID
	 * 
	 * @return
	 */
	ID getIdentifier();

	/**
	 * Set ID
	 * 
	 * @param identifiable
	 */
	void setIdentifier(ID identifiable);

	public static final Transformer TRANSFORMER = new Transformer() {

		@SuppressWarnings("unchecked")
		@Override
		public Object transform(Object identifiable) {
			return ((Identifiable<Serializable>) identifiable).getIdentifier();
		}

	};
}
