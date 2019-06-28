package com.yukisoft.themarket.JavaRepositories;

public class UserModel {
    private String id;
    private SignInMethod signInMethod;
    private boolean registered;

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SignInMethod getSignInMethod() {
        return signInMethod;
    }

    public void setSignInMethod(SignInMethod signInMethod) {
        this.signInMethod = signInMethod;
    }

    public UserModel() {
    }
}
