package com.example.MyBookShopApp.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "book_file")
@ApiModel(description = "Data model of book entity")
public class BookFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "BookFile id generated by DB", example = "1", required = true, position = 1)
    private Integer id;

    @Column(nullable = false)
    @ApiModelProperty(example = "123456ASD", required = true)
    private String hash;

    @Column(name = "type_id", nullable = false)
    @ApiModelProperty(example = "1", required = true)
    private Integer typeId;

    @Column(nullable = false)
    @ApiModelProperty(example = "book_files.pdf", required = true)
    private String path;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    public String getBookFileExtensionString() {
        return BookFileType.getExtentionStringByTypeId(this.typeId);
    }

}