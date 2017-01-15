package name.abhijitsarkar.javaee.salon.user.domain;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
import name.abhijitsarkar.javaee.salon.domain.OptionalStringConverter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Wither
@Entity
@Table(name = "USERS")
public class User implements Serializable {
	private static final long serialVersionUID = 262950482349139355L;

	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "USERS_SEQ")
	@SequenceGenerator(name = "USERS_SEQ", sequenceName = "USERS_SEQ", initialValue = 101, allocationSize = 1)
	private Long id;

	@Column(name = "FIRST_NAME", nullable = false, unique = false)
	@Convert(converter = NameConverter.class)
	private String firstName;

	@Column(name = "LAST_NAME", nullable = false, unique = false)
	@Convert(converter = NameConverter.class)
	private String lastName;

	@Column(name = "PHONE_NUM", nullable = false, unique = false)
	@Convert(converter = PhoneNumberConverter.class)
	private String phoneNum;

	@Column(name = "EMAIL", nullable = true, unique = false)
	@Convert(converter = OptionalStringConverter.class)
	private Optional<String> email;
}
