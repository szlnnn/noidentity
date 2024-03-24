package hu.thesis.msc.noidentity.entity;

import hu.thesis.msc.noidentity.config.HashMapConverter;
import hu.thesis.msc.noidentity.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table
public class ResourceAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identifier;

    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> attributesOnResource = new HashMap<>();

    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> expectedAttributes = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount user;

    private String reconStatus;

    private String operation;

    private String provisionError;

    private Date lastUpdateTime;

    private Date lastProvisionTime;

}
