package rainbow.db.refinery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import rainbow.core.bundle.Bean;
import rainbow.core.util.converter.Converters;
import rainbow.db.dao.model.Column;
import rainbow.db.model.DataType;

@Bean(extension = Refinery.class)
public class DateTimeRefinery implements Refinery {

	@Override
	public RefineryDef def() {
		List<String> list = Arrays.asList("yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy年MM月dd日");
		return new RefineryDef("datetime", list, false);
	}

	@Override
	public boolean accept(Column column) {
		return column.getType().equals(DataType.TIMESTAMP) || column.getType().equals(DataType.DATE);
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
