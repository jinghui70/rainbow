package rainbow.db.dev.api;

import java.util.List;

import rainbow.db.query.QueryRequest;
import rainbow.db.refinery.RefineryDef;

public interface DataService {

	List<String> dataSources();

	Node dataTree(String model);

	EntityNodeX entity(String model, String name);

	List<RefineryDef> getRefinery(String model, String entityName, String columnName);

	Object query(String model, QueryRequest query);

	Object sql(String model, String text);

}
