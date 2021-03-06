package liss.nvms.abstracts;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
@MappedSuperclass
public abstract class TimeStampEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="uuid", unique =true)
	@JsonProperty(value = "reference", required = false )
    private String id;
	
	@Column(name = "created_at")
	@Temporal(TemporalType.DATE)
    private Date createdAt;
	
	@Column(name = "created_on")
	@Temporal(TemporalType.TIME)
    private Date createdOn;
	 
 
    @Column(name = "updated_at")
    @Temporal(TemporalType.DATE)
    private Date updatedAt;
    
    @Column(name = "updated_on")
    @Temporal(TemporalType.TIME)
    private Date updatedOn;
    
    @Column(name = "deleted")
    private Boolean isDeleted;
 
    @PrePersist
    public void prePersist() {
		id = UUID.randomUUID().toString();
		createdAt = new Date();
		createdOn = new Date();
        updatedAt = new Date();
        updatedOn = new Date();
        isDeleted = false;
    }
 
    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
        updatedOn = new Date();
    }

}
