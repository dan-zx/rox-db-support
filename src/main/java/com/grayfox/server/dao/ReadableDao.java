package com.grayfox.server.dao;

import java.util.List;

public interface ReadableDao<E> {

    List<E> findAll();
}