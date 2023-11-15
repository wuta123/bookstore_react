package com.bookstore.www.repository;

import com.bookstore.www.entity.BookType;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookTypeRepository extends Neo4jRepository<BookType, Long>{

    @Query("MATCH (a:BookType)-[:Related]->(b) " +
            "WHERE a.name = $name "+
            "RETURN b"
    )
    List<BookType> findRelatedBookTypes1(@Param("name") String name);

    @Query("MATCH (a:BookType)-[:Related]->(b)-[:Related]->(c) " +
            "WHERE a.name = $name AND a <> c "+
            "RETURN c"
    )
    List<BookType> findRelatedBookTypes2(@Param("name") String name);

    @Query("MATCH (a:BookType) " +
            "WHERE a.name = $name "+
            "RETURN a"
    )
    BookType findBookTypeByNameLike(@Param("name") String name);

    @Query("MATCH (a:BookType) " +
            "WHERE a.name = $name "+
            "RETURN a"
    )
    List<BookType> findBookTypesByNameLike(@Param("name") String name);
}
