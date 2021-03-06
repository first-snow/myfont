package work.cxlm.model.properties;

/**
 * created 2020/11/14 23:07
 *
 * @author Chiru
 */
public enum PrimaryProperties implements PropertyEnum {
    /**
     * 系统安装日期
     */
    START_TIME("start_time", Long.class, ""),

    /**
     * 系统是否已安装
     */
    IS_INSTALLED("is_installed", Boolean.class, "false"),
    ;

    private final String value;

    private final Class<?> type;

    private final String defaultValue;

    PrimaryProperties(String value, Class<?> type, String defaultValue) {
        this.value = value;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }

    @Override
    public String getValue() {
        return value;
    }
}
