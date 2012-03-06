/**
 * vlove - web based virtual machine management
 * Copyright (C) 2010 Limone Fresco Limited
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
      for (Entry<String, Object> e : parameters.entrySet()) {
        query.setParameter(e.getKey(), e.getValue());
      }
    }
  }

  /**
   * @see vlove.dao.GenericDao#find(Class, Object)
   */
  @Override
  public <T> T find(Class<T> entityClass, Object identifier) {
    return em.find(entityClass, identifier);
  }

  /**
   * @see vlove.dao.GenericDao#merge(Object)
   */
  @Override
  public <T> T merge(T entity) {
    return em.merge(entity);
  }

  /**
   * @see vlove.dao.GenericDao#persist(Object)
   */
  @Override
  public <T> T persist(T entity) {
    em.persist(entity);
    return entity;
  }

  /**
   * @see vlove.dao.GenericDao#query(String, Map)
   */
  @Override
  public <T> List<T> query(String query, Map<String, Object> parameters) {
    Query q = em.createQuery(query);
    applyParameters(q, parameters);
    return q.getResultList();
  }

  /**
   * @see vlove.dao.GenericDao#query(String, Map, int, int)
   */
  @Override
  public <T> List<T> query(String query, Map<String, Object> parameters, int firstResult, int maxResults) {
    Query q = em.createQuery(query).setFirstResult(firstResult).setMaxResults(maxResults);
    applyParameters(q, parameters);
    return q.getResultList();
  }

  /**
   * @see vlove.dao.GenericDao#namedQuery(String, Map)
   */
  @Override
  public <T> List<T> namedQuery(String query, Map<String, Object> parameters) {
    Query q = em.createNamedQuery(query);
    applyParameters(q, parameters);
    return q.getResultList();
  }

  /**
   * @see vlove.dao.GenericDao#namedQuery(String, Map, int, int)
   */
  @Override
  public <T> List<T> namedQuery(String query, Map<String, Object> parameters, int firstResult, int maxResults) {
    Query q = em.createNamedQuery(query).setFirstResult(firstResult).setMaxResults(maxResults);
    applyParameters(q, parameters);
    return q.getResultList();
  }

  /**
   * @see vlove.dao.GenericDao#namedQuerySingle(String, Map)
   */
  @Override
  public <T> T namedQuerySingle(String query, Map<String, Object> parameters) {
    Query q = em.createNamedQuery(query);
    applyParameters(q, parameters);
    try {
      return (T) q.getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * @see vlove.dao.GenericDao#querySingle(String, Map)
   */
  @Override
  public <T> T querySingle(String query, Map<String, Object> parameters) {
    Query q = em.createQuery(query);
    applyParameters(q, parameters);
    return (T) q.getSingleResult();
  }

  /**
   * @see vlove.dao.GenericDao#remove(Object)
   */
  @Override
  public void remove(Object entity) {
    if (em.contains(entity)) {
      em.remove(entity);
    }
  }

  /**
   * @see vlove.dao.GenericDao#refresh(Object)
   */
  @Override
  public void refresh(Object entity) {
    em.refresh(entity);
  }

  /**
   * @see vlove.dao.GenericDao#nativeQuery(String, Map, Integer)
   */
  @Override
  public <T> List<T> nativeQuery(String query, Map<String, Object> parameters, Integer maxResults) {
    Query q = em.createNativeQuery(query);
    if (maxResults != null) {
      q.setMaxResults(maxResults);
    }
    applyParameters(q, parameters);
    return q.getResultList();
  }
}