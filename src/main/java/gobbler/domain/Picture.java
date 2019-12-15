package gobbler.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Picture extends AbstractPersistable<Long> {

    Long gobblerId;
    String info;
    boolean isProfilePicture;

//    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    byte[] content;

    @OneToMany(targetEntity = Comment.class, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(targetEntity = Gobbler.class, fetch = FetchType.EAGER)
    private List<Gobbler> peckers = new ArrayList<>();

    public void addComment(Comment comment) {
        if (!this.comments.contains(comment)) {
            this.comments.add(comment);
        }
    }

    public void peck(Gobbler gobbler) {
        if (!this.getPeckers().contains(gobbler)) {
            this.peckers.add(gobbler);
        }
    }
    
    public void unpeck(Gobbler gobbler) {
        if (this.getPeckers().contains(gobbler)) {
            this.peckers.remove(gobbler);
        }
    }

}
