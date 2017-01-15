package name.abhijitsarkar.javaee.salon.appointment.domain;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "APPTS")
public class Appointment implements Serializable {
	private static final long serialVersionUID = 3267192581273265412L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "USER_ID", nullable = false, unique = false)
	private Long userId;

	@Column(name = "START_DT_TIME", nullable = false, unique = false)
	@Convert(converter = OffsetDateTimeAttributeConverter.class)
	private OffsetDateTime startDateTime;

	@Column(name = "END_DT_TIME", nullable = false, unique = false)
	@Convert(converter = OffsetDateTimeAttributeConverter.class)
	private OffsetDateTime endDateTime;

	@Column(name = "COMMENT", nullable = true, unique = false)
	@Convert(converter = OptionalStringConverter.class)
	private Optional<String> comment;
}
