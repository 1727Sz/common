package cn.sz1727.mybatis.interceptor;

import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sz1727l.core.util.XPropertiesUtil;
import cn.sz1727l.core.util.XStringUtil;

/**
 * cn.sz1727.mybatis.interceptor.PagingInterception.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }))
public class PagingInterceptor implements Interceptor {

	private static final Logger LOG = LoggerFactory.getLogger(PagingInterceptor.class);
	private static final String SQL_SELECT_REGEX = "(?is)^\\s*SELECT.*$";
	private static final String SQL_COUNT_REGEX = "(?is)^\\s*SELECT\\s+COUNT\\s*\\(\\s*(?:\\*|\\w+)\\s*\\).*$";

	@Override
	public Object intercept(Invocation inv) throws Throwable {
		StatementHandler target = (StatementHandler) inv.getTarget();
		BoundSql boundSql = target.getBoundSql();
		String sql = boundSql.getSql();
		if (XStringUtil.isBlank(sql)) {
			return inv.proceed();
		}
		LOG.debug("origin sql >>> {}", sql.replaceAll("\n", ""));
		if (sql.matches(SQL_SELECT_REGEX) && !Pattern.matches(SQL_COUNT_REGEX, sql)) {
			Object obj = FieldUtils.readField(target, "delegate", true);
			RowBounds rowBounds = (RowBounds) FieldUtils.readField(obj, "rowBounds", true);
			// 分页参数存在且不为默认值时进行分页SQL构造
			if (rowBounds != null && rowBounds != RowBounds.DEFAULT) {
				FieldUtils.writeField(boundSql, "sql", newSql(sql, rowBounds), true);
				LOG.debug("new sql >>> {}", boundSql.getSql().replaceAll("\n", ""));
				// 还原
				FieldUtils.writeField(rowBounds, "offset", RowBounds.NO_ROW_OFFSET, true);
				FieldUtils.writeField(rowBounds, "limit", RowBounds.NO_ROW_LIMIT, true);
			}
		}
		return inv.proceed();
	}

	protected String newSql(String oldSql, RowBounds rowBounds) {
		StringBuilder start = new StringBuilder();
		start.append("SELECT * FROM (");
		start.append("SELECT row_.*, ROWNUM rownum_ FROM ( ");
		start.append(oldSql);
		start.append(" ) row_ ");
		start.append(") WHERE rownum_ > ");
		start.append(rowBounds.getOffset());
		start.append(" AND rownum_ <= ");
		start.append(rowBounds.getLimit()+rowBounds.getOffset());
		return start.toString();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setProperties(Properties properties) {
		// ignore
		LOG.debug(XPropertiesUtil.propertiesToString(properties, true));
	}

}
