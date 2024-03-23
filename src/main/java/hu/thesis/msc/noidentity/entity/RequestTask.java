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
public class RequestTask {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    private UserAccount approver;


    @ManyToOne(fetch=FetchType.LAZY)
    private Request parentRequest;

    /**
     * R -- rejected
     * A -- completed
     * N -- new
     */
    private String status;

    /**
     * -manager
     * -applicationOwner
     */
    private String type;

    private Date creationTime;

    private Date completionTime;



}
