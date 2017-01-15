package name.abhijitsarkar.javaee.salon.user.domain;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
import name.abhijitsarkar.javaee.salon.domain.Role;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Wither
@Entity
@Table(name = "AUTHORITIES", uniqueConstraints = @UniqueConstraint(columnNames = { "USER_ID", "AUTHORITY" }) )
public class Authority implements Serializable {
	private static final long serialVersionUID = -4711748438941844333L;

	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "AUTHORITIES_SEQ")
	@SequenceGenerator(name = "AUTHORITIES_SEQ", sequenceName = "AUTHORITIES_SEQ", initialValue = 101, allocationSize = 1)
	private Long id;

	@JoinColumn(name = "USER_ID", nullable = false, referencedColumnName = "ID")
	@ManyToOne
	private User user;

	@Column(name = "AUTHORITY", nullable = false)
	@Convert(converter = RoleConverter.class)
	private Role role;
}
