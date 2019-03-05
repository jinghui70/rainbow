package rainbow.core.bundle;

public enum BundleState {

	FOUND, // 已发现
	RESOLVING, // 解析中
	READY, // 可以启动
	STARTING, // 启动中
	ERROR, // 启动发生错误
	STOPPING, // 停止中
	ACTIVE;

}
