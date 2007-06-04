package com.wideplay.warp.persist.dao;

import com.google.inject.name.Named;
import com.wideplay.warp.hibernate.TestEntity;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * On: 3/06/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
public interface TestAccessor {

    @Finder(query = "from TestEntity")
    List<TestEntity> listAll();

    @Finder(query = "from TestEntity")
    TestEntity[] listAllAsArray();

    @Finder(namedQuery = TestEntity.LIST_ALL_QUERY)
    List<TestEntity> listEverything();

    @Finder(query = "from TestEntity where text = :text", returnAs = HashSet.class)
    Set<TestEntity> find(@Named("text") String id);

    @Finder(query = "from TestEntity where id = :id")
    TestEntity fetch(@Named("id") Long id);

    @Finder(query = "from TestEntity where id = ? and text = ?")
    TestEntity fetchById(Long id, @MaxResults int i, String text);

    @Finder(query = "from TestEntity")
    List<TestEntity> listAll(@MaxResults int i);
}
