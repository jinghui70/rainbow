package rainbow.db.refinery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import rainbow.core.bundle.Bean;
import rainbow.core.bundle.Extension;
import rainbow.core.util.converter.Converters;
import rainbow.db.dao.model.Column;

@Bean
@Extension
public class DateTimeRefinery implements Refinery {

	@Override
	public String getName() {
		return "datetime";
	}

	@Override
	public RefineryDef accept(Column column) {
		switch (column.getType()) {
		case TIMESTAMP:
			return makeDef(true, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss");
		case DATE:
			return makeDef(true, "yyyy-MM-dd", "yyyy年MM月dd日");
		default:
			return null;
		}
	}

	@Override
	public Object refine(Column column, Object data, String param) {
		if (data == null)
			return null;
		switch (column.getType()) {
		case TIMESTAMP:
			LocalDateTime datetime = Converters.convert(data, LocalDateTime.class);
			return DateTimeFormatter.ofPattern(param).format(datetime);
		case DATE:
			LocalDate date = Converters.convert(data, LocalDate.class);
			return DateTimeFormatter.ofPattern(param).format(date);
		default:
			return data;
		}
	}

}
