package name.abhijitsarkar.javaee.salon.appointment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Wither
public class Page {
	private int size;
	private int totalElements;
	private int totalPages;
	private int number;
}