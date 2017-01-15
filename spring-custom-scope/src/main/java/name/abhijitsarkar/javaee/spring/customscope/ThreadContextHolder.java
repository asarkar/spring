package name.abhijitsarkar.javaee.spring.customscope;

public class ThreadContextHolder {
    private static final ThreadLocal<ThreadAttributes> threadCtx = new InheritableThreadLocal<ThreadAttributes>() {
	protected ThreadAttributes initialValue() {
	    return new ThreadAttributes();
	}
    };

    public static void setThreadAttributes(ThreadAttributes attr) {
	ThreadContextHolder.threadCtx.set(attr);
    }

    public static ThreadAttributes currentThreadAttributes()
	    throws IllegalStateException {
	ThreadAttributes attr = threadCtx.get();

	if (attr == null) {
	    throw new IllegalStateException("No thread scoped attributes.");
	}

	return attr;
    }
}
