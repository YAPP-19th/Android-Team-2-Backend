package com.yapp.sharefood.image.domain;

import com.yapp.sharefood.common.domain.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class Image extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "images_id")
    private Long id;

    private String storeFilename; // uuid

    private String realFilename;
}
