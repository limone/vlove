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
package vlove.dao;

import java.util.List;
import java.util.Map;

/**
 * Basic implementation of a generic DAO - allows for interaction with a DB at a high level.
 * 
 * @author Michael Laccetti
 */
public interface GenericDao {
	/**
	 * Find a specific instance of an entity.
	 * @param entityClass The Entity class to search for.
	 * @param identifier The unique identifier for the object.
	 * @return The specific instance if found, null otherwise.
	 */
	public <T> T find(Class<T> entityClass, Object identifier);
	
	/**
	 * Update the existing entity in the persistence store.
	 * @param entity
	 * @return
	 */
	public <T> T merge(T entity);
	
	/**
	 * Inject the entity in to the persistence store.
	 * @param entity
	 * @return
	 */
	public <T> T persist(T entity);
	
	/**
	 * Perform a basic JQL query.
	 * @param query
	 * @param parameters
	 * @return
	 */
	public <T> List<T> query(String query, Map<String, Object> parameters);
	
	/**
	 * Perform a basic JQL query - specifying the result to start at and the number of results to return.
	 * @param query
	 * @param parameters
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public <T> List<T> query(String query, Map<String, Object> parameters, int firstResult, int maxResults);
	
	/**
	 * Perform a named JQL query - see @NamedQuery
	 * @param query
	 * @param parameters
	 * @return
	 */
	public <T> List<T> namedQuery(String query, Map<String, Object> parameters);
	
	/**
	 * Perform a named JQL query - see @NamedQuery - start at firstResult, return maxResults
	 * @param query
	 * @param firstResult
	 * @param maxResults
	 * @param parameters
	 * @return
	 */
	public <T> List<T> namedQuery(String query, Map<String, Object> parameters, int firstResult, int maxResults);
	
	/**
	 * Perform a raw SQL query.
	 * @param query
	 * @param parameters
	 * @param maxResults
	 * @return
	 */
	public <T> List<T> nativeQuery(String query, Map<String,Object> parameters, Integer maxResults);
	
	/**
	 * Perform a named JQL query, but return only one result.
	 * @param query
	 * @param parameters
	 * @return
	 */
	public <T> T namedQuerySingle(String query, Map<String, Object> parameters);
	
	/**
	 * Perform a JQL query, but return only one result.
	 * @param query
	 * @param parameters
	 * @return
	 */
	public <T> T querySingle(String query, Map<String, Object> parameters);
	
	/**
	 * Remove the specified entity from the persistence layer.
	 * @param entity
	 */
	public void remove(Object entity);
	
	/**
	 * Update the existing entity from the persistence layer.
	 * @param entity
	 */
	public void refresh(Object entity);
}