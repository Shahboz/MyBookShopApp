package com.example.MyBookShopApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "genre")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Genre id generated by DB", required = true, example = "1", position = 1)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "Parent ID genre", position = 2)
    private Genre parent;

    @Column(nullable = false)
    @ApiModelProperty(required = true, position = 3)
    private String slug;

    @Column(nullable = false)
    @ApiModelProperty(value = "Genre name", required = true, position = 4)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "genresList")
    private List<Book> bookList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    List<Genre> child = new ArrayList<>();

    public Genre(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }

    public List<Genre> getChild() {
        return this.child;
    }

    @JsonIgnore
    public Genre getRoot() {
        if(this.parent == null)
            return this;
        return this.parent.getRoot();
    }

}