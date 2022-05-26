package com.hosting.rest.api.models.Plan;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PLAN_SUBSCRIPTION")
public class PlanSubscriptionModel implements Serializable{

    private static final long serialVersionUID = -4999590484132940629L;

	@EmbeddedId
    private PlanSubscriptionUserHostId planSubscriptionUserHostId;
    
    @Column(name = "CREATED_AT")
    @CreatedDate
    private LocalDateTime createdAt;
}
