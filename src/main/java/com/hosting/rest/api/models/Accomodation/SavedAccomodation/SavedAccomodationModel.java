package com.hosting.rest.api.models.Accomodation.SavedAccomodation;


import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.hosting.rest.api.models.Accomodation.AccomodationModel;
import com.hosting.rest.api.models.User.UserModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "SAVED_ACCOMODATION")
public class SavedAccomodationModel implements Serializable{

    private static final long serialVersionUID = 5232197990106259537L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_USER")
    private UserModel idUser;

    @ManyToOne
    @JoinColumn(name = "ID_ACC")
    private AccomodationModel idAccomodation;

    @Column(name = "CREATED_AT")
    @CreatedDate
    private LocalDateTime createdAt;
    
    public SavedAccomodationModel(UserModel idUser, AccomodationModel idAccomodation) {
    	this.idUser = idUser;
    	this.idAccomodation = idAccomodation;
    }
}
