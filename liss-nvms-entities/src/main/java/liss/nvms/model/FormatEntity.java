package liss.nvms.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import liss.nvms.abstracts.UserAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Format")
@Table(name = "formats")
@JsonIgnoreProperties( ignoreUnknown = true)
public class FormatEntity extends UserAbstract {
	
    @Column(name = "name")
    @JsonProperty(value = "name", required = true)
    private String name;



}
