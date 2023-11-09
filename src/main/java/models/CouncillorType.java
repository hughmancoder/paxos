package models;

public enum CouncillorType {
    M1("council.M1"),
    M2("council.M2"),
    M3("council.M3"),
    M4_9("council.M4_9");

    private final String className;

    CouncillorType(String className) {
        this.className = className;
    }

    public Class<?> getCouncillorClass() throws ClassNotFoundException {
        return Class.forName(this.className);
    }
}
