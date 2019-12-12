package gobbler.domain;

import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Peck extends AbstractPersistable<Long> {

    Long gobblerId;
    Long pictureId;
    Long gobbleId;
    
    

}
