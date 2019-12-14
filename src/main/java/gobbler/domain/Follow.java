package gobbler.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follow extends AbstractPersistable<Long> {

    @ManyToOne
    Gobbler follower;

    @ManyToOne
    Gobbler following;

    LocalDateTime time;

}
