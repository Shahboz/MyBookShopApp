package com.example.MyBookShopApp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "int default 0", nullable = false)
    private Integer sortIndex;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition="TEXT", nullable = false)
    private String text;

}