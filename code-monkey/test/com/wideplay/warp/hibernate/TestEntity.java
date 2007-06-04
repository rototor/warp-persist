package com.wideplay.warp.hibernate;

import com.wideplay.warp.persist.dao.Finder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.NamedQuery;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
@Entity
@NamedQuery(name = TestEntity.LIST_ALL_QUERY, query = "from TestEntity")
public class TestEntity {
    private Long id;
    private String text;
    public static final String LIST_ALL_QUERY = "TestEntity.listAll";

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Finder(query = "from TestEntity")
    public List<TestEntity> listAll() { return null; }
}
