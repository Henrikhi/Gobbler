package gobbler.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private Long profilePictureId;
}
