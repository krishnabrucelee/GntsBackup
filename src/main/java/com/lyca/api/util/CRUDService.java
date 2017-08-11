package com.lyca.api.util;

import java.util.List;

/**
 * 
 * @author Krishna
 *
 * @param <T>
 */
public interface CRUDService<T> {

    /**
     * Generic method to save entity.
     *
     * @param t entity
     * @return saved entity
     * @throws Exception error occurs
     */
    T save(T t) throws Exception;

    /**
     * Generic method to update entity.
     *
     * @param t entity
     * @return updated entity
     * @throws Exception error occurs
     */
    T update(T t) throws Exception;

    /**
     * Generic method to delete entity.
     *
     * @param t entity
     * @throws Exception if error occurs
     */
    void delete(T t) throws Exception;

    /**
     * Generic method to delete entity.
     *
     * @param id of the entity
     * @throws Exception if error occurs
     */
    void delete(Integer id) throws Exception;

    /**
     * Generic method to find entity.
     *
     * @param id of the entity
     * @return entity
     * @throws Exception if error occurs
     */
    T find(Integer id) throws Exception;

    /**
     * @return result of entities
     * @throws Exception if error occurs.
     */
    List<T> findAll() throws Exception;

}
