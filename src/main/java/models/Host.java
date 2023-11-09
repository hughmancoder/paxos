package models;

import java.util.Objects;
import java.io.Serializable;

public class Host implements Serializable {

    private static final long serialVersionUID = 1L;

    private String address;
    private int port;
    private PaxosRole role;
    private String id;
    private CouncillorType councillorType;

    /*
     * Constructor that
     * includes CouncillorType
     */
    public Host(String address, int port, String id, PaxosRole role, CouncillorType councillorType) {
        this.address = address;
        this.port = port;
        this.role = role;
        this.id = id;
        this.councillorType = councillorType;
    }

    /*
     * Constructor that does not include CouncillorType, sets it to null by default
     */
    public Host(String address, int port, String id, PaxosRole role) {
        this(address, port, id, role, null);
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public PaxosRole getRole() {
        return role;
    }

    public void setRole(PaxosRole role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public CouncillorType getCouncillorType() {
        return councillorType;
    }

    public void setCouncillorType(CouncillorType councillorType) {
        this.councillorType = councillorType;
    }

    /*
     * Used to compare two host objects to see if they are equal. This is used in
     * the host list to check if a host already exists in the list.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Host host = (Host) o;
        return port == host.port && Objects.equals(address, host.address);
    }

    /*
     * when overriding equals, it is important to also override hashCode to
     * maintain
     * the general contract for hashCode, which states that equal objects must
     * have
     * equal hash codes
     */
    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }

}
