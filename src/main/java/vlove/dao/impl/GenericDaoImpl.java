package vlove.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vlove.dao.GenericDao;

@Repository
@Transactional
public class GenericDaoImpl implements GenericDao {
	@PersistenceContext
	private EntityManager em;

	private static void applyParameters(Query query, Map<String, Object> parameters) {
		if (parameters != null) {
			for (Entry<String,Object> e : parameters.entrySet()) {
				query.setParameter(e.getKey(), e.getValue());
			}
		}
	}

	public <T> T find(Class<T> entityClass, Object identifier) {
		return em.find(entityClass, identifier);
	}

	public <T> T merge(T entity) {
		return em.merge(entity);
	}
	public <T> T persist(T entity) {
		em.persist(entity);
		return entity;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> query(String query, Map<String, Object> parameters) {
		Query q = em.createQuery(query);
		applyParameters(q, parameters);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> query(String query, Map<String, Object> parameters, int firstResult, int maxResults) {
		Query q = em.createQuery(query).setFirstResult(firstResult).setMaxResults(maxResults);
		applyParameters(q, parameters);
		return q.getResultList();
	}
	@SuppressWarnings("unchecked")
	public <T> List<T> namedQuery(String query, Map<String, Object> parameters) {
		Query q = em.createNamedQuery(query);
		applyParameters(q, parameters);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> namedQuery(String query, Map<String, Object> parameters, int firstResult, int maxResults) {
		Query q = em.createNamedQuery(query).setFirstResult(firstResult).setMaxResults(maxResults);
		applyParameters(q, parameters);
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T namedQuerySingle(String query, Map<String, Object> parameters) {
		Query q = em.createNamedQuery(query);
		applyParameters(q, parameters);
		try {
			return (T)q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T querySingle(String query, Map<String, Object> parameters) {
		Query q = em.createQuery(query);
		applyParameters(q, parameters);
		return (T)q.getSingleResult();
	}

	public void remove(Object entity) {
		if (em.contains(entity)) {
			em.remove(entity);
		}
	}
	
	public void refresh(Object entity) {
		em.refresh(entity);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> nativeQuery(String query, Map<String, Object> parameters, Integer maxResults) {
		Query q = em.createNativeQuery(query);
		if (maxResults != null) {
			q.setMaxResults(maxResults);
		}
		applyParameters(q, parameters);
		return q.getResultList();
	}
}