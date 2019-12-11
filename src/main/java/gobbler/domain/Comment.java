package gobbler.domain;


import java.sql.Timestamp;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends AbstractPersistable<Long> {

    String comment;
    Timestamp timestamp;
    
    Long postId;
    Long gobblerId;
   
    
}
