package main.models;

import javax.persistence.*;

@Entity
@Table(name = "tags")
public class Tag
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;
}
