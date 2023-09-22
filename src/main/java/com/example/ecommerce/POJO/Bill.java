package com.example.ecommerce.POJO;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@NamedQuery(name = "Bill.getAllBills", query = "select b from Bill b order by b.id desc ")

@NamedQuery(name = "Bill.getBillByUsername",query = "select b from Bill b where b.createBy=:username order by b.id desc ")


@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "bill")
public class Bill implements Serializable {

    public static final long serialVersionUID =1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "total")
    private Integer total;

    @Column(name = "paymentMethod")
    private String paymentMethod;

    @Column(name = "productDetails",columnDefinition = "json")
    private String productDetails;

    @Column(name = "createBy")
    private String createBy;






}
