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
public class UserRoleAssignment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    private UserAccount user;

    @ManyToOne(fetch=FetchType.LAZY)
    private Role role;


    /**
     * PA -- waiting for provision but approved
     * PR -- waiting for provision but rejected
     * P -- pending approval
     * R -- revoked or refused
     * A -- assigned
     */
    private String assignmentStatus;


    private Date creationTime;

    private Date assignedTime;

    private Date revokedTime;


}
