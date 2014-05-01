package org.pikater.shared.database.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="TaskType_20140430")
public class JPATaskType extends JPAAbstractEntity{
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
	
	public int getId() {
        return id;
    }
	@Column(unique=true)
	private String name;
	
	public JPATaskType(){
		super();
	}
	
	public JPATaskType(String name){
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getEntityName() {
		return "TaskType";
	}

	@Override
	public void updateValues(JPAAbstractEntity newValues) throws Exception {
		JPATaskType updatedValues=(JPATaskType)newValues;
		this.name=updatedValues.getName();
	}
}
