package vlove.dao;

import java.util.List;
import java.util.Map;

public interface GenericDao {
	public <T> T find(Class<T> entityClass, Object identifier);
	public <T> T merge(T entity);
	public <T> T persist(T entity);
	public <T> List<T> query(String query, Map<String, Object> parameters);
	public <T> List<T> query(String query, Map<String, Object> parameters, int firstResult, int maxResults);
	public <T> List<T> namedQuery(String query, Map<String, Object> parameters);
	public <T> List<T> namedQuery(String query, Map<String, Object> parameters, int firstResult, int maxResults);
	public <T> List<T> nativeQuery(String query, Map<String,Object> parameters, Integer maxResults);
	public <T> T namedQuerySingle(String query, Map<String, Object> parameters);
	public <T> T querySingle(String query, Map<String, Object> parameters);
	public void remove(Object entity);
	public void refresh(Object entity);
}