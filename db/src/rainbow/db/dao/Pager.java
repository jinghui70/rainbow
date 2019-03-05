package rainbow.db.dao;

public final class Pager {

	private int page;

	private int limit;

	public Pager() {
	}

	public Pager(int pageNo, int pageSize) {
		this.page = pageNo;
		this.limit = pageSize;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getFrom() {
		return (page - 1) * limit + 1;
	}

	public int getTo() {
		return page * limit;
	}

	public static Pager make(int page, int pageSize) {
		if (page < 1 || pageSize <= 0)
			return null;
		else
			return new Pager(page, pageSize);
	}

}
