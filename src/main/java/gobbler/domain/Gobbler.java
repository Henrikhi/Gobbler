package gobbler.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gobbler extends AbstractPersistable<Long> {

    private String gobblerName;
    private String password;
    private String name;
    private String gobblerPath;

    @OneToMany(targetEntity = Gobbler.class, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Gobbler> followers = new ArrayList<>();

    @OneToMany(targetEntity = Gobbler.class, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Gobbler> following = new ArrayList<>();

  
    
   
    
}
