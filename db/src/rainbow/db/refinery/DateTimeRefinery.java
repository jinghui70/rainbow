package rainbow.db.refinery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import rainbow.core.bundle.Bean;
import rainbow.core.util.converter.Converters;
import rainbow.db.dao.model.Column;
import rainbow.db.model.DataType;

@Bean(extension = Refinery.class)
public class DateTimeRefinery implements Refinery {

	@Override
	public String getName() {
		return "datetime";
	}

	@Override
	public RefineryDef accept(Column column) {
		switch (column.getType()) {
		case TIMESTAMP:
			return makeDef(true, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
		case DATE:
			return makeDef(true, "yyyy-MM-dd", "yyyy年MM月dd日");
		case TIME:
			return makeDef(true, "HH:mm:ss");
		default:
			return null;
		}
	}

	@Override
	public void refine(Column column, Map<String, Object> data, String key, String param) {
		Object value = data.get(key);
		if (value == null)
			return;
		if (column.getType() == DataType.TIMESTAMP) {
			LocalDateTime date = Converters.convert(value, LocalDateTime.class);
			value = DateTimeFormatter.ofPattern(param).format(date);
		} else {
			LocalDate date = Converters.convert(value, LocalDate.class);
			value = DateTimeFormatter.ofPattern(param).format(date);
		}
		data.put(key, value);
	}

}
