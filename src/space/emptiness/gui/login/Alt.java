/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.gui.login;

public class Alt {
    private String mask = "";
    private final String username;
    private String password;
    public float slideTrans;

    public Alt(String username, String password) {
        this(username, password, "");
    }

    public Alt(String username, String password, String mask) {
        this.username = username;
        this.password = password;
        this.mask = mask;
    }

    public String getMask() {
        return this.mask;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

