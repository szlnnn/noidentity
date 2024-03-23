package hu.thesis.msc.noidentity.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table
public class Request {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    private UserAccount targetUser;

    @ManyToOne(fetch=FetchType.LAZY)
    private UserAccount requester;

    @ManyToOne(fetch=FetchType.LAZY)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserRoleAssignment assignment;

    /**
     * A - ADD
     * R - REVOKE
     */
    private String operation;

    /**
     * P -- pending provision
     * T -- terminated rejected
     * C -- completed
     * U -- under approval
     * N -- new
     */
    private String status;

    /**
     *  R -- refused
     *  C -- completed
     */
    private String outcome;



    private Date creationTime;



}
