package com.bookstore.www.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.*;

@Node("BookType")
public class BookType {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public List<UUID> getBook_ids() {
        return book_ids;
    }

    public void setBook_ids(List<UUID> book_ids) {
        this.book_ids = book_ids;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<UUID> book_ids;

    public BookType(String name){
        this.name = name;
    }

    @Relationship(type = "Related")
    public Set<BookType> relatedBookTypes;

    public void addRelateBookType(BookType bookType){
        if(relatedBookTypes == null)
            relatedBookTypes = new HashSet<>();
        relatedBookTypes.add(bookType);
    }

    public void addBookID(UUID id){
        if(book_ids == null)
            book_ids = new ArrayList<>();
        for(UUID book_id : book_ids){
            if(book_id == id)
                return;
        }
        book_ids.add(id);
    }

    @JsonBackReference
    public Set<BookType> getRelatedBookTypes(){
        return relatedBookTypes;
    }

    @JsonBackReference void setRelatedBookTypes(Set<BookType> relatedBookTypes){
        this.relatedBookTypes = relatedBookTypes;
    }
}
