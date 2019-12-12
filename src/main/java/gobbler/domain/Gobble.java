package gobbler.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gobble extends AbstractPersistable<Long> {
    
    private String gobblerName;
    private String content;
    private LocalDateTime time;

}
